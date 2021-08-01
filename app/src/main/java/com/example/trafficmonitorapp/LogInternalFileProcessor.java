package com.example.trafficmonitorapp;

import android.annotation.SuppressLint;
import android.app.Activity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class LogInternalFileProcessor implements LogFileProcessor{

    @Override
    public boolean checkStoragePermissions(Activity activity) {
        // internal storage는 권한이 따로 필요하지 않음
        return true;
    }

    @Override
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
}
