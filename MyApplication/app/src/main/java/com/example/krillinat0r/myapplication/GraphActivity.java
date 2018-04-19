package com.example.krillinat0r.myapplication;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class GraphActivity extends AppCompatActivity {

    private CurrencyData currentCurrency;
    public final static String CURRENCY = "CurrentCurrency";
    private final static String LOG = "GRAPH";

    //for bound counting service
    private UpdatingService updatingService;
    private ServiceConnection updatingServiceConnection;
    private boolean bound = false;

    private RadioButton HourBtn;
    private RadioButton DayBtn;
    private RadioButton WeekBtn;
    private RadioButton MonthBtn;
    private RadioGroup TimeScaleGroup;

    public enum GraphTime
    {
        Hour,
        Day,
        Week,
        Month,
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        currentCurrency = (CurrencyData) getIntent().getParcelableExtra(CURRENCY);

        setupConnectionToUpdatingService();

        //bind to service
        bindService(new Intent(GraphActivity.this,
                UpdatingService.class), updatingServiceConnection, Context.BIND_AUTO_CREATE);

        TimeScaleGroup = findViewById(R.id.TimeGroup);
        TimeScaleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
            }
        });

        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.setTitle(currentCurrency.getCoinName());

        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    return super.formatLabel(value, isValueX);
                } else {
                    // show currency for y values
                    return "$" + super.formatLabel(value,isValueX);
                }
            }
        });
    }

    @Override
    public void onStart()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UpdatingService.BROADCAST_UPDATING_SERVICE_HISTORICALDATA_RESULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(onUpdatingServiceCoinHistoryResult, filter);

        super.onStart();
    }

    private void setupConnectionToUpdatingService(){
        updatingServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                Log.d(LOG, "onServiceConnected");
                updatingService = ((UpdatingService.UpdatingServiceBinder)service).getService();

                //updatingService.GetGraph();
                Log.d("Binder", "Updating service connected");
            }

            public void onServiceDisconnected(ComponentName className) {

                //updatingService = null;
                Log.d("Binder", "Updating service disconnected");
            }
        };
    }

    private BroadcastReceiver onUpdatingServiceCoinHistoryResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG, "Broadcast received: CoinHistory");
            int result = intent.getIntExtra(UpdatingService.EXTRA_REQUEST_RESULT, 1);
            if(result == 0) {
                //success
                Log.d(LOG, "CoinHistory OK!");
                SetGraphToCoinHistoryData();
            }
            else {
                //error
                Log.d(LOG, "CoinHistory ERROR!");
            }
        }
    };

    private void SetGraphToCoinHistoryData()
    {

    }

}
