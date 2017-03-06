package com.finalysis.research.virtuality;

import com.finalysis.research.DateUtils;
import com.finalysis.research.reality.Exchange;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AsxEtpLoader implements EtpLoader {

    private static final Logger logger = LoggerFactory.getLogger(AsxEtpLoader.class);

    private static Map<String, String> securityTypeLookup;

    static {
        securityTypeLookup = new HashMap<>();
        securityTypeLookup.put("ETF", "Exchange Traded Fund");
        securityTypeLookup.put("SP", "Structured Product");
        securityTypeLookup.put("MF", "Managed Fund");
        securityTypeLookup.put("Share*", "Preference Share");
    }

    @Autowired
    private SecurityRepository securityRepository;

    @Autowired
    private SecurityTypeRepository securityTypeRepository;

    @Autowired
    private SecurityCodeChangeRepository securityCodeChangeRepository;

    @Override
    public void loadExchangeTradedProducts(Exchange exchange) {
//        System.setProperty("webdriver.chrome.driver", "D:\\web drivers\\chromedriver.exe");
        String url = exchange.getListedEtpUrl();
        WebDriver driver = new ChromeDriver();
        driver.get(url);
        List<WebElement> tabs = driver.findElements(By.cssSelector("div#tabs_9119 ul li"));
        for (WebElement tab : tabs) {
            if (tab.getText().equals("ETPs")) {
                tab.click();
                break;
            }
        }
        List<WebElement> tables = driver.findElements(By.tagName("table"));
        if (!tables.isEmpty()) {
            List<WebElement> rows = tables.get(2).findElements(By.tagName("tr"));
            for (WebElement row : rows) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if (!cells.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (WebElement cell : cells) {
                        sb.append(cell.getText()).append(" - ");
                    }
                    logger.info(sb.toString());
                    String code = cells.get(1).getText().trim();
                    String securityType = cells.get(2).getText().trim();
                    String description = cells.get(0).getText().trim();
                    if (StringUtils.hasText(securityType) && securityType.equalsIgnoreCase("SP")) {
                        logger.info("Skipped structured product " + code + " | " + description);
                    } else {
                        Security etp = securityRepository.findByCodeAndExchange(code, exchange, new Date());
                        if (etp == null) {
                            SecurityCodeChange lastCodeChange = findLastCodeChange(code, exchange);
                            if (lastCodeChange != null) {
                                etp = securityRepository.findByCodeAndExchange(lastCodeChange.getOldCode(), exchange, lastCodeChange.getChangeDate());
                                if (etp != null) {
                                    logger.info("Updating " + etp.getCode() + " to " + code);
                                    etp.setCode(code); // change to new code
                                }
                            }
                        }
                        if (etp == null) {
                            logger.info("Creating it..." );
                            etp = new Security(code, exchange);
                        }
                        etp.setDescription(description);
                        etp.setSecurityType(securityTypeRepository.findByName(securityTypeLookup.get(securityType)));
                        etp.setBenchmark(cells.get(3).getText().trim());
                        String mer = cells.get(7).getText().trim();
                        if (StringUtils.hasText(mer)) {
                            etp.setManagementExpenseRatio(new BigDecimal(mer));
                        }
                        String dateString = cells.get(8).getText().trim().replaceAll("\\s+", "").replace("Sept", "Sep");
                        etp.setListingDate(DateUtils.parse(dateString, "MMM-yy"));
                        securityRepository.saveAndFlush(etp);
                    }
                }
            }
        }
        driver.close();
        logger.info("--Done--");
    }

    private SecurityCodeChange findLastCodeChange(String newCode, Exchange exchange) {
        List<SecurityCodeChange> codeChanges = securityCodeChangeRepository.findCodeChangesByNewCode(exchange, newCode);
        if (!codeChanges.isEmpty()) {
            return codeChanges.get(0);
        }
        return null;
    }
}
