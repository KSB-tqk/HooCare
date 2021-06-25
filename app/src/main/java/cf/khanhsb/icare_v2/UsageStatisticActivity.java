package cf.khanhsb.icare_v2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.collection.LLRBBlackValueNode;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cf.khanhsb.icare_v2.Adapter.GymListViewAdapter;
import cf.khanhsb.icare_v2.Adapter.UsageStatisticListViewAdapter;
import cf.khanhsb.icare_v2.Model.AppUsageInfo;
import cf.khanhsb.icare_v2.Model.NonScrollListView;

import static android.content.ContentValues.TAG;
import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

public class UsageStatisticActivity extends AppCompatActivity {

    private ArrayList<AppUsageInfo> smallInfoList;
    private String tempString;
    private Button mOpenUsageSettingButton;
    private NonScrollListView listView;
    private ImageView backButton,moreButton;
    private LinearLayout eyeConditionBox,setUpTimeUsageLinear;
    private TextView totalTextView,dateTimeTextView,eyeConditionTextView;
    private MaterialCardView totalTimeUsageCardView;
    private static final String allowUsageAccess = "allowUsageAccess";
    private static final String tempEmail = "tempEmail";
    private FirebaseFirestore firestore;
    private DocumentReference docRef;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        long end_time = System.currentTimeMillis();

        LocalTime now = LocalTime.now();
        long start_time = now.getHour()*1000*60*60 + now.getMinute()*1000*60 + now.getSecond()*1000;

        SharedPreferences sharedPreferences = getSharedPreferences(allowUsageAccess, MODE_PRIVATE);

        getUsageStatistics(end_time - start_time, end_time);

        String allowUsage = sharedPreferences.getString("allowUsage", "");

        if(!allowUsage.equals("")){
            setUpTimeUsageLinear.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            totalTimeUsageCardView.setVisibility(View.VISIBLE);
        }
        else {
            setUpTimeUsageLinear.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            totalTimeUsageCardView.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage_statistic);

        mOpenUsageSettingButton = findViewById(R.id.open_button);
        backButton = findViewById(R.id.button_backtohomefrag_time_statistic);
        moreButton = findViewById(R.id.more_menu_time_statistic);
        totalTextView = findViewById(R.id.total_time_text);
        dateTimeTextView = findViewById(R.id.date_time_statistic);
        eyeConditionTextView = findViewById(R.id.eye_condition);
        eyeConditionBox = findViewById(R.id.eye_condition_linear);
        setUpTimeUsageLinear = findViewById(R.id.setup_time_usage_linear);
        totalTimeUsageCardView = findViewById(R.id.total_time_OS_card_view);
        listView = (NonScrollListView) findViewById(R.id.list_view_usage_statistic);

        SharedPreferences sharedPreferences = getSharedPreferences(allowUsageAccess, MODE_PRIVATE);

        String allowUsage = sharedPreferences.getString("allowUsage", "");
        if(!allowUsage.equals("")){
            setUpTimeUsageLinear.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            totalTimeUsageCardView.setVisibility(View.VISIBLE);
        }
        else {
            setUpTimeUsageLinear.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            totalTimeUsageCardView.setVisibility(View.GONE);
        }

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

            if(smallInfoList.size()>0) {
                SharedPreferences sharedPreferences = getSharedPreferences(allowUsageAccess, MODE_PRIVATE);
                SharedPreferences.Editor editor;
                editor = sharedPreferences.edit();
                editor.putString("allowUsage", "true");
                editor.apply();
            }

            AppUsageInfo[] tempInfoList = new AppUsageInfo[smallInfoList.size()];
            for(int i = 0;i < smallInfoList.size();i++) {
                tempInfoList[i] = smallInfoList.get(i);
            }
            Arrays.sort(tempInfoList);

            ArrayList<String> appNameList = new ArrayList<>();
            ArrayList<String> appUsageTimeList = new ArrayList<>();
            ArrayList<Drawable> appIconList = new ArrayList<>();
            ArrayList<Long> appUsageTimeListInMilliSec = new ArrayList<>();

            Drawable appIcon;
            String appName;
            Long othersAppTotalTime = 0L,totalTime = 0L;

            // Concatenating data to show in a text view. You may do according to your requirement
            for (AppUsageInfo appUsageInfo : tempInfoList)
            {
                appName = getAppNameFromPackage(appUsageInfo.packageName,this);
                if(appName != null){
                    appNameList.add(appName);

                    appUsageTimeList.add(getTimeUsage(appUsageInfo.timeInForeground));
                    appUsageTimeListInMilliSec.add(appUsageInfo.timeInForeground);

                    try {
                        appIcon = getPackageManager().getApplicationIcon(appUsageInfo.packageName);
                        appIconList.add(appIcon);
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.w(TAG, String.format("App Icon is not found for %s",appUsageInfo.packageName));
                        appIcon = getDrawable(R.drawable.ic_launcher_foreground);
                        appIconList.add(appIcon);
                    }
                }
                else {
                    if(appUsageInfo.packageName.equals("com.android.camera2")){
                        appNameList.add("Camera");
                        appIconList.add(getAppIcon(appUsageInfo.packageName));
                        appUsageTimeList.add(getTimeUsage(appUsageInfo.timeInForeground));
                        appUsageTimeListInMilliSec.add(appUsageInfo.timeInForeground);
                    }
                    else {
                        othersAppTotalTime += appUsageInfo.timeInForeground;
                    }
                }
                totalTime += appUsageInfo.timeInForeground;
            }

            //set value for big total time text
            //set view for eye condition box
            String eyeCondition;
            String tempTime = getTimeUsage(totalTime);
            totalTextView.setText(tempTime);

            Runnable uploadDataRunnable = new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    SharedPreferences sharedPreferences = getSharedPreferences(tempEmail, MODE_PRIVATE);
                    String theTempEmail = sharedPreferences.getString("Email", "");

                    firestore = FirebaseFirestore.getInstance();

                    LocalDate today = LocalDate.now();
                    LocalDate monday = today.with(previousOrSame(MONDAY));

                    docRef = firestore.collection("daily").
                            document("week-of-" + monday.toString()).
                            collection(today.toString()).
                            document(theTempEmail);
                    docRef.update("time_on_screen", tempTime );
                }
            };

            Thread backgroundThread = new Thread(uploadDataRunnable);
            backgroundThread.start();

            if(tempTime.contains("h")){
                String[] splitTime = tempTime.split("h");
                int tempHour = Integer.parseInt(splitTime[0]);
                if(tempHour < 3){
                    eyeCondition = "Ok";
                    eyeConditionTextView.setText(eyeCondition);
                    eyeConditionTextView.setTextColor(Color.parseColor("#2FA678"));
                    GradientDrawable drawable = (GradientDrawable) eyeConditionBox.getBackground();
                    drawable.setStroke(8, Color.parseColor("#2FA678"));
                }
                else if(tempHour < 4){
                    eyeCondition = "Medium";
                    eyeConditionTextView.setText(eyeCondition);
                    eyeConditionTextView.setTextColor(Color.parseColor("#FCBF52"));
                    GradientDrawable drawable = (GradientDrawable) eyeConditionBox.getBackground();
                    drawable.setStroke(8, Color.parseColor("#FCBF52"));
                }
                else if(tempHour < 6){
                    eyeCondition = "High";
                    eyeConditionTextView.setText(eyeCondition);
                    eyeConditionTextView.setTextColor(Color.parseColor("#FC578A"));
                    GradientDrawable drawable = (GradientDrawable) eyeConditionBox.getBackground();
                    drawable.setStroke(8, Color.parseColor("#FC578A"));
                }
                else {
                    eyeCondition = "Very High";
                    eyeConditionTextView.setText(eyeCondition);
                    eyeConditionTextView.setTextColor(Color.parseColor("#FC578A"));
                    GradientDrawable drawable = (GradientDrawable) eyeConditionBox.getBackground();
                    drawable.setStroke(8, Color.parseColor("#FC578A"));
                }
            }
            else {
                eyeCondition = "Ok";
                eyeConditionTextView.setText(eyeCondition);
                eyeConditionTextView.setTextColor(Color.GREEN);
                GradientDrawable drawable = (GradientDrawable) eyeConditionBox.getBackground();
                drawable.setStroke(8, Color.GREEN);
            }

            //set value for datetime text
            Date calendar = Calendar.getInstance().getTime();
            System.out.println("Current time => " + calendar);
            String day = (String) DateFormat.format("dd", calendar); // 20
            String monthString = (String) DateFormat.format("MMM", calendar); // Jun
            String today = day + " " + monthString;

            dateTimeTextView.setText("Today, " + today);

            appNameList.add("Other App");
            appIconList.add(getDrawable(R.drawable.other_app_icon));
            appUsageTimeList.add(getTimeUsage(othersAppTotalTime));
            appUsageTimeListInMilliSec.add(othersAppTotalTime);

            listView = (NonScrollListView) findViewById(R.id.list_view_usage_statistic);
            UsageStatisticListViewAdapter listViewAdapter = new UsageStatisticListViewAdapter(this,
                    R.layout.item_time_statistic,
                    appNameList,
                    appUsageTimeList,
                    appIconList,
                    appUsageTimeListInMilliSec,
                    othersAppTotalTime);
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

    private String getTimeUsage(long timeUsageInMilliSec){
        String ans = "";
        long timeToHour,timeToMins,timeToSec;
        timeToHour = TimeUnit.MILLISECONDS.toHours(timeUsageInMilliSec);
        timeToMins = TimeUnit.MILLISECONDS.toMinutes(timeUsageInMilliSec);
        timeToSec = TimeUnit.MILLISECONDS.toSeconds(timeUsageInMilliSec);

        if(timeToHour > 0) {
            long realMins = timeUsageInMilliSec - (timeToHour*1000*60*60);
            timeToMins = TimeUnit.MILLISECONDS.toMinutes(realMins);
            ans = String.valueOf(timeToHour) + "h " + String.valueOf(timeToMins) + "m ";
        }
        else {
            if(timeToMins > 0) {
                long realSecs = timeUsageInMilliSec - (timeToMins*1000*60 + timeToHour*1000*60*60);
                timeToSec = TimeUnit.MILLISECONDS.toSeconds(realSecs);
                ans = String.valueOf(timeToMins) + "m " + String.valueOf(timeToSec) + "s ";
            }
            else {
                ans = String.valueOf(timeToSec) + "s ";
            }
        }

        return ans;
    }

    private Drawable getAppIcon(String packageName){
        Drawable icon;
        try {
            icon = getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, String.format("App Icon is not found for %s",packageName));
            icon = getDrawable(R.drawable.ic_launcher_foreground);
        }
        return icon;
    }
}