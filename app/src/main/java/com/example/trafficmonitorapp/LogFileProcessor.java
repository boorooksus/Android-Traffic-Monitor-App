package com.example.trafficmonitorapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import java.io.FileOutputStream;
import java.io.IOException;

public class LogFileProcessor {

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    static FileOutputStream fos;  // 로그 기록 파일
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    public static boolean checkStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );

            return false;
        }

        return true;
    }

    public static void writeLog(Activity activity, String log){
        try {
            fos = activity.openFileOutput("logFile2.txt", Context.MODE_PRIVATE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fos.write(log.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
