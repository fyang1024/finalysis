package com.finalysis.research.reality;

import com.finalysis.research.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class CompanyIndustry extends BaseEntity {

    @ManyToOne
    private Company company;

    @ManyToOne
    private GicsIndustry gicsIndustry;

    @ManyToOne
    private GicsSubIndustry gicsSubIndustry;

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public GicsIndustry getGicsIndustry() {
        return gicsIndustry;
    }

    public void setGicsIndustry(GicsIndustry gicsIndustry) {
        this.gicsIndustry = gicsIndustry;
    }

    public GicsSubIndustry getGicsSubIndustry() {
        return gicsSubIndustry;
    }

    public void setGicsSubIndustry(GicsSubIndustry gicsSubIndustry) {
        this.gicsSubIndustry = gicsSubIndustry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompanyIndustry)) return false;

        CompanyIndustry that = (CompanyIndustry) o;

        if (!company.equals(that.company)) return false;
        if (!gicsIndustry.equals(that.gicsIndustry)) return false;
        if (gicsSubIndustry != null ? !gicsSubIndustry.equals(that.gicsSubIndustry) : that.gicsSubIndustry != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = company.hashCode();
        result = 31 * result + gicsIndustry.hashCode();
        result = 31 * result + (gicsSubIndustry != null ? gicsSubIndustry.hashCode() : 0);
        return result;
    }
}
