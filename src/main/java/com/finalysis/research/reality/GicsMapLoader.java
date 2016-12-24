package com.finalysis.research.reality;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class GicsMapLoader {

    @Autowired
    private GicsSectorRepository gicsSectorRepository;

    @Autowired
    private GicsIndustryGroupRepository gicsIndustryGroupRepository;

    @Autowired
    private GicsIndustryRepository gicsIndustryRepository;

    @Autowired
    private GicsSubIndustryRepository gicsSubIndustryRepository;

    private String source = "/GICS_map2014.csv";

    public void loadGICSMap() throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(getClass().getResourceAsStream(source));
        CSVReader csvReader = new CSVReader(inputStreamReader, CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, 5);
        String[] line;
        GicsSector currentGicsSector = null;
        GicsIndustryGroup currentGicsIndustryGroup = null;
        GicsIndustry currentGicsIndustry = null;
        GicsSubIndustry currentGicsSubIndustry = null;
        while ((line = csvReader.readNext()) != null) {
            if (!StringUtils.isBlank(line[0])) {
                currentGicsSector = gicsSectorRepository.findByCodeAndName(line[0], line[1]);
                if (currentGicsSector == null) {
                    currentGicsSector = new GicsSector(line[0], line[1]);
                    currentGicsSector = gicsSectorRepository.save(currentGicsSector);
                }
            }
            if (!StringUtils.isBlank(line[2])) {
                currentGicsIndustryGroup = gicsIndustryGroupRepository.findByCodeAndName(line[2], line[3]);
                if (currentGicsIndustryGroup == null) {
                    currentGicsIndustryGroup = new GicsIndustryGroup(line[2], line[3], currentGicsSector);
                    currentGicsIndustryGroup = gicsIndustryGroupRepository.save(currentGicsIndustryGroup);
                }
            }
            if (!StringUtils.isBlank(line[4])) {
                currentGicsIndustry = gicsIndustryRepository.findByCodeAndName(line[4], line[5]);
                if (currentGicsIndustry == null) {
                    currentGicsIndustry = new GicsIndustry(line[4], line[5], currentGicsIndustryGroup);
                    currentGicsIndustry = gicsIndustryRepository.save(currentGicsIndustry);
                }
            }
            if (!StringUtils.isBlank(line[6])) {
                currentGicsSubIndustry = gicsSubIndustryRepository.findByCodeAndName(line[6], line[7]);
                if (currentGicsSubIndustry == null) {
                    currentGicsSubIndustry = new GicsSubIndustry(line[6], line[7], currentGicsIndustry);
                    currentGicsSubIndustry = gicsSubIndustryRepository.save(currentGicsSubIndustry);
                }
            } else {
                if (currentGicsSubIndustry != null && currentGicsSubIndustry.getDescription() == null) {
                    currentGicsSubIndustry.setDescription(StringUtils.trimToNull(line[7]));
                    gicsSubIndustryRepository.save(currentGicsSubIndustry);
                }
            }
        }


    }
}
