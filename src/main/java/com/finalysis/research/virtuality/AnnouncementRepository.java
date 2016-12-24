package com.finalysis.research.virtuality;

import com.finalysis.research.reality.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {

    @Query("select max(announcementDate) from Announcement where exchange = ?1 and security = ?2" )
    Date findLatestAnnouncementDate(Exchange exchange, Security security);

    List<Announcement> findByExchangeAndSecurityAndAnnouncementDateAndHeadline(Exchange exchange, Security security, Date announcementDate, String headline);

    List<Announcement> findByExchangeAndSecurityAndAnnouncementDate(Exchange exchange, Security security, Date announcementDate);

    List<Announcement> findBySecurity(Security security);
}
