package com.finalysis.research.reality;

import com.finalysis.research.BaseMortalEntity;

import javax.persistence.*;
import java.sql.Time;

@Entity (name = "EXCHANGE")
public class Exchange extends BaseMortalEntity {

    private String name;

    private String thomsonReutersSymbol;

    private String googleFinanceSymbol;

    private String yahooFinanceSymbol;

    @ManyToOne
    private Country country;

    private String website;

    private String listedOrdinaryShareUrl;

    private String listedOrdinaryShareLoader;

    private String delistedSecurityUrl;

    private String delistedSecurityLoader;

    private String securityCodeChangeUrl;

    private String securityCodeChangeLoader;

    private String securityInfoUrl;

    private String recentFloatsUrl;

    private String securityInfoLoader;

    private String todayAnnouncementUrl;

    private String announcementUrl;

    private String announcementArchive;

    private String announcementLoader;

    private String tradingCalendarUrl;

    private String tradingCalendarLoader;

    private String securityPriceUrl;

    private String securityPriceLoader;

    private String securityPriceArchive;

    private String upcomingFloatDetailUrl;

    private String upcomingFloatDetailLoader;

    private String buyTipsArchive;

    private String shortSellUrl;

    private String shortSellLoader;

    private String shortSellArchive;

    private String listedEtpUrl;

    private String listedEtpLoader;

    private String timeZone;

    private Time gmtOffset;

    private boolean gmtOffsetNegative = false;

    private Time preMarketStart;

    private Time preMarketEnd;

    private Time marketStart;

    private Time marketEnd;

    private Time postMarketStart;

    private Time postMarketEnd;

    private String currencyName;

    private String currencyCode;

   public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThomsonReutersSymbol() {
        return thomsonReutersSymbol;
    }

    public void setThomsonReutersSymbol(String thomsonReutersSymbol) {
        this.thomsonReutersSymbol = thomsonReutersSymbol;
    }

    public String getGoogleFinanceSymbol() {
        return googleFinanceSymbol;
    }

    public void setGoogleFinanceSymbol(String googleFinanceSymbol) {
        this.googleFinanceSymbol = googleFinanceSymbol;
    }

    public String getYahooFinanceSymbol() {
        return yahooFinanceSymbol;
    }

    public void setYahooFinanceSymbol(String yahooFinanceSymbol) {
        this.yahooFinanceSymbol = yahooFinanceSymbol;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getListedOrdinaryShareUrl() {
        return listedOrdinaryShareUrl;
    }

    public void setListedOrdinaryShareUrl(String listedOrdinaryShareUrl) {
        this.listedOrdinaryShareUrl = listedOrdinaryShareUrl;
    }

    public String getListedOrdinaryShareLoader() {
        return listedOrdinaryShareLoader;
    }

    public void setListedOrdinaryShareLoader(String listedOrdinaryShareLoader) {
        this.listedOrdinaryShareLoader = listedOrdinaryShareLoader;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Time getGmtOffset() {
        return gmtOffset;
    }

    public void setGmtOffset(Time gmtOffset) {
        this.gmtOffset = gmtOffset;
    }

    public Time getPreMarketStart() {
        return preMarketStart;
    }

    public void setPreMarketStart(Time preMarketStart) {
        this.preMarketStart = preMarketStart;
    }

    public Time getPreMarketEnd() {
        return preMarketEnd;
    }

    public void setPreMarketEnd(Time preMarketEnd) {
        this.preMarketEnd = preMarketEnd;
    }

    public Time getMarketStart() {
        return marketStart;
    }

    public void setMarketStart(Time marketStart) {
        this.marketStart = marketStart;
    }

    public Time getMarketEnd() {
        return marketEnd;
    }

    public void setMarketEnd(Time marketEnd) {
        this.marketEnd = marketEnd;
    }

    public Time getPostMarketStart() {
        return postMarketStart;
    }

    public void setPostMarketStart(Time postMarketStart) {
        this.postMarketStart = postMarketStart;
    }

    public Time getPostMarketEnd() {
        return postMarketEnd;
    }

    public void setPostMarketEnd(Time postMarketEnd) {
        this.postMarketEnd = postMarketEnd;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getListedEtpUrl() {
        return listedEtpUrl;
    }

    public void setListedEtpUrl(String listedEtpUrl) {
        this.listedEtpUrl = listedEtpUrl;
    }

    public String getListedEtpLoader() {
        return listedEtpLoader;
    }

    public void setListedEtpLoader(String listedEtpLoader) {
        this.listedEtpLoader = listedEtpLoader;
    }

    public void setGmtOffsetNegative(boolean gmtOffsetNegative) {
        this.gmtOffsetNegative = gmtOffsetNegative;
    }

    public boolean isGmtOffsetNegative() {
        return gmtOffsetNegative;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getSecurityCodeChangeUrl() {
        return securityCodeChangeUrl;
    }

    public void setSecurityCodeChangeUrl(String securityCodeChangeUrl) {
        this.securityCodeChangeUrl = securityCodeChangeUrl;
    }

    public String getSecurityCodeChangeLoader() {
        return securityCodeChangeLoader;
    }

    public void setSecurityCodeChangeLoader(String securityCodeChangeLoader) {
        this.securityCodeChangeLoader = securityCodeChangeLoader;
    }

    public String getSecurityInfoUrl() {
        return securityInfoUrl;
    }

    public void setSecurityInfoUrl(String securityInfoUrl) {
        this.securityInfoUrl = securityInfoUrl;
    }

    public String getSecurityInfoLoader() {
        return securityInfoLoader;
    }

    public void setSecurityInfoLoader(String securityInfoLoader) {
        this.securityInfoLoader = securityInfoLoader;
    }

    public String getAnnouncementUrl() {
        return announcementUrl;
    }

    public void setAnnouncementUrl(String announcementUrl) {
        this.announcementUrl = announcementUrl;
    }

    public String getAnnouncementLoader() {
        return announcementLoader;
    }

    public void setAnnouncementLoader(String announcementLoader) {
        this.announcementLoader = announcementLoader;
    }

    public String getDelistedSecurityUrl() {
        return delistedSecurityUrl;
    }

    public String getDelistedSecurityLoader() {
        return delistedSecurityLoader;
    }

    public void setDelistedSecurityUrl(String delistedSecurityUrl) {
        this.delistedSecurityUrl = delistedSecurityUrl;
    }

    public void setDelistedSecurityLoader(String delistedSecurityLoader) {
        this.delistedSecurityLoader = delistedSecurityLoader;
    }

    public String getSecurityPriceUrl() {
        return securityPriceUrl;
    }

    public void setSecurityPriceUrl(String securityPriceUrl) {
        this.securityPriceUrl = securityPriceUrl;
    }

    public String getSecurityPriceLoader() {
        return securityPriceLoader;
    }

    public void setSecurityPriceLoader(String securityPriceLoader) {
        this.securityPriceLoader = securityPriceLoader;
    }

    public String getSecurityPriceArchive() {
        return securityPriceArchive;
    }

    public void setSecurityPriceArchive(String securityPriceArchive) {
        this.securityPriceArchive = securityPriceArchive;
    }

    public String getShortSellUrl() {
        return shortSellUrl;
    }

    public void setShortSellUrl(String shortSellUrl) {
        this.shortSellUrl = shortSellUrl;
    }

    public String getShortSellLoader() {
        return shortSellLoader;
    }

    public void setShortSellLoader(String shortSellLoader) {
        this.shortSellLoader = shortSellLoader;
    }

    public String getShortSellArchive() {
        return shortSellArchive;
    }

    public void setShortSellArchive(String shortSellArchive) {
        this.shortSellArchive = shortSellArchive;
    }

    public String getTradingCalendarUrl() {
        return tradingCalendarUrl;
    }

    public String getTradingCalendarLoader() {
        return tradingCalendarLoader;
    }

    public void setTradingCalendarUrl(String tradingCalendarUrl) {
        this.tradingCalendarUrl = tradingCalendarUrl;
    }

    public void setTradingCalendarLoader(String tradingCalendarLoader) {
        this.tradingCalendarLoader = tradingCalendarLoader;
    }

    public String getAnnouncementArchive() {
        return announcementArchive;
    }

    public void setAnnouncementArchive(String announcementArchive) {
        this.announcementArchive = announcementArchive;
    }

    public String getBuyTipsArchive() {
        return buyTipsArchive;
    }

    public void setBuyTipsArchive(String buyTipsArchive) {
        this.buyTipsArchive = buyTipsArchive;
    }

    public String getTodayAnnouncementUrl() {
        return todayAnnouncementUrl;
    }

    public void setTodayAnnouncementUrl(String todayAnnouncementUrl) {
        this.todayAnnouncementUrl = todayAnnouncementUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Exchange)) return false;

        Exchange exchange = (Exchange) o;

        if (!country.equals(exchange.country)) return false;
        if (!name.equals(exchange.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + country.hashCode();
        return result;
    }

    public String getRecentFloatsUrl() {
        return recentFloatsUrl;
    }

    public void setRecentFloatsUrl(String recentFloatsUrl) {
        this.recentFloatsUrl = recentFloatsUrl;
    }

    public String getUpcomingFloatDetailUrl() {
        return upcomingFloatDetailUrl;
    }

    public void setUpcomingFloatDetailUrl(String upcomingFloatDetailUrl) {
        this.upcomingFloatDetailUrl = upcomingFloatDetailUrl;
    }

    public String getUpcomingFloatDetailLoader() {
        return upcomingFloatDetailLoader;
    }

    public void setUpcomingFloatDetailLoader(String upcomingFloatDetailLoader) {
        this.upcomingFloatDetailLoader = upcomingFloatDetailLoader;
    }
}
