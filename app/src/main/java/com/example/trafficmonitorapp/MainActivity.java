package com.example.trafficmonitorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Button buttonRefresh;
    //Button buttonMakeTraffic;
    Switch switchTracking;
    ListView listViewHistory;
    AdapterHistory adapterHistory;
    boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonRefresh = findViewById(R.id.buttonRefresh);
       // buttonMakeTraffic = findViewById(R.id.buttonMakeTraffic);
        switchTracking = findViewById(R.id.switchTracking);
        listViewHistory = findViewById(R.id.listViewHistory);
        adapterHistory = new AdapterHistory(this);

        listViewHistory.setAdapter(adapterHistory);

//        TimerTask tt = new TimerTask() {
//            @Override
//            public void run() {
//                adapterHistory.notifyDataSetChanged();
//                listViewHistory.setAdapter(adapterHistory);
//            }
//        };
//
//        /////////// / Timer 생성 //////////////
//        Timer timer = new Timer();
//        timer.schedule(tt, 0, 3000);


        final NetworkStatsManager networkStatsManager =
                (NetworkStatsManager) getApplicationContext().
                        getSystemService(Context.NETWORK_STATS_SERVICE);

        final PackageManager pm = getPackageManager();

        final NetworkAnalyzer networkAnalyzer = new NetworkAnalyzer(networkStatsManager, pm);




        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                adapterHistory.notifyDataSetChanged();
                listViewHistory.setAdapter(adapterHistory);

            }
        });

        switchTracking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    PermissionChecker permissionChecker = new PermissionChecker();
                    if (permissionChecker.checkAppAccess() == 0) {
                        networkAnalyzer.startTracking(networkStatsManager, pm);
                    }

                    buttonRefresh.setBackgroundColor(Color.parseColor("#41A541"));
                    isRunning = true;
                }
                else{
                    networkAnalyzer.stopTracking();

                    buttonRefresh.setBackgroundColor(Color.parseColor("#FF5675"));
                    isRunning = false;
                }
            }
        });
    }

    class AdapterHistory extends BaseAdapter{

        Histories histories;
        LayoutInflater layoutInflater;
        Context context;

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

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint("ViewHolder") View view = layoutInflater.inflate(R.layout.listview_costom, null);
            History history = histories.getHistory(position);

            LocalDateTime time = history.getTime();
            String name = history.getAppLabel() + "(" + history.getAppName() + ")";
            int uid = history.getUid();
            long usage = history.getUsage();


            TextView viewTime = (TextView)view.findViewById(R.id.textViewTime);
            TextView viewName = (TextView)view.findViewById(R.id.textViewName);
            TextView viewUid = (TextView)view.findViewById(R.id.textViewUid);
            TextView viewUsage = (TextView)view.findViewById(R.id.textViewUsage);
            //TextView viewTime = new TextView(getApplicationContext());

            viewTime.setText(time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            viewName.setText(name);
            viewName.setTypeface(null, Typeface.BOLD);
            viewUid.setText("UID: " + Integer.toString(uid));
            viewUsage.setText("Usage: " + Long.toString(usage));


            return view;
        }
    }

    class PermissionChecker  {
        public int checkAppAccess(){
            try{
                NetworkStatsManager networkStatsManager =
                        (NetworkStatsManager) getApplicationContext().
                                getSystemService(Context.NETWORK_STATS_SERVICE);

                NetworkStats networkStats =
                        networkStatsManager.queryDetailsForUid(
                                NetworkCapabilities.TRANSPORT_WIFI,
                                "",
                                0,
                                System.currentTimeMillis(),
                                1000);

                networkStats.close();

            } catch(Exception e){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setMessage("앱의 사용 기록 액세스를 허용해주세요");
//                builder.setTitle("Alert!");

                builder
                        .setPositiveButton(
                                "Yes",
                                new DialogInterface
                                        .OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which)
                                    {

                                        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                                        //finish();
                                    }
                                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return -1;
            }
            return 0;
        }
    }

}
