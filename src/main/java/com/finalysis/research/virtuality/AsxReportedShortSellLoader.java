package com.finalysis.research.virtuality;

import com.finalysis.research.DateUtils;
import com.finalysis.research.reality.Exchange;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AsxReportedShortSellLoader implements ReportedShortSellLoader {

    private static final Logger logger = LoggerFactory.getLogger(AsxReportedShortSellLoader.class);

    private static Pattern datePattern = Pattern.compile("\\d{2}\\-\\w{3}\\-\\d{4}");


    @Autowired
    ReportedShortSellRepository reportedShortSellRepository;

    @Autowired
    SecurityFinder securityFinder;

    @Override
    public void loadReportedShortSell(Exchange exchange) {
        loadFromArchive(exchange);
        loadFromUrl(exchange);
        logger.info("--Done--");
    }

    private void loadFromUrl(Exchange exchange) {
        try (WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
            InputStreamReader inputStreamReader = new InputStreamReader(webClient.getPage(exchange.getShortSellUrl()).getWebResponse().getContentAsStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            loadOneDay(exchange, datePattern, bufferedReader);
            bufferedReader.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void loadFromArchive(Exchange exchange) {
        Date maxTradingDate = reportedShortSellRepository.findMaxTradingDate(exchange);
        if (maxTradingDate == null) {
            maxTradingDate = DateUtils.parse("19000101", "yyyyMMdd");
        }
        final Date latestDate = maxTradingDate;
        File dir = new File(exchange.getShortSellArchive());
        File[] files = dir.listFiles((dir1, name) -> {
            String datePart = name.substring(name.lastIndexOf('_') + 1, name.indexOf("."));
            return !DateUtils.parse(datePart, "yyyyMMdd").before(latestDate);
        });
        if (files != null && files.length > 0) {
            Arrays.parallelSort(files, (o1, o2) -> o1.getName().compareTo(o2.getName()));
            for (File file : files) {
                logger.info(file.getName());
                BufferedReader bufferedReader = null;
                try {
                    bufferedReader = new BufferedReader(new FileReader(file));
                    loadOneDay(exchange, datePattern, bufferedReader);
                }catch (IOException e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    if(bufferedReader != null) {
                        IOUtils.closeQuietly(bufferedReader);
                    }
                }
            }
        }
    }

    private void loadOneDay(Exchange exchange, Pattern pattern, BufferedReader bufferedReader) throws IOException {
        String line;
        int i = 0;
        int mark = 0, mark2 = 0;
        Date tradingDate = new Date();
        while (i++ < 8) { //skip 8 lines
            line = bufferedReader.readLine();
            if (i == 1) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String dateStr = line.substring(matcher.start(), matcher.end());
                    tradingDate = DateUtils.parse(dateStr, "dd-MMM-yyyy");
                }
            }
            if (i == 6) {
                mark = line.indexOf("Product");
                mark2 = line.indexOf("Reported Gross");
            }
        }
        while ((line = bufferedReader.readLine()) != null) {
            String codeAndName = line.substring(0, mark);
            String code = codeAndName.substring(0, codeAndName.indexOf(' '));
            if (reportedShortSellRepository.findBySecurityCodeAndExchangeAndTradingDate(code, exchange, tradingDate) == null) {
                String name = codeAndName.substring(code.length()).trim();
                String[] parts = line.substring(mark2).trim().split("\\s+");
                ReportedShortSell reportedShortSell = new ReportedShortSell();
                reportedShortSell.setSecurityCode(code);
                reportedShortSell.setCompanyName(name);
                reportedShortSell.setExchange(exchange);
                reportedShortSell.setTradingDate(tradingDate);
                reportedShortSell.setReportedGrossShorts(Long.parseLong(parts[0].replaceAll(",", "")));
                reportedShortSell.setIssuedShares(Long.parseLong(parts[1].replaceAll(",", "")));
                reportedShortSell.setSecurity(securityFinder.findSecurity(exchange, code, tradingDate));
                reportedShortSellRepository.saveAndFlush(reportedShortSell);
            }
        }
    }
}
