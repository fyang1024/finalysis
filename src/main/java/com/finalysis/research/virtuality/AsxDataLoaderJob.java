package com.finalysis.research.virtuality;

import com.finalysis.research.DateUtils;
import com.finalysis.research.reality.Exchange;
import com.finalysis.research.reality.ExchangeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Fei on 28/08/2014.
 */

@Component
public class AsxDataLoaderJob {

    private static final Logger logger = LoggerFactory.getLogger(AsxDataLoaderJob.class);

    @Autowired
    ExchangeRepository exchangeRepository;

    @Autowired
    SecurityRepository securityRepository;

    @Autowired
    AsxSecurityCodeChangeLoader securityCodeChangeLoader;

    @Autowired
    AsxEtpLoader etpLoader;

    @Autowired
    AsxOrdinaryShareLoader ordinaryShareLoader;

    @Autowired
    AsxDelistedSecurityLoader delistedSecurityLoader;

    @Autowired
    AsxReportedShortSellLoader reportedShortSellLoader;

    @Autowired
    AsxAnnouncementLoader announcementLoader;

    @Autowired
    AsxSecurityInfoLoader securityInfoLoader;

    @Autowired
    AsxSecurityPriceLoader securityPriceLoader;

    @Autowired
    TradingDateService tradingDateService;

    @Autowired
    AsxTradingCalendarLoader tradingCalendarLoader;

    @Autowired
    SwingPointMarker swingPointMarker;

    @Autowired
    MomentumChaser momentumChaser;

    @Autowired
    VolumeExplosionDetector volumeExplosionDetector;

    @Autowired
    AsxUpcomingFloatDetailLoader asxUpcomingFloatDetailLoader;

    @Scheduled(cron = "0 50 20 * * MON-FRI")
    public void loadTodayAnnouncements() {
        Exchange exchange = exchangeRepository.findByName("Australian Securities Exchange");
        announcementLoader.loadTodayAnnouncements(exchange);
//        securityInfoLoader.loadInfo(exchange);
        if (tradingDateService.isTodayTradingDay(exchange)) {
            securityPriceLoader.loadSecurityPrice(exchange);
            volumeExplosionDetector.detectVolumeExplosion(exchange, SecurityPricePeriod.Day, tradingDateService.getLatestTradingDate(exchange));
        }
        asxUpcomingFloatDetailLoader.loadUpcomingFloatsDetails(exchange);
    }

    @Scheduled(cron = "0 35 18 * * MON-FRI")
    public void loadData() {
        Exchange exchange = exchangeRepository.findByName("Australian Securities Exchange");
        securityCodeChangeLoader.loadCodeChanges(exchange);
        try {
            etpLoader.loadExchangeTradedProducts(exchange);
        }catch(Throwable t) {
            logger.error(t.getMessage(), t);
        }
        ordinaryShareLoader.loadOrdinaryShares(exchange);
        delistedSecurityLoader.loadDelistedSecurity(exchange);
        reportedShortSellLoader.loadReportedShortSell(exchange);
        securityInfoLoader.loadListingDate(exchange);
//        securityInfoLoader.loadInfo(exchange);
        if (tradingDateService.isTodayTradingDay(exchange)) {
            securityPriceLoader.loadSecurityPrice(exchange);
//            swingPointMarker.markSwingPoints(exchange);
//            momentumChaser.detectBreakout(exchange, SecurityPricePeriod.Day, tradingDateService.getLatestTradingDate(exchange));
            volumeExplosionDetector.detectVolumeExplosion(exchange, SecurityPricePeriod.Day, tradingDateService.getLatestTradingDate(exchange));
        }
        asxUpcomingFloatDetailLoader.loadUpcomingFloatsDetails(exchange);
    }

    @Scheduled(cron = "0 0 0 1 JAN *")
    public void loadTradingCalendar() {
        Exchange exchange = exchangeRepository.findByName("Australian Securities Exchange");
        tradingCalendarLoader.loadTradingCalendar(exchange);
    }


    @Scheduled(cron = "*/5 * * * * *")
    public void ping() {
        exchangeRepository.findByName("Australian Securities Exchange");
    }

    @Scheduled(cron = "0 0 12 * * SAT,SUN")
    public void loadAnnouncements() {
        try {
            Exchange exchange = exchangeRepository.findByName("Australian Securities Exchange");
            announcementLoader.loadAnnouncements(exchange);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
