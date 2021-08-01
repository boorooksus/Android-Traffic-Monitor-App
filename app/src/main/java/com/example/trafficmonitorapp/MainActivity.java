package com.example.trafficmonitorapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Button buttonRefresh;  // 목록 새로고침 버튼
    Switch switchTracking;  // 모니터링 온오프 스위치
    ListView listViewHistory;  // 트래픽 히스토리 목록 리스트뷰
    AdapterHistory adapterHistory;  // 리스트뷰 어댑터
    Activity activity;  // 메인 액티비티
    String colorRunning = "#41A541";
    String colorStopped = "#dc143c";

    TimerTask timerTaskUpdateHistories = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapterHistory.notifyDataSetChanged();
                }
            });
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonRefresh = findViewById(R.id.buttonRefresh);
        switchTracking = findViewById(R.id.switchTracking);
        listViewHistory = findViewById(R.id.listViewHistory);
        adapterHistory = new AdapterHistory(this);


        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean isRunning = preferences.getBoolean("isRunning", false);

        listViewHistory.setAdapter(adapterHistory);
        switchTracking.setChecked(isRunning);
        buttonRefresh.setBackgroundColor(Color.parseColor(isRunning ? colorRunning:colorStopped));

        activity = this;
        final TrafficMonitor trafficMonitor = new TrafficMonitor(activity);

        // 10초 간격으로 트래픽 히스토리 목록 업데이트
        Timer timerUpdateHistories = new Timer();
        timerUpdateHistories.schedule(timerTaskUpdateHistories, 0, 10000);

        if(isRunning){
            trafficMonitor.startTracking();

        }


        // 목록 새로고침 버튼 이벤트 리스너
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterHistory.notifyDataSetChanged();
            }
        });

        // 모니터링 온오프 스위치 이벤트 리스터
        switchTracking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // 스위치가 켰을 때

                    // 스토리지 파일 접근 권한 확인(internal storage 사용하는 경우 항상 true)
                    LogInternalFileProcessor logFileProcessor = new LogInternalFileProcessor();
                    if(!logFileProcessor.checkStoragePermissions(activity)){
                        switchTracking.setChecked(false);
                        isChecked = false;
                    }

                    if (isChecked && trafficMonitor.checkAppAccessPermission()) {
                        // 앱 사용 기록 엑세스 권한 있는 경우 모니터링 시작
                        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
                        editor.putBoolean("isRunning", true); // value to store
                        editor.apply();

                        trafficMonitor.startTracking();
                        buttonRefresh.setBackgroundColor(Color.parseColor(colorRunning));

                        // ===================================================
//                        MyJobIntentService myJobIntentService = new MyJobIntentService();
//                        Intent intent = new Intent(getApplicationContext(), MyJobIntentService.class);
//                        intent.putExtra("msg", "do somthing");
//                        startService(intent);
//                        MyJobIntentService.enqueueWork(activity, intent);

                        // =====================================================

                        // 10초 간격으로 트래픽 히스토리 목록 업데이트
//                        Timer timerUpdateHistories = new Timer();
//                        timerUpdateHistories.schedule(timerTaskUpdateHistories, 0, 10000);

                    } else{
                        // 앱 사용 기록 엑세스 권한 없는 경우 스위치 다시 끄기
                        switchTracking.setChecked(false);
                    }
                }
                else{
                    // 스위치가 꺼졌을 때 모니터링 중지
//                    trafficMonitor.stopTracking();
                    buttonRefresh.setBackgroundColor(Color.parseColor(colorStopped));


                    //isRunning = false;
                    SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
                    editor.putBoolean("isRunning", false); // value to store
                    editor.apply();
                }
            }
        });


    }
}
