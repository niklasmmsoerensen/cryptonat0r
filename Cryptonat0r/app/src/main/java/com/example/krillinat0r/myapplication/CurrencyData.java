package com.example.krillinat0r.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;

/**
 * Created by Krillinat0r on 12-04-2018.
 */

public class CurrencyData implements Parcelable{

    private String key;
    private String coinName;
    private float percentChange;
    private float flatChange;
    private String totalSupply;
    private String marketCap;
    private float coinPrice;
    private Bitmap coinIcon;
    private String coinIconUrl;
    private Bitmap coinRate;

    private static final String KEY = "KEY";
    private static final String NAME = "NAME";
    private static final String PRICE = "PRICE";
    private static final String CHANGE = "CHANGE";
    private static final String IMAGEURL = "IMAGEURL";
    private static final String RATE = "RATE";
    private static final String ICON = "ICON";
    private static final String SUPPLY = "SUPPLY";
    private static final String FLATCHANGE = "FLAT";
    private static final String MARKETCAP = "MARKETCAP";

    public CurrencyData(){}

    public float getFlatChange() {
        return flatChange;
    }

    public void setFlatChange(float flatChange) {
        this.flatChange = flatChange;
    }

    public String getTotalSupply() {
        return totalSupply;
    }

    public void setTotalSupply(String totalSupply) {
        this.totalSupply = totalSupply;
    }

    public String getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(String marketCap) {
        this.marketCap = marketCap;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public float getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(float percentChange) {
        this.percentChange = percentChange;
    }

    public float getCoinPrice() {
        return coinPrice;
    }

    public void setCoinPrice(float coinPrice) {
        this.coinPrice = coinPrice;
    }

    public Bitmap getCoinIcon() {
        return coinIcon;
    }

    public void setCoinIcon(Bitmap coinIcon) {
        this.coinIcon = coinIcon;
    }

    public String getCoinIconUrl() {
        return coinIconUrl;
    }

    public void setCoinIconUrl(String coinIconUrl) {
        this.coinIconUrl = coinIconUrl;
    }

    public Bitmap getCoinRate() {
        return coinRate;
    }

    public void setCoinRate(Bitmap coinRate) {
        this.coinRate = coinRate;
    }

    public static final Creator<CurrencyData> CREATOR = new Creator<CurrencyData>() {
        @Override
        public CurrencyData createFromParcel(Parcel in) {
            return new CurrencyData(in);
        }

        @Override
        public CurrencyData[] newArray(int size) {
            return new CurrencyData[size];
        }
    };

    protected CurrencyData(Parcel in)
    {
        Bundle bundle = in.readBundle();

        key = bundle.getString(KEY);
        coinName = bundle.getString(NAME);
        coinPrice = bundle.getFloat(PRICE);
        percentChange = bundle.getFloat(CHANGE);
        coinIconUrl = bundle.getString(IMAGEURL);
        flatChange = bundle.getFloat(FLATCHANGE);
        totalSupply = bundle.getString(SUPPLY);
        marketCap = bundle.getString(MARKETCAP);

        if(bundle.getByteArray(ICON) != null)
            coinIcon = BitmapFactory.decodeByteArray(bundle.getByteArray(ICON), 0 ,bundle.getByteArray(ICON).length);

        if(bundle.getByteArray(RATE) != null)
            coinRate = BitmapFactory.decodeByteArray(bundle.getByteArray(RATE), 0 ,bundle.getByteArray(RATE).length);
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();

        bundle.putString(KEY, key);
        bundle.putString(NAME, coinName);
        bundle.putString(IMAGEURL, coinIconUrl);
        bundle.putFloat(PRICE, coinPrice);
        bundle.putFloat(CHANGE,percentChange);
        bundle.putFloat(FLATCHANGE, flatChange);
        bundle.putString(SUPPLY, totalSupply);
        bundle.putString(MARKETCAP, marketCap);

        if(coinIcon != null) {
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            coinIcon.compress(Bitmap.CompressFormat.PNG, 50, bs);
            bundle.putByteArray(ICON, bs.toByteArray());
        }

        if(coinRate != null) {
            ByteArrayOutputStream bs2 = new ByteArrayOutputStream();
            coinRate.compress(Bitmap.CompressFormat.PNG, 50, bs2);
            bundle.putByteArray(RATE, bs2.toByteArray());
        }

        parcel.writeBundle(bundle);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
