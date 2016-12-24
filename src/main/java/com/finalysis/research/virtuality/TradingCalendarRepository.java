package com.finalysis.research.virtuality;

import com.finalysis.research.reality.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface TradingCalendarRepository extends JpaRepository<TradingCalendar, Integer> {

    @Query("select max(date) from TradingCalendar where exchange = ?1")
    Date findLatestDate(Exchange exchange);

    TradingCalendar findByDateAndTradingDay(Date date, Boolean tradingDay);
}
