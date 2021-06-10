package cf.khanhsb.icare_v2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cf.khanhsb.icare_v2.Adapter.GymListViewAdapter;
import cf.khanhsb.icare_v2.Adapter.UsageStatisticListViewAdapter;
import cf.khanhsb.icare_v2.Model.AppUsageInfo;
import cf.khanhsb.icare_v2.Model.NonScrollListView;

import static android.content.ContentValues.TAG;

public class UsageStatisticActivity extends AppCompatActivity {

    private ArrayList<AppUsageInfo> smallInfoList;
    private String tempString;
    private Button mOpenUsageSettingButton;
    private NonScrollListView listView;
    private ImageView backButton,moreButton;

    @Override
    protected void onResume() {
        super.onResume();
        long end_time = System.currentTimeMillis();
        long start_time = end_time - (1000*60*60);
        getUsageStatistics(start_time, end_time);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage_statistic);

        mOpenUsageSettingButton = findViewById(R.id.open_button);
        backButton = findViewById(R.id.button_backtohomefrag_time_statistic);
        moreButton = findViewById(R.id.more_menu_time_statistic);

        mOpenUsageSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            }
        });

        long end_time = System.currentTimeMillis();
        long start_time = end_time - (1000*60*60);
        getUsageStatistics(start_time, end_time);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMain = new Intent(UsageStatisticActivity.this, MainActivity.class);
                startActivity(toMain);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void getUsageStatistics(long start_time, long end_time) {

        UsageEvents.Event currentEvent;
        //  List<UsageEvents.Event> allEvents = new ArrayList<>();
        HashMap<String, AppUsageInfo> map = new HashMap<>();
        HashMap<String, List<UsageEvents.Event>> sameEvents = new HashMap<>();

        UsageStatsManager mUsageStatsManager = (UsageStatsManager) this.getSystemService(USAGE_STATS_SERVICE);

        if (mUsageStatsManager != null) {
            // Get all apps data from starting time to end time
            UsageEvents usageEvents = mUsageStatsManager.queryEvents(start_time, end_time);

            // Put these data into the map
            while (usageEvents.hasNextEvent()) {
                currentEvent = new UsageEvents.Event();
                usageEvents.getNextEvent(currentEvent);
                if (currentEvent.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED ||
                        currentEvent.getEventType() == UsageEvents.Event.ACTIVITY_PAUSED) {
                    //  allEvents.add(currentEvent);
                    String key = currentEvent.getPackageName();
                    if (map.get(key) == null) {
                        map.put(key, new AppUsageInfo(key));
                        sameEvents.put(key,new ArrayList<UsageEvents.Event>());
                    }
                    sameEvents.get(key).add(currentEvent);
                }
            }

            // Traverse through each app data which is grouped together and count launch, calculate duration
            for (Map.Entry<String,List<UsageEvents.Event>> entry : sameEvents.entrySet()) {
                int totalEvents = entry.getValue().size();
                if (totalEvents > 1) {
                    for (int i = 0; i < totalEvents - 1; i++) {
                        UsageEvents.Event E0 = entry.getValue().get(i);
                        UsageEvents.Event E1 = entry.getValue().get(i + 1);

                        if (E1.getEventType() == 1 || E0.getEventType() == 1) {
                            map.get(E1.getPackageName()).launchCount++;
                        }

                        if (E0.getEventType() == 1 && E1.getEventType() == 2) {
                            long diff = E1.getTimeStamp() - E0.getTimeStamp();
                            map.get(E0.getPackageName()).timeInForeground += diff;
                        }
                    }
                }

                // If First eventtype is ACTIVITY_PAUSED then added the difference of start_time and Event occuring time because the application is already running.
                if (entry.getValue().get(0).getEventType() == 2) {
                    long diff = entry.getValue().get(0).getTimeStamp() - start_time;
                    map.get(entry.getValue().get(0).getPackageName()).timeInForeground += diff;
                }

                // If Last eventtype is ACTIVITY_RESUMED then added the difference of end_time and Event occuring time because the application is still running .
                if (entry.getValue().get(totalEvents - 1).getEventType() == 1) {
                    long diff = end_time - entry.getValue().get(totalEvents - 1).getTimeStamp();
                    map.get(entry.getValue().get(totalEvents - 1).getPackageName()).timeInForeground += diff;
                }
            }

            smallInfoList = new ArrayList<>(map.values());
            ArrayList<String> appNameList = new ArrayList<>();
            ArrayList<String> appUsageTimeList = new ArrayList<>();
            ArrayList<Drawable> appIconList = new ArrayList<>();

            Drawable appIcon;
            String appName;
            String appUsageTime;
            Long tempTimeToHour,tempTimeToMins,tempTimeToSec;

            // Concatenating data to show in a text view. You may do according to your requirement
            for (AppUsageInfo appUsageInfo : smallInfoList)
            {
                //tempString = getAppNameFromPackage(appUsageInfo.packageName,this) + " - " + appUsageInfo.packageName + " : " + String.valueOf(appUsageInfo.timeInForeground) + "\n\n";
                try {
                    appIcon = getPackageManager().getApplicationIcon(appUsageInfo.packageName);
                    appIconList.add(appIcon);
                } catch (PackageManager.NameNotFoundException e) {
                    Log.w(TAG, String.format("App Icon is not found for %s",appUsageInfo.packageName));
                    appIcon = getDrawable(R.drawable.ic_launcher_foreground);
                    appIconList.add(appIcon);
                }

                tempTimeToHour = TimeUnit.MILLISECONDS.toHours(appUsageInfo.timeInForeground);
                tempTimeToMins = TimeUnit.MILLISECONDS.toMinutes(appUsageInfo.timeInForeground);
                tempTimeToSec = TimeUnit.MILLISECONDS.toSeconds(appUsageInfo.timeInForeground);

                appUsageTime = String.valueOf(tempTimeToMins) + " m " + String.valueOf(tempTimeToSec) +"s";
                appUsageTimeList.add(appUsageTime);

                appName = getAppNameFromPackage(appUsageInfo.packageName,this);
                appNameList.add(appName);
            }

            listView = (NonScrollListView) findViewById(R.id.list_view_usage_statistic);
            UsageStatisticListViewAdapter listViewAdapter = new UsageStatisticListViewAdapter(this,
                    R.layout.item_time_statistic,
                    appNameList,
                    appUsageTimeList,
                    appIconList);
            listView.setAdapter(listViewAdapter);
        } else {
            Toast.makeText(this, "Sorry...", Toast.LENGTH_SHORT).show();
        }
    }

    private String getAppNameFromPackage(String packageName, Context context) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        @SuppressLint("QueryPermissionsNeeded") List<ResolveInfo> pkgAppsList = context.getPackageManager()
                .queryIntentActivities(mainIntent, 0);

        for (ResolveInfo app : pkgAppsList) {
            if (app.activityInfo.packageName.equals(packageName)) {
                return app.activityInfo.loadLabel(context.getPackageManager()).toString();
            }
        }
        return null;
    }
}