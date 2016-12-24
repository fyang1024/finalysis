package com.finalysis.research.virtuality;

import com.finalysis.research.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class FinancialHistory extends BaseEntity {

    @ManyToOne
    private Security security;

    private Long outstandingShares;

    private Long outstandingOptions;

    @Temporal(TemporalType.DATE)
    private Date periodEnding;

    private BigDecimal revenue;

    private BigDecimal operatingMargin;

    private BigDecimal depreciation;

    private BigDecimal amortisation;

    private BigDecimal incomeTaxRate;

    private BigDecimal netProfit;

    private Integer numberOfEmployees;

    private BigDecimal capitalSpending;

    private BigDecimal cashFlow;

    private BigDecimal bookValue;

    private BigDecimal cashAsset;

    private BigDecimal receivables;

    private BigDecimal inventory;

    private BigDecimal otherCurrentAsset;

    private BigDecimal accountPayable;

    private BigDecimal debtDue;

    private BigDecimal otherCurrentLiability;

    private BigDecimal longTermDebt;

    private BigDecimal shareholdersEquity;

    private BigDecimal roc;

    private BigDecimal roe;

    private BigDecimal roi;

    private BigDecimal roa;

    private BigDecimal ebitda;

    private BigDecimal ebit;

    private BigDecimal preferredSharesValue;

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public Date getPeriodEnding() {
        return periodEnding;
    }

    public void setPeriodEnding(Date periodEnding) {
        this.periodEnding = periodEnding;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public BigDecimal getOperatingMargin() {
        return operatingMargin;
    }

    public void setOperatingMargin(BigDecimal operatingMargin) {
        this.operatingMargin = operatingMargin;
    }

    public BigDecimal getDepreciation() {
        return depreciation;
    }

    public void setDepreciation(BigDecimal depreciation) {
        this.depreciation = depreciation;
    }

    public BigDecimal getAmortisation() {
        return amortisation;
    }

    public void setAmortisation(BigDecimal amortisation) {
        this.amortisation = amortisation;
    }

    public BigDecimal getIncomeTaxRate() {
        return incomeTaxRate;
    }

    public void setIncomeTaxRate(BigDecimal incomeTaxRate) {
        this.incomeTaxRate = incomeTaxRate;
    }

    public BigDecimal getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(BigDecimal netProfit) {
        this.netProfit = netProfit;
    }

    public Integer getNumberOfEmployees() {
        return numberOfEmployees;
    }

    public void setNumberOfEmployees(Integer numberOfEmployees) {
        this.numberOfEmployees = numberOfEmployees;
    }

    public BigDecimal getCapitalSpending() {
        return capitalSpending;
    }

    public void setCapitalSpending(BigDecimal capitalSpending) {
        this.capitalSpending = capitalSpending;
    }

    public BigDecimal getCashFlow() {
        return cashFlow;
    }

    public void setCashFlow(BigDecimal cashFlow) {
        this.cashFlow = cashFlow;
    }

    public BigDecimal getBookValue() {
        return bookValue;
    }

    public void setBookValue(BigDecimal bookValue) {
        this.bookValue = bookValue;
    }

    public BigDecimal getCashAsset() {
        return cashAsset;
    }

    public void setCashAsset(BigDecimal cashAsset) {
        this.cashAsset = cashAsset;
    }

    public BigDecimal getReceivables() {
        return receivables;
    }

    public void setReceivables(BigDecimal receivables) {
        this.receivables = receivables;
    }

    public BigDecimal getInventory() {
        return inventory;
    }

    public void setInventory(BigDecimal inventory) {
        this.inventory = inventory;
    }

    public BigDecimal getOtherCurrentAsset() {
        return otherCurrentAsset;
    }

    public void setOtherCurrentAsset(BigDecimal otherCurrentAsset) {
        this.otherCurrentAsset = otherCurrentAsset;
    }

    public BigDecimal getAccountPayable() {
        return accountPayable;
    }

    public void setAccountPayable(BigDecimal accountPayable) {
        this.accountPayable = accountPayable;
    }

    public BigDecimal getDebtDue() {
        return debtDue;
    }

    public void setDebtDue(BigDecimal debtDue) {
        this.debtDue = debtDue;
    }

    public BigDecimal getOtherCurrentLiability() {
        return otherCurrentLiability;
    }

    public void setOtherCurrentLiability(BigDecimal otherCurrentLiability) {
        this.otherCurrentLiability = otherCurrentLiability;
    }

    public BigDecimal getLongTermDebt() {
        return longTermDebt;
    }

    public void setLongTermDebt(BigDecimal longTermDebt) {
        this.longTermDebt = longTermDebt;
    }

    public BigDecimal getShareholdersEquity() {
        return shareholdersEquity;
    }

    public void setShareholdersEquity(BigDecimal shareholdersEquity) {
        this.shareholdersEquity = shareholdersEquity;
    }

    public BigDecimal getRoc() {
        return roc;
    }

    public void setRoc(BigDecimal roc) {
        this.roc = roc;
    }

    public BigDecimal getRoe() {
        return roe;
    }

    public void setRoe(BigDecimal roe) {
        this.roe = roe;
    }

    public BigDecimal getRoi() {
        return roi;
    }

    public void setRoi(BigDecimal roi) {
        this.roi = roi;
    }

    public BigDecimal getRoa() {
        return roa;
    }

    public void setRoa(BigDecimal roa) {
        this.roa = roa;
    }

    public BigDecimal getEbitda() {
        return ebitda;
    }

    public void setEbitda(BigDecimal ebitda) {
        this.ebitda = ebitda;
    }

    public BigDecimal getEbit() {
        return ebit;
    }

    public void setEbit(BigDecimal ebit) {
        this.ebit = ebit;
    }

    public BigDecimal getPreferredSharesValue() {
        return preferredSharesValue;
    }

    public void setPreferredSharesValue(BigDecimal preferredSharesValue) {
        this.preferredSharesValue = preferredSharesValue;
    }

    public Long getOutstandingShares() {
        return outstandingShares;
    }

    public void setOutstandingShares(Long outstandingShares) {
        this.outstandingShares = outstandingShares;
    }

    public Long getOutstandingOptions() {
        return outstandingOptions;
    }

    public void setOutstandingOptions(Long outstandingOptions) {
        this.outstandingOptions = outstandingOptions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FinancialHistory)) return false;

        FinancialHistory that = (FinancialHistory) o;

        if (!periodEnding.equals(that.periodEnding)) return false;
        if (!security.equals(that.security)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = security.hashCode();
        result = 31 * result + periodEnding.hashCode();
        return result;
    }
}
