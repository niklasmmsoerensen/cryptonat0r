package com.example.krillinat0r.myapplication;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Krillinat0r on 13-04-2018.
 */

public class CurrencyJsonParser {

    private static final String JSONPARSERLOG = "jsonLog";

    //reference https://stackoverflow.com/a/39073993
    public static HashMap<String, CurrencyMapValue> parseCurrencyHashmapJson(String jsonString) {

        //Hashmap for performance (lots of data)
        HashMap<String, CurrencyMapValue>  CurrencyMap = new HashMap<String, CurrencyMapValue>();
        try {
            JSONObject Object = new JSONObject(jsonString);
            JSONObject dataListJson = Object.getJSONObject("Data");
            Iterator<String> keys = dataListJson.keys(); //Get all objectkeys within DataList
            while(keys.hasNext())
            {
                try {
                    String key = keys.next(); //Get next key in list
                    if (dataListJson.get(key) instanceof JSONObject) {
                        CurrencyMapValue currentCurrency = new CurrencyMapValue();
                        JSONObject currencyData = dataListJson.getJSONObject(key);
                        currentCurrency.setKey(key);
                        currentCurrency.setName(currencyData.getString("CoinName"));
                        currentCurrency.setImageUrl(currencyData.getString("ImageUrl"));
                        CurrencyMap.put(key, currentCurrency);
                    }
                }
                catch (JSONException e) {
                    Log.d(JSONPARSERLOG, "JSON EXCEPTION (" + keys.next() + "): " + e.toString());
                }
            }
        } catch (JSONException e) {
            Log.d(JSONPARSERLOG, "JSON EXCEPTION" + e.toString());
        }
        return CurrencyMap;
    }

    public static List<CurrencyDetailData> parseCurrencyDetails(String jsonString) {

        List<CurrencyDetailData> detailList = new ArrayList<>();

        try {
            JSONObject DetailObject = new JSONObject(jsonString);
            JSONObject RawData = DetailObject.getJSONObject("RAW");
            JSONObject DisplayData = DetailObject.getJSONObject("DISPLAY");
            Iterator<String> keys = RawData.keys(); //Get all objectkeys within DataList
            while(keys.hasNext())
            {
                try {
                    String key = keys.next(); //Get next key in list
                    if (RawData.get(key) instanceof JSONObject) {
                        JSONObject detailRaw = RawData.getJSONObject(key).getJSONObject("USD");
                        JSONObject detailDisp = DisplayData.getJSONObject(key).getJSONObject("USD");

                        float price = (float) detailRaw.getDouble("PRICE");
                        float changePercent = (float) detailRaw.getDouble("CHANGEPCT24HOUR");
                        float changeFlat = (float) detailRaw.getDouble("CHANGE24HOUR");
                        String Supply = (String) detailDisp.getString("SUPPLY");
                        String MarketCap = (String) detailDisp.getString("MKTCAP");

                        CurrencyDetailData currentCurrency = new CurrencyDetailData(key, price, changePercent, changeFlat, Supply, MarketCap);

                        detailList.add(currentCurrency);
                    }
                }
                catch (JSONException e) {
                    Log.d(JSONPARSERLOG, "JSON EXCEPTION (" + keys.next() + "): " + e.toString());
                }
            }
            return detailList;
            
        }catch (JSONException e) {
            Log.d(JSONPARSERLOG, "JSON EXCEPTION" + e.toString());
        }
        return null;
    }

    public static CurrencyHistoricalDataPoints parseCurrencyHistoricalData(String jsonString) {
        CurrencyHistoricalDataPoints historicalData;
        List<CurrencyHistoricalData> dataPoints = new ArrayList<>();
        double close, high, low, open, volumeFrom, volumeTo;

        try {
            JSONObject historicalDataObject = new JSONObject(jsonString);

            //region extract currency from request
            String requestUrl = historicalDataObject.getString("RequestUrl");
            String split[] = requestUrl.split("[=&]+");
            String currency = split[1];
            //endregion

            //region extract historical data
            JSONArray dataArray = historicalDataObject.getJSONArray("Data");
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject dataObject = dataArray.getJSONObject(i);
                Date timestamp = convertEpochTimestamp(dataObject.getInt("time"));
                close = dataObject.getDouble("close");
                high = dataObject.getDouble("high");
                low = dataObject.getDouble("low");
                open = dataObject.getDouble("open");
                volumeFrom = dataObject.getDouble("volumefrom");
                volumeTo = dataObject.getDouble("volumeto");
                CurrencyHistoricalData dataPoint = new CurrencyHistoricalData(timestamp, close, high, low, open, volumeFrom, volumeTo);
                dataPoints.add(dataPoint);
            }
            //endregion

            historicalData = new CurrencyHistoricalDataPoints(currency, dataPoints);
            return historicalData;
        }catch (JSONException e) {
            Log.d(JSONPARSERLOG, "JSON EXCEPTION" + e.toString());
        }
        return null;
    }

    static private Date convertEpochTimestamp(int timestamp) {
        long epoch = Long.parseLong(String.valueOf(timestamp));
        return new Date(epoch * 1000);
    }
}
