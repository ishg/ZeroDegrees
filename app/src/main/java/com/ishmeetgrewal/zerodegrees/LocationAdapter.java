package com.ishmeetgrewal.zerodegrees;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishmeet on 3/30/16.
 */
public class LocationAdapter  extends ArrayAdapter<Place> {
    private static final String TAG = "CardArrayAdapter";
    private List<Place> cardList = new ArrayList<Place>();

    Typeface weatherFont;
    Context c;

    static class CardViewHolder {
        TextView name;
        TextView temp;
        TextView windspeed;
        TextView windImage;
        TextView precip;
        TextView preciImage;
        TextView visibility;
        TextView visibImage;
    }

    public LocationAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.c = context;
    }

    @Override
    public void add(Place object) {
        cardList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.cardList.size();
    }

    @Override
    public Place getItem(int index) {
        return this.cardList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CardViewHolder viewHolder;

        weatherFont = Typeface.createFromAsset(c.getAssets(), "fonts/weather.ttf");

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.card, parent, false);
            viewHolder = new CardViewHolder();

            viewHolder.name = (TextView) row.findViewById(R.id.card_locationTextView);
            viewHolder.temp = (TextView) row.findViewById(R.id.card_customTempView);
            viewHolder.precip = (TextView) row.findViewById(R.id.card_precipTextView);
            viewHolder.windspeed = (TextView) row.findViewById(R.id.card_windTextView);
            viewHolder.visibility = (TextView) row.findViewById(R.id.card_visibilityTextView);

            viewHolder.windImage = (TextView) row.findViewById(R.id.card_windImageView);
            viewHolder.preciImage = (TextView) row.findViewById(R.id.card_precipImageView);
            viewHolder.visibImage = (TextView) row.findViewById(R.id.card_visibilityImageView);
            viewHolder.windImage.setTypeface(weatherFont);
            viewHolder.preciImage.setTypeface(weatherFont);
            viewHolder.visibImage.setTypeface(weatherFont);


            row.setTag(viewHolder);
        } else {
            viewHolder = (CardViewHolder)row.getTag();
        }
        Place card = getItem(position);

        viewHolder.name.setText(card.getName());
        viewHolder.temp.setText(Integer.toString(card.getTemp()));
        viewHolder.precip.setText(card.getPrecipitation() + " %");
        viewHolder.windspeed.setText(card.getWindSpeed() + " mph");
        viewHolder.visibility.setText(card.getVisibility());

        viewHolder.windImage.setText(c.getString(R.string.weather_icon_wind));
        viewHolder.preciImage.setText(c.getString(R.string.weather_icon_precip));
        viewHolder.visibImage.setText(c.getString(R.string.weather_icon_visibility));


        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
