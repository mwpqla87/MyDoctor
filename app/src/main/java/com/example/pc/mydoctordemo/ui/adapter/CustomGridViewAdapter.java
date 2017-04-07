package com.example.pc.mydoctordemo.ui.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pc.mydoctordemo.R;
import com.example.pc.mydoctordemo.ui.item.CheckedList;

import java.util.ArrayList;

/**
 * Created by PC on 2017-04-02.
 */

public class CustomGridViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList<CheckedList> data = new ArrayList<CheckedList>();
    private LayoutInflater inflater;



    public CustomGridViewAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<CheckedList> list) {
        super(context, resource, list);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = list;
      inflater = LayoutInflater.from(getContext());
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder = null;
        if (row == null) {
            row = inflater.inflate(R.layout.checkinglist, parent, false);
            holder = new RecordHolder();
            holder.txtTitle = (TextView) row.findViewById(R.id.list_text);
            holder.imageItem = (ImageView) row.findViewById(R.id.list_image);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
            }
            CheckedList item = data.get(position);
        holder.txtTitle.setText(item.getListName());
        holder.imageItem.setImageResource(item.getImageIcon());
        return row;
    }
    static class RecordHolder {
        TextView txtTitle;
        ImageView imageItem;
    }



}
