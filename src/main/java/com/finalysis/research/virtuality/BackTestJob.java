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
 * Created by Fei on 19/06/2016.
 */

@Component
public class BackTestJob {
    private static final Logger logger = LoggerFactory.getLogger(BackTestJob.class);

    @Autowired
    ExchangeRepository exchangeRepository;

    @Autowired
    SecurityRepository securityRepository;

    @Autowired
    SecurityPriceRepository securityPriceRepository;

    @Autowired
    TipRepository tipRepository;

    @Autowired
    TradingDateService tradingDateService;

    @Autowired
    VolumeExplosionDetector volumeExplosionDetector;


    @Scheduled(cron = "0 58 23 13 JUL ?")
    public void VolumeExplosionDetectorBackTest() {
        Exchange exchange = exchangeRepository.findByName("Australian Securities Exchange");
        Date tipDate = tradingDateService.getPreviousTradingDate(tipRepository.findEarliestTipDay());
        Date startDate = DateUtils.parse("01/01/2003", DateUtils.AUSSIE_DATE_FORMAT);
        while(startDate.before(tipDate)) {
            logger.info(DateUtils.format(tipDate, DateUtils.AUSSIE_DATE_FORMAT));
            List<Tip> tips = volumeExplosionDetector.getTips(exchange, SecurityPricePeriod.Day, tipDate);
            for(Tip tip : tips) {
                List<SecurityPrice> nextPrices = securityPriceRepository.findNextSecurityPrices(tip.getSecurity().getId(), SecurityPricePeriod.Day.name(), tipDate, 60);
                Integer negativeDays = 0, positiveDays = 0, maxDrawDown = 0, maxReturn = 0, holdingDays = 1;
                BigDecimal totalNegativeReturn = BigDecimal.ZERO, totalPositiveReturn = BigDecimal.ZERO;
                boolean stopLossTriggered = false;
                for(SecurityPrice price : nextPrices) {
                    BigDecimal todayReturn = price.getClosePrice()
                            .add(tip.getBuyPrice().negate())
                            .multiply(new BigDecimal("100"))
                            .divide(tip.getBuyPrice(), BigDecimal.ROUND_HALF_EVEN);
                    if(price.getClosePrice().compareTo(tip.getBuyPrice()) > 0) {
                        positiveDays++;
                        totalPositiveReturn = totalPositiveReturn.add(todayReturn);
                        if(todayReturn.intValue() > maxReturn) {
                            maxReturn = todayReturn.intValue();
                        }
                    } else {
                        negativeDays++;
                        totalNegativeReturn = totalNegativeReturn.add(todayReturn);
                        if(todayReturn.intValue() < maxDrawDown) {
                            maxDrawDown = todayReturn.intValue();
                        }
                        if(price.getClosePrice().compareTo(tip.getStopLoss()) <= 0) {
                            tip.setStopLossTriggered(true);
                        }
                    }
                    holdingDays++;
                    if(tip.isStopLossTriggered()) {
                        break;
                    }
                }
                tip.setHoldingDays(holdingDays);
                tip.setNegativeDays(negativeDays);
                if(negativeDays > 0) {
                    tip.setAverageNegativeReturn(totalNegativeReturn.divide(new BigDecimal("" + negativeDays), BigDecimal.ROUND_HALF_EVEN).intValue());
                }
                tip.setMaxDrawDown(maxDrawDown);
                tip.setPositiveDays(positiveDays);
                if(positiveDays > 0) {
                    tip.setAveragePositiveReturn(totalPositiveReturn.divide(new BigDecimal("" + positiveDays), BigDecimal.ROUND_HALF_EVEN).intValue());
                }
                tip.setMaxReturn(maxReturn);
            }
            tipRepository.save(tips);
            tipDate = tradingDateService.getPreviousTradingDate(tipDate);
        }
        logger.info("--Done--");
    }
}
