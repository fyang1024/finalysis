package com.finalysis.research.virtuality;

import com.finalysis.research.BaseEntity;
import com.finalysis.research.reality.Exchange;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
public class ReportedShortSell extends BaseEntity {

    private String securityCode;

    private String companyName;

    @ManyToOne
    private Exchange exchange;

    @ManyToOne
    private Security security;

    private Long reportedGrossShorts;

    private Long issuedShares;

    @Temporal(TemporalType.DATE)
    private Date tradingDate;

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Exchange getExchange() {
        return exchange;
    }

    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    public Long getReportedGrossShorts() {
        return reportedGrossShorts;
    }

    public void setReportedGrossShorts(Long reportedGrossShorts) {
        this.reportedGrossShorts = reportedGrossShorts;
    }

    public Long getIssuedShares() {
        return issuedShares;
    }

    public void setIssuedShares(Long issuedShares) {
        this.issuedShares = issuedShares;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public Date getTradingDate() {
        return tradingDate;
    }

    public void setTradingDate(Date tradingDate) {
        this.tradingDate = tradingDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReportedShortSell)) return false;

        ReportedShortSell that = (ReportedShortSell) o;

        if (security != null ? !security.equals(that.security) : that.security != null) return false;
        if (!securityCode.equals(that.securityCode)) return false;
        if (!tradingDate.equals(that.tradingDate)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = securityCode.hashCode();
        result = 31 * result + (security != null ? security.hashCode() : 0);
        result = 31 * result + tradingDate.hashCode();
        return result;
    }
}
