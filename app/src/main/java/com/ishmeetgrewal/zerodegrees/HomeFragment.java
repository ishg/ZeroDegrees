package com.ishmeetgrewal.zerodegrees;

import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by ishmeet on 3/27/16.
 */
public class HomeFragment extends Fragment {

    private static final String LOG = "HomeFragment";

    double currLocLat;
    double currLocLon;
    Location currLoc;
    Geocoder geocoder;

    Context context;
    Typeface weatherFont;
    private String url;
    private static final String FORECAST_API =
            "https://api.forecast.io/forecast/";

    static final String STATE_LAT = "currLocLat";
    static final String STATE_LON = "currLocLon";

    //UI ELEMENTS
    TextView windTextView, precipTextView, visibilityTextView, actualTempView, customTempView, locationTextView;
    TextView weatherIcon, windImageView, precipImageView, visibilityImageView;
    ImageView headApparel, torsoApparel, legsApparel, feetApparel;

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

    //Database
    DatabaseHelper db;
    User user;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(LOG, "HomeFragment savedinstance state called");

        // Save the user's current game state
        savedInstanceState.putDouble(STATE_LAT, currLocLat);
        savedInstanceState.putDouble(STATE_LON, currLocLon);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        geocoder = new Geocoder(context);
        Log.d(LOG, "HomeFragment - onCreate");


        // Check whether we're recreating a previously destroyed instance

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setTitle("ZeroDegrees");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        db = new DatabaseHelper(context);

        if(currLoc != null){
            Log.d(LOG, "HomeFragment - currLoc is not null");
            updateWeatherData(currLoc);
        }else{
            Log.d(LOG, "HomeFragment - currLoc is null");
        }

        if(db.userExistsInDB()) {
            user = db.getUser();
        }

        if(!db.locationsExistInDB()){
            try {
                List<Address> addresses = geocoder.getFromLocationName(Integer.toString(user.getHome()), 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    Place place = new Place(-1, address.getLocality(), address.getLatitude(), address.getLongitude());
                    long loc_id = db.createLocation(place);
                    place.setId(loc_id);
                } else {
                    // Display appropriate message when Geocoder services are not available
                    Toast.makeText(context, "Unable to geocode home zipcode", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                // handle exception
            }
        }

        Log.d(LOG, "HomeFragment - OnCreateView");

        db.closeDB();

        weatherFont = Typeface.createFromAsset(context.getAssets(), "fonts/weather.ttf");

        locationTextView = (TextView) rootView.findViewById(R.id.locationTextView);
        customTempView = (TextView) rootView.findViewById(R.id.customTempView);
        actualTempView = (TextView) rootView.findViewById(R.id.actualTempView);
        windTextView = (TextView) rootView.findViewById(R.id.windTextView);
        precipTextView = (TextView) rootView.findViewById(R.id.precipTextView);
        visibilityTextView = (TextView) rootView.findViewById(R.id.visibilityTextView);

        weatherIcon = (TextView) rootView.findViewById(R.id.currConditionView);
        windImageView = (TextView) rootView.findViewById(R.id.windImageView);
        precipImageView = (TextView) rootView.findViewById(R.id.precipImageView);
        visibilityImageView = (TextView) rootView.findViewById(R.id.visibilityImageView);

        headApparel = (ImageView) rootView.findViewById(R.id.headApparel);
        torsoApparel = (ImageView) rootView.findViewById(R.id.torsoApparel);
        legsApparel = (ImageView) rootView.findViewById(R.id.legsApparel);
        feetApparel = (ImageView) rootView.findViewById(R.id.feetApparel);

        weatherIcon.setTypeface(weatherFont);
        windImageView.setTypeface(weatherFont);
        precipImageView.setTypeface(weatherFont);
        visibilityImageView.setTypeface(weatherFont);

        return rootView;
    }

    public void updateWeatherData(Location mLastLocation){
        this.currLoc = mLastLocation;

        this.url = FORECAST_API + context.getString(R.string.forecast_app_id) + "/" + Double.toString(currLoc.getLatitude()) + "," + Double.toString(currLoc.getLongitude());


        new Thread(){
            public void run(){
                RequestQueue queue = Volley.newRequestQueue(context);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String res) {
                                // Display the first 500 characters of the response string.
                                //Log.d(LOG, "Response is: " + res.substring(0, 500));
                                renderWeather(res);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG, "That didn't work!");
                        db = new DatabaseHelper(context);
                        if (db.locationsExistInDB()){
                            renderStoredWeather();
                            Toast.makeText(context,
                                    "You are offline.",
                                    Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(context,
                                    "Please go online to use the app.",
                                    Toast.LENGTH_LONG).show();
                        }


                    }
                });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        }.start();
    }

    private void renderWeather(String response){
        try {
            //Log.d(LOG, "Ready to render weather");
            //Log.d(LOG, response);

            //TODO: Parse the JSON and update the view of the main page
            //Refer to the commented out area below for help
            //Or look at the documentation for how to work with JSONObject
            //http://developer.android.com/reference/org/json/JSONObject.html

            JSONObject main = new JSONObject(response);
            JSONObject current = main.getJSONObject("currently");

            //current weather icon
            setWeatherIcon(current.getString("icon"));

            //current temperature
            int temp = current.getInt("temperature");
            String current_temp = Integer.toString(temp) + " \u2109";
            actualTempView.setText(current_temp);

            //adjusted temperature
            String adjusted_temp = Integer.toString(temp - user.getTemp());
            customTempView.setText(adjusted_temp);

            //wind speed
            String wind_speed = Integer.toString(current.getInt("windSpeed")) + " mph";
            windTextView.setText(wind_speed);
            windImageView.setText(this.getString(R.string.weather_icon_wind));

            //chance of precipitation
            String precipitation = current.getString("precipProbability") + "%";
            precipTextView.setText(precipitation);
            precipImageView.setText(this.getString(R.string.weather_icon_precip));

            //visibility
            String visibility = Integer.toString(current.getInt("visibility")) + " miles";
            visibilityTextView.setText(visibility);
            visibilityImageView.setText(this.getString(R.string.weather_icon_visibility));

            int clothes_index = 0;
            int adj_temp = temp - user.getTemp();
            if(adj_temp >= -5 ){
                clothes_index = 0;
            }else if(adj_temp < -5 && adj_temp > -20){
                clothes_index = 4;
            }else{
                clothes_index = 8;
            }

            headApparel.setImageResource(mThumbIds[clothes_index]);
            torsoApparel.setImageResource(mThumbIds[clothes_index + 1]);
            legsApparel.setImageResource(mThumbIds[clothes_index + 2]);
            feetApparel.setImageResource(mThumbIds[clothes_index + 3]);


        }catch(Exception e){
            Log.e(LOG, "One or more fields not found in the JSON data");
        }
    }
    private void renderStoredWeather(){

        db = new DatabaseHelper(context);
        Place place = db.getLocation("Columbus");


        //current weather icon
        setWeatherIcon("clear-day");

        //current temperature
        int temp = place.getTemp();
        temp = temp + user.getTemp();
        String current_temp = Integer.toString(temp) + " \u2109";
        actualTempView.setText(current_temp);

        //adjusted temperature
        String adjusted_temp = Integer.toString(place.getTemp());
        customTempView.setText(adjusted_temp);

        //wind speed
        windTextView.setText(place.getWindSpeed());
        windImageView.setText(this.getString(R.string.weather_icon_wind));

        //chance of precipitation
        precipTextView.setText(place.getPrecipitation());
        precipImageView.setText(this.getString(R.string.weather_icon_precip));

        //visibility
        visibilityTextView.setText(place.getVisibility());
        visibilityImageView.setText(this.getString(R.string.weather_icon_visibility));

        int clothes_index = 0;
        int adj_temp = place.getTemp();
        if(adj_temp >= -5 ){
            clothes_index = 0;
        }else if(adj_temp < -5 && adj_temp > -20){
            clothes_index = 4;
        }else{
            clothes_index = 8;
        }

        headApparel.setImageResource(mThumbIds[clothes_index]);
        torsoApparel.setImageResource(mThumbIds[clothes_index + 1]);
        legsApparel.setImageResource(mThumbIds[clothes_index + 2]);
        feetApparel.setImageResource(mThumbIds[clothes_index + 3]);
    }


    private void setWeatherIcon(String icon){
        //Log.d(LOG, icon);
        switch(icon) {
            case "clear-day" : icon = this.getString(R.string.weather_clear_day);
                break;
            case "clear-night" : icon = this.getString(R.string.weather_clear_night);
                break;
            case "partly-cloudy-day" : icon = this.getString(R.string.weather_partly_cloudy_day);
                break;
            case "partly-cloudy-night" : icon = this.getString(R.string.weather_partly_cloudy_night);
                break;
            case "wind" : icon = this.getString(R.string.weather_wind);
                break;
            case "fog" : icon = this.getString(R.string.weather_fog);
                break;
            case "cloudy" : icon = this.getString(R.string.weather_cloudy);
                break;
            case "rain" : icon = this.getString(R.string.weather_rain);
                break;
            case "snow" : icon = this.getString(R.string.weather_snow);
                break;
            case "drizzle" : icon = this.getString(R.string.weather_drizzle);
                break;
            case "thunder" : icon = this.getString(R.string.weather_thunder);
                break;
        }
        weatherIcon.setText(icon);
    }


}
