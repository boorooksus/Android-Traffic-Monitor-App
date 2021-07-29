package com.example.trafficmonitorapp;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.NetworkCapabilities;
import android.os.Environment;
import android.os.RemoteException;
import android.util.Log;


import androidx.appcompat.app.AppCompatActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.Executor;

public class TrafficMonitor extends AppCompatActivity {

    private static Map<Integer, String> appNames = new HashMap<>();
    private static Map<Integer, Long> lastUsage = new HashMap<>();
    private static boolean isInitialized = false;
    private static boolean isRunning = false;
    Histories histories = new Histories();
    //private final Executor executor;

    public TrafficMonitor(final NetworkStatsManager networkStatsManager, final PackageManager pm) {

        // uid, 앱 이름 매핑
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        for (ApplicationInfo app : apps) {
            String appName = app.loadLabel(pm).toString();
            int uid = app.uid;

            appNames.put(uid, appName);
        }

        // 현재까지 앱별로 데이터 사용량 저장
        updateUsage(networkStatsManager, pm);

    }

    public void startTracking(final NetworkStatsManager networkStatsManager, final PackageManager pm) {
        // 일정 시간 간격으로 앱별 네트워크 사용량 체크
        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Updating...");
                updateUsage(networkStatsManager, pm);
            }
        };

        // 트래킹 타이머를 컨트롤하는 타이머
        final Timer timerController = new Timer();
        TimerTask timerControllerTask = new TimerTask() {
            @Override
            public void run() {
                if(!isRunning){
                    timer.cancel();
                    System.out.println("Tracking is stopped");
                    timerController.cancel();
                }
            }
        };

        isRunning = true;

        timer.schedule(timerTask, 1000, 30000);
        timerController.schedule(timerControllerTask, 0, 3000);



    }

    public void updateUsage(final NetworkStatsManager networkStatsManager, final PackageManager pm){

        class RunningAppsThread extends Thread {
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

                        String appLabel = Optional.ofNullable(appNames.get(bucket.getUid())).orElse("Unknown");
                        String appName = Optional.ofNullable(pm.getNameForUid(bucket.getUid())).orElse("Unknown");
                        int uid = bucket.getUid();
                        final long txBytes = bucket.getTxBytes();
                        long diff = txBytes - Optional.ofNullable(lastUsage.get(uid)).orElse((long) 0);

                        if(diff <= 0){
                            continue;
                        }


                        if(isInitialized){
                            String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n";
                            data += appLabel;
                            data += "(" + appName + ")\n";
                            data += "uid: " + uid + "\nusage: " + txBytes + "\n";
                            Log.v("", data);

                            History history = new History(LocalDateTime.now(), appLabel, appName, uid, txBytes, diff);

                            histories.addHistory(history);
                        }

                        lastUsage.put(uid, txBytes);

                        //writeFile(data);
                    } while (networkStats.hasNextBucket());

                    networkStats.close();

                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                isInitialized = true;
            }
        }

        RunningAppsThread thread = new RunningAppsThread();
        thread.start();

    }

    public void stopTracking(){
        isRunning = false;
        //period = 0;
    }


    public void writeFile(String data) throws IOException {
        String filename = "trafficHistory.txt";

        FileWriter writer;

        try {

            // 외부 저장 공간 root 하위에 myApp이라는 폴더 경로 획득

            String dirPath = Objects.requireNonNull(getExternalFilesDir(null)).getAbsolutePath() + "/myApp";

            File dir = new File(dirPath); // 객체 생성

            if(!dir.exists()){ // 폴더가 없으면

                dir.mkdir(); // 만들어 준다.

            }

            // myApp 폴더 밑에 myfile.txt 파일 지정

            File file = new File(dir+"/trafficHistory.txt");

            if(!file.exists()){ // 파일이 없다면

                file.createNewFile(); // 새로 만들어 준다.

            }

            // 파일에 쓰기

            writer = new FileWriter(file, true);

            writer.write("hi");

            writer.flush();

            writer.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

        //디렉토리 없으면 생성
//        File dir = new File(dirPath);
//        if(!dir.exists()){
//            dir.mkdir();
//        }

        //파일객체
        //File file = new File(dir, filename);
//        File file = new File("", filename);
//
//        try{
////            FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
//            FileOutputStream fos = new FileOutputStream(file.getName(), true);
//
//            try{
//                fos.write(data.getBytes());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

//        try {
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(file.getName(), Context.MODE_PRIVATE));
//            outputStreamWriter.write(data);
//            outputStreamWriter.close();
//        }
//        catch (IOException e) {
//            Log.e("Exception", "File write failed: " + e.toString());
//        }




//        OutputStreamWriter osw
//                = new OutputStreamWriter(
//                        openFileOutput(filename, Context.MODE_PRIVATE));
//        osw.write(data);
//        osw.close();
    }


}
