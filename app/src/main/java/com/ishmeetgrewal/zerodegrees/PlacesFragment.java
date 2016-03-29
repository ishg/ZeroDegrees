package com.ishmeetgrewal.zerodegrees;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ishmeet on 3/27/16.
 */
public class PlacesFragment extends Fragment {

    Context context;

    TextView windTextView, precipTextView, visibilityTextView, actualTempView, customTempView, locationTextView;
    TextView weatherIcon, windImageView, precipImageView, visibilityImageView;
    Typeface weatherFont;

    public PlacesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

        ((MainActivity) getActivity()).setTitle("Locations");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_places, container, false);

        weatherFont = Typeface.createFromAsset(context.getAssets(), "fonts/weather.ttf");

        locationTextView = (TextView) rootView.findViewById(R.id.card_locationTextView);
        customTempView = (TextView) rootView.findViewById(R.id.card_customTempView);
        //actualTempView = (TextView) rootView.findViewById(R.id.card_actualTempView);

        windTextView = (TextView) rootView.findViewById(R.id.card_windTextView);
        precipTextView = (TextView) rootView.findViewById(R.id.card_precipTextView);
        visibilityTextView = (TextView) rootView.findViewById(R.id.card_visibilityTextView);

        //weatherIcon = (TextView) rootView.findViewById(R.id.currConditionView);
        windImageView = (TextView) rootView.findViewById(R.id.card_windImageView);
        precipImageView = (TextView) rootView.findViewById(R.id.card_precipImageView);
        visibilityImageView = (TextView) rootView.findViewById(R.id.card_visibilityImageView);

        //weatherIcon.setTypeface(weatherFont);
        windImageView.setTypeface(weatherFont);
        precipImageView.setTypeface(weatherFont);
        visibilityImageView.setTypeface(weatherFont);

        int temp = 45;
        String current_temp = Integer.toString(temp) + " \u2109";
        //actualTempView.setText(current_temp);

        //adjusted temperature
        String adjusted_temp = Integer.toString(temp - 70);
        customTempView.setText(adjusted_temp);

        //wind speed
        String wind_speed = Integer.toString(5) + " mph";
        windTextView.setText(wind_speed);
        windImageView.setText(this.getString(R.string.weather_icon_wind));

        //chance of precipitation
        String precipitation = "30" + "%";
        precipTextView.setText(precipitation);
        precipImageView.setText(this.getString(R.string.weather_icon_precip));

        //visibility
        String visibility = Integer.toString(10) + " miles";
        visibilityTextView.setText(visibility);
        visibilityImageView.setText(this.getString(R.string.weather_icon_visibility));





        return rootView;
    }










}
