package com.example.pc.mydoctordemo.ui.fragment;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.pc.mydoctordemo.R;
import com.jellygom.miband_sdk.MiBandIO.Listener.HeartrateListener;
import com.jellygom.miband_sdk.MiBandIO.Listener.NotifyListener;
import com.jellygom.miband_sdk.MiBandIO.Listener.RealtimeStepListener;
import com.jellygom.miband_sdk.MiBandIO.MibandCallback;
import com.jellygom.miband_sdk.MiBandIO.Model.UserInfo;
import com.jellygom.miband_sdk.Miband;

import config.GlobalConstance;

/**
 * Created by PC on 2017-04-03.
 */

public class MibandControllFragment extends BaseFragment implements View.OnClickListener{
    private View myView;
    private static final String TAG = "Mobile";

    private Miband miband;
    private BluetoothAdapter mBluetoothAdapter;

    private TextView heart, step, battery;
    private TextView text;

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

    private HeartrateListener heartrateNotifyListener = new HeartrateListener() {
        @Override
        public void onNotify(final int heartRate) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext.getApplicationContext(), heartRate+"bpm",Toast.LENGTH_LONG).show();
                    System.out.println(heartRate+"bpm");
                    heart.setText(heartRate + " bpm");
                    text.append(heartRate + " bpm\n");
                }
            });
        }
    };

    private final MibandCallback mibandCallback = new MibandCallback() {
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            battery.setText(level+ " % battery");
                            text.append(level + " % battery\n");
                        }
                    });
                    break;
                case MibandCallback.STATUS_GET_ACTIVITY_DATA:
                    Log.e(TAG, "성공: STATUS_GET_ACTIVITY_DATA");
                    final int steps = (int) data;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            step.setText(steps+ " steps");
                            text.append(steps+ " steps\n");
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
    };

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.miband_controll, container, false);

        Log.d(GlobalConstance.LOGCAT_DEFAULT_TAG, "IN onCreateView");

        myView.findViewById(R.id.button_vive).setOnClickListener(this);
        myView.findViewById(R.id.button_steps).setOnClickListener(this);
        myView.findViewById(R.id.button_realtime_steps).setOnClickListener(this);
        myView.findViewById(R.id.button_battery).setOnClickListener(this);
        myView.findViewById(R.id.button_heart_start_one).setOnClickListener(this);
        myView.findViewById(R.id.button_heart_start_many).setOnClickListener(this);
        myView.findViewById(R.id.webview_test).setOnClickListener(this);

        heart = (TextView) myView.findViewById(R.id.heart);
        step = (TextView) myView.findViewById(R.id.steps);
        battery = (TextView) myView.findViewById(R.id.battery);
        text = (TextView) myView.findViewById(R.id.text);
        // webView = (WebView) findViewById(R.id.web);




        mBluetoothAdapter = ((BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();

        miband = new Miband(mContext);

        miband.searchDevice(mBluetoothAdapter, this.mibandCallback);

        miband.setDisconnectedListener(new NotifyListener() {
            @Override
            public void onNotify(byte[] data) {
                miband.searchDevice(mBluetoothAdapter, mibandCallback);
            }
        });
        return myView;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_vive) {
            miband.sendAlert(this.mibandCallback);
        } else if (i == R.id.button_steps) {
            miband.getCurrentSteps(this.mibandCallback);
        } else if (i == R.id.button_realtime_steps) {
            miband.setRealtimeStepListener(realtimeStepListener);
        } else if (i == R.id.button_battery) {

            miband.getBatteryLevel(this.mibandCallback);

        } else if (i == R.id.button_heart_start_one) {
            miband.startHeartRateScan(1, this.mibandCallback);
        } else if (i == R.id.button_heart_start_many) {
            miband.startHeartRateScan(0, this.mibandCallback);
        }
        else if(i == R.id.webview_test){
            // webView.setWebViewClient(new WebViewClient());
            //webView.loadUrl("http://" + "www.google.com");
        }
    }
}
