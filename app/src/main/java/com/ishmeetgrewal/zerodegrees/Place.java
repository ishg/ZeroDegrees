package com.ishmeetgrewal.zerodegrees;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class Place {

    private Context context;
    String url;
    private static final String FORECAST_API = "https://api.forecast.io/forecast/";
    private static final String LOG = "PlaceModel";

    double lat;
    double lon;
    String name;
    int temp;
    String windSpeed;
    String visibility;
    String precipitation;

    public Place(Context c, String name, double lat, double lon){
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.context = c;
        this.updateWeatherData();
    }

    public String getName(){
        return name;
    }

    public double getLat(){
        return lat;
    }

    public double getLon(){
        return lon;
    }

    public int getTemp(){
        return temp;
    }

    public String getVisibility(){
        return visibility;
    }

    public String getPrecipitation(){
        return precipitation;
    }

    public String getWindSpeed(){
        return windSpeed;
    }

    public void updateWeatherData(){

        this.url = FORECAST_API + context.getString(R.string.forecast_app_id) + "/" + Double.toString(this.getLat()) + "," + Double.toString(this.getLon());


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

            JSONObject main = new JSONObject(response);
            JSONObject current = main.getJSONObject("currently");


            //current temperature
            this.temp = current.getInt("temperature") -73;

            //wind speed
            this.windSpeed = Integer.toString(current.getInt("windSpeed")) + " mph";

            //chance of precipitation
            this.precipitation = current.getString("precipProbability") + "%";

            //visibility
            this.visibility = Integer.toString(current.getInt("visibility")) + " miles";


        }catch(Exception e){
            Log.e(LOG, "One or more fields not found in the JSON data");
        }
    }





}
