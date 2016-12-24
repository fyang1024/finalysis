package com.finalysis.research.virtuality;

import com.finalysis.research.reality.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SecurityCodeChangeRepository extends JpaRepository<SecurityCodeChange, Integer> {

    SecurityCodeChange findByExchangeAndNewCodeAndChangeDate(Exchange exchange, String newCode, Date changeDate);

    @Query("select c from SecurityCodeChange c where c.exchange = ?1 and c.newCode = ?2 " +
            "and c.newCode <> c.oldCode order by c.changeDate desc")
    List<SecurityCodeChange> findCodeChangesByNewCode(Exchange exchange, String newCode);

    @Query("select c from SecurityCodeChange c where c.exchange = ?1 and c.security is null order by c.changeDate desc")
    List<SecurityCodeChange> findBySecurityIsNull(Exchange exchange);

    @Query("select max(changeDate) from SecurityCodeChange")
    Date findLatestChangeDate();

    @Query("select distinct c.security from SecurityCodeChange c where c.security IS NOT NULL and c.oldCode = ?1 and c.exchange = ?2 and c.changeDate > ?3")
    List<Security> findPossibleSecurities(String code, Exchange exchange, Date tradingDate);

    List<SecurityCodeChange> findBySecurityOrderByChangeDateAsc(Security security);
}
