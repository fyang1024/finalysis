package com.finalysis.research.virtuality;

import au.com.bytecode.opencsv.CSVReader;
import com.finalysis.research.DateUtils;
import com.finalysis.research.reality.Exchange;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

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
        loadFromUrl(exchange);
        logger.info("--Done--");
    }

    public void setSecurity(Exchange exchange) {
        List<String> codes = securityPriceRepository.findSecurityPriceCodesWithoutSecurity(exchange);
        for(String code : codes) {
            logger.info(code);
            List<SecurityPrice> prices = securityPriceRepository.findByCodeAndExchangeAndSecurityIsNull(code, exchange);
            for(SecurityPrice price : prices) {
                if(price.getSecurity() != null) {
                    logger.error("Security exists");
                } else {
                    Security security = securityFinder.findSecurity(exchange, price.getCode(), price.getOpenDate());
                    if(security != null) {
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

    private void loadFromUrl(Exchange exchange) {
        Date date = tradingDateService.getLatestTradingDate(exchange);
        List<Security> securities = securityRepository.findActiveByExchange(exchange, date);
        List<Security> lastBunch = new ArrayList<>(maxCodes);
        int count = 0;
        for (Security security : securities) {
            if (count < maxCodes) {
                lastBunch.add(security);
                count++;
            } else {
                securityPriceRepository.save(loadLastBunch(exchange, lastBunch, date));
                lastBunch.clear();
                lastBunch.add(security);
                count = 1;
            }
        }
        if (!lastBunch.isEmpty()) {
            securityPriceRepository.save(loadLastBunch(exchange, lastBunch, date));
        }
    }

    List<SecurityPrice> loadLastBunch(Exchange exchange, List<Security> lastBunch, Date date) {
        List<SecurityPrice> securityPriceList = new ArrayList<>();
        Map<String, Security> map = buildMap(lastBunch);
        WebDriver driver = new HtmlUnitDriver(BrowserVersion.CHROME);
        String url = exchange.getSecurityPriceUrl().replace("${codes}", concatenateCodes(lastBunch));
        logger.info(url);
        driver.get(url);
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
                        if(volume > 0) {
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
        Arrays.parallelSort(files, (o1, o2) -> o1.getName().compareTo(o2.getName()));
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
                        if(security == null) {
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
