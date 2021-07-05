package cf.khanhsb.icare_v2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cf.khanhsb.icare_v2.Adapter.BarChartAdapter;
import cf.khanhsb.icare_v2.Adapter.StepCountViewPagerAdapter;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;


public class StepCountActivity extends AppCompatActivity {

    //initialize variable
    private ArrayList<BarEntry> dataValue = new ArrayList<BarEntry>();
    private ProgressBar progressBar;
    private ImageView backtohomefrag_button, more_menu_button;
    private TextView backToPreviusWeek, nextToFollowingWeek,
            date_time_text, doneButtonSetGoal, recommendTitle, customTitle, weekLabel, noData;
    private ColorStateList def_color;
    private ConstraintLayout bottomSheetContainer, selectedBackground;
    private ViewPager2 verticalViewPager2, setGoalViewPager2;
    private BarChartAdapter adapter;
    private StepCountViewPagerAdapter stepCountViewPagerAdapter;
    private static final String tempEmail = "tempEmail";
    /////////////////InitialalizeStepSensor///
    private TextView step_count_text;

    private FirebaseFirestore firestore;
    private DocumentReference docRef;

    private TextView km_step_count;
    private TextView kcal_step_count_text;

    private ImageView right_arrow_datetime;
    private ImageView left_arrow_datetime;

    int i = 1;

    private LocalDate tempDate;
    private ArrayList<String> daylist;
    private String joinDate, beginDay, endDay;
    private String[] beginDaySplit, endDaySplit;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);

        SharedPreferences sharedPreferences = this.
                getSharedPreferences(tempEmail, MODE_PRIVATE);
        String theTempEmail = sharedPreferences.getString("Email", "");

        /*assign variable*/

        backToPreviusWeek = (TextView) findViewById(R.id.back_to_last_week_button_step);
        nextToFollowingWeek = (TextView) findViewById(R.id.next_to_next_week_button_step);

        backtohomefrag_button = (ImageView) findViewById(R.id.button_backtohomefrag);
        verticalViewPager2 = (ViewPager2) findViewById(R.id.step_count_barchart_viewPager2);
        more_menu_button = (ImageView) findViewById(R.id.more_menu_stepcount);
        date_time_text = (TextView) findViewById(R.id.date_time_step);
        bottomSheetContainer = (ConstraintLayout) findViewById(R.id.bottom_sheet_container_step_count);
        step_count_text = (TextView) findViewById(R.id.step_count_text);
        km_step_count = (TextView) findViewById(R.id.km_step_count_text);
        kcal_step_count_text = (TextView) findViewById(R.id.kcal_step_count_text);
        weekLabel = findViewById(R.id.week_label_step);
        noData = findViewById(R.id.no_data_text_label_step);

        //set up date
        Date calendar = Calendar.getInstance().getTime();
        String day = (String) DateFormat.format("dd", calendar); // 20
        String monthString = (String) DateFormat.format("MMM", calendar); // Jun
        String realDate = day + " " + monthString;

        date_time_text.setText("Today " + realDate);

        Intent infoIntent = getIntent();
        String userEmail = infoIntent.getStringExtra("userEmail");
        String step_goal = infoIntent.getStringExtra("step_goal");

        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(previousOrSame(MONDAY));

        firestore = FirebaseFirestore.getInstance();

        docRef = firestore.collection("users").document(theTempEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        String temp = document.getString("step_goal");
                        assert temp != null;
                        if (temp.equals("empty")) {
                            noData.setVisibility(View.VISIBLE);
                            verticalViewPager2.setVisibility(View.GONE);
                        } else {
                            setUpWeekData(monday, today);
                            noData.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });

        /*back button on the toolbar click event*/
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
                selectedBackground = bottomSheetView.findViewById(R.id.tab_animation_view_step_count);

                String theTempEmail = sharedPreferences.getString("Email", "");

                stepCountViewPagerAdapter = new StepCountViewPagerAdapter(step_goal, theTempEmail);
                setGoalViewPager2.setAdapter(stepCountViewPagerAdapter);

                setGoalViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        if (position == 1) {
                            int size = customTitle.getWidth();
                            customTitle.setTextColor(getResources().getColor(R.color.black));
                            recommendTitle.setTextColor(Color.parseColor("#7E7E7E"));
                            selectedBackground.animate().x(size).setDuration(200);
                        } else {
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

        Runnable stepCountRunnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {

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
                                    double to_km = Double.parseDouble(temp);
                                    double to_cal = Double.parseDouble(temp);

                                    step_count_text.setText(temp);
                                    to_km = to_km * 0.000762;
                                    to_cal = to_cal * 0.0447;

                                    float f = (float) to_km;
                                    String s = String.format("%.3f", f);

                                    km_step_count.setText(s);
                                    kcal_step_count_text.setText(String.valueOf(to_cal));

                                    docRef = firestore.collection("daily").

                                            document("week-of-" + monday.toString()).
                                            collection(today.toString()).
                                            document(theTempEmail);


                                    docRef.update("cal_step", kcal_step_count_text.getText());
                                    docRef.update("km_step", km_step_count.getText());
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

        backToPreviusWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable backDataOfWeekRunnable = new Runnable() {
                    @Override
                    public void run() {
                        docRef = firestore.collection("users").document(theTempEmail);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null) {
                                        joinDate = document.getString("join_date");
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                        formatter = formatter.withLocale(Locale.ENGLISH);  // Locale specifies human language for translating, and cultural norms for lowercase/uppercase and abbreviations and such. Example: Locale.US or Locale.CANADA_FRENCH
                                        LocalDate theJoinDate = LocalDate.parse(joinDate, formatter);


                                        if (weekLabel.getText().toString().equals("This Week")) {
                                            LocalDate beginOfWeek = monday.minusDays(7);
                                            LocalDate endOfWeek = monday.minusDays(1);

                                            if (theJoinDate.isBefore(beginOfWeek) && theJoinDate.isBefore((endOfWeek))) {
                                                setUpWeekData(beginOfWeek, endOfWeek);

                                                beginDaySplit = monday.minusDays(7).toString().split("-");
                                                beginDay = beginDaySplit[2] + "/" + beginDaySplit[1];
                                                endDaySplit = monday.minusDays(1).toString().split("-");
                                                endDay = endDaySplit[2] + "/" + endDaySplit[1];

                                                weekLabel.setText(beginDay + " - " + endDay);
                                                nextToFollowingWeek.setVisibility(View.VISIBLE);
                                            } else {
                                                Toast.makeText(StepCountActivity.this, "No Data", Toast.LENGTH_SHORT).show();
                                            }

                                        } else {
                                            LocalDate beginDayOfData = LocalDate.parse(
                                                    beginDaySplit[0] + "-" + beginDaySplit[1] + "-" + beginDaySplit[2], formatter);
                                            LocalDate endDayOfData = LocalDate.parse(
                                                    endDaySplit[0] + "-" + endDaySplit[1] + "-" + endDaySplit[2], formatter);
                                            if (theJoinDate.isBefore(beginDayOfData)) {
                                                setUpWeekData(beginDayOfData.minusDays(7), endDayOfData.minusDays(7));

                                                beginDaySplit = beginDayOfData.minusDays(7).toString().split("-");
                                                beginDay = beginDaySplit[2] + "/" + beginDaySplit[1];
                                                endDaySplit = endDayOfData.minusDays(7).toString().split("-");
                                                endDay = endDaySplit[2] + "/" + endDaySplit[1];

                                                weekLabel.setText(beginDay + " - " + endDay);
                                            } else {
                                                Toast.makeText(StepCountActivity.this, "No Data", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }
                };
                Thread backDataOfWeekThread = new Thread(backDataOfWeekRunnable);
                backDataOfWeekThread.start();
            }
        });

        nextToFollowingWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!weekLabel.getText().toString().equals("This Week")) {
                    Runnable nextWeekDataRunnable = new Runnable() {
                        @Override
                        public void run() {
                            String[] splitBeginAndEnd = weekLabel.getText().toString().split("-");
                            String[] beginDaySplit = splitBeginAndEnd[0].trim().split("/");
                            String[] endDaySplit = splitBeginAndEnd[1].trim().split("/");

                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            formatter = formatter.withLocale(Locale.ENGLISH);

                            LocalDate beginDay = LocalDate.parse("2021-" + beginDaySplit[1] + "-" + beginDaySplit[0], formatter);
                            LocalDate endDay = LocalDate.parse("2021-" + endDaySplit[1] + "-" + endDaySplit[0], formatter);

                            if (beginDay.plusDays(7).isEqual(monday)) {
                                setUpWeekData(monday, today);
                                weekLabel.setText("This Week");
                                nextToFollowingWeek.setVisibility(View.INVISIBLE);
                            } else {
                                setUpWeekData(beginDay.plusDays(7), endDay.plusDays(7));

                                String[] plusBeginDaySplit = beginDay.plusDays(7).toString().split("-");
                                String plusBeginDay = plusBeginDaySplit[2] + "/" + plusBeginDaySplit[1];
                                String[] plusEndDaySplit = endDay.plusDays(7).toString().split("-");
                                String plusEndDay = plusEndDaySplit[2] + "/" + plusEndDaySplit[1];

                                weekLabel.setText(plusBeginDay + " - " + plusEndDay);
                            }
                        }
                    };
                    Thread nextWeekThread = new Thread(nextWeekDataRunnable);
                    nextWeekThread.start();
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUpWeekData(LocalDate thisWeekMonday, LocalDate thisWeekToday) {
        daylist = new ArrayList<String>();
        dataValue = new ArrayList<BarEntry>();
        daylist.add("Mon");
        daylist.add("Tue");
        daylist.add("Wed");
        daylist.add("Thur");
        daylist.add("Fri");
        daylist.add("Sat");
        daylist.add("Sun");

        SharedPreferences sharedPreferences = getSharedPreferences(tempEmail, MODE_PRIVATE);
        String theTempEmail = sharedPreferences.getString("Email", "");

        firestore = FirebaseFirestore.getInstance();

        ArrayList<String> tempDrink = new ArrayList<>();
        ArrayList<BarDataSet> chartBarDataSetList = new ArrayList<>();
        ArrayList<String> dayInWeek = new ArrayList<String>();
        ArrayList<String> dateInWeek = new ArrayList<String>();
        ArrayList<String> dayThatHasValue = new ArrayList<String>();
        ArrayList<String> realDrinkValue = new ArrayList<String>();

        Runnable getWaterListPerDay = new Runnable() {
            @Override
            public void run() {
                long diffInDays = ChronoUnit.DAYS.between(thisWeekMonday, thisWeekToday);

                tempDate = thisWeekMonday;
                for (i = 0; i <= diffInDays; i++) {
                    dateInWeek.add(tempDate.toString());
                    realDrinkValue.add("0");

                    firestore.collection("daily").
                            document("week-of-" + thisWeekMonday.toString()).
                            collection(tempDate.toString())
                            .whereEqualTo("userEmail", theTempEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String step = document.getString("steps");
                                    String dayOnFirebase = document.getString("datetime");
                                    dayThatHasValue.add(dayOnFirebase);
                                    if (step.equals("empty")) {
                                        tempDrink.add("0");
                                    } else {
                                        tempDrink.add(step);
                                    }

                                    assert dayOnFirebase != null;
                                    if (dayOnFirebase.equals(thisWeekToday.toString())) {
                                        final Handler handler = new Handler(Looper.getMainLooper());
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                for (int j = 0; j < dateInWeek.size(); j++) {
                                                    for (int z = 0; z < dayThatHasValue.size(); z++) {
                                                        if (dateInWeek.get(j).equals(dayThatHasValue.get(z))) {
                                                            realDrinkValue.set(j, tempDrink.get(z));
                                                        }
                                                    }
                                                }

                                                for (int k = 0; k < realDrinkValue.size(); k++) {
                                                    dataValue.add(new BarEntry(k, Float.parseFloat(realDrinkValue.get(k))));
                                                    dayInWeek.add(daylist.get(k));
                                                }

                                                int allDayContain = dayInWeek.size();

                                                if (allDayContain < 7) {
                                                    while (allDayContain < 7) {
                                                        dataValue.add(new BarEntry(allDayContain, 0));
                                                        dayInWeek.add(daylist.get(allDayContain));
                                                        allDayContain++;
                                                    }
                                                }

                                                //initialize testadapter
                                                adapter = new BarChartAdapter(dataValue, dayInWeek);
                                                //setting adapter on to the viewpager2
                                                verticalViewPager2.setUserInputEnabled(false);
                                                verticalViewPager2.setAdapter(adapter);
                                            }
                                        }, 100);
                                    }
                                }
                            } else {

                            }
                        }
                    });
                    tempDate = tempDate.plusDays(1);
                }

            }
        };

        Thread getDailyDrinkThread = new Thread(getWaterListPerDay);
        getDailyDrinkThread.start();
    }
}