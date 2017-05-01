package com.finalysis.research.virtuality;

import com.finalysis.research.reality.Company;
import com.finalysis.research.reality.Exchange;
import com.finalysis.research.reality.ManagementPosition;
import com.finalysis.research.reality.ManagementPositionRepository;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Fei on 28/12/2015.
 */

@Component
public class AsxUpcomingFloatDetailLoader implements UpcomingFloatDetailLoader {

    private static final Logger logger = LoggerFactory.getLogger(AsxUpcomingFloatDetailLoader.class);

    @Autowired
    SecurityRepository securityRepository;

    @Autowired
    AnnouncementRepository announcementRepository;

    @Autowired
    ManagementPositionRepository managementPositionRepository;

    public void deleteWithdrawnFloats(Exchange exchange) {
        String url = exchange.getUpcomingFloatDetailUrl();
        WebDriver driver = new ChromeDriver();
        logger.info(url);
        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, Integer.MAX_VALUE);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table.contenttable")));
        List<WebElement> contentTables = driver.findElements(By.cssSelector("table.contenttable"));
        if (contentTables.isEmpty()) {
            logger.info("Could not find content table");
        } else {
            List<WebElement> rows = contentTables.get(0).findElements(By.tagName("tr"));
            for (WebElement row : rows) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if (!cells.isEmpty()) {
                    if (cells.get(2).getText().toUpperCase().contains("APPLICATION WITHDRAWN")) {
                        logger.info(cells.get(1).getText() + " IPO application is withdrawn");
                        Security upcomingFloat = securityRepository.findByCodeAndExchangeAndListingDate(cells.get(1).getText().toUpperCase().trim(), exchange, null);
                        if (upcomingFloat == null) {
                            logger.info("Could not find " + cells.get(1).getText() + ", should've been deleted");
                        } else {
                            Company company = upcomingFloat.getCompany();
                            if (company != null) {
                                List<ManagementPosition> positions = managementPositionRepository.findByCompany(company);
                                if (!positions.isEmpty()) {
                                    managementPositionRepository.deleteInBatch(positions);
                                }
                            }
                            List<Announcement> announcements = announcementRepository.findBySecurity(upcomingFloat);
                            if (!announcements.isEmpty()) {
                                announcementRepository.deleteInBatch(announcements);
                            }
                            securityRepository.delete(upcomingFloat);
                            logger.info("Deleted");
                        }
                    }
                }
            }
        }
        driver.close();
        logger.info("--Done--");
    }
}
