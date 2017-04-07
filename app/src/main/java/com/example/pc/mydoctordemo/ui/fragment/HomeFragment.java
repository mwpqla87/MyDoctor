package com.example.pc.mydoctordemo.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.pc.mydoctordemo.R;
import com.example.pc.mydoctordemo.ui.adapter.CustomGridViewAdapter;
import com.example.pc.mydoctordemo.ui.item.CheckedList;

import java.util.ArrayList;

import config.GlobalConstance;

/**
 * Created by PC on 2017-04-02.
 */

public class HomeFragment extends BaseFragment {
    private ArrayList<CheckedList> checkedLists;
    private View myView;
    private GridView gridView;

    static public HomeFragment newInstance(Context pContext){
        mContext = pContext;
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkedLists = new ArrayList<>();
        setList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.home, container, false);

        Log.d(GlobalConstance.LOGCAT_DEFAULT_TAG, "IN onCreateView");

        /**
         * create list view
         */
        gridView  = (GridView) myView.findViewById(R.id.home_gridview);
        CustomGridViewAdapter radapter = new CustomGridViewAdapter(mContext, R.layout.checkinglist, checkedLists);
        gridView.setAdapter(radapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mContext, checkedLists.get(position).getListName(), Toast.LENGTH_LONG).show();
            }
        });

        return myView;
    }
    public void setList(){
        checkedLists.add(new CheckedList("맥박", R.drawable.heart_pulse));
        checkedLists.add(new CheckedList("혈압", R.drawable.heart_pulse));
        checkedLists.add(new CheckedList("걸음수", R.drawable.heart_pulse));
        checkedLists.add(new CheckedList("맥박", R.drawable.heart_pulse));
        checkedLists.add(new CheckedList("맥박", R.drawable.heart_pulse));
        checkedLists.add(new CheckedList("맥박", R.drawable.heart_pulse));
        checkedLists.add(new CheckedList("맥박", R.drawable.heart_pulse));
        checkedLists.add(new CheckedList("맥박", R.drawable.heart_pulse));
    }

}
