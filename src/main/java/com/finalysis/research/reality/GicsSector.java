package com.finalysis.research.reality;

import com.finalysis.research.BaseMortalEntity;

import javax.persistence.Entity;

@Entity (name = "GICS_SECTOR")
public class GicsSector extends BaseMortalEntity {

    private String code;

    private String name;

    public GicsSector() {
    }

    public GicsSector(String code, String name) {
        this.code = code;
        this.name = name;
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
        if (!(o instanceof GicsSector)) return false;

        GicsSector that = (GicsSector) o;

        if (!code.equals(that.code)) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = code.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
