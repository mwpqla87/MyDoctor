package com.example.pc.mydoctordemo.ui.fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pc.mydoctordemo.R;
import com.example.pc.mydoctordemo.ui.activity.MainActivity;
import com.example.pc.mydoctordemo.ui.heartRateNotifyListener.MyHeartRateNotifyListener;
import com.example.pc.mydoctordemo.ui.item.CheckedList;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jellygom.miband_sdk.MiBandIO.Listener.HeartrateListener;
import com.jellygom.miband_sdk.MiBandIO.Listener.NotifyListener;
import com.jellygom.miband_sdk.MiBandIO.Listener.RealtimeStepListener;
import com.jellygom.miband_sdk.MiBandIO.MibandCallback;
import com.jellygom.miband_sdk.MiBandIO.Model.UserInfo;
import com.jellygom.miband_sdk.Miband;

import java.util.ArrayList;
import java.util.List;

import config.GlobalConstance;

/**
 * Created by PC on 2017-04-03.
 */

public class MibandControllFragment extends BaseFragment implements View.OnClickListener{
    private View myView;
    private static final String TAG = "Mobile";
    private PieChart pieChart;
    private Miband miband;
    private BluetoothAdapter mBluetoothAdapter;

    private TextView heart, step, battery;
    private TextView text;
    private TextView walkView;
    private List<CheckedList> checkList ;
    MyMibandCallBack callback;
    private MyHeartRateNotifyListener heartRateListener;
    private ListView listView;


    static final public int MESSAGE_REDRAW = 1;

    static public MibandControllFragment newInstance(Context pContext){
        mContext = pContext;

        return new MibandControllFragment();
    }

    private RealtimeStepListener realtimeStepListener = new RealtimeStepListener() {
        @Override
        public void onNotify(final int steps) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    step.setText(steps + " steps");
                    text.append(steps + " steps\n");
                }
            });
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        heartRateListener = new MyHeartRateNotifyListener(getActivity());
        checkList = new ArrayList<>();

    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.miband_controll, container, false);

        Log.d(GlobalConstance.LOGCAT_DEFAULT_TAG, "IN onCreateView");


        myView.findViewById(R.id.get_battery_btn).setOnClickListener(this);
        myView.findViewById(R.id.find_miband_btn).setOnClickListener(this);
        myView.findViewById(R.id.get_waling_btn).setOnClickListener(this);

        walkView = (TextView) myView.findViewById(R.id.walkView);




        setupPieChart();

        listView = (ListView) myView.findViewById(R.id.miband_checklist);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            FragmentManager fragmentManager = getFragmentManager();
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mContext.getApplicationContext(), checkList.get(position).getListName(),Toast.LENGTH_LONG).show();
            }
        });
        final MibandCheckListAdapter adapter = new MibandCheckListAdapter(checkList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    MainActivity activity = (MainActivity) getActivity();
                    activity.mibandItemClickCallback(position,miband, callback,heartRateListener);
                }
                else{
//                    Thread.run()
                    miband.getCurrentSteps(callback);




                }

            }
        });




        mBluetoothAdapter = ((BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();

        miband = new Miband(mContext);
        callback = new MyMibandCallBack(miband, heartRateListener, getActivity(), walkView);

        miband.searchDevice(mBluetoothAdapter, callback);

        miband.setDisconnectedListener(new NotifyListener() {
            @Override
            public void onNotify(byte[] data) {
                miband.searchDevice(mBluetoothAdapter, callback);
            }
        });


        setCheckList();

        return myView;
    }

    private void setupPieChart() {
        float rainfall[] = {500f, 600f};
        String monthNames[] = {"work", "rest"};
        List<PieEntry> pieEntries = new ArrayList<>();
        for(int i = 0; i < rainfall.length; i++){
            pieEntries.add(new PieEntry(rainfall[i], monthNames[i]));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "   목표");
        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        PieData data = new PieData(dataSet);

        //Get the Chart
        pieChart = (PieChart) myView.findViewById(R.id.walkchart);
        pieChart.setData(data);
        pieChart.animateY(1000);
        pieChart.invalidate();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.find_miband_btn){
            miband.sendAlert(callback);
        }
        else if(id == R.id.get_battery_btn){
            miband.getBatteryLevel(callback);
        }
        else{
            miband.getCurrentSteps(callback);
        }


    }
    public void setCheckList(){
        checkList.add(new CheckedList("맥박",R.drawable.pulse,80));

        checkList.add(new CheckedList("걸음수",R.drawable.walk,100));
    }

    public void setWalingData(int steps) {
        Log.e("ABC","전달받은 값"+steps);
    }

    public class MibandCheckListAdapter extends BaseAdapter {

        private List<CheckedList> checkList = null;
        private LayoutInflater inflater = null;

        public MibandCheckListAdapter(List<CheckedList> list){
            this.checkList = list;
            inflater = LayoutInflater.from(getContext());
        }
        @Override
        public int getCount() {
            return checkList.size();
        }

        @Override
        public Object getItem(int position) {
            return checkList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemLayout = convertView;
            if(itemLayout == null){
                itemLayout = inflater.inflate(R.layout.miband_checklist, parent, false);
            }


            ImageView imageView = (ImageView) itemLayout.findViewById(R.id.imageView2);
            TextView companyName = (TextView) itemLayout.findViewById(R.id.textView2);
            TextView data = (TextView) itemLayout.findViewById(R.id.textView3);

            imageView.setImageResource(checkList.get(position).getImageIcon());
            companyName.setText(checkList.get(position).getListName());


            String a = Integer.toString(checkList.get(position).getData());
            data.setText(a);


            return itemLayout;
        }
    }

    private void changeWalkingData(int steps) {
        checkList.get(1).setData(steps);
        listView.invalidate();
        MibandCheckListAdapter adapter = new MibandCheckListAdapter(checkList);
        listView.setAdapter(adapter);
    }


    /**
     * INNER CLASS CALLBACK
     */
    public class MyMibandCallBack implements MibandCallback {
        private Miband miband;
        private static final String TAG = "Mobile";
        private HeartrateListener heartrateNotifyListener;
        private Activity activity;
        private int walkingData;
        private Context mContext;
        MibandControllFragment controll;
        TextView walkView;

        public MyMibandCallBack(Miband miband,HeartrateListener heartrateNotifyListener, Activity activity, TextView textView){
            this.heartrateNotifyListener = heartrateNotifyListener;
            this.miband = miband;
            this.activity = activity;
            this.walkView = textView;
            this.controll = controll;
        }
        public int getWalkingData(){
            Log.e("ABC",walkingData+"steps1get");
            return walkingData;
        }

        @Override
        public void onSuccess(Object data, int status) {
            switch (status) {
                case MibandCallback.STATUS_SEARCH_DEVICE:
                    Log.e(TAG, "성공: STATUS_SEARCH_DEVICE");
                    miband.connect((BluetoothDevice) data, this);
                    break;
                case MibandCallback.STATUS_CONNECT:
                    Log.e(TAG, "성공: STATUS_CONNECT");
                    miband.getUserInfo(this);
                    break;
                case MibandCallback.STATUS_SEND_ALERT:
                    Log.e(TAG, "성공: STATUS_SEND_ALERT");
                    break;
                case MibandCallback.STATUS_GET_USERINFO:
                    Log.e(TAG, "성공: STATUS_GET_USERINFO");
                    UserInfo userInfo = new UserInfo().fromByteData(((BluetoothGattCharacteristic) data).getValue());
                    miband.setUserInfo(userInfo, this);

                    break;
                case MibandCallback.STATUS_SET_USERINFO:
                    Log.e(TAG, "성공: STATUS_SET_USERINFO");
                    miband.setHeartRateScanListener(heartrateNotifyListener);
                    break;
                case MibandCallback.STATUS_START_HEARTRATE_SCAN:
                    Log.e(TAG, "성공: STATUS_START_HEARTRATE_SCAN");
                    break;
                case MibandCallback.STATUS_GET_BATTERY:
                    Log.e(TAG, "성공: STATUS_GET_BATTERY");
                    final int level = (int) data;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity.getApplicationContext(), level + "%", Toast.LENGTH_LONG).show();
                        }
                    });
                    break;
                case MibandCallback.STATUS_GET_ACTIVITY_DATA:
                    Log.e(TAG, "성공: STATUS_GET_ACTIVITY_DATA");
                    final int steps = (int) data;
                    walkingData = steps;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity.getApplicationContext(), steps + "steps", Toast.LENGTH_LONG).show();
                            walkView.setText(steps+" steps");

                            changeWalkingData(steps);

                        }
                    });


                    break;
            }

        }

        @Override
        public void onFail(int errorCode, String msg, int status) {
            switch (status) {
                case MibandCallback.STATUS_SEARCH_DEVICE:
                    Log.e(TAG, "실패: STATUS_SEARCH_DEVICE");
                    break;
                case MibandCallback.STATUS_CONNECT:
                    Log.e(TAG, "실패: STATUS_CONNECT");
                    break;
                case MibandCallback.STATUS_SEND_ALERT:
                    Log.e(TAG, "실패: STATUS_SEND_ALERT");
                    break;
                case MibandCallback.STATUS_GET_USERINFO:
                    Log.e(TAG, "실패: STATUS_GET_USERINFO");
                    break;
                case MibandCallback.STATUS_SET_USERINFO:
                    Log.e(TAG, "실패: STATUS_SET_USERINFO");
                    break;
                case MibandCallback.STATUS_START_HEARTRATE_SCAN:
                    Log.e(TAG, "실패: STATUS_START_HEARTRATE_SCAN");
                    break;
                case MibandCallback.STATUS_GET_BATTERY:
                    Log.e(TAG, "실패: STATUS_GET_BATTERY");
                    break;
                case MibandCallback.STATUS_GET_ACTIVITY_DATA:
                    Log.e(TAG, "실패: STATUS_GET_ACTIVITY_DATA");
                    break;
            }
        }

    }
}
