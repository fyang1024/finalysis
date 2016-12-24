package com.finalysis.research.virtuality;

import com.finalysis.research.DateUtils;
import com.finalysis.research.reality.Exchange;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class AsxTradingCalendarLoader implements TradingCalendarLoader {
    private static final Logger logger = LoggerFactory.getLogger(AsxTradingCalendarLoader.class);

    @Autowired
    private TradingCalendarRepository tradingCalendarRepository;

    @Override
    public void loadTradingCalendar(Exchange exchange) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date date = tradingCalendarRepository.findLatestDate(exchange);
        if(date == null || date.before(calendar.getTime())) {
            Integer year = calendar.get(Calendar.YEAR);
            WebDriver driver = new HtmlUnitDriver(BrowserVersion.CHROME);
            driver.get(exchange.getTradingCalendarUrl().replace("${year}", year.toString()));
            List<WebElement> calendarTables = driver.findElements(By.cssSelector("table.contenttable"));
            if(!calendarTables.isEmpty()) {
                List<WebElement> rows = calendarTables.get(0).findElements(By.tagName("tr"));
                for(int i = 1; i < rows.size(); i++) {
                    List<WebElement> cells = rows.get(i).findElements(By.tagName("td"));
                    if("CLOSED".equalsIgnoreCase(cells.get(3).getText().trim())) {
                        TradingCalendar tradingCalendar = new TradingCalendar();
                        tradingCalendar.setPublicHoliday(cells.get(0).getText());
                        tradingCalendar.setExchange(exchange);
                        String dateStr = cells.get(1).getText();
                        dateStr = dateStr.substring(dateStr.indexOf(' ') + 1).trim().replaceAll("\\s+", " ");
                        Date d = DateUtils.parse(dateStr, "d MMMM");
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(d);
                        cal.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                        tradingCalendar.setDate(cal.getTime());
                        tradingCalendar.setTradingDay(false);
                        tradingCalendarRepository.saveAndFlush(tradingCalendar);
                    }
                }
            }
            driver.close();
            driver.quit();
            logger.info("--Done--");
        }
    }
}
