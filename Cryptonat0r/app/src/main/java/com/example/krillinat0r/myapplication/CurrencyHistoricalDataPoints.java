package com.example.krillinat0r.myapplication;

import java.util.List;

/**
 * Created by nikla on 19-04-2018.
 */

public class CurrencyHistoricalDataPoints {
    String currency;
    List<CurrencyHistoricalData> dataPoints;

    public CurrencyHistoricalDataPoints(String requestUrl, List<CurrencyHistoricalData> dataPoints) {
        this.currency = requestUrl;
        this.dataPoints = dataPoints;
    }

    public String getRequestUrl() {
        return currency;
    }

    public void setRequestUrl(String requestUrl) {
        this.currency = requestUrl;
    }

    public List<CurrencyHistoricalData> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<CurrencyHistoricalData> dataPoints) {
        this.dataPoints = dataPoints;
    }
}
