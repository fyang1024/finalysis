package com.finalysis.research.virtuality;

import com.finalysis.research.BaseMortalEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class SecurityType extends BaseMortalEntity {

    public static final String ORDINARY_SHARE = "Ordinary Share";

    private String name;

    @ManyToOne
    private SecurityCategory securityCategory;

    public SecurityType() {
    }

    public SecurityType(String typeName, SecurityCategory securityCategory) {
        setName(typeName);
        setSecurityCategory(securityCategory);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SecurityCategory getSecurityCategory() {
        return securityCategory;
    }

    public void setSecurityCategory(SecurityCategory securityCategory) {
        this.securityCategory = securityCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecurityType)) return false;

        SecurityType that = (SecurityType) o;

        if (!name.equals(that.name)) return false;
        if (!securityCategory.equals(that.securityCategory)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + securityCategory.hashCode();
        return result;
    }
}
