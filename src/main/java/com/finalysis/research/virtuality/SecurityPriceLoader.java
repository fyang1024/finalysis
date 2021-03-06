package com.finalysis.research.virtuality;

import com.finalysis.research.reality.Exchange;

import java.util.Date;

public interface SecurityPriceLoader {

    void loadSecurityPrice(Exchange exchange);

    void loadSecurityPrice(Exchange exchange, Date from, Date to);
}
