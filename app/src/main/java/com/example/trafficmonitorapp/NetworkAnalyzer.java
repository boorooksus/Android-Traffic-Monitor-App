package com.example.trafficmonitorapp;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.NetworkCapabilities;
import android.os.RemoteException;
import android.util.Log;


import androidx.appcompat.app.AppCompatActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NetworkAnalyzer {

    static Map<Integer, String> appNames = new HashMap<>();
    static Map<Integer, Long> lastUsage = new HashMap<>();

//    private NetworkStatsManager networkStatsManager;
//    private PackageManager pm;

    public NetworkAnalyzer(final NetworkStatsManager networkStatsManager, final PackageManager pm) {

        List<ApplicationInfo> apps = pm.getInstalledApplications(0);

        // uid, 앱 이름 매핑
        for (ApplicationInfo app : apps) {
            String appName = app.loadLabel(pm).toString();
            int uid = app.uid;

            appNames.put(uid, appName);
        }

        // 현재까지 앱별로 데이터 사용량 저장

        NetworkStats networkStats;

        try {

            networkStats =
                    networkStatsManager.querySummary(NetworkCapabilities.TRANSPORT_WIFI,
                            "",
                            System.currentTimeMillis() - 10000,
                            System.currentTimeMillis());
            do {
                NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                networkStats.getNextBucket(bucket);


                //String appName = pm.getNameForUid(bucket.getUid());
                String appName = appNames.get(bucket.getUid());
                int uid = bucket.getUid();
                long txBytes = bucket.getTxBytes();

                if(lastUsage.containsKey(uid) && lastUsage.get(uid) < txBytes){
                    Log.v("last", lastUsage.get(uid) + "");
                    Log.v("cur", txBytes + "");

                    lastUsage.replace(uid, txBytes);
                } else{
                    lastUsage.put(uid, txBytes);

                }

            } while (networkStats.hasNextBucket());


        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void checkNetworkApps(final NetworkStatsManager networkStatsManager, final PackageManager pm) {

        class RunningAppsThread extends Thread {
            @Override
            public void run() {


                NetworkStats networkStats;

                try {

                    networkStats =
                            networkStatsManager.querySummary(NetworkCapabilities.TRANSPORT_WIFI,
                                    "",
                                    System.currentTimeMillis() - 10000,
                                    System.currentTimeMillis());
                    do {
                        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                        networkStats.getNextBucket(bucket);


                        //String appName = pm.getNameForUid(bucket.getUid());
                        String appName = appNames.get(bucket.getUid());
                        int uid = bucket.getUid();
                        long txBytes = bucket.getTxBytes();


                        if(lastUsage.containsKey(uid) && lastUsage.get(uid) >= txBytes){
                            //System.out.println("========hi");
                            continue;
                        }
//                        else if (lastUsage.containsKey(uid) && lastUsage.get(uid) > txBytes){
//                            System.out.println("========hi==========");
//                            Log.v("last", lastUsage.get(uid) + "");
//                            Log.v("cur", txBytes + "");
//                        }

                        if(lastUsage.containsKey(uid)){
                            Log.v("last", lastUsage.get(uid) + "");
                            Log.v("cur", txBytes + "");

                            lastUsage.replace(uid, txBytes);
                        } else{
                            lastUsage.put(uid, txBytes);

                        }

                        if (appName != null) {

                            Log.v("name", appName);
                        }
                        if(pm.getNameForUid(bucket.getUid()) != null){
                            Log.v("name2", pm.getNameForUid(bucket.getUid()));

                        }

                        Log.v("uid", uid + "");
                        Log.v("usage", txBytes + "");


                    } while (networkStats.hasNextBucket());


                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        RunningAppsThread thread = new RunningAppsThread();
        thread.start();

    }

    public void checkTotalApps (final NetworkStatsManager networkStatsManager, final PackageManager pm) {

        class RunningAppsThread extends Thread{
            @Override
            public void run() {
                NetworkStats networkStats;
                List<ApplicationInfo> apps = pm.getInstalledApplications(0);

                for (ApplicationInfo app : apps) {
                    String appName = app.loadLabel(pm).toString();
                    int uid = app.uid;

                    try {
                        networkStats =
                                networkStatsManager.queryDetailsForUid(
                                        NetworkCapabilities.TRANSPORT_WIFI,
                                        "",
                                        System.currentTimeMillis() - 10000,
                                        System.currentTimeMillis(),
                                        uid);

                        do{

                            NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                            networkStats.getNextBucket(bucket);

                            if(bucket.getTxBytes() > 0) {

                                Log.v("name", appName);
                                Log.v("uid", uid + "");
                                Log.v("txBytes", bucket.getTxBytes() + "");
                            }

//                            if (txBytes.get(appName) != null && bucket.getTxBytes() > txBytes.get(appName)){
//                                Log.v("name", appName);
//                                Log.v("uid", uid + "");
//
//                                Log.v("orig", txBytes.get(appName) + "");
//                                Log.v("txBytes", bucket.getTxBytes() + "");
//
//                                txBytes.put(appName, bucket.getTxBytes());
//
//                            }
                        } while (networkStats.hasNextBucket());


                    } catch (Exception e){
                        return;
                    }
                }
            }
        }

        RunningAppsThread thread = new RunningAppsThread();
        thread.start();
        }



    public void useNetwork () {
        // 서버와 소켓 통신

        class RunningAppsThread extends Thread {
            @Override
            public void run() {
                try {

                    Socket socket = new Socket("192.168.0.7", 5000);

                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                    dos.writeInt(0);

                    socket.close();
                } catch (Exception e) {
                    System.out.println("================ Error =====================");
                    e.printStackTrace();
                }
            }

        }

        RunningAppsThread thread = new RunningAppsThread();
        thread.start();
    }

}
