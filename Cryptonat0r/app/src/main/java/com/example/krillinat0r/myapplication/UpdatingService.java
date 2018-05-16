package com.example.krillinat0r.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class UpdatingService extends Service {

    private static final String LOG = "Updating Service";
    public static final String BROADCAST_UPDATING_SERVICE_PRICES_RESULT = "com.example.krillinat0r.myapplication.BROADCAST_UPDATING_SERVICE_PRICES_RESULT";
    public static final String BROADCAST_UPDATING_SERVICE_COINLIST_RESULT = "com.example.krillinat0r.myapplication.BROADCAST_UPDATING_SERVICE_COINLIST_RESULT";
    public static final String BROADCAST_UPDATING_SERVICE_HISTORICALDATA_RESULT = "com.example.krillinat0r.myapplication.BROADCAST_UPDATING_SERVICE_HISTORICALDATA_RESULT";
    public static final String EXTRA_REQUEST_RESULT = "UPDATING_SERVICE_RESULT";
    public static final int REQUEST_SUCCESS = 0;
    public static final int REQUEST_ERROR = 1;
    public static final String SUBSCRIBED_CURRENCIES = "SubscribedCurrencies";
    public static final String WATCHED_CURRENCIES = "WatchedCurrencies";

    //Notification stuff
    private final static int NOTIFICATION_ID = 1;
    private final static String CHANNEL_ID = "UpdatingServiceChannel";
    private final static String CHANNEL_NAME = "UpdatingService Channel";
    private final static String CHANNEL_DESCRIPTION = "Channel to display notifications from UpdatingService";

    private static final String fetchDetailsBaseURL = "https://min-api.cryptocompare.com/data/pricemultifull?fsyms=";
    private static final String currencyPrefix = "&tsyms=";
    private static final String fetchCoinListURL = "https://min-api.cryptocompare.com/data/all/coinlist";
    private static final String toCurrency = "USD";
    private static final String fetchHistoricalDataMinuteURL = "https://min-api.cryptocompare.com/data/histominute?fsym=";
    private static final String fetchHistoricalDataHourURL = "https://min-api.cryptocompare.com/data/histohour?fsym=";
    private static final String fetchHistoricalDataDayURL = "https://min-api.cryptocompare.com/data/histoday?fsym=";

    private List<CurrencyData> subscribedCurrencies = new ArrayList<>();
    private List<String> jsonResponses = new ArrayList<>();
    private HashMap<String, CurrencyMapValue> currencyMap = new HashMap<>();
    private List<CurrencyHistoricalDataPoints> currencyHistoricalDataPointsList = new ArrayList<>();
    private HashMap<String, Float> watchedCurrenciesMap = new HashMap<>();

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

        //get sharedPreferences data
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String subscribedCurrenciesJson = preferences.getString(SUBSCRIBED_CURRENCIES, "");
        String watchedCurrenciesJson = preferences.getString(WATCHED_CURRENCIES, "");
        if(subscribedCurrenciesJson != "") {
            Gson gson = new Gson();
            try{
                Type type = new TypeToken<ArrayList<CurrencyData>>(){}.getType();
                ArrayList<CurrencyData> sharedPrefSubs = gson.fromJson(subscribedCurrenciesJson, type);
                subscribedCurrencies.clear();
                subscribedCurrencies.addAll(sharedPrefSubs);
                type = new TypeToken<HashMap<String, Float>>(){}.getType();
                HashMap<String, Float> sharedPrefsWatched = gson.fromJson(watchedCurrenciesJson, type);
                if(sharedPrefsWatched != null) {
                    watchedCurrenciesMap.clear();
                    watchedCurrenciesMap.putAll(sharedPrefsWatched);
                }
            }
            catch(JsonParseException e)   {
                Log.d(LOG, "JSON ERROR: " + e.toString());
            }
        }
        fetchDetails(false);
        return super.onStartCommand(intent, flags, startId);
    }

    public void forceFetchDetails() {
        fetchDetails(true);
    }

    private void fetchDetails(boolean forceUpdate) {
        String apiRequest = "";
        if(queue == null) {
            queue = Volley.newRequestQueue(this);
        }

        apiRequest = fetchDetailsBaseURL;
        for(int i = 0; i < subscribedCurrencies.size(); i++) {
            if((i+1) != subscribedCurrencies.size()) {
                apiRequest += subscribedCurrencies.get(i).getKey() + ",";
            }
            else {
                apiRequest += subscribedCurrencies.get(i).getKey();
            }
        }
        apiRequest += currencyPrefix + toCurrency;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiRequest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //set private list of coins in here and broadcast
                        //that request was OK/FAIL
                        //bound service should be able to access list by using GET method
                        List<CurrencyDetailData> updatedDetails = CurrencyJsonParser.parseCurrencyDetails(response);

                        if(updatedDetails == null)
                            updatedDetails = new ArrayList<>();

                        for(int i = 0; i < updatedDetails.size(); i++)
                        {
                            for(int j = 0; j < subscribedCurrencies.size(); j++)
                            {
                                if(updatedDetails.get(i).getKey().equals(subscribedCurrencies.get(j).getKey()))
                                {
                                    subscribedCurrencies.get(j).setCoinPrice(updatedDetails.get(i).getPrice());
                                    subscribedCurrencies.get(j).setPercentChange(updatedDetails.get(i).getChangePercent());
                                    subscribedCurrencies.get(j).setFlatChange(updatedDetails.get(i).getChangeFlat());
                                    subscribedCurrencies.get(j).setTotalSupply(updatedDetails.get(i).getSupply());
                                    subscribedCurrencies.get(j).setMarketCap(updatedDetails.get(i).getMarketCap());
                                    //Set all variables
                                }
                            }
                        }
                        //check if any currency reached a watched currency target price
                        checkForWatchedCurrencies();
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

        if(!forceUpdate) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fetchDetails(false);
                }
            }, 10000);
        }
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
        apiRequest = fetchCoinListURL;
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

    public List<CurrencyData> getSubscribedCurrencies() {
        return subscribedCurrencies;
    }

    public HashMap<String, Float> getWatchedCurrenciesMap() {
        return watchedCurrenciesMap;
    }

    public CurrencyHistoricalDataPoints getCurrencyHistoricalDataPoints(String currency) {
        for (int i = 0; i < currencyHistoricalDataPointsList.size(); i++) {
            if(currencyHistoricalDataPointsList.get(i).currency.equals(currency)) {
                return currencyHistoricalDataPointsList.get(i);
            }
        }
        return null;
    }

    public boolean addCoin(String coin) {
        CurrencyMapValue mapValue = currencyMap.get(coin);
        if(mapValue != null) {
            for (int i = 0; i < subscribedCurrencies.size(); i++) {
                if(subscribedCurrencies.get(i).getKey().equals(coin)) {
                    return false;
                }
            }
            CurrencyData currency = new CurrencyData();
            currency.setKey(coin);
            currency.setCoinName(mapValue.getName());
            currency.setCoinIconUrl(mapValue.getImageUrl());

            subscribedCurrencies.add(currency);
            //save subscribed currencies to shared preferences
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            Gson gson = new Gson();
            editor.putString(SUBSCRIBED_CURRENCIES, gson.toJson(subscribedCurrencies));
            editor.commit();
            fetchDetails(true);
            return true;
        }
        return false;
    }

    public void removeCoin(String coin) {
        for (int i = 0;i < subscribedCurrencies.size(); i++) {
            if(subscribedCurrencies.get(i).getKey().equals(coin)) {
                subscribedCurrencies.remove(i);

                //save subscribed currencies to shared preferences
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                Gson gson = new Gson();
                editor.putString(SUBSCRIBED_CURRENCIES, gson.toJson(subscribedCurrencies));
                editor.commit();
                fetchDetails(true);
                return;
            }
        }
    }

    public void addCoinToWatch(String coin, Float watchPrice) {
        //can only have one watch per coin, replace current value if entry already exists
        watchedCurrenciesMap.put(coin, watchPrice);

        //save watched currencies to shared preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        editor.putString(WATCHED_CURRENCIES, gson.toJson(getWatchedCurrenciesMap()));
        editor.commit();
    }

    private void checkForWatchedCurrencies() {
        CurrencyData element = null;
        for (int i = 0; i < subscribedCurrencies.size(); i++) {
            element = subscribedCurrencies.get(i);
            if(watchedCurrenciesMap.containsKey(element.getKey())) { //if element is watched
                Float price = element.getCoinPrice();
                Float watchPrice = watchedCurrenciesMap.get(element.getKey());
                if((price + 2F) >= watchPrice && (price - 2F) <= watchPrice) { //only trigger if price is 2 USD over or under target price
                    //price has reached watched price, show notification
                    showNotification(element.getKey(), element.getCoinPrice());
                    watchedCurrenciesMap.remove(element.getKey()); //remove watch when target is reached
                }
            }
        }
    }

    private void showNotification(String key, Float price) {
        //region notification stuff
        //reference: https://developer.android.com/training/notify-user/build-notification.html#Priority
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is a new thing
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            // Register the channel with the system
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }

        //notification stuff heavily inspired by docs: https://developer.android.com/training/notify-user/build-notification.html
        Intent notificationTapIntent = new Intent(UpdatingService.this, OverviewActivity.class);
        notificationTapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(UpdatingService.this, 0, notificationTapIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(UpdatingService.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.price_watch_icon)
                .setContentTitle(getResources().getString(R.string.PriceWatchNotificationTitle))
                .setContentText(key + " " + getResources().getString(R.string.PriceWatchNotificationContent) + " " + price + " USD")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(UpdatingService.this);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        //endregion
    }

    public void fetchHistoricalData(String coin, GraphActivity.GraphTime type) {
        //region setup api request
        String apiRequest = "";
        int limit = 0;
        switch(type) {
            case Hour:
                apiRequest = fetchHistoricalDataMinuteURL;
                limit = 60;
            break;
            case Day:
                apiRequest = fetchHistoricalDataMinuteURL;
                limit = 60*24;
                break;
            case Week:
                apiRequest = fetchHistoricalDataHourURL;
                limit = 7*24;
                break;
            case Month:
                apiRequest = fetchHistoricalDataHourURL;
                limit = 30*24;
                break;
            case Year:
                apiRequest = fetchHistoricalDataDayURL;
                limit = 365;
        }
        apiRequest += coin + "&tsym=" + toCurrency + "&limit=" + String.valueOf(limit);
        //endregion

        if(queue == null) {
            queue = Volley.newRequestQueue(this);
        }

        HistoricalDataRequest stringRequest = new HistoricalDataRequest(apiRequest, null,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CurrencyHistoricalDataPoints dataPoints = CurrencyJsonParser.parseCurrencyHistoricalData(response);
                        for (int i = 0; i < currencyHistoricalDataPointsList.size(); i++) {
                            if(currencyHistoricalDataPointsList.get(i).currency.equals(dataPoints.currency)) {
                                currencyHistoricalDataPointsList.set(i, dataPoints);
                                sendResult(REQUEST_SUCCESS, BROADCAST_UPDATING_SERVICE_HISTORICALDATA_RESULT);
                                return;
                            }
                        }
                        currencyHistoricalDataPointsList.add(dataPoints);
                        sendResult(REQUEST_SUCCESS, BROADCAST_UPDATING_SERVICE_HISTORICALDATA_RESULT);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        sendResult(REQUEST_ERROR, BROADCAST_UPDATING_SERVICE_HISTORICALDATA_RESULT);
                    }
                });
        queue.add(stringRequest);
    }
}
