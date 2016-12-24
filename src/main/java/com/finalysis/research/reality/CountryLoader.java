package com.finalysis.research.reality;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class CountryLoader {

    Logger logger = LoggerFactory.getLogger(CountryLoader.class);

    @Autowired
    private CountryRepository countryRepository;

    private String source = "/iso_3166_2_countries.csv";

    public void test() {
        logger.info("test");
    }

    public Iterable<Country> loadCountries() throws IOException {
        List<Country> countries = new ArrayList<>();
        InputStreamReader inputStreamReader = new InputStreamReader(getClass().getResourceAsStream(source));
        CSVReader csvReader = new CSVReader(inputStreamReader, CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, 1);
        String[] line;
        while ((line = csvReader.readNext()) != null) {
            if(countryRepository.findByCommonName(line[1]) == null) {
                countries.add(buildCountry(line));
            }
        }
        return countryRepository.save(countries);
    }

    private Country buildCountry(String[] line) {
        Country country = new Country();
        country.setCommonName(line[1]);
        country.setFormalName(line[2]);
        country.setIsoCurrencyCode(line[7]);
        country.setIsoCurrencyName(line[8]);
        country.setIsoTwoLetterCode(line[10]);
        country.setIsoThreeLetterCode(line[11]);
        country.setIanaCountryCode(line[13]);
        return country;
    }
}
