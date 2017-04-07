package com.example.pc.mydoctordemo.ui.fragment;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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

import com.example.pc.mydoctordemo.R;
import com.example.pc.mydoctordemo.ui.activity.MainActivity;
import com.example.pc.mydoctordemo.ui.item.SupportThing;

import java.util.ArrayList;
import java.util.List;

import config.GlobalConstance;

/**
 * Created by PC on 2017-04-01.
 */

public class MobileHealthFragment extends BaseFragment {

    private View myView;
    private Context context;
    private List<SupportThing> supportThings ;
    private Handler.Callback mCallback;

    /**
     *  ----Fragment.newInstance(getContext())
     * 액
     * @param pContext
     * @return
     */
    static public MobileHealthFragment newInstance(Context pContext){
        mContext = pContext;
        return new MobileHealthFragment();
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportThings = new ArrayList<>();
        setList();
    }

    @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            myView = inflater.inflate(R.layout.mobile_health, container, false);

            Log.d(GlobalConstance.LOGCAT_DEFAULT_TAG, "IN onCreateView");

            /**
             * create list view
             */
            ListView listView = (ListView) myView.findViewById(R.id.mobile_health_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            FragmentManager fragmentManager = getFragmentManager();
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity activity = (MainActivity) getActivity();
                activity.mobileHealthItemClickCallback(position);
            }
        });
            MobileHealthAdapter adapter = new MobileHealthAdapter(supportThings);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return myView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }


    /**
     * Initialize list data
     */
    public void setList(){
        supportThings.add(new SupportThing("Mi band", "test1",R.drawable.miband));
        supportThings.add(new SupportThing("iHealth", "test2",R.drawable.ihealth));
    }

    public interface MobileHealthFragmentCallback extends Handler.Callback {
        public void mobileHealthItemClickCallback(SupportThing dto);
    }

    private class MobileHealthAdapter extends BaseAdapter {
        private List<SupportThing> supportList = null;
        private LayoutInflater inflater = null;

        public MobileHealthAdapter(List<SupportThing> list){
            this.supportList = list;
            inflater = LayoutInflater.from(getContext());
        }
        @Override
        public int getCount() {
            return supportList.size();
        }

        @Override
        public Object getItem(int position) {
            return supportList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemLayout = convertView;
            if(itemLayout == null){
                itemLayout = inflater.inflate(R.layout.support_list_view, parent, false);
            }


            ImageView imageView = (ImageView) itemLayout.findViewById(R.id.image);
            TextView companyName = (TextView) itemLayout.findViewById(R.id.company_name);
            TextView description = (TextView) itemLayout.findViewById(R.id.description);

            imageView.setImageResource(supportList.get(position).getIcon());
            companyName.setText(supportList.get(position).getCompanyName());
            description.setText(supportList.get(position).getDescription());

            return itemLayout;
        }
    }

}
