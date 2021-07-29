package com.example.trafficmonitorapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Button buttonRefresh;
    //Button buttonMakeTraffic;
    Switch switchTracking;
    ListView listViewHistory;
    AdapterHistory adapterHistory;
    static boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonRefresh = findViewById(R.id.buttonRefresh);
        switchTracking = findViewById(R.id.switchTracking);
        listViewHistory = findViewById(R.id.listViewHistory);
        adapterHistory = new AdapterHistory(this);

        listViewHistory.setAdapter(adapterHistory);
        switchTracking.setChecked(isRunning);
        if(isRunning){
            buttonRefresh.setBackgroundColor(Color.parseColor("#41A541"));

        } else{
            buttonRefresh.setBackgroundColor(Color.parseColor("#FF5675"));

        }

        final TrafficMonitor trafficMonitor = new TrafficMonitor(this);



        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                adapterHistory.notifyDataSetChanged();
            }
        });

        switchTracking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    if (trafficMonitor.checkAppAccess()) {
                        trafficMonitor.startTracking();
                        buttonRefresh.setBackgroundColor(Color.parseColor("#41A541"));
                        isRunning = true;

                        TimerTask tt = new TimerTask() {
                            @Override
                            public void run() {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapterHistory.notifyDataSetChanged();
                                        //listViewHistory.setAdapter(adapterHistory);
                                    }
                                });

                            }
                        };

                        Timer timer = new Timer();
                        timer.schedule(tt, 0, 10000);

                    } else{

                        switchTracking.setChecked(false);
                    }

                }
                else{
                    trafficMonitor.stopTracking();

                    buttonRefresh.setBackgroundColor(Color.parseColor("#FF5675"));
                    isRunning = false;
                }
            }
        });
    }

}
