package com.example.pc.mydoctordemo.ui.activity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.example.pc.mydoctordemo.R;
import com.example.pc.mydoctordemo.ui.fragment.HomeFragment;
import com.example.pc.mydoctordemo.ui.fragment.IhealthController;
import com.example.pc.mydoctordemo.ui.fragment.MibandControllFragment;
import com.example.pc.mydoctordemo.ui.fragment.MibandPulseManager;
import com.example.pc.mydoctordemo.ui.fragment.MobileHealthFragment;
import com.example.pc.mydoctordemo.ui.heartRateNotifyListener.MyHeartRateNotifyListener;
import com.example.pc.mydoctordemo.ui.item.CheckedList;
import com.jellygom.miband_sdk.MiBandIO.MibandCallback;
import com.jellygom.miband_sdk.Miband;

import java.util.ArrayList;

import config.GlobalConstance;
import ihealthDevices.BP7S;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , Thread.UncaughtExceptionHandler

{

    private Thread.UncaughtExceptionHandler mExceptionHandler;
    private ArrayList<CheckedList> checkedLists;
    private GridView gridView;
    android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /**
         * Error Handling
         */
        mExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);


        setContentView(R.layout.activity_main);
        checkedLists = new ArrayList<CheckedList>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fragmentManager = getFragmentManager();
        try {
            fragmentManager.beginTransaction().replace(R.id.content_frame, HomeFragment.newInstance(getApplicationContext())).commit();
        } catch (Exception e) {
            Log.d(GlobalConstance.LOGCAT_DEFAULT_TAG, Log.getStackTraceString(e));
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();
        transaction = fragmentManager.beginTransaction();
        android.app.Fragment fragment;



        switch (id) {
            case R.id.nav_mobile_health:
                try {
                    transaction.replace(R.id.content_frame, MobileHealthFragment.newInstance(getApplicationContext()));
                    transaction.addToBackStack(null);
                    transaction.commit();

                } catch (Exception e) {
                    Log.d(GlobalConstance.LOGCAT_DEFAULT_TAG, Log.getStackTraceString(e));
                }
                break;
            default:
                break;
        }

//
//        if (id == R.id.nav_mobile_health) {
//          fragmentManager.beginTransaction().replace(R.id.content_frame, new MobileHealthFragment()).commit();
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     *
     */


    /**
     * 모바일헬스프래그먼트 리스트뷰 아이템 클릭 시 콜백함수
     * 프래그먼트 전환 시 사용
     * 전달받은 dto를 번들에 인자로 담아서 전달
     */

    public void mobileHealthItemClickCallback(int position) {
        FragmentManager fragmentManager = getFragmentManager();
        transaction = fragmentManager.beginTransaction();
        if(position == 0){
            try {
                transaction.replace(R.id.content_frame, MibandControllFragment.newInstance(getApplicationContext()));
                transaction.addToBackStack("mibandControll");
                transaction.commit();


            } catch (Exception e) {
                Log.d(GlobalConstance.LOGCAT_DEFAULT_TAG, Log.getStackTraceString(e));
            }
        }
        else{
            try {
                transaction.replace(R.id.content_frame, IhealthController.newInstance(getApplicationContext()));
                transaction.addToBackStack(null);
                transaction.commit();
            } catch (Exception e) {
                Log.d(GlobalConstance.LOGCAT_DEFAULT_TAG, Log.getStackTraceString(e));
            }
        }

    }
    public void iHealthItemClickCallback(int position) {
        FragmentManager fragmentManager = getFragmentManager();
        transaction = fragmentManager.beginTransaction();
        try {
           transaction.replace(R.id.content_frame, BP7S.newInstance(getApplicationContext()));
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e) {
            Log.d(GlobalConstance.LOGCAT_DEFAULT_TAG, Log.getStackTraceString(e));
        }


    }


    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.e(GlobalConstance.LOGCAT_DEFAULT_TAG, Log.getStackTraceString(e));
        mExceptionHandler.uncaughtException(t, e);
    }

    public void mibandItemClickCallback(int position, Miband miband, MibandCallback callback, MyHeartRateNotifyListener heartrateNotifyListener) {
        FragmentManager fragmentManager = getFragmentManager();
        transaction = fragmentManager.beginTransaction();
        if(position == 0){
            try {
                transaction.replace(R.id.content_frame, MibandPulseManager.newInstance(getApplicationContext(),miband, callback,heartrateNotifyListener));
                transaction.addToBackStack(null);
                transaction.commit();
            } catch (Exception e) {
                Log.d(GlobalConstance.LOGCAT_DEFAULT_TAG, Log.getStackTraceString(e));
            }
        }
        else{

        }
    }
}
