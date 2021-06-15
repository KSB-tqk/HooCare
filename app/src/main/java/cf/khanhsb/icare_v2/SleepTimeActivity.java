package cf.khanhsb.icare_v2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import cf.khanhsb.icare_v2.Model.AlertReceiver;
import cf.khanhsb.icare_v2.Model.ProgressBarAnimation;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

public class SleepTimeActivity extends AppCompatActivity {

    private ImageView backButton,moreButton,plusButton,minusButton;
    private ConstraintLayout bottomSheetContainer;
    private ProgressBar progressBar;
    private int numOfHours = 0;
    private FirebaseFirestore firestore;
    private static final String tempEmail = "tempEmail";
    private TextView numOfHoursTextView, doneButton,setUpGoal;
    private DocumentReference docRef;
    private Button setupButton;
    private ConstraintLayout sleepTimeConstaint;
    private TimePicker timePicker,wakeTimePicker;
    private String mTime,wakeTime;

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
        sleepTimeConstaint = findViewById(R.id.sleep_time_constaint);

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
                        assert temp != null;
                        if (temp.equals("empty")) {
                            sleepTimeConstaint.setVisibility(View.INVISIBLE);
                        } else {
                            setupButton.setVisibility(View.GONE);
                            setUpGoal.setVisibility(View.GONE);
                            sleepTimeConstaint.setVisibility(View.VISIBLE);
                            numOfHours = Integer.parseInt(temp);
                        }

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
                                        String sleepTimeDatabase = document.getString("sleep_time");
                                        if (sleepTimeDatabase.equals("empty")) {

                                        } else {

                                        }
                                    } else {
                                        Log.d("LOGGER", "No such document");
                                    }
                                } else {
                                    Log.d("LOGGER", "get failed with ", task.getException());
                                }
                            }
                        });
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
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                timePicker = (TimePicker) bottomSheetView.findViewById(R.id.time_picker);
                wakeTimePicker = (TimePicker) bottomSheetView.findViewById(R.id.wake_time_picker);

                final Date[] dates = new Date[1];

                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        mTime = String.valueOf(hourOfDay) + ":" +String.valueOf(minute);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
                        try {
                            dates[0] = simpleDateFormat.parse("10/10/2013 11:30:10");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });

                wakeTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        wakeTime = String.valueOf(hourOfDay) + ":" +String.valueOf(minute);
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);
                        calendar.set(Calendar.SECOND,0);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
                        try {
                            dates[1] = simpleDateFormat.parse("10/10/2013 11:30:10"); //end time
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        long different = dates[0].getTime() - dates[1].getTime();

                        long secondsInMilli = 1000;
                        long minutesInMilli = secondsInMilli * 60;
                        long hoursInMilli = minutesInMilli * 60;
                        long daysInMilli = hoursInMilli * 24;

                        long elapsedDays = different / daysInMilli;
                        different = different % daysInMilli;

                        long elapsedHours = different / hoursInMilli;
                        different = different % hoursInMilli;

                        long elapsedMinutes = different / minutesInMilli;
                        different = different % minutesInMilli;

                        long elapsedSeconds = different / secondsInMilli;


                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(SleepTimeActivity.this, AlertReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(SleepTimeActivity.this,1,intent,0);

                        alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);

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

                doneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        firestore.collection("daily").
                                document("week-of-" + monday.toString()).
                                collection(today.toString()).
                                document(theTempEmail).update("sleep", "0");
                        bottomSheetDialog.dismiss();

                    }
                });

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();

            }
        });
    }
}