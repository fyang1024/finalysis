package com.finalysis.research.virtuality;

import com.finalysis.research.BaseEntity;
import com.finalysis.research.reality.Exchange;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class SecurityPrice extends BaseEntity {

    @ManyToOne
    private Exchange exchange;

    @ManyToOne
    private Security security;

    private String code;

    @Enumerated(EnumType.STRING)
    private SecurityPricePeriod period = SecurityPricePeriod.Day;

    @Temporal(TemporalType.DATE)
    private Date openDate;

    @Temporal(TemporalType.DATE)
    private Date closeDate;

    @Column(precision =10, scale = 3)
    private BigDecimal openPrice;

    @Column(precision = 10, scale = 3)
    private BigDecimal closePrice;

    @Column(precision = 10, scale = 3)
    private BigDecimal highestPrice;

    @Column(precision = 10, scale = 3)
    private BigDecimal lowestPrice;

    private Integer volume;

    private Boolean swingPointHigh;

    private Boolean swingPointLow;

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
    }

    public BigDecimal getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
    }

    public BigDecimal getHighestPrice() {
        return highestPrice;
    }

    public void setHighestPrice(BigDecimal highestPrice) {
        this.highestPrice = highestPrice;
    }

    public BigDecimal getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(BigDecimal lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public SecurityPricePeriod getPeriod() {
        return period;
    }

    public void setPeriod(SecurityPricePeriod period) {
        this.period = period;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Exchange getExchange() {
        return exchange;
    }

    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    public Boolean getSwingPointHigh() {
        return swingPointHigh;
    }

    public void setSwingPointHigh(Boolean swingPointHigh) {
        this.swingPointHigh = swingPointHigh;
    }

    public Boolean getSwingPointLow() {
        return swingPointLow;
    }

    public void setSwingPointLow(Boolean swingPointLow) {
        this.swingPointLow = swingPointLow;
    }

    public BigDecimal getEstimatedTurnover() {
        return openPrice.add(closePrice).add(highestPrice).add(lowestPrice)
                .multiply(new BigDecimal(volume)).divide(new BigDecimal("4"));
    }

    public BigDecimal getMiddlePrice() {
        return openPrice.add(closePrice).divide(new BigDecimal("2")).setScale(getMinScale(), BigDecimal.ROUND_DOWN);
    }

    public int getMinScale() {
        return Math.min(openPrice.scale(), closePrice.scale());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecurityPrice)) return false;

        SecurityPrice that = (SecurityPrice) o;

        if (!closeDate.equals(that.closeDate)) return false;
        if (!code.equals(that.code)) return false;
        if (!exchange.equals(that.exchange)) return false;
        if (!openDate.equals(that.openDate)) return false;
        if (security != null ? !security.equals(that.security) : that.security != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = exchange.hashCode();
        result = 31 * result + (security != null ? security.hashCode() : 0);
        result = 31 * result + code.hashCode();
        result = 31 * result + openDate.hashCode();
        result = 31 * result + closeDate.hashCode();
        return result;
    }
}
