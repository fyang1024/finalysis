package com.finalysis.research.reality;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Integer> {

    Exchange findByName(String name);

    List<Exchange> findByWebsiteIsNull();

    List<Exchange> findBySecurityCodeChangeUrlIsNotNull();

    List<Exchange> findByListedOrdinaryShareUrlIsNotNull();

    List<Exchange> findBySecurityInfoUrlIsNotNull();

    List<Exchange> findByDelistedSecurityUrlIsNotNull();

    List<Exchange> findByListedEtpUrlIsNotNull();

    List<Exchange> findBySecurityPriceUrlIsNotNull();

    List<Exchange> findByShortSellUrlIsNotNull();

    List<Exchange> findByTradingCalendarUrlIsNotNull();

    List<Exchange> findByAnnouncementUrlIsNotNull();
}
