package com.finalysis.research.virtuality;

import com.finalysis.research.BaseMortalEntity;
import com.finalysis.research.reality.Company;
import com.finalysis.research.reality.Exchange;
import com.finalysis.research.reality.ShareRegistry;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class Security extends BaseMortalEntity {

    protected String code;

    protected String description;

    protected String isin;

    @ManyToOne
    protected Exchange exchange;

    @ManyToOne
    protected SecurityType securityType;

    @ManyToOne (cascade = CascadeType.REMOVE)
    private Company company;

    @Temporal(TemporalType.DATE)
    private Date listingDate;

    @Temporal(TemporalType.DATE)
    private Date delistedDate;

    @Column(length = 4000)
    private String delistedReason;

    @Temporal(TemporalType.DATE)
    private Date registeredDate;

    @Temporal(TemporalType.DATE)
    private Date deregisteredDate;

    @Temporal(TemporalType.DATE)
    private Date mergedDate;

    @Temporal(TemporalType.DATE)
    private Date demergedDate;

    private Boolean exemptForeign;

    @ManyToOne
    private ShareRegistry shareRegistry;

    private String benchmark;

    private BigDecimal managementExpenseRatio;

    private BigDecimal ipoPrice;

    public Security() {
    }

    public Security(String code, Exchange exchange) {
        this.code = code;
        this.exchange = exchange;
    }

    public Security(String code, Company company, Exchange exchange) {
        this(code, exchange);
        this.company = company;
        setDescription(company.getName());
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Exchange getExchange() {
        return exchange;
    }

    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    public SecurityType getSecurityType() {
        return securityType;
    }

    public void setSecurityType(SecurityType securityType) {
        this.securityType = securityType;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Date getListingDate() {
        return listingDate;
    }

    public void setListingDate(Date listingDate) {
        this.listingDate = listingDate;
    }

    public Date getDelistedDate() {
        return delistedDate;
    }

    public void setDelistedDate(Date delistedDate) {
        this.delistedDate = delistedDate;
    }

    public String getDelistedReason() {
        return delistedReason;
    }

    public void setDelistedReason(String delistedReason) {
        this.delistedReason = delistedReason;
    }

    public Date getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(Date registeredDate) {
        this.registeredDate = registeredDate;
    }

    public Date getDeregisteredDate() {
        return deregisteredDate;
    }

    public void setDeregisteredDate(Date deregisteredDate) {
        this.deregisteredDate = deregisteredDate;
    }

    public Date getMergedDate() {
        return mergedDate;
    }

    public void setMergedDate(Date mergedDate) {
        this.mergedDate = mergedDate;
    }

    public Date getDemergedDate() {
        return demergedDate;
    }

    public void setDemergedDate(Date demergedDate) {
        this.demergedDate = demergedDate;
    }

    public Boolean getExemptForeign() {
        return exemptForeign;
    }

    public void setExemptForeign(Boolean exemptForeign) {
        this.exemptForeign = exemptForeign;
    }

    public ShareRegistry getShareRegistry() {
        return shareRegistry;
    }

    public void setShareRegistry(ShareRegistry shareRegistry) {
        this.shareRegistry = shareRegistry;
    }

    public String getBenchmark() {
        return benchmark;
    }

    public void setBenchmark(String benchmark) {
        this.benchmark = benchmark;
    }

    public BigDecimal getManagementExpenseRatio() {
        return managementExpenseRatio;
    }

    public void setManagementExpenseRatio(BigDecimal managementExpenseRatio) {
        this.managementExpenseRatio = managementExpenseRatio;
    }

    public BigDecimal getIpoPrice() {
        return ipoPrice;
    }

    public void setIpoPrice(BigDecimal ipoPrice) {
        this.ipoPrice = ipoPrice;
    }

    public String getGicsSector() {
        if(company != null && company.getGicsSector() != null) {
            return company.getGicsSector().getName();
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Security)) return false;

        Security security = (Security) o;

        if (!code.equals(security.code)) return false;
        if (company != null ? !company.equals(security.company) : security.company != null) return false;
        if (!exchange.equals(security.exchange)) return false;
        if (isin != null ? !isin.equals(security.isin) : security.isin != null) return false;
        if (listingDate != null ? !listingDate.equals(security.listingDate) : security.listingDate != null)
            return false;
        if (!securityType.equals(security.securityType)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = code.hashCode();
        result = 31 * result + (isin != null ? isin.hashCode() : 0);
        result = 31 * result + exchange.hashCode();
        result = 31 * result + securityType.hashCode();
        result = 31 * result + (company != null ? company.hashCode() : 0);
        result = 31 * result + (listingDate != null ? listingDate.hashCode() : 0);
        return result;
    }

}
