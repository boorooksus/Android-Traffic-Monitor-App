package com.example.trafficmonitorapp;

import android.app.Activity;

public interface LogFileProcessor {
    boolean checkStoragePermissions(Activity activity);

    void  openLogFile(Activity activity);

    void  writeLog(Activity activity, String log);

}
