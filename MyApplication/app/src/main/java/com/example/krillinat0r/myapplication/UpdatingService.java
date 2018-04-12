package com.example.krillinat0r.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class UpdatingService extends Service {

    private static final String LOG = "Updating Service";
    public static final String BROADCAST_UPDATING_SERVICE_RESULT = "com.example.krillinat0r.myapplication.BROADCAST_UPDATING_SERVICE_RESULT";
    public static final String EXTRA_COINLIST_RESULT = "UPDATING_SERVICE_RESULT";

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
        fetchData();
        return START_STICKY;
    }

    private void fetchData() {
        if(queue == null) {
            queue = Volley.newRequestQueue(this);
        }
        String ApiRequest = "https://min-api.cryptocompare.com/data/price?fsym=ETH&tsyms=USD,EUR";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ApiRequest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //set private list of coins in here and broadcast
                        //that request was OK/FAIL
                        //bound service should be able to access list by using GET method
                        sendResult(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        queue.add(stringRequest);
    }

    private void sendResult(String result) {
        Intent broadcast = new Intent();
        //so we know which kind of update this is
        broadcast.setAction(BROADCAST_UPDATING_SERVICE_RESULT);
        broadcast.putExtra(EXTRA_COINLIST_RESULT, result);
        //should probably just send that request was OK instead of all the data
        Log.d(LOG, "Broadcasting result: " + result);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
    }

}
