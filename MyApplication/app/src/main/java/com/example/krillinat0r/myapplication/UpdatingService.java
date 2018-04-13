package com.example.krillinat0r.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UpdatingService extends Service {

    private static final String LOG = "Updating Service";
    public static final String BROADCAST_UPDATING_SERVICE_PRICES_RESULT = "com.example.krillinat0r.myapplication.BROADCAST_UPDATING_SERVICE_PRICES_RESULT";
    public static final String BROADCAST_UPDATING_SERVICE_COINLIST_RESULT = "com.example.krillinat0r.myapplication.BROADCAST_UPDATING_SERVICE_COINLIST_RESULT";
    public static final String EXTRA_REQUEST_RESULT = "UPDATING_SERVICE_RESULT";
    public static final int REQUEST_SUCCESS = 0;
    public static final int REQUEST_ERROR = 1;

    private String toCurrency = "USD";
    private List<CurrencyData> subscribedCurrencies = new ArrayList<>();
    private List<String> jsonResponses = new ArrayList<>();
    private HashMap<String, CurrencyMapValue> currencyMap = new HashMap<>();

    RequestQueue queue;

    public class UpdatingServiceBinder extends Binder {
        UpdatingService getService() { return UpdatingService.this; }
    }

    public UpdatingService() {
    }

    //IBinder instance we return
    private final IBinder binder = new UpdatingServiceBinder();
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG, "UpdatingService onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG, "UpdatingService onStartCommand");
        fetchPrices();
        return super.onStartCommand(intent, flags, startId);
    }

    private void fetchPrices() {
        String apiRequest = "";
        if(queue == null) {
            queue = Volley.newRequestQueue(this);
        }

        apiRequest = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=";
        for(int i = 0; i < subscribedCurrencies.size(); i++) {
            if((i+1) != subscribedCurrencies.size()) {
                apiRequest += subscribedCurrencies.get(i).getKey() + ",";
            }
            else {
                apiRequest += subscribedCurrencies.get(i).getKey();
            }
        }
        apiRequest += "&tsyms=" + toCurrency;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiRequest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //set private list of coins in here and broadcast
                        //that request was OK/FAIL
                        //bound service should be able to access list by using GET method
                        List<CurrencyPriceData> updatedPrices = CurrencyJsonParser.parseCurrencyPrice(response);

                        for(int i = 0; i < updatedPrices.size(); i++)
                        {
                            for(int j = 0; j < subscribedCurrencies.size(); j++)
                            {
                                if(updatedPrices.get(i).getKey().equals(subscribedCurrencies.get(j).getKey()))
                                    subscribedCurrencies.get(j).setCoinPrice(updatedPrices.get(i).getPrice());
                            }
                        }
                        sendResult(REQUEST_SUCCESS, BROADCAST_UPDATING_SERVICE_PRICES_RESULT);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        sendResult(REQUEST_ERROR, BROADCAST_UPDATING_SERVICE_PRICES_RESULT);
                    }
                });
        queue.add(stringRequest);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    fetchPrices();
            }
        }, 10000);
    }

    private void sendResult(int result, String type) {
        Intent broadcast = new Intent();
        //so we know which kind of update this is
        broadcast.setAction(type);
        broadcast.putExtra(EXTRA_REQUEST_RESULT, result);
        Log.d(LOG, "Broadcasting result: " + result);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
    }

    public void fetchCoinList() {
        String apiRequest = "";
        if(queue == null) {
            queue = Volley.newRequestQueue(this);
        }
        apiRequest = "https://min-api.cryptocompare.com/data/all/coinlist";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiRequest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //set private list of coins in here and broadcast
                        //that request was OK/FAIL
                        //bound service should be able to access list by using GET method
                        currencyMap = CurrencyJsonParser.parseCurrencyHashmapJson(response);
                        sendResult(REQUEST_SUCCESS, BROADCAST_UPDATING_SERVICE_COINLIST_RESULT);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        sendResult(REQUEST_ERROR, BROADCAST_UPDATING_SERVICE_COINLIST_RESULT);
                    }
                });
        queue.add(stringRequest);
    }

    public List<String> getJsonResponses() {
        return jsonResponses;
    }

    public List<CurrencyData> getSubscribedCurrencies() {
        return subscribedCurrencies;
    }

    public boolean addCoin(String coin) {
        CurrencyMapValue mapValue = currencyMap.get(coin);
        if(mapValue != null)
        {
            CurrencyData currency = new CurrencyData();
            currency.setKey(coin);
            currency.setCoinName(mapValue.getName());
            currency.setCoinIconUrl(mapValue.getImageUrl());

            subscribedCurrencies.add(currency);
            return true;
        }
        return false;
    }
}
