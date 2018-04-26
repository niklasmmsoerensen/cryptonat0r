package com.example.krillinat0r.myapplication;

public class CurrencyDetailData {
    public CurrencyDetailData(String key, float price, float changePct, float changeFlat, String supply, String marketCap) {
        this.key = key;
        this.price = price;
        this.changePercent = changePct;
        this.changeFlat = changeFlat;
        this.Supply = supply;
        this.MarketCap = marketCap;
    }

    private String key;
    private float price;
    private float changePercent;
    private float changeFlat;
    private String Supply;
    private String MarketCap;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(float changePercent) {
        this.changePercent = changePercent;
    }

    public float getChangeFlat() {
        return changeFlat;
    }

    public void setChangeFlat(float changeFlat) {
        this.changeFlat = changeFlat;
    }

    public String getSupply() {
        return Supply;
    }

    public void setSupply(String supply) {
        Supply = supply;
    }

    public String getMarketCap() {
        return MarketCap;
    }

    public void setMarketCap(String marketCap) {
        MarketCap = marketCap;
    }
}
