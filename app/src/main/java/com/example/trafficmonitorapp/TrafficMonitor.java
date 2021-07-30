package com.example.trafficmonitorapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.NetworkCapabilities;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

// 트래픽 모니터링 클래스
public class TrafficMonitor extends AppCompatActivity {

    private static Map<Integer, String> appNames = new HashMap<>();  // 앱의 uid와 이름 매핑
    private static Map<Integer, Long> lastUsage = new HashMap<>();  // 앱의 마지막 트래픽 저장
    private static boolean isInitialized = false;  // lastUsage 리스트 초기화 여부 저장
    private static boolean isRunning = false;  // 모니터링 실행 중 여부
    Histories histories = new Histories();  // 트래픽 히스토리 내역 리스트
    private NetworkStatsManager networkStatsManager;  // 어플 별 네트워크 사용 내역 얻을 때 사용
    private Activity activity;  // 메인 액티비티 context
    private PackageManager pm;  // 앱 정보들을 얻기 위한 패키지 매니저
    FileOutputStream fos;  // 로그 기록 파일

    // Constructor
    public TrafficMonitor(Activity activity) {
        // 초기화
        this.activity = activity;
        pm = activity.getPackageManager();
        networkStatsManager =
                (NetworkStatsManager) activity.getApplicationContext().
                        getSystemService(Context.NETWORK_STATS_SERVICE);

        // uid, 앱 이름 매핑
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        for (ApplicationInfo app : apps) {
            String appName = app.loadLabel(pm).toString();
            int uid = app.uid;

            appNames.put(uid, appName);
        }

        // 현재까지 앱별로 데이터 사용량 저장
        if(checkAppAccess()) {
            // 앱 사용 기록 액세스 권한 있는 경우에만 초기화
            updateUsage();
        }

    }

    // 앱의 사용 기록 액세스 권한 체크 함수
    // 권한 있는 경우 true, 없는 경우 유저를 설정 앱으로 보내고 false 리턴
    public boolean checkAppAccess(){
        try{
            // 아래 코드를 실행해 보고 에러가 없다면 권한이 존재
            // 에러 체크 외에 다른 목적은 없음
            NetworkStats networkStats =
                    networkStatsManager.queryDetailsForUid(
                            NetworkCapabilities.TRANSPORT_WIFI,
                            "",
                            0,
                            System.currentTimeMillis(),
                            0);
            networkStats.close();

            return true;

        } catch(Exception e){

            // 위에서 에러가 존재한다면 권한이 제한되어 있음
            // 유저를 설정 페이지로 보냄
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    // 알림 생성
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage("앱의 사용 기록 액세스를 허용해주세요");
                    builder.setPositiveButton(
                                    "확인",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            // 유저를 설정 페이지로 보냄
                                            activity.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                                            //finish();
                                        }
                                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
            return false;
        }
    }

    // 일정 시간마다 앱 트래픽 모니터링하는 함수
    public void startTracking() {
        // 일정 시간 간격으로 앱별 네트워크 사용량 체크하는 타이머
        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Updating...");
                updateUsage();
            }
        };

        // 네트워크 사용량 체크 타이머를 컨트롤하는 타이머
        final Timer timerController = new Timer();
        TimerTask timerControllerTask = new TimerTask() {
            @Override
            public void run() {
                if(!isRunning){
                    // 모니터링 중지시킨 경우, 현재 타이머와 네트워크 사용량 체크 타이머 캔슬
                    timer.cancel();
                    timerController.cancel();
                    System.out.println("Tracking is stopped");
                }
            }
        };

        timer.schedule(timerTask, 1000, 30000);
        timerController.schedule(timerControllerTask, 0, 3000);

        isRunning = true;
    }

    // 앱별 네트워크 사용량을 구하고 업데이트 하는 함수
    public void updateUsage(){

        // 쓰레드 생성하여 업데이트
        class RunningAppsThread extends Thread {
            @Override
            public void run() {
                NetworkStats networkStats;

                try {
                    // 와이파이를 이용한 앱들의 목록과 사용량 구하기
                    networkStats =
                            networkStatsManager.querySummary(NetworkCapabilities.TRANSPORT_WIFI,
                                    "",
                                    0,
                                    System.currentTimeMillis());
                    do {
                        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                        networkStats.getNextBucket(bucket);

                        // 앱 정보 얻기
                        String appLabel = Optional.ofNullable(appNames.get(bucket.getUid())).orElse("Unknown");  // 앱 레이블(기본 이름)
                        String appName = Optional.ofNullable(pm.getNameForUid(bucket.getUid())).orElse("Unknown");  // 앱 이름(상세 이름)
                        int uid = bucket.getUid();  // 앱 uid
                        final long txBytes = bucket.getTxBytes();  // 현재까지 보낸 트래픽 총량
                        long diff = txBytes - Optional.ofNullable(lastUsage.get(uid)).orElse((long) 0);  // 증가한 트래픽 양

                        if(diff <= 0){
                            // 앱 네트워크 사용량에 변동이 없는 경우 continue
                            continue;
                        }

                        // 현재 함수가 lastUsage 컬렉션 초기화를 위해 실행중인 경우에는 히스토리 목록에 넣지 않는다

                        if(isInitialized){
                            // 초기화가 이미 이루어진 경우 히스토리 목록 업데이트

                            // 히스토리 인스턴스 생성 후 히스토리 목록에 추가
                            History history = new History(LocalDateTime.now(), appLabel, appName, uid, txBytes, diff);
                            histories.addHistory(history);

                            // 로그 출력 및 파일에 저장
//                            String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//                            data += "\tuid: " + String.format("%-6s", uid);
//                            data += "\tusage: " + String.format("%-11s", txBytes);
//                            data += "\tincrease: " + String.format("%-7s", diff);
//                            data += "\t" + appLabel + " (" + appName + ")\n";
                            String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                            data += "," + uid + "," + txBytes + "," + diff + "," + appLabel + "," + appName;

                            Log.v("", data);

                            LogInternalFileProcessor.writeLog(activity, data);
                            //writeFile(data);
                        }

                        // 앱의 마지막 네트워크 사용량 업데이트
                        lastUsage.put(uid, txBytes);


                    } while (networkStats.hasNextBucket());

                    networkStats.close();

                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                // 코드가 여기까지 한 번이라도 실행된다면 lastUsage 컬랙션의 초기화가 완료된 것임
                isInitialized = true;
            }
        }

        // 쓰레드 생성 후 실행
        RunningAppsThread thread = new RunningAppsThread();
        thread.start();

    }

    // 앱 모니터링 종료 함수
    public void stopTracking(){

        isRunning = false;
        // startTracking() 함수에서 타이머가 이 전역 변수 값을 보고 타이머를 중단시킴
    }
}
