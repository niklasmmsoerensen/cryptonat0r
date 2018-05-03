package com.example.krillinat0r.myapplication;

import java.util.Date;

/**
 * Created by nikla on 19-04-2018.
 */

public class CurrencyHistoricalData {
    Date timestamp;
    double close;
    double high;
    double low;
    double open;
    double volumeFrom;
    double volumeTo;

    public CurrencyHistoricalData() {
    }

    public CurrencyHistoricalData(Date timestamp, double close, double high, double low, double open, double volumeFrom, double volumeTo) {
        this.timestamp = timestamp;
        this.close = close;
        this.high = high;
        this.low = low;
        this.open = open;
        this.volumeFrom = volumeFrom;
        this.volumeTo = volumeTo;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getVolumeFrom() {
        return volumeFrom;
    }

    public void setVolumeFrom(double volumeFrom) {
        this.volumeFrom = volumeFrom;
    }

    public double getVolumeTo() {
        return volumeTo;
    }

    public void setVolumeTo(double volumeTo) {
        this.volumeTo = volumeTo;
    }
}
