package com.example.pc.mydoctordemo.ui.heartRateNotifyListener;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.jellygom.miband_sdk.MiBandIO.Listener.HeartrateListener;

/**
 * Created by PC on 2017-04-08.
 */

public class MyHeartRateNotifyListener implements HeartrateListener {
    private int data;
    private Activity activity;
    private TextView pulseDisplay;



    public MyHeartRateNotifyListener(Activity activity){
        this.activity = activity;




    }
    public void onNotify(final int heartRate) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   data = heartRate;
                    Log.e("ABC",data+"bpm");
                    Toast.makeText(activity.getApplicationContext(),data+"bpm",Toast.LENGTH_LONG).show();
                    if(pulseDisplay != null)
                        pulseDisplay.setText(data+"");
                    else
                        Toast.makeText(activity.getApplicationContext(),"뭐지?",Toast.LENGTH_LONG).show();

                }
            });
        data = heartRate;
        }

    public int getHeartRate(){
        return data;
    }



    public void setTextView(TextView displayPulse) {
        this.pulseDisplay = displayPulse;
    }
}
