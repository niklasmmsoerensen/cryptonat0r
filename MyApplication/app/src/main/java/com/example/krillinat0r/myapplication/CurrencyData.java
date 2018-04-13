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

    private String _coinName;
    private float _percentChange;
    private float _coinPrice;
    private Bitmap _coinIcon;
    private String _coinIconUrl;
    private Bitmap _coinRate;

    private static final String NAME = "NAME";
    private static final String PRICE = "PRICE";
    private static final String CHANGE = "CHANGE";
    private static final String RATE = "RATE";
    private static final String ICON = "ICON";

    public CurrencyData(){}

    public String get_coinName() {
        return _coinName;
    }

    public void set_coinName(String _coinName) {
        this._coinName = _coinName;
    }

    public float get_percentChange() {
        return _percentChange;
    }

    public void set_percentChange(float _percentChange) {
        this._percentChange = _percentChange;
    }

    public Bitmap get_coinIcon() {
        return _coinIcon;
    }

    public void set_coinIcon(Bitmap _coinIcon) {
        this._coinIcon = _coinIcon;
    }

    public Bitmap get_coinRate() {
        return _coinRate;
    }

    public void set_coinRate(Bitmap _coinRate) {
        this._coinRate = _coinRate;
    }

    public float get_coinPrice() {return _coinPrice;}

    public void set_coinPrice(float _coinPrice) {this._coinPrice = _coinPrice;}

    public String get_coinIconUrl() {
        return _coinIconUrl;
    }

    public void set_coinIconUrl(String _coinIconUrl) {
        this._coinIconUrl = _coinIconUrl;
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

    protected CurrencyData(Parcel in) {
        Bundle bundle = in.readBundle();

        _coinName = bundle.getString(NAME);
        _coinPrice = bundle.getFloat(PRICE);
        _percentChange = bundle.getFloat(CHANGE);
        _coinIcon = BitmapFactory.decodeByteArray(bundle.getByteArray(ICON), 0 ,bundle.getByteArray(ICON).length);
        _coinRate = BitmapFactory.decodeByteArray(bundle.getByteArray(RATE), 0 ,bundle.getByteArray(RATE).length);
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();

        bundle.putString(NAME, _coinName);
        bundle.putFloat(PRICE, _coinPrice);
        bundle.putFloat(CHANGE,_percentChange);

        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        _coinIcon.compress(Bitmap.CompressFormat.PNG, 50, bs);
        bundle.putByteArray(ICON,bs.toByteArray());

        ByteArrayOutputStream bs2 = new ByteArrayOutputStream();
        _coinRate.compress(Bitmap.CompressFormat.PNG, 50, bs2);
        bundle.putByteArray(RATE,bs.toByteArray());

        parcel.writeBundle(bundle);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
