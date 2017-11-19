package com.finalysis.research.virtuality;

import com.finalysis.research.reality.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface SecurityPriceRepository extends JpaRepository<SecurityPrice, Integer> {

    @Query("select max(p.closeDate) from SecurityPrice p where p.exchange = :exchange")
    Date findMaxCloseDate(@Param("exchange")Exchange exchange);

    SecurityPrice findByCodeAndExchangeAndOpenDate(String code, Exchange exchange, Date tradingDate);

    @Query("select min(p.openDate) from SecurityPrice p where p.security = ?1 and p.period = ?2 and p.swingPointHigh IS NULL and volume > 0")
    Date findEarliestDateToMarkSwingPointHigh(Security security, SecurityPricePeriod securityPricePeriod);

    @Query("select min(p.openDate) from SecurityPrice p where p.security = ?1 and p.period = ?2 and p.swingPointLow IS NULL and volume > 0")
    Date findEarliestDateToMarkSwingPointLow(Security security, SecurityPricePeriod securityPricePeriod);

    @Query("select min(p.openDate) from SecurityPrice p where p.security = ?1 and p.period = ?2")
    Date findEarliestOpenDate(Security security, SecurityPricePeriod securityPricePeriod);

    @Query("select p from SecurityPrice p where p.security = ?1 and p.period = ?2 and p.openDate >= ?3 and p.volume > 0 order by p.openDate")
    List<SecurityPrice> findSecurityPriceOnAfter(Security security, SecurityPricePeriod securityPricePeriod, Date openDate);

    @Query(value = "select * from Security_Price p where p.security = ?1 and p.period = ?2 and p.open_date > ?3  order by p.open_date desc limit ?4", nativeQuery = true)
    List<SecurityPrice> findNextSecurityPrices(Integer securityId, String securityPricePeriod, Date startDate, Integer number);

    @Query("select max(p.openDate) from SecurityPrice p where p.openDate < ?1 and p.security = ?2 and p.period = ?3 and p.volume > 0")
    Date findLatestOpenDateBefore(Date openDate, Security security, SecurityPricePeriod period);

    SecurityPrice findByOpenDateAndSecurityAndPeriod(Date date, Security security, SecurityPricePeriod period);

    @Query("select p from SecurityPrice p where p.openDate < ?1 and p.security = ?2 and p.period = ?3 and p.swingPointHigh = TRUE order by p.openDate desc")
    List<SecurityPrice> findSwingPointHighsBefore(Date openDate, Security security, SecurityPricePeriod period);

    @Query("select distinct p.openDate from SecurityPrice p where p.exchange = ?1 and p.period = ?2 and p.security IS NOT NULL order by p.openDate asc")
    List<Date> findOpenDates(Exchange exchange, SecurityPricePeriod period);

    @Query("select distinct p.security from SecurityPrice p where p.exchange = ?1 and p.period = ?2 and p.security IS NOT NULL and p.openDate = ?3")
    List<Security> findSecurities(Exchange exchange, SecurityPricePeriod period, Date openDate);

    @Query("select max(p.openDate) from SecurityPrice p where p.security = ?1 and p.period = ?2 and p.swingPointHigh = TRUE")
    Date findLatestSwingPointHighDate(Security security, SecurityPricePeriod period);

    @Query("select max(p.openDate) from SecurityPrice p where p.security = ?1 and p.period = ?2 and p.swingPointLow = TRUE")
    Date findLatestSwingPointLowDate(Security security, SecurityPricePeriod period);

    @Query(value = "select volume from Security_Price p where p.open_date < ?1 and p.security = ?2 and p.period = ?3 order by p.open_date desc limit ?4", nativeQuery = true)
    List<Integer> findVolumesLastPeriods(Date openDate, Integer securityId, String securityPeriodName, Integer number);

    @Query(value = "select count(*) from Security_Price p where p.open_date >= ?1 and p.open_date < ?2 and p.security = ?3 and p.volume >= ?4 and p.period = ?5", nativeQuery = true)
    Integer findVolumeLargerDays(Date startDate, Date endDate, Integer securityId, Integer volume, String securityPeriodName);

    @Query(value = "select count(*) from Security_Price p where p.open_date <= ?1 and adddate(p.open_date, -90) > ?1 and p.security = ?2 and p.close_price >= ?3 and p.period = ?4", nativeQuery = true)
    Integer findPriceRank(Date openDate, Integer securityId, BigDecimal price,  String securityPeriodName);

    @Query(value = "select close_price from Security_Price p where p.open_date < ?1 and p.security = ?2 and p.period = ?3 and p.close_price > 0 order by p.open_date desc limit 1", nativeQuery = true)
    BigDecimal findPreviousPrice(Date openDate, Integer securityId, String periodName);

    @Query("select distinct code from SecurityPrice p where p.exchange = ?1 and p.security is null")
    List<String> findSecurityPriceCodesWithoutSecurity(Exchange exchange);

    List<SecurityPrice> findByCodeAndExchangeAndSecurityIsNull(String code, Exchange exchange);
}
