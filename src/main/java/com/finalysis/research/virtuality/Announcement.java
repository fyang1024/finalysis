package com.finalysis.research.virtuality;

import com.finalysis.research.BaseEntity;
import com.finalysis.research.reality.Exchange;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Announcement extends BaseEntity {
    @ManyToOne
    private Exchange exchange;

    @ManyToOne
    private Security security;

    @Temporal(TemporalType.DATE)
    private Date announcementDate;

    private String code;

    private boolean priceSensitive;

    private String headline;

    private Integer pages;

    @Column(length = 4000)
    private String fileNames;

    public Announcement() {
    }

    public Announcement(Exchange exchange, Security security, String code, Date announcementDate, boolean priceSensitive, String headline) {
        this.exchange = exchange;
        this.security = security;
        this.code = code;
        this.announcementDate = announcementDate;
        this.priceSensitive = priceSensitive;
        this.headline = headline;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public Date getAnnouncementDate() {
        return announcementDate;
    }

    public void setAnnouncementDate(Date announcementDate) {
        this.announcementDate = announcementDate;
    }

    public boolean isPriceSensitive() {
        return priceSensitive;
    }

    public void setPriceSensitive(boolean priceSensitive) {
        this.priceSensitive = priceSensitive;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public String getFileNames() {
        return fileNames;
    }

    public void setFileNames(String fileNames) {
        this.fileNames = fileNames;
    }

    public Exchange getExchange() {
        return exchange;
    }

    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Announcement)) return false;

        Announcement that = (Announcement) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
