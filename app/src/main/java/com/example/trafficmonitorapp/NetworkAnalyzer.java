package com.example.trafficmonitorapp;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.NetworkCapabilities;
import android.os.RemoteException;
import android.util.Log;


import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class NetworkAnalyzer extends AppCompatActivity {

    public void checkTotalApps(final NetworkStatsManager networkStatsManager, final PackageManager pm){

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
                                        0,
                                        System.currentTimeMillis(),
                                        uid);
                        do {
                            NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                            networkStats.getNextBucket(bucket);

                            if (bucket.getTxBytes() > 0){
                                Log.v("uid", bucket.getUid() + "");
                                Log.v("txBytes", bucket.getTxBytes() + "");

                            }

                        } while (networkStats.hasNextBucket());

                    } catch (Exception e){
                        break;
                    }
                }
            }
        }

        RunningAppsThread thread = new RunningAppsThread();
        thread.start();
    }

    public void checkNetworkApps(final NetworkStatsManager networkStatsManager, final PackageManager pm){

        class RunningAppsThread extends Thread{
            @Override
            public void run() {

                NetworkStats networkStats;

            try {
                networkStats =
                    networkStatsManager.querySummary(NetworkCapabilities.TRANSPORT_WIFI,
                            "",
                            0,
                            System.currentTimeMillis());
                do {
                    NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                    networkStats.getNextBucket(bucket);

                    Log.v("uid", bucket.getUid() + "");
                    Log.v("txBytes", bucket.getTxBytes() + "");

                } while (networkStats.hasNextBucket());

            } catch (RemoteException e) {
                e.printStackTrace();
            }
            }
        }

        RunningAppsThread thread = new RunningAppsThread();
        thread.start();

    }

}
