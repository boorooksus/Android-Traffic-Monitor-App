package com.example.trafficmonitorapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import java.io.FileOutputStream;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Button buttonRefresh;  // 목록 새로고침 버튼
    Switch switchTracking;  // 모니터링 온오프 스위치
    ListView listViewHistory;  // 트래픽 히스토리 목록 리스트뷰
    AdapterHistory adapterHistory;  // 리스트뷰 어댑터
    static boolean isRunning = false;  // 모니터링 실행 중 여부
    static FileOutputStream fos;  // 로그 기록 파일
    Activity activity;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonRefresh = findViewById(R.id.buttonRefresh);
        switchTracking = findViewById(R.id.switchTracking);
        listViewHistory = findViewById(R.id.listViewHistory);
        adapterHistory = new AdapterHistory(this);

        listViewHistory.setAdapter(adapterHistory);
        switchTracking.setChecked(isRunning);
        buttonRefresh.setBackgroundColor(Color.parseColor(isRunning ? "#41A541":"#FF5675"));

        activity = this;
        final TrafficMonitor trafficMonitor = new TrafficMonitor(activity);
        //LogInternalFileProcessor.openLogFile(activity);

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
                    // 스위치가 켜졌을 때

                    if(!LogInternalFileProcessor.checkStoragePermissions(activity)){
                        switchTracking.setChecked(false);
                        isChecked = false;
                    }

                    if (isChecked && trafficMonitor.checkAppAccess()) {
                        // 앱 사용 기록 엑세스 권한 있는 경우 모니터링 시작
                        trafficMonitor.startTracking();
                        buttonRefresh.setBackgroundColor(Color.parseColor("#41A541"));
                        isRunning = true;

                        // 10초 간격으로 트래픽 히스토리 목록 업데이트
                        TimerTask tt = new TimerTask() {
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

                        Timer timer = new Timer();
                        timer.schedule(tt, 0, 10000);

                    } else{
                        // 앱 사용 기록 엑세스 권한 없는 경우 스위치 다시 끄기
                        switchTracking.setChecked(false);
                    }
                }
                else{
                    // 스위치가 꺼졌을 때 모니터링 중지
                    trafficMonitor.stopTracking();
                    buttonRefresh.setBackgroundColor(Color.parseColor("#FF5675"));
                    isRunning = false;
                }
            }
        });
    }
}
