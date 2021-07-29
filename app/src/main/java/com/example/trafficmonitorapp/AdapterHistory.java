package com.example.trafficmonitorapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdapterHistory extends BaseAdapter {

    Histories histories;


    LayoutInflater layoutInflater;
    Context context;

    public AdapterHistory(Context context) {
        this.context = context;
        histories = new Histories();
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return histories.getLength();
    }

    @Override
    public History getItem(int position) {
        return histories.getHistory(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") View view = layoutInflater.inflate(R.layout.listview_costom, null);
        History history = histories.getHistory(position);

        LocalDateTime time = history.getTime();
        String name = history.getAppLabel() + " (" + history.getAppName() + ")";
        int uid = history.getUid();
        long usage = history.getUsage();
        long diff = history.getDiff();


        TextView viewTime = (TextView)view.findViewById(R.id.textViewTime);
        TextView viewName = (TextView)view.findViewById(R.id.textViewName);
        TextView viewUid = (TextView)view.findViewById(R.id.textViewUid);
        TextView viewUsage = (TextView)view.findViewById(R.id.textViewUsage);
        //TextView viewTime = new TextView(getApplicationContext());

        viewTime.setText(time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        viewName.setText(name);
        viewName.setTypeface(null, Typeface.BOLD);
        viewUid.setText("UID: " + Integer.toString(uid));
        viewUsage.setText("Usage: " + Long.toString(usage) + " (+" + diff + ") bytes");


        return view;
    }
}
