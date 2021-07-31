package com.example.trafficmonitorapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class MyJobIntentService extends JobIntentService {

    public static void enqueueWork(Context context, Intent intent){
        enqueueWork(context, MyJobIntentService.class, 1001, intent);
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

            }
        }

        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                RunningAppsThread thread = new RunningAppsThread();
                thread.start();
            }
        };

        timer.schedule(timerTask, 0, 1000);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("msg", "============================================Job destroyed");
    }

}
