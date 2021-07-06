package cf.khanhsb.icare_v2;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import cf.khanhsb.icare_v2.Adapter.BarChartAdapter;
import cf.khanhsb.icare_v2.Model.AlertReceiver;
import cf.khanhsb.icare_v2.Model.ProgressBarAnimation;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

public class SleepTimeActivity extends AppCompatActivity {

    private ImageView backButton, moreButton, plusButton, minusButton;
    private ConstraintLayout bottomSheetContainer, setUpSleepGoalConstraint;
    private ProgressBar progressBar;
    private int sleepGoal = 8, sleepHour = 10, sleepMin = 30;
    private FirebaseFirestore firestore;
    private static final String tempEmail = "tempEmail";
    private TextView numOfHoursTextView, doneButton, setUpGoal
            , dateTextView, recommendLabel, loadingLabel, timeToSleep, timeToWake
            ,noData,backToPreviusWeek,nextToFollowingWeek,weekLabel;
    private DocumentReference docRef;
    private Button setupButton, chooseTimeToSleepButton;
    private LinearLayout sleepTimeLinear, wakeTimeLinear;
    private String theTempEmail;

    private LocalDate tempDate;
    private ArrayList<String> daylist;
    private String joinDate, beginDay, endDay;
    private String[] beginDaySplit, endDaySplit;
    private ArrayList<BarEntry> dataValue = new ArrayList<BarEntry>();
    private ViewPager2 verticalViewPager2;
    private BarChartAdapter adapter;
    private int i = 1;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_time);

        backButton = findViewById(R.id.button_back_sleep_time);
        moreButton = findViewById(R.id.more_menu_sleep_time);
        bottomSheetContainer = findViewById(R.id.bottom_sheet_container_sleep_time);
        setupButton = findViewById(R.id.open_button_sleep_time);
        setUpGoal = findViewById(R.id.setup_sleep_goal);
        setUpSleepGoalConstraint = findViewById(R.id.setup_sleep_goal_constaint);
        dateTextView = findViewById(R.id.date_time_statistic);
        chooseTimeToSleepButton = findViewById(R.id.choose_button_sleep_time);
        recommendLabel = findViewById(R.id.recommend_label_sleep_time);
        sleepTimeLinear = findViewById(R.id.sleep_time_linear);
        wakeTimeLinear = findViewById(R.id.wake_time_linear);
        loadingLabel = findViewById(R.id.loading_title_sleep_time);
        timeToSleep = findViewById(R.id.time_to_sleep);
        timeToWake = findViewById(R.id.time_to_wake);
        noData = findViewById(R.id.no_data_text_label_step);
        backToPreviusWeek = findViewById(R.id.back_to_last_week_button_sleep);
        nextToFollowingWeek = findViewById(R.id.next_to_next_week_button_sleep);
        weekLabel = findViewById(R.id.week_label_sleep);
        verticalViewPager2 = findViewById(R.id.sleep_barchart_viewPager2);


        Intent intent = getIntent();
        String tempSleepTime = intent.getStringExtra("sleepTime");

        //set up date
        Date calendar = Calendar.getInstance().getTime();
        System.out.println("Current time => " + calendar);
        String day = (String) DateFormat.format("dd", calendar); // 20
        String monthString = (String) DateFormat.format("MMM", calendar); // Jun
        String realDate = day + " " + monthString;

        dateTextView.setText("Today, " + realDate);

        SharedPreferences sharedPreferences = getSharedPreferences(tempEmail, MODE_PRIVATE);
        theTempEmail = sharedPreferences.getString("Email", "");

        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(previousOrSame(MONDAY));

        firestore = FirebaseFirestore.getInstance();

        Runnable setUpSleepTimeRunnable = new Runnable() {
            @Override
            public void run() {
                docRef = firestore.collection("users").document(theTempEmail);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                String tempSleepGoal = document.getString("sleep_goal");
                                String timeToSleep = document.getString("time_to_sleep");
                                assert tempSleepGoal != null;
                                if (tempSleepGoal.equals("empty")) {
                                    noData.setVisibility(View.VISIBLE);
                                    verticalViewPager2.setVisibility(View.GONE);

                                    setUpSleepGoalConstraint.setVisibility(View.VISIBLE);
                                    setUpGoal.setVisibility(View.VISIBLE);
                                    setupButton.setVisibility(View.VISIBLE);
                                    loadingLabel.setVisibility(View.GONE);
                                    setUpGoal.bringToFront();
                                    setupButton.bringToFront();
                                } else {
                                    setUpWeekData(monday, today);
                                    noData.setVisibility(View.GONE);

                                    setUpSleepGoalConstraint.setVisibility(View.VISIBLE);
                                    setUpGoal.setVisibility(View.GONE);
                                    setupButton.setVisibility(View.GONE);
                                    sleepGoal = Integer.parseInt(tempSleepGoal);

                                    if (timeToSleep.equals("empty")) {
                                        chooseTimeToSleepButton.setVisibility(View.VISIBLE);
                                    } else {
                                        String[] splitString = timeToSleep.split(":");
                                        sleepHour = Integer.parseInt(splitString[0]);
                                        sleepMin = Integer.parseInt(splitString[1]);
                                        SetUpSleepTime();

                                        sleepTimeLinear.setVisibility(View.VISIBLE);
                                        wakeTimeLinear.setVisibility(View.VISIBLE);
                                        recommendLabel.setVisibility(View.VISIBLE);
                                    }
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

        Thread setUpSleepTimeThread = new Thread(setUpSleepTimeRunnable);
        setUpSleepTimeThread.start();

        Runnable setUpSleepNotificationRunnable = new Runnable() {
            @Override
            public void run() {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, sleepHour);
                c.set(Calendar.MINUTE, sleepMin);
                c.set(Calendar.SECOND,0);

                SetUpSleepNotification(c);
            }
        };
        Thread setUpSleepNotiThread = new Thread(setUpSleepNotificationRunnable);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMain = new Intent(SleepTimeActivity.this, MainActivity.class);
                startActivity(toMain);
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetUpDialog(theTempEmail, false);
            }
        });

        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetUpDialog(theTempEmail, true);
            }
        });

        chooseTimeToSleepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        SleepTimeActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                sleepHour = hourOfDay;
                                sleepMin = minute;
                                SetUpSleepTime();
                                setUpSleepNotiThread.start();
                            }
                        }, 12, 0, true
                );
                timePickerDialog.updateTime(sleepHour, sleepMin);
                timePickerDialog.show();
            }
        });

        timeToSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String splitString[] = timeToSleep.getText().toString().split(":");
                String oldHour = splitString[0];
                String oldMin = splitString[1];
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        SleepTimeActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                sleepHour = hourOfDay;
                                sleepMin = minute;
                                SetUpSleepTime();
                                if (setUpSleepNotiThread.getState() == Thread.State.NEW)
                                {
                                    setUpSleepNotiThread.start();
                                }
                                else {
                                    Toast.makeText(SleepTimeActivity.this, "Please wait a min and try again", Toast.LENGTH_SHORT).show();
                                    timeToSleep.setText(oldHour+":"+oldMin);
                                }
                            }
                        }, 12, 0, true
                );
                timePickerDialog.updateTime(sleepHour, sleepMin);
                timePickerDialog.show();
            }
        });

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
                                                Toast.makeText(SleepTimeActivity.this, "No Data", Toast.LENGTH_SHORT).show();
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
                                                Toast.makeText(SleepTimeActivity.this, "No Data", Toast.LENGTH_SHORT).show();
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
            }
        });
    }

    private void SetUpSleepNotification(Calendar calendar) {
        //sleep Alert
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(SleepTimeActivity.this, AlertReceiver.class);
        intent.putExtra("time", String.valueOf(sleepGoal));
        intent.putExtra("userEmail", theTempEmail);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SleepTimeActivity.this, 1, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void SetUpSleepTime() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(0, 0, 0, sleepHour, sleepMin);
        chooseTimeToSleepButton.setVisibility(View.GONE);
        sleepTimeLinear.setVisibility(View.VISIBLE);
        wakeTimeLinear.setVisibility(View.VISIBLE);
        recommendLabel.setVisibility(View.VISIBLE);
        loadingLabel.setVisibility(View.GONE);

        String tempSleepMin = "";
        if(sleepMin == 0) {
            tempSleepMin = "00";
        } else if(sleepMin < 10 && sleepMin> 0){
            tempSleepMin = "0" + String.valueOf(sleepMin);
        } else {
            tempSleepMin = String.valueOf(sleepMin);
        }

        String tempSleepTime = String.valueOf(sleepHour) + ":" + tempSleepMin;
        timeToSleep.setText(tempSleepTime);

        long timeInSecs = calendar.getTimeInMillis();
        Date wakeUpTime = new Date(timeInSecs + ((sleepGoal * 60 + 14) * 60 * 1000));
        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        df.setTimeZone(TimeZone.getDefault());
        String formattedDate = df.format(wakeUpTime);

        timeToWake.setText(formattedDate);

        Runnable saveSleepTimeRunnable = new Runnable() {
            @Override
            public void run() {
                firestore = FirebaseFirestore.getInstance();
                firestore.collection("users")
                        .document(theTempEmail)
                        .update("time_to_sleep", tempSleepTime);
            }
        };

        Thread backgroundThread = new Thread(saveSleepTimeRunnable);
        backgroundThread.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void SetUpDialog(String theTempEmail, boolean isSetUp) {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(previousOrSame(MONDAY));

        firestore = FirebaseFirestore.getInstance();

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                SleepTimeActivity.this,
                R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(
                        R.layout.bottom_sheet_dialog_sleep_time,
                        bottomSheetContainer,
                        false
                );

        plusButton = (ImageView) bottomSheetView.findViewById(R.id.plus_button_sleep_menu);
        minusButton = (ImageView) bottomSheetView.findViewById(R.id.minus_button_sleep_menu);
        progressBar = (ProgressBar) bottomSheetView.findViewById(R.id.progressbar_sleep_time);
        numOfHoursTextView = (TextView) bottomSheetView.findViewById(R.id.num_of_hours_progress);
        doneButton = (TextView) bottomSheetView.findViewById(R.id.close_button_sleep_time);

        final Date[] dates = new Date[2];
        String[] splitDate = today.toString().split("-");
        String tempDate = splitDate[2] + "/" + splitDate[1] + "/" + splitDate[0];
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");

        progressBar.setMax(12000);
        progressBar.setProgress(sleepGoal * 1000);
        numOfHoursTextView.setText(String.valueOf(sleepGoal));

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sleepGoal == 11) {
                    plusButton.setVisibility(View.GONE);
                }
                if (sleepGoal < 12) {
                    sleepGoal += 1;
                    if (sleepGoal == 5) {
                        minusButton.setVisibility(View.VISIBLE);
                    }
                    numOfHoursTextView.setText(String.valueOf(sleepGoal));

                    //progress bar animation
                    float progress = Float.parseFloat(String.valueOf(sleepGoal)) * 1000;
                    ProgressBarAnimation anim = new ProgressBarAnimation(progressBar,
                            progress - 1000,
                            progress);
                    anim.setDuration(1000);
                    progressBar.startAnimation(anim);

                    //update value
                    firestore.collection("users").document(theTempEmail).
                            update("sleep_goal", String.valueOf(sleepGoal));

                }
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sleepGoal = Integer.parseInt(numOfHoursTextView.getText().toString());
                if (sleepGoal == 5) {
                    minusButton.setVisibility(View.GONE);
                }
                if (sleepGoal > 4) {
                    sleepGoal -= 1;
                    if (sleepGoal == 11) {
                        plusButton.setVisibility(View.VISIBLE);
                    }
                    numOfHoursTextView.setText(String.valueOf(sleepGoal));

                    //progress bar animation
                    float progress = Float.parseFloat(String.valueOf(sleepGoal)) * 1000;
                    ProgressBarAnimation anim = new ProgressBarAnimation(progressBar,
                            progress + 1000,
                            progress);
                    anim.setDuration(1000);
                    progressBar.startAnimation(anim);

                    //update value
                    firestore.collection("users").document(theTempEmail).
                            update("sleep_goal", String.valueOf(sleepGoal));
                }
            }
        });

        boolean finalIsSetUp = isSetUp;
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update value
                firestore.collection("users").document(theTempEmail).
                        update("sleep_goal", String.valueOf(sleepGoal));

                docRef = firestore.collection("users").document(theTempEmail);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                String temp = document.getString("sleep_goal");
                                String timeToSleep = document.getString("time_to_sleep");
                                if (temp.equals("empty")) {
                                    setUpSleepGoalConstraint.setVisibility(View.VISIBLE);
                                    setUpGoal.setVisibility(View.VISIBLE);
                                    setupButton.setVisibility(View.VISIBLE);
                                    loadingLabel.setVisibility(View.GONE);
                                    setUpGoal.bringToFront();
                                    setupButton.bringToFront();
                                } else {
                                    setUpSleepGoalConstraint.setVisibility(View.VISIBLE);
                                    setUpGoal.setVisibility(View.GONE);
                                    setupButton.setVisibility(View.GONE);
                                    sleepGoal = Integer.parseInt(temp);

                                    if (timeToSleep.equals("empty")) {
                                        chooseTimeToSleepButton.setVisibility(View.VISIBLE);
                                    } else {
                                        String[] splitString = timeToSleep.split(":");
                                        sleepHour = Integer.parseInt(splitString[0]);
                                        sleepMin = Integer.parseInt(splitString[1]);
                                        SetUpSleepTime();

                                        sleepTimeLinear.setVisibility(View.VISIBLE);
                                        wakeTimeLinear.setVisibility(View.VISIBLE);
                                        recommendLabel.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                    }
                });
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
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
                                    String sleepTime = document.getString("sleep_time");
                                    String dayOnFirebase = document.getString("datetime");
                                    dayThatHasValue.add(dayOnFirebase);
                                    if (sleepTime.equals("empty")) {
                                        tempDrink.add("0");
                                    } else {
                                        tempDrink.add(sleepTime);
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