package com.example.trafficmonitorapp;

import android.app.Activity;

public interface LogFileProcessor {
    // 앱이 파일 읽고쓰기 권한을 가지고 있는 경우 true, 아닌 경우 false 리턴
    boolean checkStoragePermissions(Activity activity);

    // 로그 파일에 로그 추가
    void  writeLog(Activity activity, String log);
}
