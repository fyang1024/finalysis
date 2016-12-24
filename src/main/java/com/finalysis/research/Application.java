package com.finalysis.research;

import com.finalysis.research.reality.Exchange;
import com.finalysis.research.reality.ExchangeLoader;
import com.finalysis.research.reality.ExchangeRepository;
import com.finalysis.research.virtuality.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAutoConfiguration
@EnableScheduling
@ComponentScan
@PropertySource("classpath:application.properties")
public class Application {

    @Bean(destroyMethod = "shutdown")
    public Executor taskScheduler() {
        return Executors.newScheduledThreadPool(2);
    }

    public static void main(String[] args) throws IOException {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
//        CountryLoader countryLoader = ctx.getBean(CountryLoader.class);
//        countryLoader.loadCountries();
//        ExchangeLoader exchangeLoader = ctx.getBean(ExchangeLoader.class);
//        exchangeLoader.loadExchanges();
//        exchangeLoader.updateWebsites();
//        GicsMapLoader gicsMapLoader = ctx.getBean(GicsMapLoader.class);
//        gicsMapLoader.loadGICSMap();
//
//        SecurityTypeLoader securityTypeLoader = ctx.getBean(SecurityTypeLoader.class);
//        securityTypeLoader.loadSecurityTypes();

//        SwingPointMarker swingPointMarker = ctx.getBean(SwingPointMarker.class);
//        MomentumChaser momentumChaser = ctx.getBean(MomentumChaser.class);
//        TradingDateService tradingDateService = ctx.getBean(TradingDateService.class);

//        ExchangeRepository exchangeRepository = ctx.getBean(ExchangeRepository.class);
//        List<Exchange> exchanges = exchangeRepository.findByTradingCalendarUrlIsNotNull();
//        for(Exchange exchange : exchanges) {
//            TradingCalendarLoader tradingCalendarLoader = ctx.getBean(exchange.getTradingCalendarLoader(), TradingCalendarLoader.class);
//            tradingCalendarLoader.loadTradingCalendar(exchange);
//        }
//
//        exchanges = exchangeRepository.findBySecurityCodeChangeUrlIsNotNull();
//        for(Exchange exchange : exchanges) {
//            SecurityCodeChangeLoader securityCodeChangeLoader = ctx.getBean(exchange.getSecurityCodeChangeLoader(), SecurityCodeChangeLoader.class);
//            securityCodeChangeLoader.loadCodeChanges(exchange);
//        }
//
//        List<Exchange> exchanges = exchangeRepository.findByListedEtpUrlIsNotNull();
//        for(Exchange exchange : exchanges) {
//            EtpLoader etpLoader = ctx.getBean(exchange.getListedEtpLoader(), EtpLoader.class);
//            etpLoader.loadExchangeTradedProducts(exchange);
//        }
//
//        exchanges = exchangeRepository.findByListedOrdinaryShareUrlIsNotNull();
//        for(Exchange exchange : exchanges) {
//            OrdinaryShareLoader ordinaryShareLoader = ctx.getBean(exchange.getListedOrdinaryShareLoader(), OrdinaryShareLoader.class);
//            ordinaryShareLoader.loadOrdinaryShares(exchange);
//        }
//
//        exchanges = exchangeRepository.findByDelistedSecurityUrlIsNotNull();
//        for(Exchange exchange : exchanges) {
//            DelistedSecurityLoader delistedSecurityLoader = ctx.getBean(exchange.getDelistedSecurityLoader(), DelistedSecurityLoader.class);
//            delistedSecurityLoader.loadDelistedSecurity(exchange);
//        }
//
//        exchanges = exchangeRepository.findByShortSellUrlIsNotNull();
//        for(Exchange exchange : exchanges) {
//            ReportedShortSellLoader reportedShortSellLoader = ctx.getBean(exchange.getShortSellLoader(), ReportedShortSellLoader.class);
//            reportedShortSellLoader.loadReportedShortSell(exchange);
//        }

//        exchanges = exchangeRepository.findByAnnouncementUrlIsNotNull();
//        for(Exchange exchange : exchanges) {
//            AnnouncementLoader announcementLoader = ctx.getBean(exchange.getAnnouncementLoader(), AnnouncementLoader.class);
//            if(exchange.getTodayAnnouncementUrl() != null) {
//                announcementLoader.loadTodayAnnouncements(exchange);
//            } else {
//                announcementLoader.loadAnnouncements(exchange);
//            }
//        }

//        exchanges = exchangeRepository.findBySecurityInfoUrlIsNotNull();
//        for(Exchange exchange : exchanges) {
//            SecurityInfoLoader securityInfoLoader = ctx.getBean(exchange.getSecurityInfoLoader(), SecurityInfoLoader.class);
//            securityInfoLoader.loadInfo(exchange);
//        }
//
//        exchanges = exchangeRepository.findBySecurityPriceUrlIsNotNull();
//        for(Exchange exchange : exchanges) {
//            SecurityPriceLoader securityPriceLoader = ctx.getBean(exchange.getSecurityPriceLoader(), SecurityPriceLoader.class);
//            securityPriceLoader.loadSecurityPrice(exchange);
//            swingPointMarker.markSwingPoints(exchange);
//            momentumChaser.detectBreakout(exchange, SecurityPricePeriod.Day, tradingDateService.getLatestTradingDate(exchange));
//        }
    }
}