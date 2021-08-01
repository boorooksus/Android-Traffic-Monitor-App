package com.example.trafficmonitorapp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class LogExternalFileProcessor implements LogFileProcessor {

    @Override
    public boolean checkStoragePermissions(Activity activity) {
        // 쓰기 권한 있는지 체크
        int permission = ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

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

    @Override
    public void writeLog(Activity activity, String log) {
        // 아직 구현 안됨
        throw new UnsupportedOperationException();
    }
}
