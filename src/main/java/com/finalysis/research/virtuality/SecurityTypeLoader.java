package com.finalysis.research.virtuality;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class SecurityTypeLoader {
    private String source = "/security_types.csv";

    @Autowired
    private SecurityCategoryRepository securityCategoryRepository;

    @Autowired
    private SecurityTypeRepository securityTypeRepository;

    public void loadSecurityTypes() throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(getClass().getResourceAsStream(source));
        CSVReader csvReader = new CSVReader(inputStreamReader, CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, 1);
        String[] line;
        while ((line = csvReader.readNext()) != null) {
            SecurityType securityType = securityTypeRepository.findByName(line[0]);
            if(securityType == null) {
                createSecurityType(line[0], line[1]);
            }
        }
    }

    private SecurityType createSecurityType(String typeName, String categoryName) {
        SecurityType securityType = new SecurityType(typeName, findOrCreateSecurityCategory(categoryName));
        return securityTypeRepository.save(securityType);
    }

    private SecurityCategory findOrCreateSecurityCategory(String categoryName) {
        SecurityCategory securityCategory = securityCategoryRepository.findByName(categoryName);
        if(securityCategory == null) {
            securityCategory = securityCategoryRepository.save(new SecurityCategory(categoryName));
        }
        return securityCategory;
    }


}
