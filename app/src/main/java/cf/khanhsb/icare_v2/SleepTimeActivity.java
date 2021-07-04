package cf.khanhsb.icare_v2;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

import cf.khanhsb.icare_v2.Model.AlertReceiver;
import cf.khanhsb.icare_v2.Model.ProgressBarAnimation;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

public class SleepTimeActivity extends AppCompatActivity {

    private ImageView backButton, moreButton, plusButton, minusButton;
    private ConstraintLayout bottomSheetContainer, setUpSleepGoalConstraint;
    private ProgressBar progressBar;
    private int numOfHours = 0, setTimeHour, setTimeMin, wakeTimeHour, wakeTimeMin;
    private FirebaseFirestore firestore;
    private static final String tempEmail = "tempEmail";
    private TextView numOfHoursTextView, doneButton, setUpGoal, dateTextView, totalSleepTime;
    private DocumentReference docRef;
    private Button setupButton;
    private ConstraintLayout sleepTimeConstaint;
    private TimePicker timePicker, wakeTimePicker;
    private String mTime, wakeTime;

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

        Intent intent = getIntent();
        String tempSleepTime = intent.getStringExtra("sleepTime");

        if (tempSleepTime != null) {
            if(tempSleepTime.contains(":")) {
                String[] splitString = tempSleepTime.split(":");
                if (!splitString[1].equals("0")) {
                    totalSleepTime.setText(splitString[0] + "h " + splitString[1] + "m");
                } else {
                    totalSleepTime.setText(splitString[0] + "h");
                }
            }
        } else {
            totalSleepTime.setText("0");
        }

        //set up date
        Date calendar = Calendar.getInstance().getTime();
        System.out.println("Current time => " + calendar);
        String day = (String) DateFormat.format("dd", calendar); // 20
        String monthString = (String) DateFormat.format("MMM", calendar); // Jun
        String realDate = day + " " + monthString;

        dateTextView.setText(realDate);

        SharedPreferences sharedPreferences = getSharedPreferences(tempEmail, MODE_PRIVATE);
        String theTempEmail = sharedPreferences.getString("Email", "");

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
                        String temp = document.getString("sleep_goal");
                        String timeToSleep = document.getString("time_to_sleep");
                        String timeToWake = document.getString("time_to_wake");
                        assert temp != null;
                        if (temp.equals("empty")) {
                            sleepTimeConstaint.setVisibility(View.INVISIBLE);
                            setUpSleepGoalConstraint.setVisibility(View.VISIBLE);
                            setUpGoal.bringToFront();
                            setupButton.bringToFront();
                        } else {
                            setUpSleepGoalConstraint.setVisibility(View.GONE);
                            sleepTimeConstaint.setVisibility(View.VISIBLE);
                            numOfHours = Integer.parseInt(temp);
                        }


                    } else {
                        Log.d("LOGGER", "No such document");
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }
        });

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

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                setTimeHour = hourOfDay;
                setTimeMin = minute;
            }
        });
        Calendar calendar = Calendar.getInstance();
        wakeTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                wakeTimeHour = hourOfDay;
                wakeTimeMin = minute;
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);

                if (calendar.before(Calendar.getInstance())) {
                    calendar.add(Calendar.DATE, 1);
                }
            }
        });

        progressBar.setMax(12000);
        progressBar.setProgress(numOfHours * 1000);

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numOfHours = Integer.parseInt(numOfHoursTextView.getText().toString());
                if (numOfHours == 11) {
                    plusButton.setVisibility(View.GONE);
                }
                if (numOfHours < 12) {
                    numOfHours += 1;
                    if (numOfHours == 5) {
                        minusButton.setVisibility(View.VISIBLE);
                    }
                    numOfHoursTextView.setText(String.valueOf(numOfHours));

                    //progress bar animation
                    float progress = Float.parseFloat(String.valueOf(numOfHours)) * 1000;
                    ProgressBarAnimation anim = new ProgressBarAnimation(progressBar,
                            progress - 1000,
                            progress);
                    anim.setDuration(1000);
                    progressBar.startAnimation(anim);

                    //update value
                    firestore.collection("users").document(theTempEmail).
                            update("sleep_goal", String.valueOf(numOfHours));

                }
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numOfHours = Integer.parseInt(numOfHoursTextView.getText().toString());
                if (numOfHours == 5) {
                    minusButton.setVisibility(View.GONE);
                }
                if (numOfHours > 4) {
                    numOfHours -= 1;
                    if (numOfHours == 11) {
                        plusButton.setVisibility(View.VISIBLE);
                    }
                    numOfHoursTextView.setText(String.valueOf(numOfHours));

                    //progress bar animation
                    float progress = Float.parseFloat(String.valueOf(numOfHours)) * 1000;
                    ProgressBarAnimation anim = new ProgressBarAnimation(progressBar,
                            progress + 1000,
                            progress);
                    anim.setDuration(1000);
                    progressBar.startAnimation(anim);

                    //update value
                    firestore.collection("users").document(theTempEmail).
                            update("sleep_goal", String.valueOf(numOfHours));
                }
            }
        });

        boolean finalIsSetUp = isSetUp;
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long tempHour = 0L, tempMin = 0L;
                if (setTimeHour > wakeTimeHour) {
                    if (setTimeMin > wakeTimeMin) {
                        tempHour = (24 - setTimeHour + wakeTimeHour - 1);
                        tempMin = (setTimeMin + wakeTimeMin);
                    } else if (setTimeMin == wakeTimeMin) {
                        tempHour = (24 - setTimeHour + wakeTimeHour);
                        tempMin = 0;
                    } else {
                        tempHour = (24 - setTimeHour + wakeTimeHour);
                        tempMin = (wakeTimeMin - setTimeMin);
                    }
                } else if (setTimeHour < wakeTimeHour) {
                    if (setTimeMin > wakeTimeMin) {
                        tempHour = (wakeTimeHour - setTimeHour - 1);
                        tempMin = (60 - wakeTimeMin + setTimeMin);
                    } else if (setTimeMin == wakeTimeMin) {
                        tempHour = (wakeTimeHour - setTimeHour);
                        tempMin = 0;
                    } else {
                        tempHour = (wakeTimeHour - setTimeHour);
                        tempMin = (wakeTimeMin - setTimeMin);
                    }
                } else {
                    Toast.makeText(SleepTimeActivity.this, "You should not sleep too much", Toast.LENGTH_SHORT).show();
                }

                //sleep Alert
//                if (setTimeHour != wakeTimeHour) {
//                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                    Intent intent = new Intent(SleepTimeActivity.this, AlertReceiver.class);
//                    intent.putExtra("time", String.valueOf(tempHour) + ":" + String.valueOf(tempMin));
//                    intent.putExtra("userEmail", theTempEmail);
//                    PendingIntent pendingIntent = PendingIntent.getBroadcast(SleepTimeActivity.this, 1, intent, 0);
//                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                    //update value
                    firestore.collection("users").document(theTempEmail).
                            update("time_to_sleep", String.valueOf(setTimeHour) + ":" + String.valueOf(setTimeMin));
                    firestore.collection("users").document(theTempEmail).
                            update("time_to_wake", String.valueOf(wakeTimeHour) + ":" + String.valueOf(wakeTimeMin));

                    docRef = firestore.collection("users").document(theTempEmail);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) {
                                    String temp = document.getString("sleep_goal");
                                    if (temp.equals("empty")) {
                                        sleepTimeConstaint.setVisibility(View.INVISIBLE);
                                        setUpSleepGoalConstraint.setVisibility(View.VISIBLE);
                                        setUpGoal.bringToFront();
                                        setupButton.bringToFront();
                                    } else {
                                        setUpSleepGoalConstraint.setVisibility(View.GONE);
                                        sleepTimeConstaint.setVisibility(View.VISIBLE);
                                        numOfHours = Integer.parseInt(temp);
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