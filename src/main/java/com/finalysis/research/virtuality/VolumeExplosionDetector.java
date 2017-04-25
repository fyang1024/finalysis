package com.finalysis.research.virtuality;

import com.finalysis.research.reality.Exchange;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class VolumeExplosionDetector {

    private static final Logger logger = LoggerFactory.getLogger(VolumeExplosionDetector.class);
    private static final BigDecimal TURNOVER_THRESHOLD = new BigDecimal("300000");
    private static final BigDecimal PRICE_MOVE_THRESHOLD = new BigDecimal("0.20");
    private static final Integer MIN_TIMES = 5;
    private static final Integer MIN_TIMES_AT_NOON = 2;
    private static final Integer MIN_TIMES_BEFORE_CLOSE = 4;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final BigDecimal STOP_LOSS_RATIO = new BigDecimal("0.9");

    @Autowired
    SecurityRepository securityRepository;

    @Autowired
    SecurityPriceRepository securityPriceRepository;

    @Autowired
    AnnouncementRepository announcementRepository;

    @Autowired
    AsxSecurityPriceLoader securityPriceLoader;

    @Autowired
    JavaMailSender mailSender;

    private void sendEMail(String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("fyang1024@gmail.com");
        message.setFrom("fyang1024@gmail.com");
        message.setSubject(text);
        message.setText("Found " + text);
        mailSender.send(message);
        logger.info("Email Sent");
    }

    public void detectVolumeExplosion(Exchange exchange, SecurityPricePeriod period, Date openDate) {
        List<Tip> tips = getTips(exchange, period, openDate);
        if (!tips.isEmpty()) {
            writeToFile(exchange, tips, openDate);
        }
        logger.info("--Done--");
    }

    public List<Tip> getTips(Exchange exchange, SecurityPricePeriod period, Date openDate) {
        List<Tip> tips = new ArrayList<>();
        List<Security> securities = securityRepository.findActiveByExchange(exchange, openDate, SecurityType.ORDINARY_SHARE);
        for (Security security : securities) {
            SecurityPrice securityPrice = securityPriceRepository.findByOpenDateAndSecurityAndPeriod(openDate, security, period);
            if (securityPrice != null && securityPrice.getEstimatedTurnover().compareTo(TURNOVER_THRESHOLD) > 0) {
                List<Integer> volumes = securityPriceRepository.findVolumesLastPeriods(openDate, security.getId(), period.toString(), 5);
                if (volumes != null && !volumes.isEmpty()) {
                    Integer averageVolume = (int)volumes.stream().mapToInt(a -> a).average().getAsDouble();
                    Date startDate = org.apache.commons.lang3.time.DateUtils.addDays(openDate, -120);
                    BigDecimal previousPrice = securityPriceRepository.findPreviousPrice(securityPrice.getOpenDate(), security.getId(), period.toString());
                    if (securityPrice.getVolume() > MIN_TIMES * averageVolume &&
                            securityPriceRepository.findVolumeLargerDays(startDate, openDate, security.getId(), securityPrice.getVolume(), period.toString()) == 0 &&
                            previousPrice.compareTo(BigDecimal.ZERO) > 0 &&
                            securityPrice.getClosePrice().add(previousPrice.negate()).divide(previousPrice, 3, BigDecimal.ROUND_HALF_UP).compareTo(PRICE_MOVE_THRESHOLD) < 0) {
                        logger.info(security.getCode());
                        BigDecimal stopLoss = securityPrice.getClosePrice().multiply(STOP_LOSS_RATIO).setScale(3, BigDecimal.ROUND_HALF_DOWN);
                        Tip tip = new Tip(TipType.VOLUME_EXPLOSION, openDate, security, security.getCode(), securityPrice.getClosePrice(), stopLoss);
                        List<Announcement> announcements = announcementRepository.findByExchangeAndSecurityAndAnnouncementDate(exchange, security, openDate);
                        if (!announcements.isEmpty()) {
                            tip.setAnnouncementOccurred(true);
                            StringBuilder sb = new StringBuilder();
                            for (Announcement announcement : announcements) {
                                if (sb.length() != 0) sb.append(", ");
                                sb.append(announcement.getHeadline());
                            }
                            if (sb.length() > 4000) sb.delete(4000, sb.length() - 1);
                            tip.setAnnouncementHeadline(sb.toString());
                        }
                        tip.setGicsSector(security.getGicsSector());
                        tip.setVolumeExplosionRatio(new BigDecimal(securityPrice.getVolume() / averageVolume));
                        tip.setTipDayTurnOver(securityPrice.getEstimatedTurnover().setScale(2, BigDecimal.ROUND_HALF_EVEN));
                        tip.setBuyPriceRank(securityPriceRepository.findPriceRank(openDate, security.getId(), tip.getBuyPrice(), period.toString()));
                        tips.add(tip);
                    }
                }
            }
        }
        return tips;
    }

    public void writeToFile(Exchange exchange, List<Tip> tips, Date openDate) {
        String dateStr = new SimpleDateFormat("yyyy-MMM-dd").format(openDate);
        StringBuilder sb = new StringBuilder();
        for (Tip tip : tips) {
            sb.append(tip.getCode()).append(LINE_SEPARATOR);
        }
        try {
            String fileName = "volume_exploded_" + dateStr + ".txt";
            File file = new File(exchange.getBuyTipsArchive(), fileName);
            FileUtils.write(file, sb);
            FileUtils.copyFile(file, new File("/Users/yangf/Google Drive/daily buy tips", fileName));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void sendVolumeExplosionTips(Exchange exchange, SecurityPricePeriod day, Date openDate) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        final int maxCodes = 10;
        List<Security> securities = securityRepository.findActiveByExchange(exchange, openDate);
        List<Security> lastBunch = new ArrayList<>(maxCodes);
        int count = 0;
        for (Security security : securities) {
            if (count < maxCodes) {
                lastBunch.add(security);
                count++;
            } else {
                final List<SecurityPrice> prices = securityPriceLoader.loadLastBunch(exchange, lastBunch, openDate);
                executorService.execute(new DetectAndSend(prices));
                lastBunch.clear();
                lastBunch.add(security);
                count = 1;
            }
        }
        if (!lastBunch.isEmpty()) {
            final List<SecurityPrice> prices = securityPriceLoader.loadLastBunch(exchange, lastBunch, openDate);
            executorService.execute(new DetectAndSend(prices));
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    class DetectAndSend implements Runnable {

        private List<SecurityPrice> prices;

        DetectAndSend(List<SecurityPrice> prices) {
            this.prices = prices;
        }

        @Override
        public void run() {
            StringBuilder sb = new StringBuilder();
            Integer times = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 12 ? MIN_TIMES_AT_NOON : MIN_TIMES_BEFORE_CLOSE;
            for(SecurityPrice securityPrice : prices) {
                if(securityPrice.getEstimatedTurnover().compareTo(TURNOVER_THRESHOLD) > 0) {
                    Date openDate = securityPrice.getOpenDate();
                    Security security = securityPrice.getSecurity();
                    String securityPeriodName = securityPrice.getPeriod().toString();
                    List<Integer> volumes = securityPriceRepository.findVolumesLastPeriods(openDate, security.getId(), securityPeriodName, 5);
                    if (volumes != null && !volumes.isEmpty()) {
                        Integer averageVolume = (int) volumes.stream().mapToInt(a -> a).average().getAsDouble();
                        Date startDate = org.apache.commons.lang3.time.DateUtils.addDays(openDate, -120);
                        BigDecimal previousPrice = securityPriceRepository.findPreviousPrice(securityPrice.getOpenDate(), security.getId(), securityPrice.getPeriod().toString());
                        if (securityPrice.getVolume() > times * averageVolume &&
                                securityPriceRepository.findVolumeLargerDays(startDate, openDate, security.getId(), securityPrice.getVolume(), securityPeriodName) == 0 &&
                                previousPrice.compareTo(BigDecimal.ZERO) > 0 &&
                                securityPrice.getClosePrice().add(previousPrice.negate()).divide(previousPrice, 3, BigDecimal.ROUND_HALF_UP).compareTo(PRICE_MOVE_THRESHOLD) < 0) {
                            logger.info(security.getCode());
                            sb.append(security.getCode()).append(" ");
                        }
                    }
                }
            }
            if(sb.length() > 0) {
                sendEMail(sb.toString());
            }
        }
    }
}
