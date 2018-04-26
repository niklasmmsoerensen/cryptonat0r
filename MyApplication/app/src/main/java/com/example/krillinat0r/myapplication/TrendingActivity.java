package com.example.krillinat0r.myapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TrendingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String LOG = "TRENDING_ACTIVITY";

    //firebase database reference
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("trending");
    List<TrendingCurrency> trendingCurrencyList = new ArrayList<>();

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ListView trendingCurrencies;
    private TrendingCurrencyListAdapter adapter;

    //updating service connection
    private UpdatingService updatingService;
    private ServiceConnection updatingServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //setup connection to updatingService
        setupConnectionToUpdatingService();
        //bind to service
        bindService(new Intent(TrendingActivity.this,
                UpdatingService.class), updatingServiceConnection, Context.BIND_AUTO_CREATE);

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //ref https://stackoverflow.com/questions/39925871/android-firebase-cant-convert-object-of-type-java-util-arraylist-to-type-my
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                List<TrendingCurrency> tempList = new ArrayList<>();
                for(DataSnapshot templateSnapshot : dataSnapshot.getChildren()){
                    TrendingCurrency currency = templateSnapshot.getValue(TrendingCurrency.class);
                    tempList.add(currency);
                }
                trendingCurrencyList.clear();
                trendingCurrencyList.addAll(tempList);
                //notify adapter we updated the list
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d(LOG, "Failed to read value.", error.toException());
            }
        });

        trendingCurrencies = findViewById(R.id.listTrendingCurrencies);
        //Init adapter and setup connection
        adapter = new TrendingCurrencyListAdapter(getApplicationContext(), trendingCurrencyList);
        trendingCurrencies.setAdapter(adapter);

        registerForContextMenu(trendingCurrencies);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.listTrendingCurrencies) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.trending_currency_longpress_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        TrendingCurrency currency = trendingCurrencyList.get(info.position);
        String itemTitle = item.toString();
        String subscribeItem = getString(R.string.menu_subscribe);
        if(itemTitle.equals(subscribeItem)) {
            updatingService.addCoin(currency.getTitle());
            return true;
        }
        else {
            return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onStart()
    {
        navigationView.getMenu().getItem(1).setChecked(true);
        super.onStart();
    }

    @Override
    public void onStop() {
        unbindService(updatingServiceConnection);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.trending, menu);
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

        if (id == R.id.nav_my_currencies)
        {
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupConnectionToUpdatingService(){
        updatingServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                Log.d(LOG, "onServiceConnected");
                updatingService = ((UpdatingService.UpdatingServiceBinder)service).getService();
                Log.d(LOG, "Updating service connected");
            }

            public void onServiceDisconnected(ComponentName className) {
                //updatingService = null;
                Log.d(LOG, "Updating service disconnected");
            }
        };
    }
}
