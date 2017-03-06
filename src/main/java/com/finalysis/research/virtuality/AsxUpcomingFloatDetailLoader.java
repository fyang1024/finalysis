package com.finalysis.research.virtuality;

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

    public void loadUpcomingFloatsDetails(Exchange exchange) {
        String url = exchange.getUpcomingFloatDetailUrl();
        WebDriver driver = new HtmlUnitDriver(BrowserVersion.CHROME);
        List<Security> upcomingFloats = securityRepository.findByExchangeAndListingDateIsNull(exchange);
        for (Security upcomingFloat : upcomingFloats) {
            String replacedUrl = url.replace("${code}", upcomingFloat.getCode());
            logger.info(replacedUrl);
            driver.get(replacedUrl);
            List<WebElement> detailsDivs = driver.findElements(By.cssSelector("div.upcoming-floats"));
            if (detailsDivs.isEmpty()) {
                logger.info("Could not find details");
            } else {
                if (detailsDivs.get(0).getText().toUpperCase().contains("APPLICATION WITHDRAWN")) {
                    logger.info(upcomingFloat.getCode() + " IPO application is withdrawn");
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
        driver.close();
        logger.info("--Done--");
    }
}
