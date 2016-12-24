package com.finalysis.research.virtuality;

import com.finalysis.research.DateUtils;
import com.finalysis.research.reality.*;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class AsxDelistedSecurityLoader implements DelistedSecurityLoader {

    Logger logger = LoggerFactory.getLogger(AsxDelistedSecurityLoader.class);

    @Autowired
    private SecurityRepository securityRepository;

    @Autowired
    private SecurityCodeChangeRepository securityCodeChangeRepository;

    @Autowired
    private GicsIndustryGroupRepository gicsIndustryGroupRepository;

    @Autowired
    private GicsIndustryRepository gicsIndustryRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ShareRegistryRepository shareRegistryRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ManagementPositionRepository managementPositionRepository;

    @Autowired
    private CompanyIndustryRepository companyIndustryRepository;

    @Autowired
    SecurityTypeRepository securityTypeRepository;

    @Autowired
    private ManagementRoleRepository managementRoleRepository;

    @Override
    public void loadDelistedSecurity(Exchange exchange) {
        String url = exchange.getDelistedSecurityUrl();
        WebDriver driver = new HtmlUnitDriver(BrowserVersion.CHROME);
        driver.get(url);
        List<WebElement> tables = driver.findElements(By.cssSelector("table.contenttable"));
        if (!tables.isEmpty()) {
            List<WebElement> rows = tables.get(0).findElements(By.tagName("tr"));
            if (rows.size() >= 2) {
                for (int i = 1; i < rows.size(); i++) {
                    List<WebElement> cells = rows.get(i).findElements(By.tagName("td"));
                    String name = cells.get(0).getText();
                    String code = cells.get(1).getText();
                    logger.info(name + " - " + code);
                    String dateString = cells.get(2).getText();
                    String reason = cells.get(3).getText();
                    Date delistedDate = DateUtils.parse(dateString, DateUtils.AUSSIE_DATE_FORMAT);
                    Security security = securityRepository.findListingSecurity(code, exchange, delistedDate);
                    if (security != null) {
                        logger.info(security.getCode() + " delisted on " + dateString);
                        security.setDelistedDate(delistedDate);
                        security.setDelistedReason(reason);
                        securityRepository.saveAndFlush(security);
                    }
                }
            }
        }
        driver.close();
        driver.quit();
        logger.info("--Done--");
    }

//    @Override
//    public void loadDelistedSecurity(Exchange exchange) {
//        String url = exchange.getDelistedSecurityUrl();
//        WebDriver driver = new HtmlUnitDriver(BrowserVersion.CHROME);
//        driver.get(url);
//        List<WebElement> tables = driver.findElements(By.cssSelector(".newsModule table"));
//        if (!tables.isEmpty()) {
//            List<WebElement> rows = tables.get(0).findElements(By.tagName("tr"));
//            if (rows.size() >= 3) {
//                for (int i = 2; i < rows.size(); i++) {
//                    List<WebElement> cells = rows.get(i).findElements(By.tagName("td"));
//                    String nameAndCode = cells.get(0).getText();
//                    logger.info(nameAndCode);
//                    String dateString = cells.get(1).getText();
//                    String reason = cells.get(2).getText();
//                    String code = nameAndCode.substring(nameAndCode.lastIndexOf('(') + 1, nameAndCode.lastIndexOf(')'));
//                    Date delistedDate = DateUtils.parse(dateString, "dd MMM yy");
//                    Security security = securityRepository.findListingSecurity(code, exchange, delistedDate);
//                    if (security != null) {
//                        logger.info(security.getCode() + " delisted on " + dateString);
//                        security.setDelistedDate(delistedDate);
//                        security.setDelistedReason(reason);
//                        securityRepository.saveAndFlush(security);
//                    } else {
//                        security = securityRepository.findByCodeAndExchangeAndDelistedDate(code, exchange, delistedDate);
//                        if (security == null) {
//                            WebElement link = cells.get(0).findElement(By.tagName("a"));
//                            loadDelistedSecurity(exchange, link.getAttribute("href"));
//                        }
//                    }
//                }
//            }
//        }
//        driver.close();
//        driver.quit();
//        logger.info("--Done--");
//    }

    public void loadDelistedSecurity(Exchange exchange, String code, String description) {
        WebDriver driver = new HtmlUnitDriver(BrowserVersion.CHROME);
        driver.get(exchange.getDelistedSecurityUrl());
        WebElement codeInput = driver.findElement(By.name("keywords_code"));
        codeInput.sendKeys(code);
        codeInput.submit();
        if (driver.getCurrentUrl().endsWith("search_results")) {
            List<WebElement> tables = driver.findElements(By.id("tablesorter_browse"));
            if (!tables.isEmpty()) {
                List<WebElement> rows = tables.get(0).findElements(By.tagName("tr"));
                for (int i = 1; i < rows.size(); i++) {
                    List<WebElement> cells = rows.get(i).findElements(By.tagName("td"));
                    String text = cells.get(0).getText().replaceAll("[^a-zA-Z0-9\\s]", "");
                    if (description.replaceAll("[^a-zA-Z0-9\\s]", "").equalsIgnoreCase(text)) {
                        List<WebElement> links = cells.get(0).findElements(By.tagName("a"));
                        if (!links.isEmpty()) {
                            loadDelistedSecurity(exchange, links.get(0).getAttribute("href"));
                        }
                    }
                }
            }
        } else {
            loadDelistedSecurity(exchange, driver.getCurrentUrl());
        }
        driver.close();
        driver.quit();
    }

    public void loadDelistedSecurity(Exchange exchange, String href) {
        WebDriver driver = new HtmlUnitDriver(BrowserVersion.CHROME);
        driver.get(href);
        Security security = findOrCreateSecurity(exchange, driver);
        if (security != null) {
            findOrCreateCurrentManagementPositions(driver, security.getCompany());
            findOrCreateFormerManagementPositions(driver, security.getCompany());

            List<WebElement> formerNamesTables = driver.findElements(By.cssSelector("div#formerNames table"));
            if (!formerNamesTables.isEmpty()) {
                WebElement formerNamesTable = formerNamesTables.get(0);
                List<WebElement> rows = formerNamesTable.findElements(By.tagName("tr"));
                SecurityCodeChange codeChange = new SecurityCodeChange();
                codeChange.setSecurity(security);
                codeChange.setExchange(exchange);
                for (int i = rows.size() - 1; i > 0; i--) {
                    WebElement row = rows.get(i);
                    List<WebElement> cells = row.findElements(By.tagName("td"));
                    if (codeChange.getOldCode() == null) {
                        String oldCode = getOldCode(cells.get(0).findElement(By.tagName("a")).getAttribute("href"));
                        codeChange.setOldCode(oldCode);
                        codeChange.setOldDescription(cells.get(0).getText());
                    } else if (codeChange.getNewCode() == null) {
                        String newCode = getOldCode(cells.get(0).findElement(By.tagName("a")).getAttribute("href"));
                        codeChange.setNewCode(newCode);
                        codeChange.setNewDescription(cells.get(0).getText());
                        codeChange.setChangeDate(DateUtils.parse(cells.get(1).getText(), DateUtils.AUSSIE_DATE_FORMAT));
                        SecurityCodeChange existingCodeChange = securityCodeChangeRepository.findByExchangeAndNewCodeAndChangeDate(exchange, newCode, codeChange.getChangeDate());
                        if (existingCodeChange != null) {
                            logger.info("Found code change: " + codeChange.getChangeDate());
                            if (existingCodeChange.getSecurity() == null) {
                                logger.info("Setting security for code change");
                                existingCodeChange.setSecurity(security);
                                securityCodeChangeRepository.saveAndFlush(existingCodeChange);
                            }
                        } else {
                            logger.info("Saving code change: " + codeChange.getChangeDate());
                            securityCodeChangeRepository.saveAndFlush(codeChange);
                        }
                        codeChange = new SecurityCodeChange();
                        codeChange.setSecurity(security);
                        codeChange.setExchange(exchange);
                        codeChange.setOldCode(newCode);
                        codeChange.setOldDescription(cells.get(0).getText());
                    }
                }
            }
        }

        driver.close();
        driver.quit();
    }

    private Security findOrCreateSecurity(Exchange exchange, WebDriver driver) {
        List<WebElement> titles = driver.findElements(By.cssSelector("div.titleOfComp h1"));
        String nameAndCode = titles.get(0).getText();
        String name = nameAndCode.substring(0, nameAndCode.lastIndexOf('(')).trim();
        String code = nameAndCode.substring(nameAndCode.lastIndexOf('(') + 1, nameAndCode.lastIndexOf(')')).trim();
        List<WebElement> leftBoxes = driver.findElements(By.cssSelector("div.firstcolumn div.fluidRptr"));
        String companyDetailsText = leftBoxes.get(2).getText();
        int indexOfAddress = companyDetailsText.indexOf("Address:");
        int indexOfTel = companyDetailsText.indexOf("Tel:");
        int indexOfFax = companyDetailsText.indexOf("Fax:");
        int indexOfDateFirstListed = companyDetailsText.indexOf("Date first listed:");
        int indexOfDateDelisted = companyDetailsText.indexOf("Date delisted:");
        int indexOfCompanySecretary = companyDetailsText.indexOf("Company Secretary:");
        int indexOfSector = companyDetailsText.indexOf("Sector:");
        int indexOfIndustryGroup = companyDetailsText.indexOf("Industry Group:");
        int indexOfActivities = companyDetailsText.indexOf("Activities:");
        String companyAddress = companyDetailsText.substring(indexOfAddress + "Address:".length(), indexOfTel).trim();
        String companyTel = companyDetailsText.substring(indexOfTel + "Tel:".length(), indexOfFax).trim();
        String companyFax = companyDetailsText.substring(indexOfFax + "Fax:".length(),
                indexOfDateFirstListed != -1 ? indexOfDateFirstListed : (indexOfDateDelisted != -1 ? indexOfDateDelisted : indexOfCompanySecretary)).trim();
        Date listingDate = null;
        if (indexOfDateFirstListed != -1) {
            listingDate = DateUtils.parse(companyDetailsText.substring(indexOfDateFirstListed + "Date first listed:".length(),
                    indexOfDateDelisted != -1 ? indexOfDateDelisted : (indexOfCompanySecretary != -1 ? indexOfCompanySecretary : indexOfSector)).trim(), DateUtils.AUSSIE_DATE_FORMAT);
        } else {
            List<String> formerCompanyInfoLinks = new ArrayList<>();
            List<WebElement> formerNamesTables = driver.findElements(By.cssSelector("div#formerNames table"));
            if (!formerNamesTables.isEmpty()) {
                WebElement formerNamesTable = formerNamesTables.get(0);
                List<WebElement> rows = formerNamesTable.findElements(By.tagName("tr"));
                for (int i = rows.size() - 1; i > 1; i--) {
                    List<WebElement> links = rows.get(i).findElements(By.tagName("td")).get(0).findElements(By.tagName("a"));
                    if (!links.isEmpty()) {
                        formerCompanyInfoLinks.add(links.get(0).getAttribute("href"));
                    }
                }
            }
            listingDate = findListingDate(formerCompanyInfoLinks);
        }
        Date delistedDate = null;
        if (indexOfDateDelisted != -1) {
            delistedDate = DateUtils.parse(companyDetailsText.substring(indexOfDateDelisted + "Date delisted:".length(),
                    indexOfCompanySecretary != -1 ? indexOfCompanySecretary : indexOfSector).trim(), DateUtils.AUSSIE_DATE_FORMAT);
        } else {
            delistedDate = findDelistedDate(driver);
        }

        String secretary = companyDetailsText.substring(indexOfCompanySecretary + "Company Secretary:".length(), indexOfSector).trim();
        GicsIndustryGroup gicsIndustryGroup = gicsIndustryGroupRepository.findByName(companyDetailsText.substring(indexOfSector + "Sector:".length(), indexOfIndustryGroup).trim());
        GicsIndustry gicsIndustry = gicsIndustryRepository.findByName(companyDetailsText.substring(indexOfIndustryGroup + "Industry Group:".length(), indexOfActivities).trim());
        String activities = companyDetailsText.substring(indexOfActivities + "Activities:".length()).trim();

        String companyWebsite = findCompanyWebsite(leftBoxes);

        ShareRegistry shareRegistry = findShareRegistry(leftBoxes);

        StringBuilder delistedReason = findDelistedReason(driver, delistedDate);

        Security security = null;
        if (listingDate != null) {
            security = securityRepository.findByCodeAndExchange(code, exchange, listingDate);
            if (security == null) {
                logger.info("Creating security: " + code);
                Company company = new Company();
                company.setName(name);
                company.setGicsIndustryGroup(gicsIndustryGroup);
                company.setHeadOfficeTelephone(companyTel);
                company.setHeadOfficeFax(companyFax);
                company.setRegisteredOfficeAddress(companyAddress);
                company.setPrincipalActivities(activities);
                company.setWebsite(companyWebsite);
                company = companyRepository.saveAndFlush(company);
                if (gicsIndustry != null) {
                    CompanyIndustry companyIndustry = new CompanyIndustry();
                    companyIndustry.setCompany(company);
                    companyIndustry.setGicsIndustry(gicsIndustry);
                    companyIndustryRepository.saveAndFlush(companyIndustry);
                }
                if (StringUtils.hasText(secretary)) {
                    Person person = new Person(secretary);
                    Person existingPerson = personRepository.findByGivenNameAndSurname(person.getGivenName(), person.getSurname());
                    if (existingPerson == null) {
                        person = personRepository.saveAndFlush(person);
                    } else {
                        person = existingPerson;
                    }
                    ManagementRole managementRole = managementRoleRepository.findByName("Secretary");
                    ManagementPosition managementPosition = managementPositionRepository.findByPersonAndCompanyAndManagementRole(person, company, managementRole);
                    if (managementPosition == null) {
                        managementPosition = new ManagementPosition(person, company, managementRole);
                        managementPositionRepository.saveAndFlush(managementPosition);
                    }
                }
                SecurityType securityType = securityTypeRepository.findByName("Ordinary Share");
                security = new Security(code, company, exchange);
                security.setSecurityType(securityType);
                security.setListingDate(listingDate);
                security.setDelistedDate(delistedDate);
                if (delistedDate != null) {
                    security.setDelistedReason(delistedReason.toString());
                }
                security.setDescription(name);
                security.setShareRegistry(shareRegistry);
                security = securityRepository.saveAndFlush(security);
            } else {
                logger.info("Found security: " + code);
            }
        }
        return security;
    }

    private ShareRegistry findShareRegistry(List<WebElement> leftBoxes) {
        ShareRegistry shareRegistry = null;
        List<WebElement> shareRegistryParagraphs = leftBoxes.get(1).findElements(By.tagName("p"));
        if (!shareRegistryParagraphs.isEmpty()) {
            String shareRegistryText = shareRegistryParagraphs.get(0).getText().replace("REGISTRY:", "").trim();
            int firstCommaIndex = shareRegistryText.indexOf(",");
            if (firstCommaIndex != -1) {
                String shareRegistryName = shareRegistryText.substring(0, firstCommaIndex).trim().toUpperCase();
                shareRegistry = shareRegistryRepository.findByName(shareRegistryName);
                if (shareRegistry == null) {
                    logger.info("Creating share registry: " + shareRegistryName);
                    int telIndex = shareRegistryText.indexOf("Tel :");
                    String address = shareRegistryText.substring(firstCommaIndex + 1, telIndex).trim().toUpperCase();
                    int faxIndex = shareRegistryText.indexOf("Fax :");
                    String tel = shareRegistryText.substring(telIndex + "Tel :".length(), faxIndex).trim();
                    shareRegistry = new ShareRegistry(shareRegistryName, address, tel);
                    shareRegistry = shareRegistryRepository.saveAndFlush(shareRegistry);
                } else {
                    logger.info("Found share registry: " + shareRegistryName);
                }
            }
        }
        return shareRegistry;
    }

    private String findCompanyWebsite(List<WebElement> leftBoxes) {
        List<WebElement> shareHolderLinks = leftBoxes.get(1).findElements(By.tagName("a"));
        String companyWebsite = null;
        for (WebElement link : shareHolderLinks) {
            if (link.getText().endsWith(" website")) {
                companyWebsite = link.getAttribute("href");
                logger.info("Found company website: " + companyWebsite);
                break;
            }
        }
        if (companyWebsite == null) {
            logger.info("Company website not found");
        }
        return companyWebsite;
    }

    private Date findDelistedDate(WebDriver driver) {
        List<WebElement> newsEventsTables = driver.findElements(By.cssSelector("div#newsEvents table"));
        if (!newsEventsTables.isEmpty()) {
            WebElement newsEventsTable = newsEventsTables.get(0);
            List<WebElement> newsEvents = newsEventsTable.findElements(By.tagName("tr"));
            for (WebElement newsEvent : newsEvents) {
                List<WebElement> cells = newsEvent.findElements(By.tagName("td"));
                if (cells.get(0).getText().toLowerCase().contains("delisted")) {
                    return DateUtils.parse(cells.get(1).getText(), DateUtils.AUSSIE_DATE_FORMAT);
                }
            }
        }
        return null;
    }

    private StringBuilder findDelistedReason(WebDriver driver, Date delistedDate) {
        StringBuilder delistedReason = new StringBuilder();
        List<WebElement> newsEventsTables = driver.findElements(By.cssSelector("div#newsEvents table"));
        if (!newsEventsTables.isEmpty()) {
            WebElement newsEventsTable = newsEventsTables.get(0);
            List<WebElement> newsEvents = newsEventsTable.findElements(By.tagName("tr"));
            for (WebElement newsEvent : newsEvents) {
                List<WebElement> cells = newsEvent.findElements(By.tagName("td"));
                if (DateUtils.parse(cells.get(1).getText(), DateUtils.AUSSIE_DATE_FORMAT).equals(delistedDate)) {
                    delistedReason.append(cells.get(0).getText()).append(" ");
                }
            }
        }
        logger.info("Delisted reason: " + delistedReason);
        return delistedReason;
    }

    private void findOrCreateCurrentManagementPositions(WebDriver driver, Company company) {
        List<WebElement> directorsTables = driver.findElements(By.cssSelector("div#directorExecutive table"));
        if (!directorsTables.isEmpty()) {
            WebElement directorsTable = directorsTables.get(0);
            List<WebElement> rows = directorsTable.findElements(By.tagName("tr"));
            for (int i = 1; i < rows.size(); i++) {
                List<WebElement> cells = rows.get(i).findElements(By.tagName("td"));
                String directorName = cells.get(0).getText();
                logger.info(directorName);
                String rolesTxt = cells.get(1).getText();
                Date dateAppointed = DateUtils.parse(cells.get(2).getText(), DateUtils.AUSSIE_DATE_FORMAT);
                Person person = new Person(directorName);
                Person existingPerson = personRepository.findByGivenNameAndSurname(person.getGivenName(), person.getSurname());
                if (existingPerson == null) {
                    person = personRepository.saveAndFlush(person);
                } else {
                    person = existingPerson;
                }
                String[] roles = rolesTxt.split(",");
                for (String role : roles) {
                    ManagementRole managementRole = managementRoleRepository.findByName(role);
                    if (managementRole == null) {
                        logger.info("Creating management role: " + role);
                        managementRole = new ManagementRole(role);
                        managementRole = managementRoleRepository.saveAndFlush(managementRole);
                    } else {
                        logger.info("Found management role: " + role);
                    }
                    ManagementPosition managementPosition = managementPositionRepository.findByPersonAndCompanyAndManagementRole(person, company, managementRole);
                    if (managementPosition == null) {
                        managementPosition = new ManagementPosition(person, company, managementRole);
                    }
                    managementPosition.setBirthDate(dateAppointed);
                    managementPositionRepository.saveAndFlush(managementPosition);
                }
            }
        }
    }

    private void findOrCreateFormerManagementPositions(WebDriver driver, Company company) {
        List<WebElement> formerDirectorsTables = driver.findElements(By.cssSelector("div#directorExecutiveFormer table"));
        if (!formerDirectorsTables.isEmpty()) {
            WebElement formerDirectorsTable = formerDirectorsTables.get(0);
            List<WebElement> rows = formerDirectorsTable.findElements(By.tagName("tr"));
            for (int i = 1; i < rows.size(); i++) {
                List<WebElement> cells = rows.get(i).findElements(By.tagName("td"));
                String directorName = cells.get(0).getText();
                logger.info(directorName);
                String rolesTxt = cells.get(1).getText();
                Date dateAppointed = DateUtils.parse(cells.get(2).getText(), DateUtils.AUSSIE_DATE_FORMAT);
                Date dateResigned = DateUtils.parse(cells.get(3).getText(), DateUtils.AUSSIE_DATE_FORMAT);
                Person person = new Person(directorName);
                Person existingPerson = personRepository.findByGivenNameAndSurname(person.getGivenName(), person.getSurname());
                if (existingPerson == null) {
                    person = personRepository.saveAndFlush(person);
                } else {
                    person = existingPerson;
                }
                String[] roles = rolesTxt.split(",");
                for (String role : roles) {
                    ManagementRole managementRole = managementRoleRepository.findByName(role);
                    if (managementRole == null) {
                        logger.info("Creating management role: " + role);
                        managementRole = new ManagementRole(role);
                        managementRole = managementRoleRepository.saveAndFlush(managementRole);
                    } else {
                        logger.info("Found management role: " + role);
                    }
                    ManagementPosition managementPosition = managementPositionRepository.findByPersonAndCompanyAndManagementRole(person, company, managementRole);
                    if (managementPosition == null) {
                        managementPosition = new ManagementPosition(person, company, managementRole);
                    }
                    managementPosition.setBirthDate(dateAppointed);
                    managementPosition.setDeathDate(dateResigned);
                    managementPositionRepository.saveAndFlush(managementPosition);
                }
            }
        }
    }

    private Date findListingDate(List<String> formerCompanyInfoLinks) {
        Date listingDate = null;
        for (String link : formerCompanyInfoLinks) {
            WebDriver driver = new HtmlUnitDriver(BrowserVersion.CHROME);
            driver.get(link);
            List<WebElement> leftBoxes = driver.findElements(By.cssSelector("div.firstcolumn div.fluidRptr"));
            String companyDetailsText = leftBoxes.get(2).getText();
            int indexOfDateFirstListed = companyDetailsText.indexOf("Date first listed:");
            int indexOfDateDelisted = companyDetailsText.indexOf("Date delisted:");
            int indexOfCompanySecretary = companyDetailsText.indexOf("Company Secretary:");
            int indexOfSector = companyDetailsText.indexOf("Sector:");
            if (indexOfDateFirstListed != -1) {
                listingDate = DateUtils.parse(companyDetailsText.substring(indexOfDateFirstListed + "Date first listed:".length(),
                                indexOfDateDelisted != -1 ? indexOfDateDelisted : (indexOfCompanySecretary != -1 ? indexOfCompanySecretary : indexOfSector)).trim(),
                        DateUtils.AUSSIE_DATE_FORMAT);
                break;
            }
            driver.close();
            driver.quit();
        }
        return listingDate;
    }

    private String getOldCode(String link) {
        WebDriver driver = new HtmlUnitDriver(BrowserVersion.CHROME);
        driver.get(link);
        List<WebElement> titles = driver.findElements(By.cssSelector("div.titleOfComp h1"));
        if (!titles.isEmpty()) {
            String nameAndCode = titles.get(0).getText();
            String oldCode = nameAndCode.substring(nameAndCode.lastIndexOf('(') + 1, nameAndCode.lastIndexOf(')')).trim();
            logger.info("Found old code: " + oldCode);
            return oldCode;
        }
        driver.close();
        driver.quit();
        logger.info("Couldn't find old code");
        return null;
    }
}
