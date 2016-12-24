package com.finalysis.research.reality;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;


@Component
public class ExchangeLoader {

    @Autowired
    private ExchangeRepository exchangeRepository;

    @Autowired
    private CountryRepository countryRepository;

    private String source = "/exchanges.csv";

    public Iterable<Exchange> loadExchanges() throws IOException {
        List<Exchange> exchanges = new ArrayList<>();
        InputStreamReader inputStreamReader = new InputStreamReader(getClass().getResourceAsStream(source));
        CSVReader csvReader = new CSVReader(inputStreamReader, CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, 1);
        Country currentCountry = null;
        String[] line;
        while ((line = csvReader.readNext()) != null) {
            Exchange exchange = buildExchange(line, currentCountry);
            if (exchangeRepository.findByName(line[1]) == null) {
                exchanges.add(exchange);
            }
            currentCountry = exchange.getCountry();
        }
        return exchangeRepository.save(exchanges);
    }

    public void updateWebsites() {
        List<Exchange> exchanges = exchangeRepository.findByWebsiteIsNull();
        for (Exchange exchange : exchanges) {
            String website = googleWebsite(exchange);
            if (website != null) {
                exchange.setWebsite(website);
                exchangeRepository.save(exchange);
            }
        }
    }

    private Exchange buildExchange(String[] line, Country currentCountry) {
        Exchange exchange = new Exchange();
        if (StringUtils.isBlank(line[0])) {
            exchange.setCountry(currentCountry);
        } else {
            exchange.setCountry(countryRepository.findByCommonName(line[0]));
        }
        exchange.setName(line[1]);
        exchange.setThomsonReutersSymbol(line[2]);
        exchange.setGoogleFinanceSymbol(line[3]);
        exchange.setYahooFinanceSymbol(line[4]);
        exchange.setTimeZone(line[5]);
        if (line[6].startsWith("-")) {
            exchange.setGmtOffsetNegative(true);
            String s = line[6].substring(1);
            exchange.setGmtOffset(Time.valueOf(formatToTime(s)));
        } else {
            exchange.setGmtOffset(Time.valueOf(formatToTime(line[6])));
        }
        if (!StringUtils.isBlank(line[7])) {
            String[] parts = line[7].split("-");
            exchange.setPreMarketStart(Time.valueOf(formatToTime(parts[0].trim())));
            exchange.setPreMarketEnd(Time.valueOf(formatToTime(parts[1].trim())));
        }
        if (!StringUtils.isBlank(line[8])) {
            String[] parts = line[8].split("-");
            exchange.setMarketStart(Time.valueOf(formatToTime(parts[0].trim())));
            exchange.setMarketEnd(Time.valueOf(formatToTime(parts[1].trim())));
        }
        if (!StringUtils.isBlank(line[9])) {
            String[] parts = line[9].split("-");
            exchange.setPostMarketStart(Time.valueOf(formatToTime(parts[0].trim())));
            exchange.setPostMarketEnd(Time.valueOf(formatToTime(parts[1].trim())));
        }
        if (!StringUtils.isBlank(line[10])) {
            int i = line[10].indexOf('(');
            int j = line[10].indexOf(')');
            exchange.setCurrencyName(line[10].substring(0, i).trim());
            exchange.setCurrencyCode(line[10].substring(i+1, j).trim());
        }
        return exchange;
    }

    private String formatToTime(String s) {
        if (s.contains(":")) {
            return s += ":00";
        } else {
            return s += ":00:00";
        }
    }

    private String googleWebsite(Exchange exchange) {
        WebDriver driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_17);
        driver.get("http://www.google.com.au");
        WebElement queryInput = driver.findElement(By.name("q"));
        queryInput.sendKeys(exchange.getName());
        WebElement luckyButton = driver.findElement(By.name("btnI"));
        try {
            luckyButton.click();
            String strippedWebsite = strip(driver.getCurrentUrl());
            if (strippedWebsite.contains("wikipedia")) {
                return null;
            }
            return strippedWebsite;
        } catch (Exception e) {
            return null;
        } finally {
            driver.close();
            driver.quit();
        }
    }

    private String strip(String url) {
        return url.substring(0, url.indexOf('/', "https://".length()) + 1);
    }
}
