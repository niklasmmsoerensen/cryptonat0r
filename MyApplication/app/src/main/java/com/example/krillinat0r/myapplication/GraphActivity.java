package com.example.krillinat0r.myapplication;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class GraphActivity extends AppCompatActivity {

    private CurrencyData currentCurrency;
    public final static String CURRENCY = "CurrentCurrency";
    private final static String LOG = "GRAPH";

    //for bound counting service
    private UpdatingService updatingService;
    private ServiceConnection updatingServiceConnection;
    private boolean bound = false;

    private GraphView graph;
    private GraphTime currentTime;
    private SeekBar timeBar;

    private TextView Hour;
    private TextView Day;
    private TextView Week;
    private TextView Month;
    private TextView Year;

    private TextView Price;
    private TextView ChangeFlat;
    private TextView ChangePct;
    private TextView Supply;
    private TextView MarketCap;

    private CurrencyHistoricalDataPoints currencyHistory;

    public enum GraphTime
    {
        Hour,
        Day,
        Week,
        Month,
        Year
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Price = findViewById(R.id.PriceText);
        ChangeFlat = findViewById(R.id.ChangeFlatText);
        ChangePct = findViewById(R.id.ChangePctText);
        Supply = findViewById(R.id.SupplyText);
        MarketCap = findViewById(R.id.MktCapText);

        currentCurrency = (CurrencyData) getIntent().getParcelableExtra(CURRENCY);

        DecimalFormat df = new DecimalFormat("#.##");

        if(currentCurrency.getFlatChange() > 0) {
            ChangeFlat.setText("+" + String.valueOf(df.format(currentCurrency.getFlatChange()))); //Set coin value change
            ChangeFlat.setTextColor(getResources().getColor(R.color.PositivePrice));
        }
        else {
            ChangeFlat.setText(String.valueOf(df.format(currentCurrency.getFlatChange()))); //Set coin value change
            ChangeFlat.setTextColor(getResources().getColor(R.color.NegativePrice));
        }

        if(currentCurrency.getPercentChange() > 0) {
            ChangePct.setText("+" + String.valueOf(df.format(currentCurrency.getPercentChange())) + " %"); //Set coin value change
            ChangePct.setTextColor(getResources().getColor(R.color.PositivePrice));
        }
        else {
            ChangePct.setText(String.valueOf(df.format(currentCurrency.getPercentChange())) + " %"); //Set coin value change
            ChangePct.setTextColor(getResources().getColor(R.color.NegativePrice));
        }

        Price.setText("$ " + String.valueOf(currentCurrency.getCoinPrice()));
        Supply.setText(String.valueOf(currentCurrency.getTotalSupply()));
        MarketCap.setText(String.valueOf(currentCurrency.getMarketCap()));

        Hour = findViewById(R.id.HourText);
        Day = findViewById(R.id.DayText);
        Week = findViewById(R.id.WeekText);
        Month = findViewById(R.id.MonthText);
        Year = findViewById(R.id.YearText);

        currentTime = GraphTime.Week;

        Hour.setEnabled(false);
        Day.setEnabled(false);
        Week.setEnabled(true);
        Month.setEnabled(false);
        Year.setEnabled(false);

        timeBar = findViewById(R.id.TimeScale);
        timeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                switch(seekBar.getProgress())
                {
                    case 0:
                        currentTime = GraphTime.Hour;
                        Hour.setEnabled(true);
                        Day.setEnabled(false);
                        Week.setEnabled(false);
                        Month.setEnabled(false);
                        Year.setEnabled(false);
                        break;
                    case 1:
                        currentTime = GraphTime.Day;
                        Hour.setEnabled(false);
                        Day.setEnabled(true);
                        Week.setEnabled(false);
                        Month.setEnabled(false);
                        Year.setEnabled(false);
                        break;
                    case 2:
                        currentTime = GraphTime.Week;
                        Hour.setEnabled(false);
                        Day.setEnabled(false);
                        Week.setEnabled(true);
                        Month.setEnabled(false);
                        Year.setEnabled(false);
                        break;
                    case 3:
                        currentTime = GraphTime.Month;
                        Hour.setEnabled(false);
                        Day.setEnabled(false);
                        Week.setEnabled(false);
                        Month.setEnabled(true);
                        Year.setEnabled(false);
                        break;
                    case 4:
                        currentTime = GraphTime.Year;
                        Hour.setEnabled(false);
                        Day.setEnabled(false);
                        Week.setEnabled(false);
                        Month.setEnabled(false);
                        Year.setEnabled(true);
                        break;
                }
                if(updatingService != null && updatingServiceConnection != null)
                {
                    updatingService.fetchHistoricalData(currentCurrency.getKey(), currentTime);
                }
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch(progress)
                {
                    case 0:
                        Hour.setEnabled(true);
                        Day.setEnabled(false);
                        Week.setEnabled(false);
                        Month.setEnabled(false);
                        Year.setEnabled(false);
                        break;
                    case 1:
                        Hour.setEnabled(false);
                        Day.setEnabled(true);
                        Week.setEnabled(false);
                        Month.setEnabled(false);
                        Year.setEnabled(false);
                        break;
                    case 2:
                        Hour.setEnabled(false);
                        Day.setEnabled(false);
                        Week.setEnabled(true);
                        Month.setEnabled(false);
                        Year.setEnabled(false);
                        break;
                    case 3:
                        Hour.setEnabled(false);
                        Day.setEnabled(false);
                        Week.setEnabled(false);
                        Month.setEnabled(true);
                        Year.setEnabled(false);
                        break;
                    case 4:
                        Hour.setEnabled(false);
                        Day.setEnabled(false);
                        Week.setEnabled(false);
                        Month.setEnabled(false);
                        Year.setEnabled(true);
                        break;
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
        });

        setupConnectionToUpdatingService();

        //bind to service
        bindService(new Intent(GraphActivity.this,
                UpdatingService.class), updatingServiceConnection, Context.BIND_AUTO_CREATE);

        graph = (GraphView) findViewById(R.id.graph);
        graph.setTitle(currentCurrency.getCoinName());

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {

                                                           DateFormat mDateFormat =  android.text.format.DateFormat.getDateFormat(GraphActivity.this);
                                                           @Override

                                                           public String formatLabel(double value, boolean isValueX) {
                                                               if (isValueX) {
                                                                   Calendar mCalendar = Calendar.getInstance();
                                                                   // show normal x values
                                                                   long val = (long)value;
                                                                   Date d = new Date(val);
                                                                   mCalendar.setTime(d);

                                                                   String ret = "";
                                                                   graph.getGridLabelRenderer().setHorizontalLabelsAngle(20);
                                                                   switch(currentTime)
                                                                   {
                                                                       case Hour:
                                                                           if(String.valueOf(mCalendar.get(Calendar.HOUR_OF_DAY)).length() < 2)
                                                                               ret += "0" + String.valueOf(mCalendar.get(Calendar.HOUR_OF_DAY));
                                                                           else
                                                                               ret += String.valueOf(mCalendar.get(Calendar.HOUR_OF_DAY));

                                                                           ret += ":";
                                                                           if(String.valueOf(mCalendar.get(Calendar.MINUTE)).length() < 2)
                                                                               ret += "0" + String.valueOf(mCalendar.get(Calendar.MINUTE));
                                                                           else
                                                                               ret += String.valueOf(mCalendar.get(Calendar.MINUTE));

                                                                           break;

                                                                       case Day:
                                                                           ret += String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH)) + "/";

                                                                       if(String.valueOf(mCalendar.get(Calendar.HOUR_OF_DAY)).length() < 2)
                                                                           ret += "0" + String.valueOf(mCalendar.get(Calendar.HOUR_OF_DAY));
                                                                       else
                                                                           ret += String.valueOf(mCalendar.get(Calendar.HOUR_OF_DAY));

                                                                       ret += ":";
                                                                       if(String.valueOf(mCalendar.get(Calendar.MINUTE)).length() < 2)
                                                                           ret += "0" + String.valueOf(mCalendar.get(Calendar.MINUTE));
                                                                       else
                                                                           ret += String.valueOf(mCalendar.get(Calendar.MINUTE));
                                                                       break;

                                                                       case Week:
                                                                           ret += String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH)) + "/";

                                                                           if(String.valueOf(mCalendar.get(Calendar.HOUR_OF_DAY)).length() < 2)
                                                                               ret += "0" + String.valueOf(mCalendar.get(Calendar.HOUR_OF_DAY));
                                                                           else
                                                                               ret += String.valueOf(mCalendar.get(Calendar.HOUR_OF_DAY));

                                                                           ret += ":";
                                                                           if(String.valueOf(mCalendar.get(Calendar.MINUTE)).length() < 2)
                                                                               ret += "0" + String.valueOf(mCalendar.get(Calendar.MINUTE));
                                                                           else
                                                                               ret += String.valueOf(mCalendar.get(Calendar.MINUTE));
                                                                           break;

                                                                       case Month:

                                                                           if(String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH)).length() < 2)
                                                                               ret += "0" + String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH));
                                                                           else
                                                                               ret += String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH));

                                                                           ret += new SimpleDateFormat("MMM").format(mCalendar.getTime());

                                                                           break;

                                                                       case Year:
                                                                           return String.valueOf(mCalendar.get(Calendar.YEAR)) + "/" + String.valueOf(mCalendar.get(Calendar.MONTH));
                                                                   }
                                                                   return ret;

                                                               } else {
                                                                   // show currency for y values
                                                                   return "$" + super.formatLabel(value, isValueX);
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
                updatingService.fetchHistoricalData(currentCurrency.getKey(), currentTime);
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
                currencyHistory = updatingService.getCurrencyHistoricalDataPoints(currentCurrency.getKey());
                setGraphToCoinHistoryData();
            }
            else {
                //error
                Log.d(LOG, "CoinHistory ERROR!");
            }
        }
    };

    private void setGraphToCoinHistoryData() {
        DataPoint[] graphPoints = new DataPoint[currencyHistory.dataPoints.size()];

        for (int i = 0; i < currencyHistory.dataPoints.size(); i++) {

            graphPoints[i] = new DataPoint(currencyHistory.dataPoints.get(i).timestamp.getTime(), currencyHistory.dataPoints.get(i).getClose());
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(graphPoints);
        graph.removeAllSeries();

        double minValY = 0;
        double maxValY = 0;
        for(int i = 0; i < graphPoints.length; i++)
        {
            if(i == 0)
            {
                minValY = graphPoints[i].getY();
                maxValY = graphPoints[i].getY();
            }
            if(graphPoints[i].getY() < minValY)
                minValY = graphPoints[i].getY();

            if(graphPoints[i].getY() > maxValY)
                maxValY = graphPoints[i].getY();
        }

        graph.getViewport().setXAxisBoundsManual(true);

        double xAxisSpaceAddition = 0;
        switch(currentTime)
        {
            case Hour:
                xAxisSpaceAddition = 1000*60*2; // 2 minutes
                break;

            case Day:
                xAxisSpaceAddition = 1000*60*60; // 1 Hour
                break;

            case Week:
                xAxisSpaceAddition = 1000*60*60*8; // 8 Hours
                break;

            case Month:
                xAxisSpaceAddition = 1000*60*60*24; //24 Hours
                break;
        }

        graph.getViewport().setMinX(graphPoints[0].getX() - xAxisSpaceAddition);
        graph.getViewport().setMaxX(graphPoints[graphPoints.length-1].getX() + xAxisSpaceAddition*2);
        graph.getViewport().setScalable(true);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(minValY - (maxValY-minValY)/5);
        graph.getViewport().setMaxY(maxValY + (maxValY-minValY)/5);
        graph.addSeries(series);

    }
}
