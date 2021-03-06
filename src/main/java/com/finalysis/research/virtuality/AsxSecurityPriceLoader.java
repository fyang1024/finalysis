package com.finalysis.research.virtuality;

import au.com.bytecode.opencsv.CSVReader;
import com.finalysis.research.DateUtils;
import com.finalysis.research.reality.Exchange;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuote;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AsxSecurityPriceLoader implements SecurityPriceLoader {

    private static final Logger logger = LoggerFactory.getLogger(AsxSecurityPriceLoader.class);

    private static final int maxCodes = 10;


    @Autowired
    SecurityPriceRepository securityPriceRepository;

    @Autowired
    SecurityRepository securityRepository;

    @Autowired
    SecurityFinder securityFinder;

    @Autowired
    TradingDateService tradingDateService;

    @Override
    public void loadSecurityPrice(Exchange exchange) {
        loadFromArchive(exchange);
        loadFromInternet(exchange);
        logger.info("--Done--");
    }

    @Override
    public void loadSecurityPrice(Exchange exchange, Date from, Date to) {
        List<Security> securities = securityRepository.findActiveByExchange(exchange, from);
        List<Security> lastBunch = new ArrayList<>(maxCodes);
        int count = 0;
        for (Security security : securities) {
            if (count < maxCodes) {
                lastBunch.add(security);
                count++;
            } else {
                securityPriceRepository.save(loadHistoricalFromYahoo(exchange, lastBunch, from, to));
                lastBunch.clear();
                lastBunch.add(security);
                count = 1;
            }
        }
        if (!lastBunch.isEmpty()) {
            securityPriceRepository.save(loadHistoricalFromYahoo(exchange, lastBunch, from, to));
        }
    }

    public List<SecurityPrice> loadHistoricalFromYahoo(Exchange exchange, List<Security> lastBunch, Date fromDate, Date toDate) {
        List<SecurityPrice> securityPriceList = new ArrayList<>();
        Map<String, Security> map = buildMap(lastBunch);
        String[] codes = lastBunch.stream().map(s -> s.getCode() + ".AX").collect(Collectors.toSet()).toArray(new String[lastBunch.size()]);
        Calendar from = Calendar.getInstance();
        from.setTime(fromDate);
        Calendar to = Calendar.getInstance();
        to.setTime(toDate);
        for (String yahooCode : codes) {
            try {
                Stock stock = YahooFinance.get(yahooCode, from, to, Interval.DAILY);
                String code = stock.getSymbol().replace(".AX", "");
                logger.info("Load price - " + code);
                List<HistoricalQuote> history = stock.getHistory();
                logger.info("history : " + history.size());
                for (HistoricalQuote quote : history) {
                    Date date = quote.getDate().getTime();
                    SecurityPrice securityPrice = securityPriceRepository.findByCodeAndExchangeAndOpenDate(code, exchange, date);
                    if (securityPrice == null) {
                        Security security = map.get(code);
                        if (security.getListingDate() != null && !security.getListingDate().after(date)
                                && quote.getVolume() != null && quote.getVolume() > 0
                                ) {
                            securityPrice = new SecurityPrice();
                            securityPrice.setCode(code);
                            securityPrice.setExchange(exchange);
                            securityPrice.setOpenDate(date);
                            securityPrice.setCloseDate(date);
                            securityPrice.setSecurity(security);
                            securityPrice.setOpenPrice(quote.getOpen());
                            securityPrice.setHighestPrice(quote.getHigh());
                            securityPrice.setLowestPrice(quote.getLow());
                            securityPrice.setClosePrice(quote.getClose());
                            securityPrice.setVolume(quote.getVolume().intValue());
                            securityPriceList.add(securityPrice);
                        }
                    } else {
                        securityPriceList.add(securityPrice);
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return securityPriceList;
    }


    private void loadFromInternet(Exchange exchange) {
        Date date = tradingDateService.getLatestTradingDate(exchange);
        List<Security> securities = securityRepository.findActiveByExchange(exchange, date);
        List<Security> lastBunch = new ArrayList<>(maxCodes);
        int count = 0;
        for (Security security : securities) {
            if (count < maxCodes) {
                lastBunch.add(security);
                count++;
            } else {
                securityPriceRepository.save(loadLastBunchFromInternet(exchange, lastBunch, date));
                lastBunch.clear();
                lastBunch.add(security);
                count = 1;
            }
        }
        if (!lastBunch.isEmpty()) {
            securityPriceRepository.save(loadLastBunchFromInternet(exchange, lastBunch, date));
        }
    }

    List<SecurityPrice> loadLastBunchFromInternet(Exchange exchange, List<Security> lastBunch, Date date) {
        try {
            return loadLastBunchFromYahoo(exchange, lastBunch, date);
        } catch (Exception e1) {
            logger.error(e1.getMessage(), e1);
            try {
                return loadLastBunchFromExchange(exchange, lastBunch, date);
            } catch (Exception e2) {
                logger.error(e2.getMessage(), e2);
                return new ArrayList<>();
            }
        }
    }

    private List<SecurityPrice> loadLastBunchFromYahoo(Exchange exchange, List<Security> lastBunch, Date date) throws IOException {
        List<SecurityPrice> securityPriceList = new ArrayList<>();
        Map<String, Security> map = buildMap(lastBunch);
        String[] codes = lastBunch.stream().map(s -> s.getCode() + ".AX").collect(Collectors.toSet()).toArray(new String[lastBunch.size()]);
        Map<String, Stock> stocks = YahooFinance.get(codes);
        for (Stock stock : stocks.values()) {
            String code = stock.getSymbol().replace(".AX", "");
            logger.info("Load price - " + code);
            SecurityPrice securityPrice = securityPriceRepository.findByCodeAndExchangeAndOpenDate(code, exchange, date);
            if (securityPrice == null) {
                Security security = map.get(code);
                StockQuote quote = stock.getQuote();
                if (security.getListingDate() != null && !security.getListingDate().after(date)
                        && quote != null && quote.getVolume() != null && quote.getVolume() > 0
                        && quote.getLastTradeTime() != null && !quote.getLastTradeTime().getTime().before(date)) {
                    securityPrice = new SecurityPrice();
                    securityPrice.setCode(code);
                    securityPrice.setExchange(exchange);
                    securityPrice.setOpenDate(date);
                    securityPrice.setCloseDate(date);
                    securityPrice.setSecurity(security);
                    securityPrice.setOpenPrice(quote.getOpen());
                    securityPrice.setHighestPrice(quote.getDayHigh());
                    securityPrice.setLowestPrice(quote.getDayLow());
                    securityPrice.setClosePrice(quote.getPrice());
                    securityPrice.setVolume(quote.getVolume().intValue());
                    securityPriceList.add(securityPrice);
                }
            } else {
                securityPriceList.add(securityPrice);
            }
        }
        return securityPriceList;
    }

    public void setSecurity(Exchange exchange) {
        List<String> codes = securityPriceRepository.findSecurityPriceCodesWithoutSecurity(exchange);
        for (String code : codes) {
            logger.info(code);
            List<SecurityPrice> prices = securityPriceRepository.findByCodeAndExchangeAndSecurityIsNull(code, exchange);
            for (SecurityPrice price : prices) {
                if (price.getSecurity() != null) {
                    logger.error("Security exists");
                } else {
                    Security security = securityFinder.findSecurity(exchange, price.getCode(), price.getOpenDate());
                    if (security != null) {
                        logger.info("Found security " + security.getId() + " - current code: " + security.getCode());
                        price.setSecurity(security);
                        securityPriceRepository.save(price);
                    } else {
                        logger.info("Could not find security for " + price.getCode() + " - " + price.getOpenDate());
                    }
                }
            }
        }

    }

    private List<SecurityPrice> loadLastBunchFromExchange(Exchange exchange, List<Security> lastBunch, Date date) {
        List<SecurityPrice> securityPriceList = new ArrayList<>();
        Map<String, Security> map = buildMap(lastBunch);
        ChromeDriver driver = new ChromeDriver();
        String url = exchange.getSecurityPriceUrl().replace("${codes}", concatenateCodes(lastBunch));
        logger.info(url);
        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, Integer.MAX_VALUE);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table.datatable")));
        List<WebElement> priceTables = driver.findElements(By.cssSelector("table.datatable"));
        if (!priceTables.isEmpty()) {
            List<WebElement> rows = priceTables.get(0).findElements(By.tagName("tr"));
            for (int i = 1; i < rows.size(); i++) {
                WebElement row = rows.get(i);
                String code = row.findElement(By.tagName("th")).getText().replace("*", "").trim();
                logger.info("Load price - " + code);
                SecurityPrice securityPrice = securityPriceRepository.findByCodeAndExchangeAndOpenDate(code, exchange, date);
                if (securityPrice == null) {
                    List<WebElement> cells = row.findElements(By.tagName("td"));
                    Security security = map.get(code);
                    if (security.getListingDate() != null && !security.getListingDate().after(date)
                            && hasText(cells)) {
                        Integer volume = new Integer(cells.get(8).getText().replaceAll(",", ""));
                        if (volume > 0) {
                            securityPrice = new SecurityPrice();
                            securityPrice.setCode(code);
                            securityPrice.setExchange(exchange);
                            securityPrice.setOpenDate(date);
                            securityPrice.setCloseDate(date);
                            securityPrice.setSecurity(security);
                            securityPrice.setOpenPrice(new BigDecimal(cells.get(5).getText()));
                            securityPrice.setHighestPrice(new BigDecimal(cells.get(6).getText()));
                            securityPrice.setLowestPrice(new BigDecimal(cells.get(7).getText()));
                            securityPrice.setClosePrice(new BigDecimal(cells.get(0).getText()));
                            securityPrice.setVolume(volume);
                            securityPriceList.add(securityPrice);
                        }
                    }
                } else {
                    securityPriceList.add(securityPrice);
                }
            }
        }
        driver.close();
        return securityPriceList;
    }

    private boolean hasText(List<WebElement> cells) {
        return StringUtils.hasText(cells.get(0).getText()) &&
                StringUtils.hasText(cells.get(5).getText()) &&
                StringUtils.hasText(cells.get(6).getText()) &&
                StringUtils.hasText(cells.get(7).getText()) &&
                StringUtils.hasText(cells.get(8).getText());
    }

    private Map<String, Security> buildMap(List<Security> lastBunch) {
        Map<String, Security> map = new HashMap<>();
        for (Security security : lastBunch) {
            map.put(security.getCode(), security);
        }
        return map;
    }

    private String concatenateCodes(List<Security> securities) {
        StringBuilder sb = new StringBuilder();
        for (Security security : securities) {
            if (sb.length() > 0) {
                sb.append("+");
            }
            sb.append(security.getCode());
        }
        return sb.toString();
    }

    private void loadFromArchive(Exchange exchange) {
        String securityPriceArchiveBase = exchange.getSecurityPriceArchive();
        loadFromTxtArchive(exchange, new File(securityPriceArchiveBase, "www.float.com.au"));
        loadFromTxtArchive(exchange, new File(securityPriceArchiveBase, "www.asxhistoricaldata.com"));
    }

    private void loadFromTxtArchive(Exchange exchange, File dir, Date fromDate) {
        File[] files = dir.listFiles((dir1, name) -> {
            String datePart = name.substring(0, name.indexOf("."));
            return !DateUtils.parse(datePart, "yyyyMMdd").before(fromDate);
        });
        Arrays.parallelSort(files, Comparator.comparing(File::getName));
        for (File file : files) {
            logger.info(file.getName());
            String datePart = file.getName().substring(0, file.getName().indexOf("."));
            Date tradingDate = DateUtils.parse(datePart, "yyyyMMdd");
            CSVReader csvReader = null;
            try {
                csvReader = new CSVReader(new FileReader(file));
                String[] line;
                while ((line = csvReader.readNext()) != null) {
                    String code = line[0];
                    if (securityPriceRepository.findByCodeAndExchangeAndOpenDate(code, exchange, tradingDate) == null) {
                        Security security = securityFinder.findSecurity(exchange, code, tradingDate);
                        if (security == null) {
                            logger.warn("Could not find security " + code + ", skipped");
                        } else {
                            SecurityPrice securityPrice = new SecurityPrice();
                            securityPrice.setCode(code);
                            securityPrice.setExchange(exchange);
                            securityPrice.setOpenDate(tradingDate);
                            securityPrice.setCloseDate(tradingDate);
                            securityPrice.setOpenPrice(new BigDecimal(line[2]));
                            securityPrice.setHighestPrice(new BigDecimal(line[3]));
                            securityPrice.setLowestPrice(new BigDecimal(line[4]));
                            securityPrice.setClosePrice(new BigDecimal(line[5]));
                            securityPrice.setVolume(new Integer(line[6]));
                            securityPrice.setSecurity(security);
                            securityPriceRepository.saveAndFlush(securityPrice);
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            } finally {
                if (csvReader != null) {
                    IOUtils.closeQuietly(csvReader);
                }
            }
        }
    }

    private void loadFromTxtArchive(Exchange exchange, File dir) {
        Date maxCloseDate = securityPriceRepository.findMaxCloseDate(exchange);
        if (maxCloseDate == null) {
            maxCloseDate = DateUtils.parse("19000101", "yyyyMMdd");
        }
        loadFromTxtArchive(exchange, dir, maxCloseDate);
    }
}
