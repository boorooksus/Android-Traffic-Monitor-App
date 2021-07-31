package com.example.trafficmonitorapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LogInternalFileProcessor implements LogFileProcessor{

    public boolean checkStoragePermissions(Activity activity) {
        // 쓰기 권한 있는지 체크
        int permission = ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

//        String[] test = {Manifest.permission.REQUEST_COMPANION_RUN_IN_BACKGROUND};
//        if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.REQUEST_COMPANION_RUN_IN_BACKGROUND) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(
//                    activity,
//                    test,
//                    0
//            );
//        }

        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        int REQUEST_EXTERNAL_STORAGE = 1;

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 권한 없는 경우 권한 허용 설정 오픈
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );

            return false;
        }

        return true;
    }

    public void writeLog(Activity activity, String log){
        @SuppressLint("SdCardPath") File file = new File(activity.getFilesDir(), "LogInternalFile.csv");

        if(!file.exists()){
            try {
                FileWriter fw = new FileWriter( file.getAbsoluteFile() ,true);
                BufferedWriter bw = new BufferedWriter( fw );

                String[] titles = new String[]{"time", "uid", "usage", "increase", "app label", "app name"};

                for (String title: titles){
                    bw.write(title);
                    bw.write(",");
                }

                bw.newLine();
                //bw.flush();
                bw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileWriter fw = new FileWriter( file.getAbsoluteFile() ,true);
            BufferedWriter bw = new BufferedWriter( fw );

            bw.write(log);
            bw.newLine();
            //bw.flush();
            bw.close();

            //System.out.println("######## log file path: " + file.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeLog2(Activity activity, String log){
        @SuppressLint("SdCardPath") File file = new File(activity.getFilesDir(), "LogInternalFile2.csv");

        if(!file.exists()){
            try {
                FileWriter fw = new FileWriter( file.getAbsoluteFile() ,true);
                BufferedWriter bw = new BufferedWriter( fw );

                String[] titles = new String[]{"uid", "app name", "app process name"};

                for (String title: titles){
                    bw.write(title);
                    bw.write(",");
                }

                bw.newLine();
                //bw.flush();
                bw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileWriter fw = new FileWriter( file.getAbsoluteFile() ,true);
            BufferedWriter bw = new BufferedWriter( fw );

            bw.write(log);
            bw.newLine();
            //bw.flush();
            bw.close();

            //System.out.println("######## log file path: " + file.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static void writeLog(String log){
//        try {
//            //fos = new FileOutputStream("testKeep.txt", true);
//            //fos.write(log.getBytes());
//
//            bw.write(log);
//            bw.newLine();
//            bw.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    public static void closeLogFile() throws IOException {
//        bw.close();
//    }

}
