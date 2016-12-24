package com.finalysis.research.virtuality;

import com.finalysis.research.reality.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ReportedShortSellRepository extends JpaRepository<ReportedShortSell, Integer> {

    @Query("select max(tradingDate) from ReportedShortSell where exchange = ?1")
    Date findMaxTradingDate(Exchange exchange);

    ReportedShortSell findBySecurityCodeAndExchangeAndTradingDate(String securityCode, Exchange exchange, Date tradingDate);
}
