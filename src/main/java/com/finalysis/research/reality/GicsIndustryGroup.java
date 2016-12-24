package com.finalysis.research.reality;

import com.finalysis.research.BaseMortalEntity;

import javax.persistence.*;

@Entity(name = "GICS_INDUSTRY_GROUP")
public class GicsIndustryGroup extends BaseMortalEntity {

    @Column(name = "CODE")
    private String code;

    @Column(name = "NAME")
    private String name;

    @ManyToOne
    @JoinColumn (name = "GICS_SECTOR")
    private GicsSector gicsSector;

    public GicsIndustryGroup() {
    }

    public GicsIndustryGroup(String code, String name, GicsSector gicsSector) {
        this.code = code;
        this.name = name;
        this.gicsSector = gicsSector;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        if (!(o instanceof GicsIndustryGroup)) return false;

        GicsIndustryGroup that = (GicsIndustryGroup) o;

        if (!code.equals(that.code)) return false;
        if (!gicsSector.equals(that.gicsSector)) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = code.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + gicsSector.hashCode();
        return result;
    }
}
