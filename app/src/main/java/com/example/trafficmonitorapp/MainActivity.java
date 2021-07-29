package com.example.trafficmonitorapp;

import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity {

    Button buttonRefresh;
    //Button buttonMakeTraffic;
    Switch switchTracking;
    ListView listViewHistory;
    AdapterHistory adapterHistory;
    boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonRefresh = findViewById(R.id.buttonRefresh);
       // buttonMakeTraffic = findViewById(R.id.buttonMakeTraffic);
        switchTracking = findViewById(R.id.switchTracking);
        listViewHistory = findViewById(R.id.listViewHistory);
        adapterHistory = new AdapterHistory(this);

        listViewHistory.setAdapter(adapterHistory);

//        TimerTask tt = new TimerTask() {
//            @Override
//            public void run() {
//                adapterHistory.notifyDataSetChanged();
//                listViewHistory.setAdapter(adapterHistory);
//            }
//        };
//
//        /////////// / Timer 생성 //////////////
//        Timer timer = new Timer();
//        timer.schedule(tt, 0, 3000);


        final NetworkStatsManager networkStatsManager =
                (NetworkStatsManager) getApplicationContext().
                        getSystemService(Context.NETWORK_STATS_SERVICE);

        final PackageManager pm = getPackageManager();
        final PermissionChecker permissionChecker = new PermissionChecker();
        permissionChecker.checkAppAccess();

        final TrafficMonitor trafficMonitor = new TrafficMonitor(networkStatsManager, pm);



        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                adapterHistory.notifyDataSetChanged();
                listViewHistory.setAdapter(adapterHistory);

            }
        });

        switchTracking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    if (permissionChecker.checkAppAccess()) {
                        trafficMonitor.startTracking(networkStatsManager, pm);
                        buttonRefresh.setBackgroundColor(Color.parseColor("#41A541"));
                        isRunning = true;
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

    class PermissionChecker  {
        public boolean checkAppAccess(){
            try{
                NetworkStatsManager networkStatsManager =
                        (NetworkStatsManager) getApplicationContext().
                                getSystemService(Context.NETWORK_STATS_SERVICE);

                NetworkStats networkStats =
                        networkStatsManager.queryDetailsForUid(
                                NetworkCapabilities.TRANSPORT_WIFI,
                                "",
                                0,
                                System.currentTimeMillis(),
                                1000);

                networkStats.close();

            } catch(Exception e){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setMessage("앱의 사용 기록 액세스를 허용해주세요");
//                builder.setTitle("Alert!");

                builder
                        .setPositiveButton(
                                "Yes",
                                new DialogInterface
                                        .OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which)
                                    {

                                        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                                        //finish();
                                    }
                                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return false;
            }
            return true;
        }
    }

}
