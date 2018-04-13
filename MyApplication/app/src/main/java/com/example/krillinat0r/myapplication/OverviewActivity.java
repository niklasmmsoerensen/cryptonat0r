package com.example.krillinat0r.myapplication;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class OverviewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ListView CurrencyList;
    private CurrencyListAdapter adapter;
    private IntentFilter filter;
    private List<CurrencyData> mCurrencyDataList = new ArrayList<>();

    //for bound counting service
    private UpdatingService updatingService;
    private ServiceConnection updatingServiceConnection;
    private boolean bound = false;

    //Drawer / navigationview
    private DrawerLayout drawer;
    private NavigationView navigationView;

    //Add new Coin
    FloatingActionButton AddNewCoin;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        CurrencyList = findViewById(R.id.Currency_List);

        //Init adapter and setup connection
        adapter = new CurrencyListAdapter(getApplicationContext(), mCurrencyDataList);
        CurrencyList.setAdapter(adapter);
        setupConnectionToCountingService();

        CurrencyData data1 = new CurrencyData();
        data1.set_coinName("ETH");
        data1.set_coinPrice(1000);
        data1.set_percentChange(5);
        CurrencyData data2 = new CurrencyData();
        data2.set_coinName("DOGE");
        data2.set_coinPrice(0.1f);
        data2.set_percentChange(4);
        mCurrencyDataList.add(data1);
        mCurrencyDataList.add(data2);

        //Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Setup AlertDialog
        builder = new AlertDialog.Builder(OverviewActivity.this);
        builder.setTitle(getString(R.string.InsertCoinName));

        //Setup AddNewCoin button
        AddNewCoin = (FloatingActionButton) findViewById(R.id.add_Coin_Btn);
        AddNewCoin.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                final EditText input = new EditText(OverviewActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CheckIfValidCoin coinDownloader = new CheckIfValidCoin();
                                coinDownloader.execute(input.getText().toString());
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
        });

        //Setup drawer + navigationView
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onStart()
    {
        navigationView.getMenu().getItem(0).setChecked(true);
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_trending_currencies)
        {
            Intent startTrendingActivity = new Intent(OverviewActivity.this, TrendingActivity.class);
            startActivity(startTrendingActivity);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupConnectionToCountingService(){
        updatingServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {

                updatingService = ((UpdatingService.UpdatingServiceBinder)service).getService();
                Log.d("Binder", "Counting service connected");
            }

            public void onServiceDisconnected(ComponentName className) {

                //updatingService = null;
                Log.d("Binder", "Counting service disconnected");
            }
        };
    }

    public class CheckIfValidCoin extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            return AddCoinToOverview(strings[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result)
            {
                Toast.makeText(OverviewActivity.this, getString(R.string.AddCoinSuccesful), Toast.LENGTH_SHORT).show();
                /*if(UpdatingService != null)
                    UpdatingService.UpdateList();*/
            }
            else
            {
                Toast.makeText(OverviewActivity.this, R.string.AddCoinFailed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean AddCoinToOverview(String coinName)
    {
        return true;
    }
}
