package ihealthDevices;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pc.mydoctordemo.R;
import com.example.pc.mydoctordemo.ui.fragment.BaseFragment;
import com.ihealth.communication.control.Bp7sControl;
import com.ihealth.communication.control.BpProfile;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.communication.utils.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by PC on 2017-04-06.
 */

public class BP7S extends BaseFragment implements View.OnClickListener{

    private static final String TAG = "BP7S";
    private Bp7sControl bp7sControl;
    private String deviceMac;
    private int clientCallbackId;
    private TextView tv_return;
    private View myView;

    static public BP7S newInstance(Context pContext){
        mContext = pContext;
        return new BP7S();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.bp7_s, container, false);

        Bundle bundle = this.getArguments();
        if(bundle != null){
            deviceMac = bundle.getString("mac");
        }
        System.out.println(deviceMac);
        myView.findViewById(R.id.btn_getIDPS).setOnClickListener(this);
        myView.findViewById(R.id.btn_getbattery).setOnClickListener(this);
        myView.findViewById(R.id.btn_getOfflineNum).setOnClickListener(this);
        myView.findViewById(R.id.btn_getOffineData).setOnClickListener(this);
        myView.findViewById(R.id.btn_setangle).setOnClickListener(this);
        myView.findViewById(R.id.btn_disconnect).setOnClickListener(this);
        myView.findViewById(R.id.btn_setUnit).setOnClickListener(this);
        myView.findViewById(R.id.btn_getFunctionInfo).setOnClickListener(this);
        tv_return = (TextView)myView.findViewById(R.id.tv_return);

        clientCallbackId = iHealthDevicesManager.getInstance().registerClientCallback(miHealthDevicesCallback);
		/* Limited wants to receive notification specified device */
        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(clientCallbackId, iHealthDevicesManager.TYPE_BP7S);
		/* Get BP7S controller */
        bp7sControl = iHealthDevicesManager.getInstance().getBp7sControl(deviceMac);

        return myView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        iHealthDevicesManager.getInstance().unRegisterClientCallback(clientCallbackId);
    }



    private iHealthDevicesCallback miHealthDevicesCallback = new iHealthDevicesCallback() {

        @Override
        public void onDeviceConnectionStateChange(String mac,
                                                  String deviceType, int status, int errorID) {
            Log.i(TAG, "mac: " + mac);
            Log.i(TAG, "deviceType: " + deviceType);
            Log.i(TAG, "status: " + status);
        }

        @Override
        public void onUserStatus(String username, int userStatus) {
            Log.i(TAG, "username: " + username);
            Log.i(TAG, "userState: " + userStatus);
        }

        @Override
        public void onDeviceNotify(String mac, String deviceType,
                                   String action, String message) {
            Log.i(TAG, "mac: " + mac);
            Log.i(TAG, "deviceType: " + deviceType);
            Log.i(TAG, "action: " + action);
            Log.i(TAG, "message: " + message);

            if(BpProfile.ACTION_BATTERY_BP.equals(action)){
                try {
                    JSONObject info = new JSONObject(message);
                    String battery =info.getString(BpProfile.BATTERY_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "battery: " + battery;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }else if(BpProfile.ACTION_ERROR_BP.equals(action)){
                try {
                    JSONObject info = new JSONObject(message);
                    String num =info.getString(BpProfile.ERROR_NUM_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "error num: " + num;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else if(BpProfile.ACTION_HISTORICAL_DATA_BP.equals(action)){
                String str = "";
                try {
                    JSONObject info = new JSONObject(message);
                    if (info.has(BpProfile.HISTORICAL_DATA_BP)) {
                        JSONArray array = info.getJSONArray(BpProfile.HISTORICAL_DATA_BP);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String date          = obj.getString(BpProfile.MEASUREMENT_DATE_BP);
                            String hightPressure = obj.getString(BpProfile.HIGH_BLOOD_PRESSURE_BP);
                            String lowPressure   = obj.getString(BpProfile.LOW_BLOOD_PRESSURE_BP);
                            String pulseWave     = obj.getString(BpProfile.PULSE_BP);
                            String ahr           = obj.getString(BpProfile.MEASUREMENT_AHR_BP);
                            String hsd           = obj.getString(BpProfile.MEASUREMENT_HSD_BP);
                            str = "date:" + date
                                    + "hightPressure:" + hightPressure + "\n"
                                    + "lowPressure:" + lowPressure + "\n"
                                    + "pulseWave" + pulseWave + "\n"
                                    + "ahr:" + ahr + "\n"
                                    + "hsd:" + hsd + "\n";
                        }
                    }
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj =  str;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else if(BpProfile.ACTION_HISTORICAL_NUM_BP.equals(action)){
                try {
                    JSONObject info = new JSONObject(message);
                    String num = info.getString(BpProfile.HISTORICAL_NUM_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "num: " + num;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {

            case R.id.btn_getIDPS:
                if (bp7sControl != null) {
                    String idps = bp7sControl.getIdps();
                    Log.e(TAG,"IDPS = " + idps);
                }
                else
                    Toast.makeText(mContext, "bp7sControl == null", Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_getbattery:
                if(bp7sControl != null) {
                    bp7sControl.getBattery();
                }
                else
                    Toast.makeText(mContext, "bp7sControl == null", Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_getOfflineNum:
                if(bp7sControl != null)
                    bp7sControl.getOfflineNum();
                else
                    Toast.makeText(mContext, "bp7sControl == null", Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_getOffineData:
                if(bp7sControl != null)
                    bp7sControl.getOfflineData();
                else
                    Toast.makeText(mContext, "bp7sControl == null", Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_disconnect:
                if(bp7sControl != null)
                    bp7sControl.disconnect();
                else
                    Toast.makeText(mContext, "bp7sControl == null", Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_setUnit:
                if(bp7sControl != null)
                    bp7sControl.setUnit(1);
                else
                    Toast.makeText(mContext, "bp7sControl == null", Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_setangle:
                if(bp7sControl != null)
                    bp7sControl.angleSet((byte)90, (byte)60, (byte)90, (byte)60);
                else
                    Toast.makeText(mContext, "bp7sControl == null", Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_getFunctionInfo:
                if(bp7sControl != null)
                    bp7sControl.getFunctionInfo();
                else
                    Toast.makeText(mContext, "bp7sControl == null", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    private static final int HANDLER_MESSAGE = 101;
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_MESSAGE:
                    tv_return.setText((String)msg.obj);
                    break;
            }
            super.handleMessage(msg);
        }
    };

}
