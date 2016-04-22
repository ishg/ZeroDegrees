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
    private static final String LOG = "ApparelFragment";
    ApparelImageAdapter mAdapter;
    DatabaseHelper db;

    public ApparelFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();


        Log.d(LOG, "ApparelFragment - onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((MainActivity) getActivity()).setTitle("Clothes");
        Log.d(LOG, "ApparelFragment - onCreateView");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_apparel, container, false);

        final GridView gridview = (GridView) rootView.findViewById(R.id.apparel_gridView);

        if(mAdapter == null){
            Log.d(LOG, "ApparelFragment - mAdapter is null");
            mAdapter = new ApparelImageAdapter(context);
        }else{
            Log.d(LOG, "ApparelFragment - old mAdapter restored");
        }

        gridview.setAdapter(mAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // Update whether user has or not
                db = new DatabaseHelper(context);
                Apparel item = mAdapter.getApparel(position);
                Apparel new_item = new Apparel(item.getIcon(), !item.getUserHas());
                db.updateCloth(item.getIcon(), !item.getUserHas());
                mAdapter.updateClothes(position, new_item);
                db.closeDB();

            }
        });

        return rootView;
    }

}
