package com.finalysis.research.reality;

import com.finalysis.research.BaseMortalEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Company extends BaseMortalEntity {

    private String name;

    @ManyToOne
    private GicsSector gicsSector;

    @ManyToOne
    private GicsIndustryGroup gicsIndustryGroup;

    private String website;

    private String registeredOfficeAddress;

    private String headOfficeTelephone;

    private String headOfficeFax;

    @Column(length = 1023)
    private String principalActivities;

    public Company() {
    }

    public Company(String name, GicsIndustryGroup gicsIndustryGroup) {
        this.name = name;
        this.gicsIndustryGroup = gicsIndustryGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GicsIndustryGroup getGicsIndustryGroup() {
        return gicsIndustryGroup;
    }

    public void setGicsIndustryGroup(GicsIndustryGroup gicsIndustryGroup) {
        this.gicsSector = gicsIndustryGroup == null ? null : gicsIndustryGroup.getGicsSector();
        this.gicsIndustryGroup = gicsIndustryGroup;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getRegisteredOfficeAddress() {
        return registeredOfficeAddress;
    }

    public void setRegisteredOfficeAddress(String registeredOfficeAddress) {
        this.registeredOfficeAddress = registeredOfficeAddress;
    }

    public String getHeadOfficeTelephone() {
        return headOfficeTelephone;
    }

    public void setHeadOfficeTelephone(String headOfficeTelephone) {
        this.headOfficeTelephone = headOfficeTelephone;
    }

    public String getHeadOfficeFax() {
        return headOfficeFax;
    }

    public void setHeadOfficeFax(String headOfficeFax) {
        this.headOfficeFax = headOfficeFax;
    }

    public String getPrincipalActivities() {
        return principalActivities;
    }

    public void setPrincipalActivities(String principalActivities) {
        this.principalActivities = principalActivities;
    }

    public GicsSector getGicsSector() {
        return gicsSector;
    }

    public void setGicsSector(GicsSector gicsSector) {
        this.gicsSector = gicsSector;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Company)) return false;

        Company company = (Company) o;

        if (gicsSector != null ? !gicsSector.equals(company.gicsSector) : company.gicsSector != null)
            return false;
        if (gicsIndustryGroup != null ? !gicsIndustryGroup.equals(company.gicsIndustryGroup) : company.gicsIndustryGroup != null)
            return false;
        if (headOfficeFax != null ? !headOfficeFax.equals(company.headOfficeFax) : company.headOfficeFax != null)
            return false;
        if (headOfficeTelephone != null ? !headOfficeTelephone.equals(company.headOfficeTelephone) : company.headOfficeTelephone != null)
            return false;
        if (!name.equals(company.name)) return false;
        if (principalActivities != null ? !principalActivities.equals(company.principalActivities) : company.principalActivities != null)
            return false;
        if (registeredOfficeAddress != null ? !registeredOfficeAddress.equals(company.registeredOfficeAddress) : company.registeredOfficeAddress != null)
            return false;
        if (website != null ? !website.equals(company.website) : company.website != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (gicsSector != null ? gicsSector.hashCode() : 0);
        result = 31 * result + (gicsIndustryGroup != null ? gicsIndustryGroup.hashCode() : 0);
        result = 31 * result + (website != null ? website.hashCode() : 0);
        result = 31 * result + (registeredOfficeAddress != null ? registeredOfficeAddress.hashCode() : 0);
        result = 31 * result + (headOfficeTelephone != null ? headOfficeTelephone.hashCode() : 0);
        result = 31 * result + (headOfficeFax != null ? headOfficeFax.hashCode() : 0);
        result = 31 * result + (principalActivities != null ? principalActivities.hashCode() : 0);
        return result;
    }
}
