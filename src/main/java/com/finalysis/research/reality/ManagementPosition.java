package com.finalysis.research.reality;

import com.finalysis.research.BaseMortalEntity;

import javax.persistence.*;

@Entity
public class ManagementPosition extends BaseMortalEntity {

    @ManyToOne
    private Person person;

    @ManyToOne
    private Company company;

    @ManyToOne
    private ManagementRole managementRole;

    public ManagementPosition() {
    }

    public ManagementPosition(Person person, Company company, ManagementRole managementRole) {
        this.person = person;
        this.company = company;
        this.managementRole = managementRole;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public ManagementRole getManagementRole() {
        return managementRole;
    }

    public void setManagementRole(ManagementRole managementRole) {
        this.managementRole = managementRole;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ManagementPosition)) return false;

        ManagementPosition that = (ManagementPosition) o;

        if (!company.equals(that.company)) return false;
        if (!managementRole.equals(that.managementRole)) return false;
        if (!person.equals(that.person)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = person.hashCode();
        result = 31 * result + company.hashCode();
        result = 31 * result + managementRole.hashCode();
        return result;
    }
}
