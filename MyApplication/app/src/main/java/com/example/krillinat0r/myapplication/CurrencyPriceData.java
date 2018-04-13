package com.example.krillinat0r.myapplication;

/**
 * Created by Krillinat0r on 13-04-2018.
 */

public class CurrencyPriceData {
    public CurrencyPriceData(String key, float price) {
        this.key = key;
        this.price = price;
    }

    private String key;
    private float price;

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
}
