package com.finalysis.research.virtuality;

import com.finalysis.research.BaseEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class Dividend extends BaseEntity {

    @ManyToOne
    private Security security;

    @Enumerated(EnumType.STRING)
    private DividendType dividendType;

    @Column(precision = 10, scale = 3)
    private BigDecimal amount;

    @Temporal(TemporalType.DATE)
    private Date exDividendDate;

    @Temporal(TemporalType.DATE)
    private Date payDate;

    @Column(precision = 5, scale = 2)
    private BigDecimal frankedPercentage;

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public DividendType getDividendType() {
        return dividendType;
    }

    public void setDividendType(DividendType dividendType) {
        this.dividendType = dividendType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getExDividendDate() {
        return exDividendDate;
    }

    public void setExDividendDate(Date exDividendDate) {
        this.exDividendDate = exDividendDate;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public BigDecimal getFrankedPercentage() {
        return frankedPercentage;
    }

    public void setFrankedPercentage(BigDecimal frankedPercentage) {
        this.frankedPercentage = frankedPercentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dividend)) return false;

        Dividend dividend = (Dividend) o;

        if (!exDividendDate.equals(dividend.exDividendDate)) return false;
        if (!security.equals(dividend.security)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = security.hashCode();
        result = 31 * result + exDividendDate.hashCode();
        return result;
    }
}
