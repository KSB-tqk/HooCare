package cf.khanhsb.icare_v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cf.khanhsb.icare_v2.Adapter.BarChartAdapter;
import cf.khanhsb.icare_v2.Adapter.StepCountViewPagerAdapter;
import cf.khanhsb.icare_v2.StepCounter.StepDetector;
import cf.khanhsb.icare_v2.StepListener;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

public class StepCountActivity extends AppCompatActivity implements View.OnClickListener {

    //initialize variable
    private ArrayList<BarEntry> dataValue = new ArrayList<BarEntry>();
    private ProgressBar progressBar;
    private ImageView backtohomefrag_button, more_menu_button;
    private TextView statusOfProgressBar, day_tab, week_tab, month_tab,
            select_background, date_time_text,doneButtonSetGoal,recommendTitle,customTitle;
    private ColorStateList def_color;
    private ConstraintLayout bottomSheetContainer,selectedBackground;
    private ViewPager2 verticalViewPager2, setGoalViewPager2;
    private BarChartAdapter adapter;
    private StepCountViewPagerAdapter stepCountViewPagerAdapter;
    private static final String tempEmail = "tempEmail";
    /////////////////InitialalizeStepSensor///
    private TextView step_count_text;

    private FirebaseFirestore firestore;
    private DocumentReference docRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);

        SharedPreferences sharedPreferences = this.
                getSharedPreferences(tempEmail, MODE_PRIVATE);
        String theTempEmail = sharedPreferences.getString("Email", "");

        /**assign variable*/
        statusOfProgressBar = (TextView) findViewById(R.id.progressbar_status);
        day_tab = (TextView) findViewById(R.id.text_item1);
        week_tab = (TextView) findViewById(R.id.text_item2);
        month_tab = (TextView) findViewById(R.id.text_item3);
        select_background = (TextView) findViewById(R.id.selected_background_tab);
        backtohomefrag_button = (ImageView) findViewById(R.id.button_backtohomefrag);
        verticalViewPager2 = (ViewPager2) findViewById(R.id.step_count_barchart_viewPager2);
        more_menu_button = (ImageView) findViewById(R.id.more_menu_stepcount);
        date_time_text = (TextView) findViewById(R.id.date_time_text);
        bottomSheetContainer = (ConstraintLayout) findViewById(R.id.bottom_sheet_container_step_count);
        step_count_text = (TextView) findViewById(R.id.step_count_text);

        //set up date
        Date calendar = Calendar.getInstance().getTime();
        System.out.println("Current time => " + calendar);
        String day = (String) DateFormat.format("dd", calendar); // 20
        String monthString = (String) DateFormat.format("MMM", calendar); // Jun
        String today = day + " " + monthString;

        date_time_text.setText(today);


        /**set tabview onclick listener*/
        day_tab.setOnClickListener(this);
        week_tab.setOnClickListener(this);
        month_tab.setOnClickListener(this);
        def_color = week_tab.getTextColors();

        Intent infoIntent = getIntent();
        String userEmail = infoIntent.getStringExtra("userEmail");
        String step_goal = infoIntent.getStringExtra("step_goal");



        /**back button on the toolbar click event*/
        backtohomefrag_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMain = new Intent(StepCountActivity.this, MainActivity.class);
                toMain.putExtra("userEmail", userEmail);
                startActivity(toMain);
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        more_menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        StepCountActivity.this,
                        R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.bottom_sheet_dialog_stepcount,
                                bottomSheetContainer,
                                false
                        );
                setGoalViewPager2 = (ViewPager2) bottomSheetView
                        .findViewById(R.id.step_count_set_goal_viewPager);
                doneButtonSetGoal = bottomSheetView.findViewById(R.id.close_button_step_goal_set_up);
                recommendTitle = bottomSheetView.findViewById(R.id.recommend_title);
                customTitle = bottomSheetView.findViewById(R.id.custom_title);
                selectedBackground =  bottomSheetView.findViewById(R.id.tab_animation_view_step_count);

                String theTempEmail = sharedPreferences.getString("Email","");

                stepCountViewPagerAdapter = new StepCountViewPagerAdapter(step_goal,theTempEmail);
                setGoalViewPager2.setAdapter(stepCountViewPagerAdapter);

                setGoalViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        if(position == 1){
                            int size = customTitle.getWidth();
                            customTitle.setTextColor(getResources().getColor(R.color.black));
                            recommendTitle.setTextColor(Color.parseColor("#7E7E7E"));
                            selectedBackground.animate().x(size).setDuration(200);
                        }
                        else {
                            customTitle.setTextColor(Color.parseColor("#7E7E7E"));
                            recommendTitle.setTextColor(getResources().getColor(R.color.black));
                            selectedBackground.animate().x(0).setDuration(200);
                        }
                    }
                });

                customTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int size = customTitle.getWidth();
                        customTitle.setTextColor(getResources().getColor(R.color.black));
                        recommendTitle.setTextColor(Color.parseColor("#7E7E7E"));
                        selectedBackground.animate().x(size).setDuration(200);
                        setGoalViewPager2.setCurrentItem(1);
                    }
                });

                recommendTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customTitle.setTextColor(Color.parseColor("#7E7E7E"));
                        recommendTitle.setTextColor(getResources().getColor(R.color.black));
                        selectedBackground.animate().x(0).setDuration(200);
                        setGoalViewPager2.setCurrentItem(0);
                    }
                });

                doneButtonSetGoal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });

        ArrayList<BarDataSet> chartBarDataSetList = new ArrayList<>();

        /**add data to barchart*/
        dataValue.add(new BarEntry(0, 0));
        dataValue.add(new BarEntry(1, 0));
        dataValue.add(new BarEntry(2, 0));
        dataValue.add(new BarEntry(3, 0));
        dataValue.add(new BarEntry(4, 0));
        dataValue.add(new BarEntry(5, 0));
        dataValue.add(new BarEntry(6, 0));

        /**create bardata and add bardataset to bardata*/
        ArrayList<String> daylist = new ArrayList<String>();
        daylist.add("Mon");
        daylist.add("Tue");
        daylist.add("Wed");
        daylist.add("Thur");
        daylist.add("Fri");
        daylist.add("Sat");
        daylist.add("Sun");

        //initialize testadapter
        adapter = new BarChartAdapter(dataValue, daylist);
        //setting adapter on to the viewpager2
        verticalViewPager2.setUserInputEnabled(false);
        verticalViewPager2.setAdapter(adapter);

        Runnable stepCountRunnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                LocalDate today = LocalDate.now();
                LocalDate monday = today.with(previousOrSame(MONDAY));
                firestore = FirebaseFirestore.getInstance();
                docRef = firestore.collection("daily").
                        document("week-of-" + monday.toString()).
                        collection(today.toString()).
                        document(theTempEmail);

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                String temp = document.getString("steps");
                                if (!"empty".equals(temp)) {
                                    step_count_text.setText(String.valueOf(temp));
                                }
                            } else {
                                Log.d("LOGGER", "No such document");
                            }
                        } else {
                            Log.d("LOGGER", "get failed with ", task.getException());
                        }

                    }
                });

            }

        };
        Thread backgroundThread = new Thread(stepCountRunnable);
        backgroundThread.start();



    }



    /**
     * sliding navigation bar (day/week/month)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_item1:
                verticalViewPager2.setCurrentItem(0);
                select_background.animate().x(0).setDuration(200);
                day_tab.setTextColor(Color.WHITE);
                week_tab.setTextColor(def_color);
                month_tab.setTextColor(def_color);
                break;
            case R.id.text_item2:
                verticalViewPager2.setCurrentItem(1);
                int size = week_tab.getWidth();
                select_background.animate().x(size).setDuration(200);
                week_tab.setTextColor(Color.WHITE);
                day_tab.setTextColor(def_color);
                month_tab.setTextColor(def_color);
                break;
            case R.id.text_item3:
                verticalViewPager2.setCurrentItem(2);
                int size0fMonthTab = month_tab.getWidth() * 2;
                select_background.animate().x(size0fMonthTab).setDuration(200);
                month_tab.setTextColor(Color.WHITE);
                day_tab.setTextColor(def_color);
                week_tab.setTextColor(def_color);
                break;

        }
    }



}