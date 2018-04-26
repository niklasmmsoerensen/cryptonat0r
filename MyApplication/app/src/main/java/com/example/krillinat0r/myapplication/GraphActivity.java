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
    private RadioGroup TimeScaleGroup;
    private GraphTime currentTime;

    private CurrencyHistoricalDataPoints currencyHistory;

    public enum GraphTime
    {
        Hour,
        Day,
        Week,
        Month,
        Year
    }

    public class CustomDateAsXAxisLabelFormatter extends DefaultLabelFormatter {
        protected final DateFormat mDateFormat;
        protected final Calendar mCalendar;

        public CustomDateAsXAxisLabelFormatter(Context context) {
            this.mDateFormat = android.text.format.DateFormat.getDateFormat(context);
            this.mCalendar = Calendar.getInstance();
        }

        public CustomDateAsXAxisLabelFormatter(Context context, DateFormat dateFormat) {
            this.mDateFormat = dateFormat;
            this.mCalendar = Calendar.getInstance();
        }

        public String formatLabel(double value, boolean isValueX) {
            if(isValueX) {
                this.mCalendar.setTimeInMillis((long)value);
                return this.mDateFormat.format(Long.valueOf(this.mCalendar.getTimeInMillis()));
            } else {
                return super.formatLabel(value, isValueX);
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        currentTime = GraphTime.Week;

        currentCurrency = (CurrencyData) getIntent().getParcelableExtra(CURRENCY);

        setupConnectionToUpdatingService();

        //bind to service
        bindService(new Intent(GraphActivity.this,
                UpdatingService.class), updatingServiceConnection, Context.BIND_AUTO_CREATE);

        TimeScaleGroup = findViewById(R.id.TimeGroup);
        TimeScaleGroup.check(R.id.LatestWeekBtn);
        TimeScaleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId)
                {
                    case R.id.LatestHourBtn:
                        currentTime = GraphTime.Hour;
                        break;
                    case R.id.LatestDayBtn:
                        currentTime = GraphTime.Day;
                        break;
                    case R.id.LatestWeekBtn:
                        currentTime = GraphTime.Week;
                        break;
                    case R.id.LatestMonthBtn:
                        currentTime = GraphTime.Month;
                        break;
                    case R.id.LatestYearBtn:
                        currentTime = GraphTime.Year;
                        break;
                }
                if(updatingService != null && updatingServiceConnection != null)
                {
                    updatingService.fetchHistoricalData(currentCurrency.getKey(), currentTime);
                }
            }
        });

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
                                                                   return super.formatLabel(value, isValueX) + "$";
                                                               }
                                                           }
                                                       });


        // set date label formatter
        //graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(GraphActivity.this));
        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        graph.getGridLabelRenderer().setHumanRounding(true);
        graph.getGridLabelRenderer().setLabelVerticalWidth(100);
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
