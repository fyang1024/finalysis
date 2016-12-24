package com.finalysis.research.reality;

import com.finalysis.research.BaseMortalEntity;

import javax.persistence.*;

@Entity(name = "GICS_INDUSTRY" )
public class GicsIndustry extends BaseMortalEntity {

    private String code;

    private String name;

    @ManyToOne
    private GicsIndustryGroup gicsIndustryGroup;

    public GicsIndustry() {
    }

    public GicsIndustry(String code, String name, GicsIndustryGroup gicsIndustryGroup) {
        this.code = code;
        this.name = name;
        this.gicsIndustryGroup = gicsIndustryGroup;
    }

    public GicsIndustryGroup getGicsIndustryGroup() {
        return gicsIndustryGroup;
    }

    public void setGicsIndustryGroup(GicsIndustryGroup gicsIndustryGroup) {
        this.gicsIndustryGroup = gicsIndustryGroup;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GicsIndustry)) return false;

        GicsIndustry that = (GicsIndustry) o;

        if (!code.equals(that.code)) return false;
        if (!gicsIndustryGroup.equals(that.gicsIndustryGroup)) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = code.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + gicsIndustryGroup.hashCode();
        return result;
    }
}
