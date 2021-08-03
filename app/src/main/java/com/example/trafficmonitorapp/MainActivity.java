package com.example.trafficmonitorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;


public class MainActivity extends AppCompatActivity {

    Button buttonStatus;  // 목록 새로고침 버튼
    Switch switchTracking;  // 모니터링 온오프 스위치
    ListView listViewHistory;  // 트래픽 히스토리 목록 리스트뷰
    AdapterHistory adapterHistory;  // 리스트뷰 어댑터
    Activity activity;  // 메인 액티비티
    String colorRunning = "#41A541";  // 러닝 중일 때 버튼 색상(녹색)
    String colorStopped = "#808080";  // 중단 됐을 때 버튼 색상(회색)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        buttonStatus = findViewById(R.id.buttonStatus);
        switchTracking = findViewById(R.id.switchTracking);
        listViewHistory = findViewById(R.id.listViewHistory);
        adapterHistory = new AdapterHistory(this);


        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean isRunning = preferences.getBoolean("isRunning", false);  // 스위치가 켜졌는지 여부

        listViewHistory.setAdapter(adapterHistory);
        switchTracking.setChecked(isRunning);
        buttonStatus.setBackgroundColor(Color.parseColor(isRunning ? colorRunning:colorStopped));

        final TrafficMonitor trafficMonitor = new TrafficMonitor(activity, adapterHistory);

        if(isRunning){
            // 스위치가 켜져있다면 모니터링 실행
            trafficMonitor.startTracking();
        }

        // 모니터링 온오프 스위치 이벤트 리스터
        switchTracking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // 스위치 켰을 때

                    //  권한 확인
                    if (trafficMonitor.checkPermissions()) {
                        // 앱 사용 기록 엑세스 권한 있는 경우

                        // 작동 여부 공유 변수 true로 변경
                        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
                        editor.putBoolean("isRunning", true); // value to store
                        editor.apply();

                        // 모니터링 시작
                        trafficMonitor.startTracking();
                        buttonStatus.setBackgroundColor(Color.parseColor(colorRunning));
                        buttonStatus.setText("모니터링 작동 중");

                        // ===================================================
                        // 서비스 테스트
//                        MyJobIntentService myJobIntentService = new MyJobIntentService();
//                        myJobIntentService.setArgs(activity, adapterHistory);
//                        Intent intent = new Intent(getApplicationContext(), MyJobIntentService.class);
//                        intent.putExtra("msg", "==================== do somthing");
//                        startService(intent);
//                        MyJobIntentService.enqueueWork(activity, intent);
                        // =====================================================

                    } else{
                        // 앱 사용 기록 엑세스 권한 없는 경우 스위치 다시 끄기
                        switchTracking.setChecked(false);
                    }
                }
                else{
                    // 스위치 끄면 모니터링 중지
                    buttonStatus.setBackgroundColor(Color.parseColor(colorStopped));
                    buttonStatus.setText("모니터링 정지");
                    SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
                    editor.putBoolean("isRunning", false); // value to store
                    editor.apply();
                }
            }
        });
    }
}
