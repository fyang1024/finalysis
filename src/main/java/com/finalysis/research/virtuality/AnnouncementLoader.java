package com.finalysis.research.virtuality;

import com.finalysis.research.reality.Exchange;

public interface AnnouncementLoader {

    void loadAnnouncements(Exchange exchange);

    void loadTodayAnnouncements(Exchange exchange);

    void loadYesterdayAnnouncements(Exchange exchange);
}

