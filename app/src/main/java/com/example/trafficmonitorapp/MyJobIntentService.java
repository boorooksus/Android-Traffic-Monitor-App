package com.example.trafficmonitorapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class MyJobIntentService extends JobIntentService {

    @SuppressLint("StaticFieldLeak")
    static Activity activity;
    @SuppressLint("StaticFieldLeak")
    static AdapterHistory adapterHistory;

    public void setArgs(Activity activity, AdapterHistory adapterHistory) {
        MyJobIntentService.activity = activity;
        MyJobIntentService.adapterHistory = adapterHistory;
    }

    public static void enqueueWork(Activity activity, Intent intent){

        enqueueWork(activity, MyJobIntentService.class, 1001, intent);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        Log.v("msg", "=============================================Job started...");
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        class RunningAppsThread extends Thread {
            @Override
            public void run() {
                Log.v("JobIntentService", LocalDateTime.now().toString());

                final TrafficMonitor trafficMonitor = new TrafficMonitor(activity, adapterHistory);

                trafficMonitor.startTracking();
            }
        }
//
//        final Timer timer = new Timer();
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//
//            }
//        };
//
//        timer.schedule(timerTask, 0, 1000);

        RunningAppsThread thread = new RunningAppsThread();
        thread.start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("msg", "============================================Job destroyed");
    }

}
