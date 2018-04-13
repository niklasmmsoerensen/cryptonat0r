package com.example.krillinat0r.myapplication;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Krillinat0r on 13-04-2018.
 */

public class CurrencyJsonParser {

    private static final String JSONPARSERLOG = "jsonLog";

    public static HashMap<CurrencyData, String> parseCurrencyHashmapJson(String jsonString) {

        //Hashmap for performance (lots of data)
        HashMap<CurrencyData, String>  CurrencyMap = new HashMap<CurrencyData, String>();

        try {
            JSONObject dataListJson = new JSONObject(jsonString);
            Iterator<String> keys = dataListJson.keys(); //Get all objectkeys within DataList
            while(keys.hasNext())
            {
                String key = keys.next(); //Get next key in list
                if(dataListJson.get(key) instanceof  JSONObject)
                {
                    CurrencyData currentCurrency = new CurrencyData();
                    JSONObject currencyData = dataListJson.getJSONObject(key);
                    currentCurrency.set_coinName(currencyData.getString("CoinName"));
                    currentCurrency.set_coinIconUrl(currencyData.getString("ImageUrl"));
                    CurrencyMap.put(currentCurrency, key);
                }
            }
        } catch (JSONException e) {
            Log.d(JSONPARSERLOG, "Json exception, damm");
        }
        return CurrencyMap;
    }
}
