package com.finalysis.research.reality;

import com.finalysis.research.BaseMortalEntity;

import javax.persistence.Entity;

@Entity
public class Person extends BaseMortalEntity {

    private String givenName;

    private String surname;

    public Person() {
    }

    public Person(String givenName, String surname) {
        this.givenName = givenName;
        this.surname = surname;
    }

    public Person(String titledFullName) {
        String fullName = titledFullName.replaceAll("Mr |Ms |Mrs |Dr |Miss |Prof |Mr. |Ms. |Mrs. |Dr. |Miss |Prof. ", "").trim();
        int i = fullName.lastIndexOf(' ');
        if (i != -1) {
            this.givenName = fullName.substring(0, i);
            this.surname = fullName.substring(i + 1);
        } else {
            this.surname = fullName;
        }
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;

        Person person = (Person) o;

        if (!givenName.equals(person.givenName)) return false;
        if (!surname.equals(person.surname)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = givenName.hashCode();
        result = 31 * result + surname.hashCode();
        return result;
    }
}
