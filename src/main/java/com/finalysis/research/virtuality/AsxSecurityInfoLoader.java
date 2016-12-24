package com.finalysis.research.virtuality;

import com.finalysis.research.DateUtils;
import com.finalysis.research.reality.*;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AsxSecurityInfoLoader implements SecurityInfoLoader {

    private final static Logger logger = LoggerFactory.getLogger(AsxSecurityInfoLoader.class);

    @Autowired
    private SecurityRepository securityRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ShareRegistryRepository shareRegistryRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ManagementRoleRepository managementRoleRepository;

    @Autowired
    private ManagementPositionRepository managementPositionRepository;

    @Autowired
    private GicsIndustryGroupRepository gicsIndustryGroupRepository;

    @Autowired
    private TradingDateService tradingDateService;

    public void loadListingDate(Exchange exchange) {
        WebDriver driver = new HtmlUnitDriver(BrowserVersion.CHROME);
        driver.get(exchange.getRecentFloatsUrl());
        List<WebElement> tables = driver.findElements(By.cssSelector("table.floats"));
        if (tables.isEmpty()) {
            logger.error("Could not find recent floats table");
        } else {
            List<WebElement> rows = tables.get(0).findElements(By.tagName("tr"));
            for (WebElement row : rows) {
                List<WebElement> dataCells = row.findElements(By.tagName("td"));
                if (!dataCells.isEmpty()) {
                    String description = dataCells.get(0).getText();
                    String code = dataCells.get(1).getText();
                    String dateStr = dataCells.get(2).getText();
                    Date listingDate = DateUtils.parse(dateStr, DateUtils.AUSSIE_DATE_FORMAT);
                    Security security = securityRepository.findByCodeAndExchange(code, exchange, new Date());
                    if (security == null) {
                        logger.error("Could not find security for " + code + ", try description " + description);
                        security = securityRepository.findByDescriptionAndExchange(description.toUpperCase(), exchange, new Date());
                        if (security == null) {
                            logger.error("Could not find security by description " + description);
                        }
                    }
                    if (security != null) {
                        if (security.getListingDate() == null) {
                            logger.info("Set " + security.getCode() + " listing date - " + dateStr);
                            security.setListingDate(listingDate);
                            securityRepository.save(security);
                        } else if (listingDate.compareTo(security.getListingDate()) != 0) {
                            String oldListingDate = DateUtils.format(security.getListingDate(), DateUtils.AUSSIE_DATE_FORMAT);
                            logger.info("Change " + security.getCode() + " listing date from " + oldListingDate + " to " + dateStr);
                            security.setListingDate(listingDate);
                            securityRepository.save(security);
                        }
                    }
                }
            }
        }
        logger.info("--Done--");
    }

    public void loadInfo(Exchange exchange) {
        String url = exchange.getSecurityInfoUrl();
        Date tradingDate = tradingDateService.getLatestTradingDate(exchange);
        List<Security> securities = securityRepository.findSecuritiesToUpdateInfo(exchange, tradingDate);
//        System.setProperty("webdriver.chrome.driver", "D:\\web drivers\\chromedriver.exe");
        for (Security security : securities) {
            WebDriver driver = new ChromeDriver();
            logger.info(security.getCode() + " -- loading info...");
            driver.get(url.replace("${code}", security.getCode()));
            List<WebElement> infoTables = driver.findElements(By.className("company-details"));
            if (infoTables.isEmpty()) {
                logger.error("Could not find company details table");
            } else {
                try {
                    Company company = security.getCompany();
                    Map<String, String> values = new HashMap<>();
                    List<WebElement> rows = infoTables.get(0).findElements(By.tagName("tr"));
                    for (WebElement row : rows) {
                        values.put(row.findElement(By.tagName("th")).getText().toLowerCase(), row.findElement(By.tagName("td")).getText());
                    }
                    if (values.get("official listing date") != null) {
                        Date listingDate = DateUtils.parse(values.get("official listing date"), DateUtils.AUSSIE_DATE_FORMAT);
                        if (security.getListingDate() == null && listingDate != null
                                && securityRepository.findByCodeAndExchangeAndListingDate(security.getCode(), exchange, listingDate) != null) {

                            securityRepository.delete(security);
                            driver.close();
                            driver.quit();
                            continue;
                        }
                        security.setListingDate(listingDate);

                    }
                    if (company == null) {
                        String companyName = getCompanyName(driver);
                        company = companyRepository.findByName(companyName);
                        if (company == null) {
                            company = new Company();
                            company.setName(companyName);
                        }
                    }
                    if (company.getGicsIndustryGroup() == null && values.get("gics industry group") != null) {
                        company.setGicsIndustryGroup(gicsIndustryGroupRepository.findByName(values.get("gics industry group")));
                    }
                    security.setExemptForeign("Yes".equals(values.get("exempt foreign?")));
                    company.setWebsite(values.get("internet address"));
                    company.setRegisteredOfficeAddress(values.get("registered office address"));
                    company.setHeadOfficeTelephone(values.get("head office telephone"));
                    company.setHeadOfficeFax(values.get("head office fax"));
                    if (StringUtils.hasText(values.get("share registry"))) {
                        String[] shareRegistryNameAddress = values.get("share registry").split("\\n");
                        String shareRegistryTelephone = values.get("share registry telephone");
                        security.setShareRegistry(findOrCreateShareRegistry(shareRegistryNameAddress[0], shareRegistryNameAddress[1], shareRegistryTelephone));
                    }

                    company = companyRepository.saveAndFlush(company);
                    security.setCompany(company);
                    securityRepository.saveAndFlush(security);

                    List<WebElement> directorsTables = driver.findElements(By.className("company-people"));
                    if (directorsTables.isEmpty()) {
                        logger.warn("Could not find directors tables");
                    } else {
                        for (WebElement directorTable : directorsTables) {
                            rows = directorTable.findElements(By.tagName("tr"));
                            for (WebElement row : rows) {
                                createCompanyDirector(company, row.findElement(By.tagName("th")).getText(), row.findElement(By.tagName("td")).getText());
                            }
                        }
                    }
                } catch (StaleElementReferenceException e) {
                    logger.info("Code may be invalid and page got redirected");
                }
            }
            driver.close();
            driver.quit();
        }
        logger.info("--Done--");
    }

    private String getCompanyName(WebDriver driver) {
        List<WebElement> elements = driver.findElements(By.tagName("h2"));
        for (WebElement element : elements) {
            if (element.getText().endsWith(" details")) {
                return element.getText().replace(" details", "");
            }
        }
        return null;
    }

    private void createCompanyDirector(Company company, String titledFullName, String roleTxt) {
        Person person = new Person(titledFullName);
        Person existingPerson = personRepository.findByGivenNameAndSurname(person.getGivenName(), person.getSurname());
        if (existingPerson == null) {
            person = personRepository.saveAndFlush(person);
        } else {
            person = existingPerson;
        }
        String[] roleNames = roleTxt.split(",");
        for (String roleName : roleNames) {
            String trimmedRoleName = roleName.trim();
            if(trimmedRoleName.toLowerCase().contains("secretary")) {
                trimmedRoleName = "Secretary";
            }
            ManagementRole role = findOrCreateManagementRole(trimmedRoleName);
            if (managementPositionRepository.findByPersonAndCompanyAndManagementRole(person, company, role) == null) {
                ManagementPosition position = new ManagementPosition(person, company, role);
                managementPositionRepository.saveAndFlush(position);
            }
        }
    }

    private ManagementRole findOrCreateManagementRole(String name) {
        ManagementRole role = managementRoleRepository.findByName(name);
        if (role == null) {
            role = new ManagementRole(name);
            role = managementRoleRepository.saveAndFlush(role);
        }
        return role;
    }

    private ShareRegistry findOrCreateShareRegistry(String name, String address, String telephone) {
        ShareRegistry shareRegistry = shareRegistryRepository.findByName(name);
        if (shareRegistry == null) {
            shareRegistry = new ShareRegistry(name, address, telephone);
            shareRegistry = shareRegistryRepository.saveAndFlush(shareRegistry);
        } else if (!shareRegistry.getAddress().equalsIgnoreCase(address) ||
                telephone != null && !telephone.equalsIgnoreCase(shareRegistry.getTelephone()) ||
                telephone == null && shareRegistry.getTelephone() != null) {
            shareRegistry.setAddress(address);
            shareRegistry.setTelephone(telephone);
            shareRegistry = shareRegistryRepository.saveAndFlush(shareRegistry);
        }
        return shareRegistry;
    }
}
