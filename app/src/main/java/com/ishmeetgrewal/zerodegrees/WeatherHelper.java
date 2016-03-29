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

public class WeatherHelper {

    // Logcat tag
    private static final String LOG = "WeatherHelper";
    private String response;
    private String url;

    Context context;
    private static final String FORECAST_API =
            "https://api.forecast.io/forecast/";

    public WeatherHelper(Context context){
        this.context = context;
    }



    public String getResponse(){
        return response;
    }

}
