package com.finalysis.research.reality;

import com.finalysis.research.BaseMortalEntity;

import javax.persistence.Entity;

@Entity
public class ManagementRole extends BaseMortalEntity {

    private String name;

    public ManagementRole() {
    }

    public ManagementRole(String name) {
        this.name = name;
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
        if (!(o instanceof ManagementRole)) return false;

        ManagementRole that = (ManagementRole) o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
