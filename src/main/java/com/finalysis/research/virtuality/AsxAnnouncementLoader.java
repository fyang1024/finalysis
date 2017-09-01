package com.finalysis.research.virtuality;

import com.finalysis.research.DateUtils;
import com.finalysis.research.reality.Exchange;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
class AsxAnnouncementLoader implements AnnouncementLoader {

    private static final Logger logger = LoggerFactory.getLogger(AsxAnnouncementLoader.class);
    private static final int BUFFER_SIZE = 1024 * 64;

    @Autowired
    private SecurityRepository securityRepository;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private SecurityTypeRepository securityTypeRepository;

    @Override
    public void loadTodayAnnouncements(Exchange exchange) {
        logger.info("Loading today's announcements");
        SecurityType ordinaryShare = securityTypeRepository.findByName("Ordinary Share");
        String url = exchange.getTodayAnnouncementUrl();
        loadAnnouncements(exchange, ordinaryShare, url);
        logger.info("--Done--");
    }

    @Override
    public void loadYesterdayAnnouncements(Exchange exchange) {
        logger.info("Loading yesterday's announcements");
        SecurityType ordinaryShare = securityTypeRepository.findByName("Ordinary Share");
        String url = exchange.getYesterdayAnnouncementUrl();
        loadAnnouncements(exchange, ordinaryShare, url);
        logger.info("--Done--");
    }


    private void loadAnnouncements(Exchange exchange, SecurityType securityType, String url) {
        ChromeDriver driver = new ChromeDriver();
        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, Integer.MAX_VALUE);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table.announcements")));
        List<WebElement> tables = driver.findElements(By.cssSelector("table.announcements"));
        logger.info(tables.size() + " announcement table found");
        if (!tables.isEmpty()) {
            Date announcementDate = Calendar.getInstance().getTime();//TODO should read from the header
            WebElement table = tables.get(0);
            List<WebElement> rows = table.findElements(By.tagName("tr"));
            Map<String, Integer> counter = new HashMap<>();
            for (int j = rows.size() - 1; j > 0; j--) {
                WebElement row = rows.get(j);
                List<WebElement> codeCells = row.findElements(By.tagName("th"));
                String code;
                if (!codeCells.isEmpty()) {
                    code = codeCells.get(0).getText();
                } else {
                    code = row.findElements(By.tagName("td")).get(0).getText();
                }
                Security security = securityRepository.findByCodeAndExchange(code, exchange, announcementDate);
                if (security != null) {
                    if (security.getSecurityType().getId().equals(securityType.getId())) {
                        List<WebElement> cells = row.findElements(By.tagName("td"));
                        String headline = cells.get(codeCells.isEmpty() ? 3 : 2).getText();
                        String key = code + " - " + headline;
                        logger.info(key);
                        if (counter.get(key) == null) {
                            counter.put(key, 1);
                        } else {
                            counter.put(key, counter.get(key) + 1);
                        }
                        List<Announcement> announcements = announcementRepository.findByExchangeAndSecurityAndAnnouncementDateAndHeadline(exchange, security, announcementDate, headline);
                        if (counter.get(key) > announcements.size()) {
                            boolean priceSensitive = !cells.get(codeCells.isEmpty() ? 2 : 1).findElements(By.tagName("img")).isEmpty();
                            Announcement announcement = new Announcement(exchange, security, code, announcementDate, priceSensitive, headline);
                            if (cells.get(codeCells.isEmpty() ? 4 : 3).getText().matches("\\d+")) {
                                announcement.setPages(Integer.parseInt(cells.get(codeCells.isEmpty() ? 4 : 3).getText()));
                            }
                            String filePath = generateFileName(headline);
//                            List<WebElement> pdfLinks = cells.get(4).findElements(By.tagName("a"));
//                            if (!pdfLinks.isEmpty()) {
//                                String pdfUrl = pdfLinks.get(0).getAttribute("href");
//                                String pdfPath = downloadPdf(exchange, security, code, announcementDate, headline, pdfUrl);
//                                if (pdfPath != null) {
//                                    filePath = pdfPath;
//                                }
//                            }
                            if (StringUtils.hasText(filePath)) {
                                if (filePath.length() > 4000) {
                                    filePath = filePath.substring(0, 4000);
                                }
                                announcement.setFileNames(filePath);
                            } else {
                                announcement.setFileNames(null);
                            }
                            announcementRepository.saveAndFlush(announcement);
                        }
                    } else {
                        logger.info(code + " is " + security.getSecurityType().getName() + ", skipped");
                    }
                } else {
                    logger.info("Couldn't find security for " + code);
                }
            }
            driver.close();
        }
    }

    @Override
    public void loadAnnouncements(Exchange exchange) {
        Integer endYear = getCurrentYear();
        String url = exchange.getAnnouncementUrl();
        SecurityType ordinaryShare = securityTypeRepository.findByName("Ordinary Share");
        List<Security> securities = securityRepository.findByExchangeAndSecurityType(exchange, ordinaryShare);
        for (Security security : securities) {
            if (security.getCode().length() <= 4 && (security.getDelistedDate() == null || security.getDelistedYear().equals(getCurrentYear()))) {
                Integer startYear = 1998;
                if (security.getListingDate() != null) {
                    Calendar listingDateCal = Calendar.getInstance();
                    listingDateCal.setTime(security.getListingDate());
                    startYear = Math.max(startYear, listingDateCal.get(Calendar.YEAR));
                }
                Date latestAnnouncementDate = announcementRepository.findLatestAnnouncementDate(exchange, security);
                if (latestAnnouncementDate != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(latestAnnouncementDate);
                    startYear = calendar.get(Calendar.YEAR);
                }
                if (security.getDelistedDate() != null) {
                    Calendar delistedDateCal = Calendar.getInstance();
                    delistedDateCal.setTime(security.getDelistedDate());
                    endYear = delistedDateCal.get(Calendar.YEAR);
                }
                for (int year = startYear; year <= endYear; year++) {
                    String actualUrl = url.replace("${year}", "" + year).replace("${code}", security.getCode().substring(0, 3));
                    logger.info("Loading announcements for " + security.getCode() + " - " + year + " " + actualUrl);
                    WebDriver driver = new ChromeDriver();
                    driver.get(actualUrl);
                    try {
                        WebDriverWait wait = new WebDriverWait(driver, 30);
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table.contenttable")));
                        List<WebElement> tables = driver.findElements(By.cssSelector("table.contenttable"));
                        logger.info(tables.size() + " announcement table found");
                        List<String> codes = findCodes(driver);
                        for (int i = tables.size() - 1; i >= 0; i--) {
                            WebElement table = tables.get(i);
                            String code = security.getCode().length() == 4 ? security.getCode() : codes.get(i);
                            List<WebElement> rows = table.findElements(By.tagName("tr"));
                            Map<String, Integer> counter = new HashMap<>();
                            for (int j = rows.size() - 1; j > 0; j--) {
                                WebElement row = rows.get(j);
                                List<WebElement> cells = row.findElements(By.tagName("td"));
                                String dateStr = cells.get(0).getText();
                                Date announcementDate = DateUtils.parse(dateStr, DateUtils.AUSSIE_DATE_FORMAT);
                                String headline = cells.get(2).getText();
                                String key = dateStr + "-" + headline;
                                counter.merge(key, 1, (a, b) -> a + b);
                                List<Announcement> announcements = announcementRepository.findByExchangeAndSecurityAndAnnouncementDateAndHeadline(exchange, security, announcementDate, headline);
                                if (counter.get(key) > announcements.size()) {
                                    boolean priceSensitive = !cells.get(1).findElements(By.tagName("img")).isEmpty();
                                    Announcement announcement = new Announcement(exchange, security, code, announcementDate, priceSensitive, headline);
                                    if (cells.get(3).getText().matches("\\d+")) {
                                        announcement.setPages(Integer.parseInt(cells.get(3).getText()));
                                    }
                                    String filePath = generateFileName(headline);
//                                List<WebElement> pdfLinks = cells.get(4).findElements(By.tagName("a"));
//                                if (!pdfLinks.isEmpty()) {
//                                    String pdfUrl = pdfLinks.get(0).getAttribute("href");
//                                    String pdfPath = downloadPdf(exchange, security, code, announcementDate, headline, pdfUrl);
//                                    if (pdfPath != null) {
//                                        filePath = pdfPath;
//                                    }
//                                }
//                                List<WebElement> txtLinks = cells.get(5).findElements(By.tagName("a"));
//                                if (!txtLinks.isEmpty()) {
//                                    String txtLink = txtLinks.get(0).getAttribute("href");
//                                    String txtPath = downloadTxt(exchange, security, code, announcementDate, headline, txtLink);
//                                    if (txtPath != null) {
//                                        if (filePath.length() > 0) {
//                                            filePath = filePath + ", " + txtPath;
//                                        } else {
//                                            filePath = txtPath;
//                                        }
//                                    }
//                                }
                                    if (StringUtils.hasText(filePath)) {
                                        if (filePath.length() > 4000) {
                                            filePath = filePath.substring(0, 4000);
                                        }
                                        announcement.setFileNames(filePath);
                                    } else {
                                        announcement.setFileNames(null);
                                    }
                                    announcementRepository.saveAndFlush(announcement);
                                }
                            }

                        }
                    } catch (TimeoutException e) {
                        logger.info("Time out...Probably no announcement table found");
                    }
                    driver.close();
                }
            }
        }
        logger.info("--Done--");
    }

    private String downloadTxt(Exchange exchange, Security security, String code, Date announcementDate, String headline, String txtLink) {
        StringBuilder pathBuilder = new StringBuilder();
        WebDriver txtDriver = new HtmlUnitDriver(BrowserVersion.CHROME);
        txtDriver.get(txtLink);
        List<WebElement> partsLinks = txtDriver.findElements(By.cssSelector("div#content ul li a"));
        for (WebElement partsLink : partsLinks) {
            final String partName = partsLink.getText().replaceAll("\\s+", " ").trim();
            WebDriver partsDriver = new HtmlUnitDriver(BrowserVersion.CHROME);
            partsDriver.get(partsLink.getAttribute("href"));
            WebElement content = partsDriver.findElement(By.cssSelector("div#content"));
            String text = content.getText().replace("Sponsored links", "");
            try {
                File dateDir = getOrCreateDateDir(exchange, security, code, announcementDate);
                final String fileName = generateFileName(headline);
                File[] files = dateDir.listFiles((dir, name) -> name.startsWith(fileName) && name.contains(partName) && name.endsWith(".txt"));
                String newFileName = fileName;
                if (files.length > 0) {
                    newFileName = fileName + " (" + files.length + ")";
                }
                File file = new File(dateDir, newFileName + " " + partName + ".txt");
                logger.info(file.getAbsolutePath());
                if (pathBuilder.length() > 0) {
                    pathBuilder.append(", ");
                }
                pathBuilder.append(file.getName());
                FileUtils.write(file, text);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            partsDriver.close();
        }
        txtDriver.close();
        return pathBuilder.toString();
    }

    private String generateFileName(String headline) {
        return headline.replace('"', '\'').replace('/', '-').replace('\\', '-').replace('*', '-').replace(':', '-').replace('?', ' ').trim().replaceAll("\\s{2,}", " ");
    }

    private File getOrCreateDateDir(Exchange exchange, Security security, String code, Date announcementDate) throws Exception {
        File securityDir = new File(exchange.getAnnouncementArchive(), security.getId().toString());
        if (!securityDir.exists()) {
            if (!securityDir.mkdir()) {
                throw new Exception("Failed to make dir " + securityDir.getAbsolutePath());
            }
        }
        File codeDir = new File(securityDir, code);
        if (!codeDir.exists()) {
            if (!codeDir.mkdir()) {
                throw new Exception("Failed to make dir " + codeDir.getAbsolutePath());
            }
        }
        File dateDir = new File(codeDir, new SimpleDateFormat("yyyyMMdd").format(announcementDate));
        if (!dateDir.exists()) {
            if (!dateDir.mkdir()) {
                throw new Exception("Failed to make dir " + dateDir.getAbsolutePath());
            }
        }
        return dateDir;
    }

    private String downloadPdf(Exchange exchange, Security security, String code, Date announcementDate, String headline, String pdfUrl) {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(true);
        BufferedInputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            HtmlPage page = webClient.getPage(pdfUrl);
            String realPdfUrl = page.getFullyQualifiedUrl(page.getElementByName("pdfURL").getAttribute("value")).toString();
            inputStream = new BufferedInputStream(webClient.getPage(realPdfUrl).getWebResponse().getContentAsStream());
            File dateDir = getOrCreateDateDir(exchange, security, code, announcementDate);
            final String fileName = generateFileName(headline);
            File[] files = dateDir.listFiles((dir, name) -> name.startsWith(fileName) && name.endsWith(".pdf"));
            String newFileName = fileName;
            if (files.length > 0) {
                newFileName = fileName + " (" + files.length + ")";
            }
            File file = new File(dateDir, newFileName + ".pdf");
            logger.info(file.getAbsolutePath());
            fileOutputStream = new FileOutputStream(file);
            byte[] buffer = new byte[BUFFER_SIZE];
            IOUtils.copyLarge(inputStream, fileOutputStream, buffer);
            return file.getName();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        } finally {
            if (inputStream != null) {
                IOUtils.closeQuietly(inputStream);
            }
            if (fileOutputStream != null) {
                IOUtils.closeQuietly(fileOutputStream);
            }
            webClient.close();
        }
    }

    private List<String> findCodes(WebDriver driver) {
        List<String> codes = new ArrayList<>();
        List<WebElement> headers = driver.findElements(By.tagName("h2"));
        for (WebElement header : headers) {
            if (header.getText().startsWith("Announcements released as ")) {
                codes.add(header.getText().replace("Announcements released as ", ""));
            }
        }
        return codes;
    }

    private Integer getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }
}
