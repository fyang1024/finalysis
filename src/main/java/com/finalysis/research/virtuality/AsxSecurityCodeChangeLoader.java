package com.finalysis.research.virtuality;

import com.finalysis.research.reality.Exchange;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AsxSecurityCodeChangeLoader implements SecurityCodeChangeLoader {

    private static final Logger logger = LoggerFactory.getLogger(AsxSecurityCodeChangeLoader.class);

    private static final Integer startYear = 2001;

    private static final String[] dateFormats = {"dd MMM yyyy", "ddMMM yyyy"};

    @Autowired
    private SecurityCodeChangeRepository securityCodeChangeRepository;

    @Autowired
    private SecurityRepository securityRepository;

    @Autowired
    private AsxDelistedSecurityLoader delistedSecurityLoader;

    @Override
    public void loadCodeChanges(Exchange exchange) {
        Calendar calendar = Calendar.getInstance();
        Integer currentYear = calendar.get(Calendar.YEAR);
        Integer year = startYear;
        Date latestChangeDate = securityCodeChangeRepository.findLatestChangeDate();
        if (latestChangeDate != null) {
            calendar.setTime(latestChangeDate);
            year = calendar.get(Calendar.YEAR);
        }
        while (year <= currentYear) {
            WebDriver driver = new HtmlUnitDriver(BrowserVersion.CHROME);
            String url = exchange.getSecurityCodeChangeUrl();
            String modifiedUrl = url.replace("${year}", year.toString());
            logger.info(modifiedUrl);
            driver.get(modifiedUrl);
            List<WebElement> tables = driver.findElements(By.tagName("table"));
            if (!tables.isEmpty()) {
                List<WebElement> rows = tables.get(0).findElements(By.tagName("tr"));
                int size = rows.size();
                for (int i = size - 1; i >= 0; i--) {
                    WebElement row = rows.get(i);
                    List<WebElement> cells = row.findElements(By.tagName("td"));
                    if (!cells.isEmpty()) {
                        String dateStr = cells.get(0).getText();
                        if (StringUtils.isBlank(dateStr)) {
                            continue;
                        }
                        String oldCode = cells.get(1).getText().trim();
                        String oldDescription = cells.get(2).getText().trim();
                        String newCode = cells.size() == 5 ? cells.get(3).getText().trim() : cells.get(4).getText().trim();
                        String newDescription = cells.size() == 5 ? cells.get(4).getText().trim() : cells.get(5).getText().trim();
                        if (year == 2008 || year == 2009) {
                            newDescription = newCode.substring(3).trim();
                            newCode = newCode.substring(0, 3);
                        }
                        if (!dateStr.endsWith(year.toString())) {
                            dateStr = dateStr + " " + year;
                        }
                        Date changeDate = DateUtils.parseDate(dateStr.replace("Sept", "Sep").replaceAll("\\s+", " ").trim(), dateFormats);
                        SecurityCodeChange securityCodeChange =
                                securityCodeChangeRepository.findByExchangeAndNewCodeAndChangeDate(exchange, newCode, changeDate);
                        if (securityCodeChange == null) {
                            securityCodeChange = new SecurityCodeChange(exchange, oldCode, newCode, oldDescription, newDescription, changeDate);
                            securityCodeChangeRepository.saveAndFlush(securityCodeChange);
                        }
                    }
                }
            }
            driver.close();
            year++;
        }
        populateSecurity(exchange);
        logger.info("--Done--");
    }

    public void processCodeChangesOfToday(Exchange exchange) {
        Date date = com.finalysis.research.DateUtils.today();
        List<SecurityCodeChange> codeChanges = securityCodeChangeRepository.findCodeChangesOn(exchange, date);
        for (SecurityCodeChange codeChange : codeChanges) {
            Security security = codeChange.getSecurity();
            if (security.getCode().equals(codeChange.getNewCode())) {
                logger.warn(security.getCode() + " code changed before change date");
            } else {
                security.setCode(codeChange.getNewCode());
                securityRepository.save(security);
                logger.info(codeChange.getOldCode() + " changed to " + codeChange.getNewCode());
            }
        }
        logger.info("--Done--");
    }

    private void populateSecurity(Exchange exchange) {
        List<SecurityCodeChange> codeChanges = securityCodeChangeRepository.findBySecurityIsNull(exchange);
        List<SecurityCodeChange> linkedCodeChanges = new ArrayList<>();
        Iterator<SecurityCodeChange> iterator = codeChanges.iterator();
        while (iterator.hasNext()) {
            SecurityCodeChange codeChange = iterator.next();
            SecurityCodeChange nextCodeChange = findNextCodeChange(linkedCodeChanges, codeChange);
            if (nextCodeChange == null) {
                linkedCodeChanges.add(codeChange);
            } else {
                nextCodeChange.setPrevious(codeChange);
            }
            iterator.remove();
        }
        for (SecurityCodeChange linkedCodeChange : linkedCodeChanges) {
            String currentCode = findCurrentCode(linkedCodeChange);
            Date changeDate = findCurrentCodeChangeDate(linkedCodeChange);
            Security security = securityRepository.findByCodeAndExchange(currentCode, exchange, changeDate);
            if (security != null) {
                logger.info(" Found security " + security.getCode() + " - " + security.getDescription());
                setSecurity(linkedCodeChange, security);
            } else {
                logger.info(" Could not find security for code " + currentCode + ", perhaps delisted");
//                String currentDescription = findCurrentDescription(linkedCodeChange);
//                delistedSecurityLoader.loadDelistedSecurity(exchange, currentCode, currentDescription);
            }
        }
    }

    private void setSecurity(SecurityCodeChange linkedCodeChange, Security security) {
        SecurityCodeChange codeChange = linkedCodeChange;
        codeChange.setSecurity(security);
        securityCodeChangeRepository.saveAndFlush(codeChange);
        while (codeChange.getPrevious() != null) {
            codeChange = codeChange.getPrevious();
            codeChange.setSecurity(security);
            securityCodeChangeRepository.saveAndFlush(codeChange);
        }
    }

    private SecurityCodeChange findNextCodeChange(List<SecurityCodeChange> linkedCodeChanges, SecurityCodeChange codeChange) {
        for (SecurityCodeChange linkedCodeChange : linkedCodeChanges) {
            SecurityCodeChange earliestCodeChange = findEarliestCodeChange(linkedCodeChange);
            if (codeChange.getNewCode().equalsIgnoreCase(earliestCodeChange.getOldCode()) &&
                    codeChange.getNewDescription().equalsIgnoreCase(earliestCodeChange.getOldDescription())) {
                return earliestCodeChange;
            }
        }
        return null;
    }

    private SecurityCodeChange findEarliestCodeChange(SecurityCodeChange linkedCodeChange) {
        SecurityCodeChange earliestCodeChange = linkedCodeChange;
        while (earliestCodeChange.getPrevious() != null) {
            earliestCodeChange = earliestCodeChange.getPrevious();
        }
        return earliestCodeChange;
    }

    private String findCurrentCode(SecurityCodeChange linkedCodeChange) {
        SecurityCodeChange currentCodeChange = linkedCodeChange;
        SecurityCodeChange nextCodeChange = currentCodeChange;
        while (currentCodeChange != null && currentCodeChange.getChangeDate() != null && currentCodeChange.getChangeDate().after(new Date())) {
            nextCodeChange = currentCodeChange;
            currentCodeChange = currentCodeChange.getPrevious();
        }
        return currentCodeChange == null ? nextCodeChange.getOldCode() : currentCodeChange.getNewCode();
    }

    private String findCurrentDescription(SecurityCodeChange linkedCodeChange) {
        SecurityCodeChange currentCodeChange = linkedCodeChange;
        SecurityCodeChange nextCodeChange = currentCodeChange;
        while (currentCodeChange != null && currentCodeChange.getChangeDate().after(new Date())) {
            nextCodeChange = currentCodeChange;
            currentCodeChange = currentCodeChange.getPrevious();
        }
        return currentCodeChange == null ? nextCodeChange.getOldDescription() : currentCodeChange.getNewDescription();
    }

    private Date findCurrentCodeChangeDate(SecurityCodeChange linkedCodeChange) {
        SecurityCodeChange currentCodeChange = linkedCodeChange;
        SecurityCodeChange nextCodeChange = currentCodeChange;
        while (currentCodeChange != null && currentCodeChange.getChangeDate() != null && currentCodeChange.getChangeDate().after(new Date())) {
            nextCodeChange = currentCodeChange;
            currentCodeChange = currentCodeChange.getPrevious();
        }
        return currentCodeChange == null ? nextCodeChange.getChangeDate() : currentCodeChange.getChangeDate();
    }
}
