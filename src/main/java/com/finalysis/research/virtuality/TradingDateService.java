package com.finalysis.research.virtuality;

import com.finalysis.research.reality.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Fei on 6/08/2014.
 */

@Service
public class TradingDateService {

    @Autowired
    private TradingCalendarRepository tradingCalendarRepository;

    public Date getLatestTradingDate(Exchange exchange) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Calendar time = Calendar.getInstance();
        time.setTime(calendar.getTime());
        time.set(Calendar.YEAR, 1970);
        time.set(Calendar.MONTH, Calendar.JANUARY);
        time.set(Calendar.DAY_OF_MONTH, 1);
        //TODO consider timezone and price delay
        if (new Time(time.getTimeInMillis()).before(exchange.getMarketStart())) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
        while (tradingCalendarRepository.findByDateAndTradingDay(calendar.getTime(), false) != null
                || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
        return calendar.getTime();
    }
    
    public boolean isTodayTradingDay(Exchange exchange) {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        return calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY 
                && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
                && tradingCalendarRepository.findByDateAndTradingDay(today, false) == null;
    }

    public Date getPreviousTradingDate(Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        while (tradingCalendarRepository.findByDateAndTradingDay(calendar.getTime(), false) != null
                || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
        return calendar.getTime();
    }

}
