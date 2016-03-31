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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishmeet on 3/27/16.
 */
public class PlacesFragment extends Fragment {

    private static final String LOG = "PlacesFragment";

    Context context;
    Geocoder geocoder;
    Location location;

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

        if(mAdapter == null) {
            mAdapter = new LocationAdapter(context, R.layout.card);
            Place card = new Place(context, "Columbus", location.getLatitude(), location.getLongitude());
            mAdapter.add(card);
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
                        Place place = new Place(context, address.getLocality(), location.getLatitude(), location.getLongitude());
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










}
