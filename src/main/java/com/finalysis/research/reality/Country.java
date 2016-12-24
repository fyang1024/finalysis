package com.finalysis.research.reality;

import com.finalysis.research.BaseMortalEntity;

import javax.persistence.Entity;

@Entity
public class Country extends BaseMortalEntity {

    private String isoTwoLetterCode;

    private String isoThreeLetterCode;

    private String commonName;

    private String formalName;

    private String isoCurrencyCode;

    private String isoCurrencyName;

    private String ianaCountryCode;

    private String flagUrl;

    public String getIsoTwoLetterCode() {
        return isoTwoLetterCode;
    }

    public void setIsoTwoLetterCode(String isoTwoLetterCode) {
        this.isoTwoLetterCode = isoTwoLetterCode;
    }

    public String getIsoThreeLetterCode() {
        return isoThreeLetterCode;
    }

    public void setIsoThreeLetterCode(String isoThreeLetterCode) {
        this.isoThreeLetterCode = isoThreeLetterCode;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getFormalName() {
        return formalName;
    }

    public void setFormalName(String formalName) {
        this.formalName = formalName;
    }

    public String getIsoCurrencyCode() {
        return isoCurrencyCode;
    }

    public void setIsoCurrencyCode(String isoCurrencyCode) {
        this.isoCurrencyCode = isoCurrencyCode;
    }

    public String getIsoCurrencyName() {
        return isoCurrencyName;
    }

    public void setIsoCurrencyName(String isoCurrencyName) {
        this.isoCurrencyName = isoCurrencyName;
    }

    public String getIanaCountryCode() {
        return ianaCountryCode;
    }

    public void setIanaCountryCode(String ianaCountryCode) {
        this.ianaCountryCode = ianaCountryCode;
    }

    public String getFlagUrl() {
        return flagUrl;
    }

    public void setFlagUrl(String flagUrl) {
        this.flagUrl = flagUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Country)) return false;

        Country country = (Country) o;

        if (!ianaCountryCode.equals(country.ianaCountryCode)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return ianaCountryCode.hashCode();
    }
}
