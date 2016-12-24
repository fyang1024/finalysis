package com.finalysis.research.virtuality;

import com.finalysis.research.DateUtils;
import com.finalysis.research.reality.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class SwingPointMarker {

    private final static Logger logger = LoggerFactory.getLogger(SwingPointMarker.class);

    @Autowired
    SecurityRepository securityRepository;

    @Autowired
    SecurityPriceRepository securityPriceRepository;

    public void eraseLatestSwingPoints(Exchange exchange) {
        List<Security> securities = securityRepository.findByExchange(exchange);
        securities.forEach(this::eraseLatestSwingPoints);
    }

    private void eraseLatestSwingPoints(Security security) {
        logger.info("Erase Latest Swing Points for " + security.getCode());
        eraseLatestSwingPoints(security, SecurityPricePeriod.Day);
    }

    private void eraseLatestSwingPoints(Security security, SecurityPricePeriod period) {
        eraseLatestSwingPointHigh(security, period);
        eraseLatestSwingPointLow(security, period);
    }

    private void eraseLatestSwingPointLow(Security security, SecurityPricePeriod period) {
        Date latestSwingPointLowDate = securityPriceRepository.findLatestSwingPointLowDate(security, period);
        if(latestSwingPointLowDate != null) {
            String swingPointLowDate = DateUtils.format(latestSwingPointLowDate, DateUtils.AUSSIE_DATE_FORMAT);
            logger.info(security.getCode() + " - latest Swing Point Low is on " + swingPointLowDate);
            SecurityPrice swingPointLow = securityPriceRepository.findByOpenDateAndSecurityAndPeriod(latestSwingPointLowDate, security, period);
            if(swingPointLow != null) {
                logger.info(security.getCode() + " - Swing Point Low found on " + swingPointLowDate);
                swingPointLow.setSwingPointLow(null);
                securityPriceRepository.saveAndFlush(swingPointLow);
            } else {
                logger.info(security.getCode() + " - No Swing Point Low found on " + swingPointLowDate);
            }
        } else {
            logger.info(security.getCode() + " - no Swing Point Low date was found");
        }
    }

    private void eraseLatestSwingPointHigh(Security security, SecurityPricePeriod period) {
        Date latestSwingPointHighDate = securityPriceRepository.findLatestSwingPointHighDate(security, period);
        if(latestSwingPointHighDate != null) {
            String swingPointHighDate = DateUtils.format(latestSwingPointHighDate, DateUtils.AUSSIE_DATE_FORMAT);
            logger.info(security.getCode() + " - latest Swing Point High is on " + swingPointHighDate);
            SecurityPrice swingPointHigh = securityPriceRepository.findByOpenDateAndSecurityAndPeriod(latestSwingPointHighDate, security, period);
            if(swingPointHigh != null) {
                logger.info(security.getCode() + " - Swing Point High found on " + swingPointHighDate);
                swingPointHigh.setSwingPointHigh(null);
                securityPriceRepository.saveAndFlush(swingPointHigh);
            } else {
                logger.info(security.getCode() + " - No Swing Point High found on " + swingPointHighDate);
            }
        } else {
            logger.info(security.getCode() + " - no Swing Point High date was found");
        }
    }


    public void markSwingPoints(Exchange exchange) {
        List<Security> securities = securityRepository.findByExchange(exchange);
        securities.forEach(this::markSwingPoints);
        logger.info("--Done--");
    }

    private void markSwingPoints(Security security) {
        logger.info("Mark Swing Points for " + security.getCode());
        markSwingPoints(security, SecurityPricePeriod.Day);
    }

    private void markSwingPoints(Security security, SecurityPricePeriod securityPricePeriod) {
        markSwingPointsHigh(security, securityPricePeriod);
        markSwingPointsLow(security, securityPricePeriod);
    }

    private void markSwingPointsHigh(Security security, SecurityPricePeriod securityPricePeriod) {
        Date startDate = securityPriceRepository.findEarliestDateToMarkSwingPointHigh(security, securityPricePeriod);
        if (startDate != null) {
            List<SecurityPrice> prices = securityPriceRepository.findSecurityPriceOnAfter(security, securityPricePeriod, startDate);
            int i = 0, count = 0;
            SecurityPrice previousPrice = null, potentialSwingPointHigh = null;
            while (i < prices.size()) {
                SecurityPrice currentPrice = prices.get(i);
                if (currentPrice.getSwingPointHigh() == null) {
                    if (potentialSwingPointHigh == null) {
                        if (previousPrice == null) {
                            previousPrice = findPreviousSecurityPrice(currentPrice);
                        }
                        if (previousPrice != null && previousPrice.getHighestPrice().compareTo(currentPrice.getHighestPrice()) >= 0) {
                            currentPrice.setSwingPointHigh(false);
                            previousPrice = securityPriceRepository.saveAndFlush(currentPrice);
                        } else {
                            potentialSwingPointHigh = currentPrice;
                            count = 0;
                        }
                    } else {
                        if (potentialSwingPointHigh.getHighestPrice().compareTo(currentPrice.getHighestPrice()) > 0 ||
                                potentialSwingPointHigh.getHighestPrice().equals(currentPrice.getHighestPrice()) &&
                                        potentialSwingPointHigh.getVolume() > currentPrice.getVolume()) {
                            currentPrice.setSwingPointHigh(false);
                            previousPrice = securityPriceRepository.saveAndFlush(currentPrice);
                            count++;
                            if (count == 6) {
                                potentialSwingPointHigh.setSwingPointHigh(true);
                                securityPriceRepository.saveAndFlush(potentialSwingPointHigh);
                                potentialSwingPointHigh = null;
                            }
                        } else {
                            potentialSwingPointHigh.setSwingPointHigh(false);
                            securityPriceRepository.saveAndFlush(potentialSwingPointHigh);
                            potentialSwingPointHigh = currentPrice;
                            count = 0;
                        }
                    }
                } else if (!currentPrice.getSwingPointHigh()) {
                    if (potentialSwingPointHigh != null) {
                        count++;
                    }
                } else {
                    throw new RuntimeException("Impossible!");
                }
                i++;
            }
        }
    }

    private void markSwingPointsLow(Security security, SecurityPricePeriod securityPricePeriod) {
        Date startDate = securityPriceRepository.findEarliestDateToMarkSwingPointLow(security, securityPricePeriod);
        if (startDate != null) {
            List<SecurityPrice> prices = securityPriceRepository.findSecurityPriceOnAfter(security, securityPricePeriod, startDate);
            int i = 0, count = 0;
            SecurityPrice previousPrice = null, potentialSwingPointLow = null;
            while (i < prices.size()) {
                SecurityPrice currentPrice = prices.get(i);
                if (currentPrice.getSwingPointLow() == null) {
                    if (potentialSwingPointLow == null) {
                        if (previousPrice == null) {
                            previousPrice = findPreviousSecurityPrice(currentPrice);
                        }
                        if (previousPrice != null && previousPrice.getLowestPrice().compareTo(currentPrice.getLowestPrice()) <= 0) {
                            currentPrice.setSwingPointLow(false);
                            previousPrice = securityPriceRepository.saveAndFlush(currentPrice);
                        } else {
                            potentialSwingPointLow = currentPrice;
                            count = 0;
                        }
                    } else {
                        if (potentialSwingPointLow.getLowestPrice().compareTo(currentPrice.getLowestPrice()) < 0 ||
                                potentialSwingPointLow.getLowestPrice().equals(currentPrice.getLowestPrice()) &&
                                        potentialSwingPointLow.getVolume() > currentPrice.getVolume()) {
                            currentPrice.setSwingPointLow(false);
                            previousPrice = securityPriceRepository.saveAndFlush(currentPrice);
                            count++;
                            if (count == 6) {
                                potentialSwingPointLow.setSwingPointLow(true);
                                securityPriceRepository.saveAndFlush(potentialSwingPointLow);
                                potentialSwingPointLow = null;
                            }
                        } else {
                            potentialSwingPointLow.setSwingPointLow(false);
                            securityPriceRepository.saveAndFlush(potentialSwingPointLow);
                            potentialSwingPointLow = currentPrice;
                            count = 0;
                        }
                    }
                } else if (!currentPrice.getSwingPointLow()) {
                    if (potentialSwingPointLow != null) {
                        count++;
                    }
                } else {
                    throw new RuntimeException("Impossible!");
                }
                i++;
            }
        }
    }


    private SecurityPrice findPreviousSecurityPrice(SecurityPrice price) {
        //TODO use pagination and sorting
        Date date = securityPriceRepository.findLatestOpenDateBefore(price.getOpenDate(), price.getSecurity(), price.getPeriod());
        if (date != null) {
            return securityPriceRepository.findByOpenDateAndSecurityAndPeriod(date, price.getSecurity(), price.getPeriod());
        }
        return null;
    }
}
