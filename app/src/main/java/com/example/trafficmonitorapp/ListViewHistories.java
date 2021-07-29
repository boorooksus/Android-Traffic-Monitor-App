package com.example.trafficmonitorapp;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ListViewHistories extends AppCompatActivity {

    ListView listViewHistory = (ListView)findViewById(R.id.listViewHistory);
    HistoryAdapter historyAdapter =  new HistoryAdapter();

    public void updateListView(){
        listViewHistory.setAdapter(historyAdapter);
    }


    class HistoryAdapter extends BaseAdapter {

        Histories histories = new Histories();

        @Override
        public int getCount() {
            return histories.getLength();
        }

        @Override
        public Object getItem(int position) {
            return histories.getHistory(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = new TextView(getApplicationContext());
            view.setText(histories.getHistory(position));
            return view;
        }
    }
}
