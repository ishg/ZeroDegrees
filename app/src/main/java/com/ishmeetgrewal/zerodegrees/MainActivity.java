package com.ishmeetgrewal.zerodegrees;

import android.Manifest;

import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationRequest;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String LOG = "MainActivity";


    FragmentManager fm;
    Fragment fragment;
    HomeFragment hf;
    PlacesFragment pf;
    ApparelFragment af;
    boolean first_run = true;


    Context context;
    Handler handler;
    private static final String FORECAST_API =
            "https://api.forecast.io/forecast/";

    //Google API
    private GoogleApiClient mGoogleApiClient;

    // Database
    DatabaseHelper db;
    User user;

    //Location
    double mLatitude;
    double mLongitude;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION  = 76;


    private Integer[] mThumbIds = {
            R.drawable.baseball_cap,
            R.drawable.t_shirt,
            R.drawable.shorts,
            R.drawable.flip_flops,

            R.drawable.beanie,
            R.drawable.shirt,
            R.drawable.trousers,
            R.drawable.trainers,

            R.drawable.mittens,
            R.drawable.coat,
            R.drawable.trousers,
            R.drawable.winter_boots,
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG, "OnCreate Triggered.");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this.getApplicationContext();
        handler = new Handler();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        db = new DatabaseHelper(this);
        if (!db.userExistsInDB()) {
            // launch login activity
            // get new user data
            // add new user to table
            Log.d(LOG, "User not found");

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            // load user from database
            user = db.getUser();

        }

        if(!db.clothesExistsInDB()){
            for (int cloth : mThumbIds) {
                db.createCloth(cloth);
            }
        }

        db.closeDB();


//        TextView userName = (TextView) findViewById(R.id.userNameView);
//        TextView userTemp = (TextView) findViewById(R.id.userTempView);
//
//        userName.setText(user.getName());
//        userTemp.setText(Integer.toString(user.getTemp()));

        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }



        fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.fragmentContainer);
        if (fragment == null){
            hf = new HomeFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, hf)
                    .commit();
        }

    }


    /* LOCATION METHODS */


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    public void getLocation(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitude = mLastLocation.getLatitude();
            mLongitude = mLastLocation.getLongitude();
            //Log.d(LOG, "Latitude: " + Double.toString(mLatitude));
           // Log.d(LOG, "Longitude: " + Double.toString(mLongitude));

            if(first_run) {
                HomeFragment hfm = (HomeFragment) fm.findFragmentById(R.id.fragmentContainer);
                hfm.updateWeatherData(mLastLocation);
                first_run = false;
            }

        } else {
            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        //Log.d(LOG, "Connected to Google Play Services");
        getLocation();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d(LOG, "Permission Granted");
                    getLocation();

                } else {
                    Log.d(LOG, "Permission Denied");
                    Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.d(LOG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode() + result.getErrorMessage());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.d(LOG, "Connection suspended");
        mGoogleApiClient.connect();
    }




    /* APP METHODS */

    @Override
    protected void onStart() {
        //Log.d(LOG, "OnStart Triggered.");
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onResume() {
        //Log.d(LOG, "OnResume Triggered.");
        super.onResume();
        checkPlayServices();
    }

    @Override
    protected void onStop() {
        //Log.d(LOG, "OnStop Triggered.");
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        //Log.d(LOG, "OnPause Triggered.");
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        fragment = fm.findFragmentById(R.id.fragmentContainer);

        if(id == R.id.nav_home){
            if(hf==null){
                hf = new HomeFragment();
            }


            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragmentContainer, hf);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
        else if (id == R.id.nav_places) {
            if(pf == null){
                pf = new PlacesFragment();
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragmentContainer, pf);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        } else if (id == R.id.nav_apparel) {
            if(af == null){
                af = new ApparelFragment();
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragmentContainer, af);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public Location getCurrentLocation(){
        return mLastLocation;
    }

}
