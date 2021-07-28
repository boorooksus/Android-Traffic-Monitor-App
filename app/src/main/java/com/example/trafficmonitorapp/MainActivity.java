package com.example.trafficmonitorapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button button;
    Button buttonMakeTraffic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        buttonMakeTraffic = findViewById(R.id.buttonMakeTraffic);

        final NetworkStatsManager networkStatsManager =
                (NetworkStatsManager) getApplicationContext().
                        getSystemService(Context.NETWORK_STATS_SERVICE);

        final PackageManager pm = getPackageManager();

        final NetworkAnalyzer networkAnalyzer = new NetworkAnalyzer(networkStatsManager, pm);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                PermissionChecker permissionChecker = new PermissionChecker();
                if(permissionChecker.checkAppAccess() == 0){
                    //networkAnalyzer.checkTotalApps(networkStatsManager, pm);
                    networkAnalyzer.checkNetworkApps(networkStatsManager, pm);
                }


            }
        });

        buttonMakeTraffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NetworkStatsManager networkStatsManager =
                        (NetworkStatsManager) getApplicationContext().
                                getSystemService(Context.NETWORK_STATS_SERVICE);

                PackageManager pm = getPackageManager();

                //NetworkAnalyzer networkAnalyzer = new NetworkAnalyzer(networkStatsManager, pm);

                networkAnalyzer.useNetwork();
            }
        });




    }

    class PermissionChecker  {
        public int checkAppAccess(){
            try{
                NetworkStatsManager networkStatsManager =
                        (NetworkStatsManager) getApplicationContext().
                                getSystemService(Context.NETWORK_STATS_SERVICE);
                NetworkStats networkStats;


                networkStats =
                        networkStatsManager.queryDetailsForUid(
                                NetworkCapabilities.TRANSPORT_WIFI,
                                "",
                                0,
                                System.currentTimeMillis(),
                                1000);



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
