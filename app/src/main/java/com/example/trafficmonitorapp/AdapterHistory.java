package com.example.trafficmonitorapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// 네트워크 히스토리 리스트뷰 어댑터
public class AdapterHistory extends BaseAdapter {

    Histories histories;  // 트래픽 히스토리 목록 인스턴스
    LayoutInflater layoutInflater;
    Context context;  // 메인 액티비티 컨텍스트

    // Constructor
    public AdapterHistory(Context context) {
        this.context = context;
        histories = new Histories();
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return histories.getLength();
    }

    @Override
    public History getItem(int position) {
        return histories.getHistory(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // 리스트뷰의 히스토리 출력 방식 설정
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") View view = layoutInflater.inflate(R.layout.listview_costom, null);
        History history = histories.getHistory(position);

        LocalDateTime time = history.getTime();  // 업데이트 시각
        String name = history.getAppLabel() + " (" + history.getAppName() + ")";  // 앱 이름
        int uid = history.getUid();  // 앱 uid
        long usage = history.getUsage();  // 앱 사용량
        long diff = history.getDiff();  // 앱 트래픽 증가양

        TextView viewTime = view.findViewById(R.id.textViewTime);
        TextView viewName = view.findViewById(R.id.textViewName);
        TextView viewUid = view.findViewById(R.id.textViewUid);
        TextView viewUsage = view.findViewById(R.id.textViewUsage);

        viewTime.setText(time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        viewName.setText(name);
        viewName.setTypeface(null, Typeface.BOLD);
        viewUid.setText("UID: " + uid);
        viewUsage.setText("Usage: " + usage + " (+" + diff + ") bytes");

        return view;
    }
}
