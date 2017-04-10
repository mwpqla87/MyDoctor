package com.example.pc.mydoctordemo.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pc.mydoctordemo.R;
import com.example.pc.mydoctordemo.ui.heartRateNotifyListener.MyHeartRateNotifyListener;
import com.example.pc.mydoctordemo.ui.item.MibandPulseRecordItem;
import com.jellygom.miband_sdk.MiBandIO.MibandCallback;
import com.jellygom.miband_sdk.Miband;

import java.util.ArrayList;
import java.util.List;

import config.GlobalConstance;

/**
 * Created by PC on 2017-04-08.
 */

public class MibandPulseManager extends BaseFragment implements View.OnClickListener{
    private ArrayList<MibandPulseRecordItem> itemList;
    private View myView;
    private static Miband myMiband;
    private static MibandCallback mibandCallback;
    private static MyHeartRateNotifyListener heartrateNotify;




    static public MibandPulseManager newInstance(Context pContext, Miband miband , MibandCallback callback, MyHeartRateNotifyListener heartrateNotifyListener){
        mContext = pContext;
        myMiband = miband;
        mibandCallback = callback;
        heartrateNotify = heartrateNotifyListener;



        return new MibandPulseManager();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        itemList = new ArrayList<>();
        setList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.miband_pulse_controll, container, false);

        TextView displayPulse = (TextView) myView.findViewById(R.id.display_pulse_data);
        heartrateNotify.setTextView(displayPulse);

        Log.d(GlobalConstance.LOGCAT_DEFAULT_TAG, "IN onCreateView");

        ListView listView = (ListView) myView.findViewById(R.id.pulse_list);
        listView.setAdapter(new MibandRecordListAdapter(itemList));

        myView.findViewById(R.id.check_pulse_btn).setOnClickListener(this);



        return myView;
    }

    public void setList(){
        itemList.add(new MibandPulseRecordItem("4","06","04","38","오후","81"));
        itemList.add(new MibandPulseRecordItem("4","06","04","38","오후","81"));
        itemList.add(new MibandPulseRecordItem("4","06","04","38","오후","81"));
        itemList.add(new MibandPulseRecordItem("4","06","04","38","오후","81"));
        itemList.add(new MibandPulseRecordItem("4","06","04","38","오후","81"));

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.check_pulse_btn){
          ///  myMiband.sendAlert(this.mibandCallback);
            myMiband.startHeartRateScan(1, mibandCallback);
            int data = heartrateNotify.getHeartRate();

           // Toast.makeText(mContext.getApplicationContext(), data+"bpm", Toast.LENGTH_LONG).show();

        }
    }


    private class MibandRecordListAdapter extends BaseAdapter {
        private List<MibandPulseRecordItem> list = null;
        private LayoutInflater inflater = null;

        public MibandRecordListAdapter(List<MibandPulseRecordItem> list){
            this.list = list;
            inflater = LayoutInflater.from(getContext());
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemLayout = convertView;
            if(itemLayout == null){
                itemLayout = inflater.inflate(R.layout.miband_pulse_list_view, parent, false);
            }

            TextView month = (TextView) itemLayout.findViewById(R.id.month);
            TextView day = (TextView) itemLayout.findViewById(R.id.day);
            TextView time = (TextView) itemLayout.findViewById(R.id.time);
            TextView minute = (TextView) itemLayout.findViewById(R.id.minute);
            TextView when = (TextView) itemLayout.findViewById(R.id.when);
            TextView pulse = (TextView) itemLayout.findViewById(R.id.pulse);



            month.setText(list.get(position).getMonth());
            day.setText(list.get(position).getDay());
            time.setText(list.get(position).getTime());
            minute.setText(list.get(position).getMinute());
            when.setText(list.get(position).getWhen());
            pulse.setText(list.get(position).getPulse());

            return itemLayout;
        }
    }
}
