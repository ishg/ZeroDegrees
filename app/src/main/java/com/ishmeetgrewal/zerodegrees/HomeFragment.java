package com.ishmeetgrewal.zerodegrees;

import android.content.Intent;
import android.graphics.Typeface;
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

/**
 * Created by ishmeet on 3/27/16.
 */
public class HomeFragment extends Fragment {

    private static final String LOG = "MainActivity";

    Location currLocation;
    Context context;
    Typeface weatherFont;
    private String url;
    private static final String FORECAST_API =
            "https://api.forecast.io/forecast/";

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        Log.d(LOG, "HomeFragment Created");
        ((MainActivity) getActivity()).setTitle("ZeroDegrees");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        db = new DatabaseHelper(context);
        if (!db.userExistsInDB()) {
            // launch login activity
            // get new user data
            // add new user to table
            Log.d(LOG, "User not found");

            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
        } else {
            // load user from database
            user = db.getUser();

        }

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
        this.currLocation = mLastLocation;

        this.url = FORECAST_API + context.getString(R.string.forecast_app_id) + "/" + Double.toString(mLastLocation.getLatitude()) + "," + Double.toString(mLastLocation.getLongitude());


        new Thread(){
            public void run(){
                RequestQueue queue = Volley.newRequestQueue(context);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String res) {
                                // Display the first 500 characters of the response string.
                                Log.d(LOG, "Response is: " + res.substring(0, 500));
                                renderWeather(res);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG, "That didn't work!");
                        Toast.makeText(context,
                                "Place not found",
                                Toast.LENGTH_LONG).show();
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
            Log.d(LOG, response);

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

    private void setWeatherIcon(String icon){
        Log.d(LOG, icon);
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
