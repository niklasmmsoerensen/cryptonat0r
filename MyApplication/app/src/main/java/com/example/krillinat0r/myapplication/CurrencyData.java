package com.example.krillinat0r.myapplication;

import android.graphics.Bitmap;

/**
 * Created by Krillinat0r on 12-04-2018.
 */

public class CurrencyData {

    private String _coinName;
    private float _percentChange;
    private Bitmap _coinIcon;
    private Bitmap _coinRate;

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

}
