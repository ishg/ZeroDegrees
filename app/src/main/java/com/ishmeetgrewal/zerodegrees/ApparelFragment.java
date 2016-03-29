package com.ishmeetgrewal.zerodegrees;

import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by ishmeet on 3/27/16.
 */
public class ApparelFragment extends Fragment {

    Context context;
    private static final String LOG = "MainActivity";
    ApparelImageAdapter mAdapter;

    public ApparelFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        ((MainActivity) getActivity()).setTitle("Clothes");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_apparel, container, false);

        final GridView gridview = (GridView) rootView.findViewById(R.id.apparel_gridView);

        mAdapter = new ApparelImageAdapter(context);
        gridview.setAdapter(mAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // Update whether user has or not
                ImageView imageView = (ImageView) v;
                imageView.setColorFilter(R.color.white);
                imageView.setImageResource(R.drawable.bra);
                Apparel item = mAdapter.getApparel(position);
                Apparel new_item = new Apparel(item.getIcon(), !item.getUserHas());
                mAdapter.updateClothes(position, new_item);
                Log.d(LOG, Boolean.toString(item.getUserHas()));

            }
        });

        return rootView;
    }
}
