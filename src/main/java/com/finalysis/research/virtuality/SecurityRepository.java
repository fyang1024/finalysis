package com.finalysis.research.virtuality;

import com.finalysis.research.reality.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SecurityRepository extends JpaRepository<Security, Integer> {

    List<Security> findByExchange(Exchange exchange);

    List<Security> findByExchangeAndSecurityType(Exchange exchange, SecurityType securityType);

    List<Security> findByExchangeAndListingDateIsNull(Exchange exchange);

    @Query("select s from Security s where s.exchange = ?1 and s.securityType.name='Ordinary Share' and " +
            "(s.listingDate is NULL or exists(select a from Announcement a where a.security = s and a.announcementDate = ?2))")
    List<Security> findSecuritiesToUpdateInfo(Exchange exchange, Date announcementDate);

    @Query("select s from Security s where s.code = ?1 and s.exchange = ?2 and (s.listingDate <= ?3 or s.listingDate is NULL) and (s.delistedDate IS NULL or s.delistedDate > ?3)")
    Security findByCodeAndExchange(String code, Exchange exchange, Date tradingDate);

    @Query("select s from Security s where upper(s.description) = ?1 and s.exchange = ?2 and (s.listingDate <= ?3 or s.listingDate is NULL) and (s.delistedDate IS NULL or s.delistedDate > ?3)")
    Security findByDescriptionAndExchange(String description, Exchange exchange, Date tradingDate);

    Security findByCodeAndExchangeAndListingDate(String code, Exchange exchange, Date tradingDate);

    @Query("select s from Security s where s.code = ?1 and s.exchange = ?2 and s.listingDate < ?3 and s.delistedDate IS NULL")
    Security findListingSecurity(String code, Exchange exchange, Date currentDate);

    @Query("select s from Security s where s.exchange = ?1 and s.listingDate <= ?2 and s.delistedDate IS NULL")
    List<Security> findActiveByExchange(Exchange exchange, Date date);

    @Query("select s from Security s where s.exchange = ?1 and s.listingDate <= ?2 and s.securityType.name = ?3 and s.delistedDate IS NULL")
    List<Security> findActiveByExchange(Exchange exchange, Date date, String securityType);

    Security findByCodeAndExchangeAndDelistedDate(String code, Exchange exchange, Date delistedDate);
}
