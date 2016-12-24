package com.finalysis.research.virtuality;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import com.finalysis.research.reality.*;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

@Component
public class AsxOrdinaryShareLoader implements OrdinaryShareLoader {

    private Logger logger = LoggerFactory.getLogger(AsxOrdinaryShareLoader.class);

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private GicsIndustryGroupRepository gicsIndustryGroupRepository;

    @Autowired
    private SecurityCodeChangeRepository securityCodeChangeRepository;

    @Autowired
    private SecurityTypeRepository securityTypeRepository;

    @Autowired
    private SecurityRepository securityRepository;

    public void loadOrdinaryShares(Exchange exchange) {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        try {
            SecurityType ordinaryShareType = securityTypeRepository.findByName("Ordinary Share");
            InputStreamReader inputStreamReader = new InputStreamReader(webClient.getPage(exchange.getListedOrdinaryShareUrl()).getWebResponse().getContentAsStream());
            CSVReader csvReader = new CSVReader(inputStreamReader, CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, 3);
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                String description = line[0].toUpperCase();
                if(description.contains("TRUST") && description.contains("SERIES")
                        || description.contains("MASTERFUND") || description.contains("PUMA SERIES")) {
                    logger.info("Skipped " + line[0] + " | " + line[1] + " | " + line[2]);
                    continue;
                }
                logger.info(line[0] + " | " + line[1] + " | " + line[2]);
                Security security = securityRepository.findByCodeAndExchange(line[1], exchange, new Date());
                if (security == null) {
                    SecurityCodeChange codeChange = findLastCodeChange(line[1], exchange);
                    if (codeChange != null) {
                        security = securityRepository.findByCodeAndExchange(codeChange.getOldCode(), exchange, codeChange.getChangeDate());
                    }
                }
                GicsIndustryGroup gicsIndustryGroup = gicsIndustryGroupRepository.findByName(line[2]);
                if (security == null) {
                    logger.info("Could not find it and creating it...");
                    Company company = new Company(line[0], gicsIndustryGroup);
                    company = companyRepository.saveAndFlush(company);
                    security = new Security(line[1], company, exchange);
                    security.setSecurityType(ordinaryShareType);
                    securityRepository.saveAndFlush(security);
                } else if (!security.getCode().equalsIgnoreCase(line[1]) || !security.getDescription().equalsIgnoreCase(line[0])
                        || gicsIndustryGroup != null && !gicsIndustryGroup.equals(security.getCompany().getGicsIndustryGroup()) ||
                        gicsIndustryGroup == null && security.getCompany().getGicsIndustryGroup() != null) {
                    logger.info("Found it and updating " + security.getCode());
                    Company company = security.getCompany();
                    if (!company.getName().equalsIgnoreCase(line[0]) ||
                            gicsIndustryGroup != null && !gicsIndustryGroup.equals(company.getGicsIndustryGroup()) ||
                            gicsIndustryGroup == null && company.getGicsIndustryGroup() != null) {
                        company.setName(line[0]);
                        company.setGicsIndustryGroup(gicsIndustryGroup);
                        companyRepository.saveAndFlush(company);
                    }
                    security.setCode(line[1]);
                    security.setDescription(line[0]);
                    securityRepository.saveAndFlush(security);
                } else {
                    logger.info("Found it");
                }
            }
            csvReader.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            webClient.closeAllWindows();
        }
        logger.info("--Done--");
    }

    private SecurityCodeChange findLastCodeChange(String newCode, Exchange exchange) {
        List<SecurityCodeChange> codeChanges = securityCodeChangeRepository.findCodeChangesByNewCode(exchange, newCode);
        if (!codeChanges.isEmpty()) {
            return codeChanges.get(0);
        }
        return null;
    }
}
