package com.finalysis.research.virtuality;

import com.finalysis.research.BaseEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class Tip extends BaseEntity implements Comparable<Tip> {

    @ManyToOne
    private Security security;

    private String code;

    @Temporal(TemporalType.DATE)
    private Date tipDay;

    @Column(precision =10, scale = 3)
    private BigDecimal buyPrice;

    //buy price rank in the last 90 days
    private Integer buyPriceRank;

    private boolean announcementOccurred;

    private String announcementHeadline;

    @Column(precision =10, scale = 3)
    private BigDecimal stopLoss;

    private boolean stopLossTriggered;

    private BigDecimal volumeExplosionRatio;

    private BigDecimal tipDayTurnOver;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Enumerated(EnumType.STRING)
    private TipType tipType;

    private Integer holdingDays;

    private Integer maxDrawDown;

    private Integer maxReturn;

    private Integer positiveDays;

    private Integer averagePositiveReturn;

    private Integer negativeDays;

    private Integer averageNegativeReturn;

    private String gicsSector;

    public Tip(TipType tipType, Date tipDay, Security security, String code, BigDecimal buyPrice,  BigDecimal stopLoss) {
        this.tipType = tipType;
        this.tipDay = tipDay;
        this.security = security;
        this.code = code;
        this.buyPrice = buyPrice;
        this.stopLoss = stopLoss;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public BigDecimal getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(BigDecimal buyPrice) {
        this.buyPrice = buyPrice;
    }

    public Integer getBuyPriceRank() {
        return buyPriceRank;
    }

    public void setBuyPriceRank(Integer buyPriceRank) {
        this.buyPriceRank = buyPriceRank;
    }

    public BigDecimal getVolumeExplosionRatio() {
        return volumeExplosionRatio;
    }

    public void setVolumeExplosionRatio(BigDecimal volumeExplosionRatio) {
        this.volumeExplosionRatio = volumeExplosionRatio;
    }

    public boolean isStopLossTriggered() {
        return stopLossTriggered;
    }

    public void setStopLossTriggered(boolean stopLossTriggered) {
        this.stopLossTriggered = stopLossTriggered;
    }

    public BigDecimal getTipDayTurnOver() {
        return tipDayTurnOver;
    }

    public void setTipDayTurnOver(BigDecimal tipDayTurnOver) {
        this.tipDayTurnOver = tipDayTurnOver;
    }

    public BigDecimal getStopLoss() {
        return stopLoss;
    }

    public void setStopLoss(BigDecimal stopLoss) {
        this.stopLoss = stopLoss;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getTipDay() {
        return tipDay;
    }

    public void setTipDay(Date tipDay) {
        this.tipDay = tipDay;
    }

    public TipType getTipType() {
        return tipType;
    }

    public void setTipType(TipType tipType) {
        this.tipType = tipType;
    }

    public Integer getHoldingDays() {
        return holdingDays;
    }

    public void setHoldingDays(Integer holdingDays) {
        this.holdingDays = holdingDays;
    }

    public Integer getMaxDrawDown() {
        return maxDrawDown;
    }

    public void setMaxDrawDown(Integer maxDrawDown) {
        this.maxDrawDown = maxDrawDown;
    }

    public Integer getMaxReturn() {
        return maxReturn;
    }

    public void setMaxReturn(Integer maxReturn) {
        this.maxReturn = maxReturn;
    }

    public Integer getPositiveDays() {
        return positiveDays;
    }

    public void setPositiveDays(Integer positiveDays) {
        this.positiveDays = positiveDays;
    }

    public Integer getAveragePositiveReturn() {
        return averagePositiveReturn;
    }

    public void setAveragePositiveReturn(Integer averagePositiveReturn) {
        this.averagePositiveReturn = averagePositiveReturn;
    }

    public Integer getNegativeDays() {
        return negativeDays;
    }

    public void setNegativeDays(Integer negativeDays) {
        this.negativeDays = negativeDays;
    }

    public Integer getAverageNegativeReturn() {
        return averageNegativeReturn;
    }

    public void setAverageNegativeReturn(Integer averageNegativeReturn) {
        this.averageNegativeReturn = averageNegativeReturn;
    }

    public boolean isAnnouncementOccurred() {
        return announcementOccurred;
    }

    public void setAnnouncementOccurred(boolean announcementOccurred) {
        this.announcementOccurred = announcementOccurred;
    }

    public String getAnnouncementHeadline() {
        return announcementHeadline;
    }

    public void setAnnouncementHeadline(String announcementHeadline) {
        this.announcementHeadline = announcementHeadline;
    }

    public String getGicsSector() {
        return gicsSector;
    }

    public void setGicsSector(String gicsSector) {
        this.gicsSector = gicsSector;
    }

    @Override
    public int compareTo(Tip o) {
        return code.compareTo(o.code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tip)) return false;

        Tip tip = (Tip) o;

        if (!code.equals(tip.code)) return false;
        if (!security.equals(tip.security)) return false;
        if (!stopLoss.equals(tip.stopLoss)) return false;
        if (!tipDay.equals(tip.tipDay)) return false;
        if (!tipType.equals(tip.tipType)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = security.hashCode();
        result = 31 * result + code.hashCode();
        result = 31 * result + tipDay.hashCode();
        result = 31 * result + stopLoss.hashCode();
        result = 31 * result + tipType.hashCode();
        return result;
    }
}
