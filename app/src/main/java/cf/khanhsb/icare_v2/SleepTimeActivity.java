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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
    private TextView numOfHoursTextView, doneButton, setUpGoal, dateTextView, recommendLabel, loadingLabel, timeToSleep, timeToWake;
    private DocumentReference docRef;
    private Button setupButton, chooseTimeToSleepButton;
    private LinearLayout sleepTimeLinear, wakeTimeLinear;
    private String theTempEmail;

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
        String tempSleepTime = String.valueOf(sleepHour) + ":" + String.valueOf(sleepMin);
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
}