package com.finalysis.research.virtuality;

import com.finalysis.research.BaseEntity;
import com.finalysis.research.reality.Exchange;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
public class TradingCalendar extends BaseEntity {

    private String publicHoliday;

    @ManyToOne
    private Exchange exchange;

    @Temporal(TemporalType.DATE)
    private Date date;

    private boolean tradingDay;

    public Exchange getExchange() {
        return exchange;
    }

    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isTradingDay() {
        return tradingDay;
    }

    public void setTradingDay(boolean tradingDay) {
        this.tradingDay = tradingDay;
    }

    public String getPublicHoliday() {
        return publicHoliday;
    }

    public void setPublicHoliday(String publicHoliday) {
        this.publicHoliday = publicHoliday;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TradingCalendar)) return false;

        TradingCalendar that = (TradingCalendar) o;

        if (tradingDay != that.tradingDay) return false;
        if (!date.equals(that.date)) return false;
        if (!exchange.equals(that.exchange)) return false;
        if (!publicHoliday.equals(that.publicHoliday)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = publicHoliday.hashCode();
        result = 31 * result + exchange.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + (tradingDay ? 1 : 0);
        return result;
    }
}
