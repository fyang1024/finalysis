package com.finalysis.research.virtuality;

import com.finalysis.research.BaseEntity;
import com.finalysis.research.reality.Exchange;

import javax.persistence.*;
import java.util.Date;

@Entity
public class SecurityCodeChange extends BaseEntity {

    @ManyToOne
    private Exchange exchange;

    @ManyToOne
    private Security security;

    private String oldCode;

    private String newCode;

    private String oldDescription;

    private String newDescription;

    @Temporal(TemporalType.DATE)
    private Date changeDate;

    @Transient
    private SecurityCodeChange previous;

    public SecurityCodeChange() {
    }

    public SecurityCodeChange(Exchange exchange, String oldCode, String newCode, String oldDescription, String newDescription, Date changeDate) {
        this.exchange = exchange;
        this.oldCode = oldCode;
        this.newCode = newCode;
        this.oldDescription = oldDescription;
        this.newDescription = newDescription;
        this.changeDate = changeDate;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public String getOldCode() {
        return oldCode;
    }

    public void setOldCode(String oldCode) {
        this.oldCode = oldCode;
    }

    public String getNewCode() {
        return newCode;
    }

    public void setNewCode(String newCode) {
        this.newCode = newCode;
    }

    public String getOldDescription() {
        return oldDescription;
    }

    public void setOldDescription(String oldDescription) {
        this.oldDescription = oldDescription;
    }

    public String getNewDescription() {
        return newDescription;
    }

    public void setNewDescription(String newDescription) {
        this.newDescription = newDescription;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public Exchange getExchange() {
        return exchange;
    }

    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    public SecurityCodeChange getPrevious() {
        return previous;
    }

    public void setPrevious(SecurityCodeChange previous) {
        this.previous = previous;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecurityCodeChange)) return false;

        SecurityCodeChange that = (SecurityCodeChange) o;

        if (!changeDate.equals(that.changeDate)) return false;
        if (!exchange.equals(that.exchange)) return false;
        if (!newCode.equals(that.newCode)) return false;
        if (!newDescription.equals(that.newDescription)) return false;
        if (!oldCode.equals(that.oldCode)) return false;
        if (!oldDescription.equals(that.oldDescription)) return false;
        if (security != null ? !security.equals(that.security) : that.security != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = exchange.hashCode();
        result = 31 * result + (security != null ? security.hashCode() : 0);
        result = 31 * result + oldCode.hashCode();
        result = 31 * result + newCode.hashCode();
        result = 31 * result + oldDescription.hashCode();
        result = 31 * result + newDescription.hashCode();
        result = 31 * result + changeDate.hashCode();
        return result;
    }
}
