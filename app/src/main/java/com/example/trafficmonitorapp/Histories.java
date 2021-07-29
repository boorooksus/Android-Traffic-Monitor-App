package com.example.trafficmonitorapp;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Histories {

    private static List<String> histories = new Vector<>();
//    private static List<String> histories = new ArrayList<>();

    public void addHistory(String history){
        histories.add(0, history);
    }

    public int getLength(){
        return histories.size();
    }

    public String getHistory(int position){
        histories.get(position);
        return histories.get(position);
    }



}
