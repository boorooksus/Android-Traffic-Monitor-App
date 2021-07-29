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
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    Button buttonStart;
    //Button buttonMakeTraffic;
    Switch switchTracking;
    boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStart = findViewById(R.id.buttonStart);
       // buttonMakeTraffic = findViewById(R.id.buttonMakeTraffic);
        switchTracking = findViewById(R.id.switchTracking);

        final NetworkStatsManager networkStatsManager =
                (NetworkStatsManager) getApplicationContext().
                        getSystemService(Context.NETWORK_STATS_SERVICE);

        final PackageManager pm = getPackageManager();

        final NetworkAnalyzer networkAnalyzer = new NetworkAnalyzer(networkStatsManager, pm);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!isRunning) {
                    PermissionChecker permissionChecker = new PermissionChecker();
                    if (permissionChecker.checkAppAccess() == 0) {
                        networkAnalyzer.startTracking(networkStatsManager, pm);
                    }

                    buttonStart.setText((CharSequence) "Running...");
                    buttonStart.setBackgroundColor(Color.parseColor("#41A541"));
                    isRunning = true;
                }
                else{

                    networkAnalyzer.stopTracking();

                    buttonStart.setText((CharSequence) "Start Tracking");
                    buttonStart.setBackgroundColor(Color.parseColor("#FF5675"));
                    isRunning = false;
                }
            }
        });

        switchTracking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    PermissionChecker permissionChecker = new PermissionChecker();
                    if (permissionChecker.checkAppAccess() == 0) {
                        networkAnalyzer.startTracking(networkStatsManager, pm);
                    }

                    buttonStart.setText((CharSequence) "Running...");
                    buttonStart.setBackgroundColor(Color.parseColor("#41A541"));
                    isRunning = true;
                }
                else{
                    networkAnalyzer.stopTracking();

                    buttonStart.setText((CharSequence) "Start Tracking");
                    buttonStart.setBackgroundColor(Color.parseColor("#FF5675"));
                    isRunning = false;
                }
            }
        });



    }

    class PermissionChecker  {
        public int checkAppAccess(){
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

                return -1;
            }
            return 0;
        }
    }

}
