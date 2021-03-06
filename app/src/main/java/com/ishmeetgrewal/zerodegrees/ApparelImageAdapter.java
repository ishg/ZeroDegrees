package com.ishmeetgrewal.zerodegrees;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by ishmeet on 3/28/16.
 */
public class ApparelImageAdapter extends BaseAdapter {
    private Context mContext;
    DatabaseHelper db;

    private static final String LOG = "ImageAdapter";
    private ArrayList<Apparel> clothes;


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

    public ApparelImageAdapter(Context c) {
        mContext = c;
        clothes = new ArrayList<Apparel>();
        Apparel item;
        db = new DatabaseHelper(c);

        for (int cloth:  mThumbIds) {
            item = new Apparel(cloth, db.clothIsOwned(cloth));
            clothes.add(item);
        }

        db.closeDB();

    }

    public int getCount() {
        return 12;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(300, 300));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(20, 20, 20, 20);

        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(clothes.get(position).getIcon());

        if(clothes.get(position).getUserHas()){
            imageView.setColorFilter(null);
            imageView.setBackgroundColor(mContext.getResources().getColor(R.color.green));
        }else{
            imageView.setColorFilter(R.color.gray);
            imageView.setBackgroundColor(mContext.getResources().getColor(R.color.background));
        }

        return imageView;
    }

    public Apparel getApparel(int position){
        return clothes.get(position);
    }

    public void updateClothes(int position, Apparel item){
        clothes.set(position, item);
        this.notifyDataSetChanged();
    }

}
