package com.example.trafficmonitorapp;

import java.time.LocalDateTime;

public class History {
    private LocalDateTime time;
    private String appLabel;
    private String appName;
    private int uid;
    private long usage;

    public History(LocalDateTime time, String appLabel, String appName, int uid, long usage) {
        this.time = time;
        this.appLabel = appLabel;
        this.appName = appName;
        this.uid = uid;
        this.usage = usage;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getAppLabel() {
        return appLabel;
    }

    public void setAppLabel(String appLabel) {
        this.appLabel = appLabel;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public long getUsage() {
        return usage;
    }

    public void setUsage(long usage) {
        this.usage = usage;
    }
}
