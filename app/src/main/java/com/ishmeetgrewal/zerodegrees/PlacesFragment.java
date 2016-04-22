package com.ishmeetgrewal.zerodegrees;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishmeet on 3/27/16.
 */
public class PlacesFragment extends Fragment {

    private static final String LOG = "PlacesFragment";
    private static final String FORECAST_API = "https://api.forecast.io/forecast/";

    DatabaseHelper db;
    String url;
    Context context;
    Geocoder geocoder;
    Location location;
    long loc_id;
    Place place_for_update;

    private String m_Text = "";

    private ListView listView;
    private LocationAdapter mAdapter;

    public PlacesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        geocoder = new Geocoder(context);

        location = ((MainActivity) getActivity()).getCurrentLocation();

        Log.d(LOG, "PlacesFragment - onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((MainActivity) getActivity()).setTitle("Locations");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_places, container, false);


        // List View Create
        listView = (ListView) rootView.findViewById(R.id.card_listView);

        db = new DatabaseHelper(context);

        if(mAdapter == null) {

            ArrayList<Place> places;

            if (db.locationsExistInDB()){
                places = db.getAllPlaces();
            }else{
                places = new ArrayList<>();
            }

            mAdapter = new LocationAdapter(context, R.layout.card, places);
        }

        for(int i = 0; i < mAdapter.getCount(); i++){
            updateWeatherData(mAdapter.getItem(i));
        }

        listView.setAdapter(mAdapter);

        FloatingActionButton myFab = (FloatingActionButton) rootView.findViewById(R.id.add_location_fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addLocation();
            }
        });



        return rootView;
    }

    public void addLocation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add New Location");

        // Set up the input
        final EditText input = new EditText(context);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        int maxLength = 5;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        input.setFilters(fArray);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint(R.string.add_location_hint);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                Log.d(LOG, m_Text);

                try {
                    List<Address> addresses = geocoder.getFromLocationName(m_Text, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        // Use the address as needed
                        String message = String.format("Latitude: %f, Longitude: %f",
                                address.getLatitude(), address.getLongitude());
                        Log.d(LOG, message);

                        location = new Location(location);
                        location.setLatitude(address.getLatitude());
                        location.setLongitude(address.getLongitude());



                        //Add to adapter
                        Place place = new Place(-1, address.getLocality(), location.getLatitude(), location.getLongitude());
                        db = new DatabaseHelper(context);
                        loc_id = db.createLocation(place);
                        place.setId(loc_id);
                        updateWeatherData(place);
                        db.closeDB();
                        mAdapter.add(place);

                        //Log.d(LOG, "Locations size = " + Integer.toString(locations.size()));

                    } else {
                        // Display appropriate message when Geocoder services are not available
                        Toast.makeText(context, "Unable to geocode zipcode", Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    // handle exception
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();



    }

    public void updateWeatherData(Place place){

        this.url = FORECAST_API + context.getString(R.string.forecast_app_id) + "/" + Double.toString(place.getLat()) + "," + Double.toString(place.getLon());
        this.place_for_update = place;

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
                                renderWeather(res, place_for_update);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG, "That didn't work!");
                    }
                });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        }.start();
    }

    private void renderWeather(String response, Place place){
        try {

            JSONObject main = new JSONObject(response);
            JSONObject current = main.getJSONObject("currently");

            place.setTemp(current.getInt("temperature") - 60);
            place.setWindSpeed(Integer.toString(current.getInt("windSpeed")) + " mph");
            place.setPrecipitation(current.getString("precipProbability") + "%");
            place.setVisibility(Integer.toString(current.getInt("visibility")) + " miles");

            db = new DatabaseHelper(context);
            db.updateLocation(place);
            mAdapter.notifyDataSetChanged();
            db.closeDB();

        }catch(Exception e){
            Log.e(LOG, "One or more fields not found in the JSON data");
        }
    }










}
