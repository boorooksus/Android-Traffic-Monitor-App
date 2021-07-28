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

    long last = 0;

    class RunningAppsThread extends Thread{


        @Override
        public void run() {

//            // 서버와 소켓 통신
//            try {
//
//                Socket socket = new Socket("192.168.0.7", 5000);
//
//                DataInputStream dis = new DataInputStream(socket.getInputStream());
//                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
//
//                dos.writeInt(0);
//
//                socket.close();
//            } catch (Exception e) {
//                System.out.println("================ Error =====================");
//                e.printStackTrace();
//            }

            // 실행중인 앱의 트래픽

            ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningApps = manager.getRunningAppProcesses();

            for(ActivityManager.RunningAppProcessInfo runningApp : runningApps) {
                Log.v("uid", runningApp.uid + "");
                Log.v("send", TrafficStats.getUidTxBytes(runningApp.uid) + "");
                Log.v("diff", (TrafficStats.getUidTxBytes(runningApp.uid) - last) + "");
                last = TrafficStats.getUidTxBytes(runningApp.uid);
            }

            System.out.println("===================================================");

            // 네트워크 사용중인 앱의 트래픽
            // networkStatsManager.querySummary 이용

            NetworkStats networkStats;
            NetworkStatsManager networkStatsManager =
                    (NetworkStatsManager) getApplicationContext().
                            getSystemService(Context.NETWORK_STATS_SERVICE);

//            try {
//                networkStats =
//                        networkStatsManager.querySummary(NetworkCapabilities.TRANSPORT_WIFI,
//                                "",
//                                0,
//                                System.currentTimeMillis());
//                do {
//                    NetworkStats.Bucket bucket = new NetworkStats.Bucket();
//                    networkStats.getNextBucket(bucket);
//
//                    Log.v("uid", bucket.getUid() + "");
//                    Log.v("txBytes", bucket.getTxBytes() + "");
//
//                } while (networkStats.hasNextBucket());
//
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }

        }
    }

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                NetworkAnalyzer networkAnalyzer = new NetworkAnalyzer();

                NetworkStatsManager networkStatsManager =
                        (NetworkStatsManager) getApplicationContext().
                                getSystemService(Context.NETWORK_STATS_SERVICE);

                PackageManager pm = getPackageManager();


                PermissionChecker permissionChecker = new PermissionChecker();
                if(permissionChecker.checkAppAccess() == 0){
                    //networkAnalyzer.checkTotalApps(networkStatsManager, pm);
                    networkAnalyzer.checkNetworkApps(networkStatsManager, pm);
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
