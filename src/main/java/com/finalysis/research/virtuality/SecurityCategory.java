package com.finalysis.research.virtuality;

import com.finalysis.research.BaseMortalEntity;

import javax.persistence.Entity;

@Entity
public class SecurityCategory extends BaseMortalEntity {

    private String name;

    public SecurityCategory() {
    }

    public SecurityCategory(String categoryName) {
        this.name = categoryName;
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
        if (!(o instanceof SecurityCategory)) return false;

        SecurityCategory that = (SecurityCategory) o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
