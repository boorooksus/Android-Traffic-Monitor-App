package com.example.trafficmonitorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.RemoteException;
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
//
//            // current running app traffic
//            ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//            List<ActivityManager.RunningAppProcessInfo> runningApps = manager.getRunningAppProcesses();
//
//            for(ActivityManager.RunningAppProcessInfo runningApp : runningApps) {
//                Log.v("uid", runningApp.uid + "");
//                Log.v("send", TrafficStats.getUidTxBytes(runningApp.uid) + "");
//                Log.v("diff", (TrafficStats.getUidTxBytes(runningApp.uid) - last) + "");
//                last = TrafficStats.getUidTxBytes(runningApp.uid);
//
//            }
//
//            System.out.println("===================================================");

            // 설치된 모든 앱 정보

//            PackageManager pm = getPackageManager();
//            List<ApplicationInfo> apps = pm.getInstalledApplications(0);
//
//            for (ApplicationInfo app : apps) {
//                // 설치된 앱들의 트래픽 - 이 앱 제외하고는 -1 리턴
//                String appName = app.loadLabel(pm).toString();
//                int uid = app.uid;
//                long send = TrafficStats.getUidTxBytes(uid);
//                long received = TrafficStats.getUidRxBytes(uid);
//
//                Log.v("" + uid , "Send :" + send + ", Received :" + received);
//            }

            NetworkStats networkStatsByApp;
            NetworkStatsManager networkStatsManager =
                    (NetworkStatsManager) getApplicationContext().getSystemService(Context.NETWORK_STATS_SERVICE);
            try {
                networkStatsByApp =
                        networkStatsManager.querySummary(NetworkCapabilities.TRANSPORT_WIFI, "", 0, System.currentTimeMillis());
                do {
                    NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                    networkStatsByApp.getNextBucket(bucket);

                    System.out.println("\nbucket: " + bucket.getUid());
                    System.out.println("txBytes: " + bucket.getTxBytes());
                } while (networkStatsByApp.hasNextBucket());

            } catch (RemoteException e) {
                e.printStackTrace();
            }

            PackageManager pm = getPackageManager();
            List<ApplicationInfo> apps = pm.getInstalledApplications(0);


            for (ApplicationInfo app : apps) {
                // 설치된 앱들의 트래픽 - 이 앱 제외하고는 -1 리턴
                String appName = app.loadLabel(pm).toString();
                int uid = app.uid;
                long send = TrafficStats.getUidTxBytes(uid);
                long received = TrafficStats.getUidRxBytes(uid);

                networkStatsByApp =
                        networkStatsManager.queryDetailsForUid(NetworkCapabilities.TRANSPORT_WIFI,
                                "",
                                0,
                                System.currentTimeMillis(),
                                uid);
//                do {
//                    NetworkStats.Bucket bucket = new NetworkStats.Bucket();
//                    networkStatsByApp.getNextBucket(bucket);
//
//                    System.out.println("bucket: " + bucket.getUid());
//                    System.out.println("txBytes: " + bucket.getTxBytes());
//                } while (networkStatsByApp.hasNextBucket());

                //                NetworkStats.Bucket bucket = new NetworkStats.Bucket();
//                networkStatsByApp.getNextBucket(bucket);
//                System.out.println("bucket: " + bucket.getUid());
//                System.out.println("txBytes: " + bucket.getTxBytes());
                //Log.v("" + uid , "Send :" + send + ", Received :" + received);

            }




            //super.run();
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
                RunningAppsThread thread = new RunningAppsThread();
                thread.start();

            }
        });



    }
}