package com.finalysis.research.virtuality;

import com.finalysis.research.reality.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class SecurityFinder {

    @Autowired
    SecurityRepository securityRepository;

    @Autowired
    SecurityCodeChangeRepository securityCodeChangeRepository;

    public Security findSecurity(Exchange exchange, String code, Date tradingDate) {
        Security security = securityRepository.findByCodeAndExchange(code, exchange, tradingDate);
        if(security != null) {
            if (verify(security, code, tradingDate)) return security;
        }
        List<Security> possibleSecurities = securityCodeChangeRepository.findPossibleSecurities(code, exchange, tradingDate);
        if(security != null) {
            possibleSecurities.remove(security);
        }
        for(Security possibleSecurity : possibleSecurities) {
            if (verify(possibleSecurity, code, tradingDate)) return possibleSecurity;
        }
        return null;
    }

    private boolean verify(Security security, String code, Date tradingDate) {
        if(security.getListingDate() == null) {
            return false;
        }
        List<SecurityCodeChange> codeChanges = securityCodeChangeRepository.findBySecurityOrderByChangeDateAsc(security);
        if (codeChanges.isEmpty()) {
            if(!tradingDate.before(security.getListingDate()) &&
                    (security.getDelistedDate() == null || security.getDelistedDate().after(tradingDate))) {
                return true;
            }
        } else {
            Date startDate = security.getListingDate();
            Iterator<SecurityCodeChange> iterator = codeChanges.iterator();
            SecurityCodeChange currentChange = iterator.next();
            String currentCode = currentChange.getOldCode();
            if(currentCode.equals(code) && !tradingDate.before(startDate) && tradingDate.before(currentChange.getChangeDate())) {
                return true;
            }
            while (iterator.hasNext()) {
                SecurityCodeChange nextChange = iterator.next();
                if (nextChange.getOldCode().equals(currentCode)) { // code didn't change
                    currentChange = nextChange;
                    if(currentCode.equals(code) && !tradingDate.before(startDate) && tradingDate.before(currentChange.getChangeDate())) {
                        return true;
                    }
                } else { // code changed
                    startDate = currentChange.getChangeDate();
                    currentChange = nextChange;
                    currentCode = currentChange.getOldCode();
                    if(currentCode.equals(code) && !tradingDate.before(startDate) && tradingDate.before(currentChange.getChangeDate())) {
                        return true;
                    }
                }
            }
            if (currentChange.getNewCode().equals(currentChange.getOldCode())) { // code didn't change in the latest change
                if(currentCode.equals(code) && !tradingDate.before(startDate) &&
                        (security.getDelistedDate() == null || tradingDate.before(security.getDelistedDate()))) {
                    return true;
                }
            } else { // code changed in the latest change
                if(currentCode.equals(code) && !tradingDate.before(startDate) && tradingDate.before(currentChange.getChangeDate())) {
                    return true;
                }
                if(currentChange.getNewCode().equals(code) && !tradingDate.before(currentChange.getChangeDate())
                        && (security.getDelistedDate() == null || tradingDate.before(security.getDelistedDate()))) {
                    return true;
                }
            }
        }
        return false;
    }
}
