package com.finalysis.research.virtuality;

import com.finalysis.research.reality.Exchange;

public interface SecurityInfoLoader {
    void loadInfo(Exchange exchange);
    void loadListingDate(Exchange exchange);
}
