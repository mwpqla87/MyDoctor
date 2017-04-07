package com.example.pc.mydoctordemo.ui.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pc.mydoctordemo.R;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.GlobalConstance;

/**
 * Created by PC on 2017-04-06.
 */

public class IhealthController extends BaseFragment implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback{
    private View myView;
    private static final int HANDLER_SCAN = 101;
    private static final int HANDLER_CONNECTED = 102;
    private static final int HANDLER_DISCONNECT = 103;
    private static final int HANDLER_USER_STATUE = 104;
    private static final String TAG = "IhealthController";
    /**
     * Id to identify permissions request.
     */
    private static final int REQUEST_PERMISSIONS = 0;
    String userName = "";
    String clientId = "63769ca4c52b40ef9ecdc4ce91d17b5d";
    String clientSecret = "551217b78d3c49efacc2d62334b9d581";

    private ListView listview_scan;
    private ListView listview_connected;
    private SimpleAdapter sa_scan;
    private SimpleAdapter sa_connected;
    private TextView tv_discovery;
    private List<HashMap<String, String>> list_ScanDevices = new ArrayList<HashMap<String, String>>();
    private List<HashMap<String, String>> list_ConnectedDevices = new ArrayList<HashMap<String, String>>();
    private int callbackId;
    private Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_SCAN:
                    Bundle bundle_scan = msg.getData();
                    String mac_scan = bundle_scan.getString("mac");
                    String type_scan = bundle_scan.getString("type");
                    HashMap<String, String> hm_scan = new HashMap<String, String>();
                    hm_scan.put("mac", mac_scan);
                    hm_scan.put("type", type_scan);
                    list_ScanDevices.add(hm_scan);
                    updateViewForScan();
                    break;

                case HANDLER_CONNECTED:
                    Bundle bundle_connect = msg.getData();
                    String mac_connect = bundle_connect.getString("mac");
                    String type_connect = bundle_connect.getString("type");
                    HashMap<String, String> hm_connect = new HashMap<String, String>();
                    hm_connect.put("mac", mac_connect);
                    hm_connect.put("type", type_connect);
                    list_ConnectedDevices.add(hm_connect);
                    updateViewForConnected();
                    Log.e(TAG, "idps:" + iHealthDevicesManager.getInstance().getDevicesIDPS(mac_connect));
                    list_ScanDevices.remove(hm_connect);
                    updateViewForScan();
                    break;

                case HANDLER_DISCONNECT:
                    Bundle bundle_disconnect = msg.getData();
                    String mac_disconnect = bundle_disconnect.getString("mac");
                    String type_disconnect = bundle_disconnect.getString("type");
                    HashMap<String, String> hm_disconnect = new HashMap<String, String>();
                    hm_disconnect.put("mac", mac_disconnect);
                    hm_disconnect.put("type", type_disconnect);
                    list_ConnectedDevices.remove(hm_disconnect);

                    updateViewForConnected();

                    break;
                case HANDLER_USER_STATUE:
                    Bundle bundle_status = msg.getData();
                    String username = bundle_status.getString("username");
                    String userstatus = bundle_status.getString("userstatus");
                    String str = "username:" + username + " - userstatus:" + userstatus;
                    //Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();

                    break;

                default:
                    break;
            }
        }
    };
    private iHealthDevicesCallback miHealthDevicesCallback = new iHealthDevicesCallback() {


        @Override
        public void onScanDevice(String mac, String deviceType, int rssi, Map manufactorData) {
            Log.i(TAG, "onScanDevice - mac:" + mac + " - deviceType:" + deviceType + " - rssi:" + rssi + " -manufactorData:" + manufactorData);
            Bundle bundle = new Bundle();
            bundle.putString("mac", mac);
            bundle.putString("type", deviceType);
            Message msg = new Message();
            msg.what = HANDLER_SCAN;
            msg.setData(bundle);
            myHandler.sendMessage(msg);
        }

        @Override
        public void onDeviceConnectionStateChange(String mac, String deviceType, int status, int errorID, Map manufactorData) {
            Log.e(TAG, "mac:" + mac + " deviceType:" + deviceType + " status:" + status + " errorid:" + errorID + " -manufactorData:" + manufactorData);
            Bundle bundle = new Bundle();
            bundle.putString("mac", mac);
            bundle.putString("type", deviceType);
            Message msg = new Message();
            if (status == iHealthDevicesManager.DEVICE_STATE_CONNECTED) {
                msg.what = HANDLER_CONNECTED;
            } else if (status == iHealthDevicesManager.DEVICE_STATE_DISCONNECTED) {
                msg.what = HANDLER_DISCONNECT;
            }
            msg.setData(bundle);
            myHandler.sendMessage(msg);
        }

        @Override
        public void onUserStatus(String username, int userStatus) {
            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            bundle.putString("userstatus", userStatus + "");
            Message msg = new Message();
            msg.what = HANDLER_USER_STATUE;
            msg.setData(bundle);
            myHandler.sendMessage(msg);
        }

        @Override
        public void onDeviceNotify(String mac, String deviceType, String action, String message) {
        }

        @Override
        public void onScanFinish() {
            tv_discovery.setText("discover finish");
        }

    };
    static public IhealthController newInstance(Context pContext){
        mContext = pContext;
        return new IhealthController();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.ihealth_controll, container, false);
        Log.d(GlobalConstance.LOGCAT_DEFAULT_TAG, "IN onCreateView");

        FloatingActionButton fab = (FloatingActionButton) myView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        /**
         * create list view
         */
        myView.findViewById(R.id.btn_discorvery).setOnClickListener(this);
        myView.findViewById(R.id.btn_stopdiscorvery).setOnClickListener(this);
        myView.findViewById(R.id.btn_Certification).setOnClickListener(this);
        myView.findViewById(R.id.btn_GotoBG1).setOnClickListener(this);
        myView.findViewById(R.id.btn_GotoABI).setOnClickListener(this);
        myView.findViewById(R.id.btn_GotoHS6).setOnClickListener(this);
        myView.findViewById(R.id.btn_GotoBPM1).setOnClickListener(this);
        myView.findViewById(R.id.btn_Miband).setOnClickListener(this);
//        findViewById(R.id.btn_GotoTest).setOnClickListener(this);

        tv_discovery = (TextView) myView.findViewById(R.id.tv_discovery);
        listview_scan = (ListView) myView.findViewById(R.id.list_scan);
        listview_connected = (ListView) myView.findViewById(R.id.list_connected);
        if (list_ConnectedDevices != null)
            list_ConnectedDevices.clear();
        if (list_ScanDevices != null)
            list_ScanDevices.clear();
        sa_scan = new SimpleAdapter(getActivity(), this.list_ScanDevices, R.layout.bp_listview_baseview,
                new String[]{
                        "type", "mac"
                },
                new int[]{
                        R.id.tv_type, R.id.tv_mac
                });

        listview_scan.setAdapter(sa_scan);
        listview_scan.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                HashMap<String, String> hm = list_ScanDevices.get(position);
                 String type = hm.get("type");
                String mac = hm.get("mac");
                Log.i(TAG, "mac = " + mac);
                Log.i(TAG, "type = " + type);
                Log.i(TAG, "userName = " + userName);
                boolean req = iHealthDevicesManager.getInstance().connectDevice(userName, mac, type);
                if (!req) {
                    Toast.makeText(mContext.getApplicationContext(), "Haven’t permission to connect this device or the mac is not valid", Toast.LENGTH_LONG).show();
                }
            }

        });

        /*
         * Initializes the iHealth devices manager. Can discovery available iHealth devices nearby
         * and connect these devices through iHealthDevicesManager.
         */
        iHealthDevicesManager.getInstance().init(mContext);

        /*
         * Register callback to the manager. This method will return a callback Id.
        */

        callbackId = iHealthDevicesManager.getInstance().registerClientCallback(miHealthDevicesCallback);


        checkPermissions();
        SharedPreferences mySharedPreferences = mContext.getSharedPreferences("preference", mContext.MODE_PRIVATE);
        long discoveryType = mySharedPreferences.getLong("discoveryType", 0);
        for (DeviceStruct struct : deviceStructList) {
            struct.isSelected = ((discoveryType & struct.type) != 0);
        }

        return myView;
    }

    private static class DeviceStruct {
        String name;
        long type;
        boolean isSelected;
    }

    private static ArrayList<DeviceStruct> deviceStructList = new ArrayList<>();

    static {
        Field[] fields = iHealthDevicesManager.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (fieldName.contains("DISCOVERY_")) {
                DeviceStruct struct = new DeviceStruct();
                struct.name = fieldName.substring(10);
                try {
                    struct.type = field.getLong(null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                deviceStructList.add(struct);
            }
        }

    }

    private class SelectDeviceAdapter extends BaseAdapter {
            @Override
            public int getCount() {
                return deviceStructList.size();
            }

            @Override
            public Object getItem(int position) {
                return deviceStructList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.select_device_item_layout, null);
                }
                CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.select_device_checkbox);
                checkBox.setText(deviceStructList.get(position).name);
                checkBox.setChecked(deviceStructList.get(position).isSelected);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        deviceStructList.get(position).isSelected = isChecked;
                    }
                });
                return convertView;
            }
    }

    private void startDiscovery() {
        long discoveryType = 0;
        for (DeviceStruct struct : deviceStructList) {
            if (struct.isSelected) {
                discoveryType |= struct.type;
            }
        }
        SharedPreferences mySharedPreferences = mContext.getSharedPreferences("preference", mContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putLong("discoveryType", discoveryType);
        editor.apply();
        if (discoveryType != 0) {
            iHealthDevicesManager.getInstance().startDiscovery(discoveryType);
            tv_discovery.setText("discovering...");
        }
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btn_discorvery:
                /*
                 * discovery iHealth devices, This method can specify only to search for the devices
                 * that you want to connect
                 */
                list_ScanDevices.clear();
                updateViewForScan();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Please select the devices you want to discover.");
                View contentView = View.inflate(mContext, R.layout.select_device_dialog_layout, null);
                builder.setView(contentView);
                GridView gridView = (GridView) contentView.findViewById(R.id.device_list_grid_view);
                gridView.setAdapter(new SelectDeviceAdapter());
                builder.setPositiveButton("Start", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startDiscovery();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
                break;

            case R.id.btn_stopdiscorvery:
                /* stop discovery iHealth devices */
                iHealthDevicesManager.getInstance().stopDiscovery();
                break;

            case R.id.btn_Certification:
                iHealthDevicesManager.getInstance().sdkUserInAuthor(mContext, userName, clientId,
                        clientSecret, callbackId);
                iHealthDevicesManager.getInstance().sdkUserInAuthor(mContext, userName, clientId,
                        clientSecret, callbackId, Environment.getExternalStorageDirectory().getAbsolutePath() + "/tencent/QQfile_recv/idscertificate.p12", "ELPWfWdA");
                break;

            default:
                break;
        }
    }

    private void updateViewForScan() {
        sa_scan.notifyDataSetChanged();
        ViewGroup.LayoutParams params = listview_scan.getLayoutParams();
        params.height = dp2px(list_ScanDevices.size() * 48 + 5);
        listview_scan.setLayoutParams(params);
    }

    private int dp2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void updateViewForConnected() {
        sa_connected = new SimpleAdapter(getActivity(), this.list_ConnectedDevices, R.layout.bp_listview_baseview,
                new String[]{
                        "type", "mac"
                },
                new int[]{
                        R.id.tv_type, R.id.tv_mac
                });

        listview_connected.setAdapter(sa_connected);
        listview_connected.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                Toast.makeText(mContext.getApplicationContext(), "눌림", Toast.LENGTH_LONG).show();
                HashMap<String, String> hm = list_ConnectedDevices.get(position);
            String type = hm.get("type");
            String mac = hm.get("mac");
          Log.d("Error",type);

            Bundle args = new Bundle();
                args.putString("mac",mac);


                if (iHealthDevicesManager.TYPE_BP7S.equals(type)) {

            }
        }
        });
        sa_connected.notifyDataSetChanged();
    }

    private void checkPermissions() {
        StringBuilder tempRequest = new StringBuilder();
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            tempRequest.append(Manifest.permission.WRITE_EXTERNAL_STORAGE + ",");
        }
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            tempRequest.append(Manifest.permission.RECORD_AUDIO + ",");
        }
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            tempRequest.append(Manifest.permission.ACCESS_FINE_LOCATION + ",");
        }
        if (tempRequest.length() > 0) {
            tempRequest.deleteCharAt(tempRequest.length() - 1);
            ActivityCompat.requestPermissions(getActivity(), tempRequest.toString().split(","), REQUEST_PERMISSIONS);
        }
    }
}
