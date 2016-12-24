package com.finalysis.research.reality;

import com.finalysis.research.BaseMortalEntity;

import javax.persistence.*;

@Entity
public class GicsSubIndustry extends BaseMortalEntity {

    private String code;

    private String name;

    @Column (length = 1023)
    private String description;

    @ManyToOne
    private GicsIndustry gicsIndustry;

    public GicsSubIndustry() {
    }

    public GicsSubIndustry(String code, String name, GicsIndustry gicsIndustry) {
        this.code = code;
        this.name = name;
        this.gicsIndustry = gicsIndustry;
    }

    public GicsIndustry getGicsIndustry() {
        return gicsIndustry;
    }

    public void setGicsIndustry(GicsIndustry gicsIndustry) {
        this.gicsIndustry = gicsIndustry;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GicsSubIndustry)) return false;

        GicsSubIndustry that = (GicsSubIndustry) o;

        if (!code.equals(that.code)) return false;
        if (!gicsIndustry.equals(that.gicsIndustry)) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = code.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + gicsIndustry.hashCode();
        return result;
    }
}
