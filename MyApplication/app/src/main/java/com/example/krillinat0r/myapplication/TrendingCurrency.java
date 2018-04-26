package com.example.krillinat0r.myapplication;

import java.util.Date;

/**
 * Created by nikla on 26-04-2018.
 */

public class TrendingCurrency {
    public TrendingCurrency(String title, String description, String date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public TrendingCurrency() {
    }

    String title;
    String description;
    String date;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
