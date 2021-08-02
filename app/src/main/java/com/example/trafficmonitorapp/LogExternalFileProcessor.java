package com.example.trafficmonitorapp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

// External Storage 파일 관리 class
public class LogExternalFileProcessor implements LogFileProcessor {

    // 외부 스토리지 권한 유무 체크 함수
    // 권한 있으면 true, 없으면 false 리턴
    @Override
    public boolean checkStoragePermissions(Activity activity) {
        // 쓰기 권한 있는지 체크
        int permission = ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // 외부 스토리지 읽기, 쓰기 권한
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

    // 로그 파일 입력 함수
    @Override
    public void writeLog(Activity activity, String log) {
        // 아직 구현 안됨
        throw new UnsupportedOperationException();
    }
}
