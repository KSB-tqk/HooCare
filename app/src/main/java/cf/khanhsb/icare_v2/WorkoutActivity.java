package cf.khanhsb.icare_v2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import cf.khanhsb.icare_v2.Model.ProgressBarAnimation;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

public class WorkoutActivity extends AppCompatActivity {

    private ImageView backButton, gifExerciseWorkout, loadingIcon;
    private TextView countDownTextView, exerciseCounter, exerciseTimeCounter, exerciseTitleWorkout,
            excerciseNextTitleWorkout, afterStartWorkoutTitle, afterStartDuration, readyTextLabel,
            workoutPartMainTitle, skipButton, plusSecondButton, restCountDownTimer, nextLabel,
            finishedTitle, finishedTime, finishedKcal, totalFinishedExercise, addWeightLabel,
            doneWorkoutButton;
    private ProgressBar progressBar;
    private int tempProgress = 15000, exercisePos, exerDuration, miniteOfWorkout, plusDuration,workoutImage;
    private FirebaseFirestore firestore;
    private DocumentReference docRef;
    private static final String tempEmail = "tempEmail";
    private String exercise_contain, currentExercise, lastExercise,bigWorkoutDuration;
    private ArrayList<String> workoutTitleList, workoutDuration, workoutUri,
            workoutDurationValue, workoutDurationType, workoutVideoUrl;
    private String[] exerciseList;
    private boolean startTicking = true;
    private LinearLayout finishedLinear;
    private CountDownTimer exerciseCountDown, restCountDown;
    private long countDownDuration;
    private ImageView finishExercise;
    private LottieAnimationView congratAnimation;
    private Handler timerHandler;
    private String workoutTitle;
    private ConstraintLayout gifWorkoutConstaint;
    long startTime = 0;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        Intent intent = getIntent();
        bigWorkoutDuration = intent.getStringExtra("workoutTime");
        workoutImage = intent.getIntExtra("workoutImage", 0);

        countDownTextView = findViewById(R.id.count_down_text);
        backButton = findViewById(R.id.button_backtohomefrag_workout);
        progressBar = findViewById(R.id.progressbar_workout);
        exerciseCounter = findViewById(R.id.exercise_counter);
        exerciseTimeCounter = findViewById(R.id.exercise_time_counter);
        exerciseTitleWorkout = findViewById(R.id.exercise_title_workout);
        excerciseNextTitleWorkout = findViewById(R.id.exercise_next_title_workout);
        gifExerciseWorkout = findViewById(R.id.gif_exer_workout);
        afterStartDuration = findViewById(R.id.exercise_real_time_counter_workout);
        afterStartWorkoutTitle = findViewById(R.id.after_start_exercise_title_workout);
        loadingIcon = findViewById(R.id.gif_exer_workout_loading_image);
        readyTextLabel = findViewById(R.id.ready_text_label);
        finishExercise = findViewById(R.id.finish_exercise_image);
        workoutPartMainTitle = findViewById(R.id.workout_part_main_title);
        skipButton = findViewById(R.id.skip_button_exercise);
        plusSecondButton = findViewById(R.id.plus_button_exercise);
        restCountDownTimer = findViewById(R.id.rest_count_down_timer);
        congratAnimation = findViewById(R.id.congrat_animation_lottie);
        nextLabel = findViewById(R.id.exercise_next_title_workout_label);
        finishedLinear = findViewById(R.id.finish_exercise_linear);
        finishedTitle = findViewById(R.id.exercise_done_title);
        finishedTime = findViewById(R.id.time_of_exercise);
        finishedKcal = findViewById(R.id.kcal_of_exercise);
        totalFinishedExercise = findViewById(R.id.num_of_exercise);
        addWeightLabel = findViewById(R.id.add_weight_recommended);
        gifWorkoutConstaint = findViewById(R.id.gif_exer_workout_constaint);
        doneWorkoutButton = findViewById(R.id.done_button_workout);

        //thread pool to stop timer
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        //runs without a timer by reposting this handler at the end of the runnable
        timerHandler = new Handler();
        Runnable timerRunnable = new Runnable() {

            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                exerciseTimeCounter.setText(String.format("%02d:%02d", minutes, seconds));
                miniteOfWorkout = minutes;
                timerHandler.postDelayed(this, 500);
            }
        };

        countDownDuration = TimeUnit.SECONDS.toMillis(15);
        progressBar.setMax(15000);


        firestore = FirebaseFirestore.getInstance();

        String tempString = intent.getStringExtra("workoutTitle");
        String tempTotal = intent.getStringExtra("workoutTotalExercise");
        workoutPartMainTitle.setText(tempTotal + " Exercises");
        workoutTitle = tempString;

        Runnable homeBackGroundRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    docRef = firestore.collection("exerciseList").document(tempString);
                    boolean isComplete = true;
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) {
                                    exercise_contain = document.getString("exercise_contain");
                                    assert exercise_contain != null;
                                    exerciseList = exercise_contain.split("-");

                                    lastExercise = exerciseList[exerciseList.length - 1];

                                    workoutTitleList = new ArrayList<>();
                                    workoutDuration = new ArrayList<>();
                                    workoutUri = new ArrayList<>();
                                    workoutDurationValue = new ArrayList<>();
                                    workoutDurationType = new ArrayList<>();
                                    workoutVideoUrl = new ArrayList<>();

                                    for (String exerciseItem : exerciseList) {
                                        currentExercise = exerciseItem;
                                        docRef = firestore.collection("exercise").document(currentExercise);
                                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot exerciseDocument) {
                                                if (exerciseDocument != null) {
                                                    workoutTitleList.add(exerciseDocument.getString("name"));
                                                    workoutUri.add(exerciseDocument.getString("hd_url"));
                                                    workoutDurationType.add(exerciseDocument.getString("duration_type"));
                                                    workoutVideoUrl.add(exerciseDocument.getString("video_url"));
                                                    workoutDuration.add(exerciseDocument.getString("duration"));
                                                    workoutDurationValue.add(exerciseDocument.getString("duration_value"));

                                                    if (workoutTitleList.size() == 1) {
                                                        exerciseTitleWorkout.setText(workoutTitleList.get(0));
                                                        afterStartWorkoutTitle.setText(workoutTitleList.get(exercisePos));
                                                        excerciseNextTitleWorkout.setText("Loading");
                                                        Glide.with(WorkoutActivity.this)
                                                                .load(workoutUri.get(0))
                                                                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                                                                .listener(new RequestListener<Drawable>() {
                                                                    @Override
                                                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                                        return false;
                                                                    }

                                                                    @Override
                                                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                                        gifExerciseWorkout.setVisibility(View.VISIBLE);
                                                                        loadingIcon.setVisibility(View.INVISIBLE);
                                                                        return false;
                                                                    }
                                                                })
                                                                .into(gifExerciseWorkout);
                                                    } else if (workoutTitleList.size() == 2) {
                                                        exerciseTitleWorkout.setText(workoutTitleList.get(0));
                                                    }
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    Log.d("LOGGER", "No such document");
                                }
                            } else {
                                Log.d("LOGGER", "get failed with ", task.getException());
                            }
                        }
                    });
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        };

        Thread backgroundThread = new Thread(homeBackGroundRunnable);
        backgroundThread.start();

        CountDownTimer result = new CountDownTimer(countDownDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String temp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                String sDuration = String.format(Locale.ENGLISH, "%02d",
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                if (sDuration.equals("00")) {
                    sDuration = "GO!";
                }
                countDownTextView.setText(sDuration);
                if (startTicking) {
                    int progress = Integer.parseInt(temp) * 1000;
                    ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, 0, 15000);
                    anim.setDuration(15000);
                    progressBar.startAnimation(anim);
                    tempProgress = progress;
                    startTicking = false;
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                exercisePos = 0;
                exerciseCounter.setVisibility(View.VISIBLE);
                exerciseCounter.setText("Exercise " + String.valueOf(exercisePos + 1) + "/" + workoutTitleList.size());
                exerciseTitleWorkout.setText(workoutTitleList.get(exercisePos));
                excerciseNextTitleWorkout.setText(workoutTitleList.get(exercisePos + 1));
                countDownTextView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                afterStartDuration.setVisibility(View.VISIBLE);
                afterStartWorkoutTitle.setVisibility(View.VISIBLE);
                readyTextLabel.setVisibility(View.INVISIBLE);
                exerciseTitleWorkout.setVisibility(View.INVISIBLE);
                workoutPartMainTitle.setVisibility(View.INVISIBLE);
                exerciseTimeCounter.setVisibility(View.VISIBLE);
                startTime = System.currentTimeMillis();
                timerHandler.postDelayed(timerRunnable, 0);

                loadingIcon.setVisibility(View.VISIBLE);

                final Context context = getApplication().getApplicationContext();

                Glide.with(WorkoutActivity.this)
                        .load(workoutUri.get(exercisePos))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                gifExerciseWorkout.setVisibility(View.VISIBLE);
                                loadingIcon.setVisibility(View.INVISIBLE);
                                return false;
                            }
                        })
                        .into(gifExerciseWorkout);

                if (workoutDuration.get(exercisePos).contains(":")) {
                    String[] splitDuration = workoutDuration.get(exercisePos).split(":");
                    if (Integer.parseInt(splitDuration[0]) > 0) {
                        exerDuration = Integer.parseInt(splitDuration[0]) * 60 + Integer.parseInt(splitDuration[1]);
                    } else {
                        exerDuration = Integer.parseInt(splitDuration[1]);
                    }
                    reverseTimer(exerDuration, afterStartDuration);
                } else {
                    afterStartDuration.setAllCaps(false);
                    afterStartDuration.setText(workoutDuration.get(exercisePos));
                    finishExercise.setVisibility(View.VISIBLE);
                }
            }
        };
        result.start();

        finishExercise.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (workoutDuration.get(exercisePos).contains(":")) {
                    exerciseCountDown.cancel();
                }
                setUpNextExercise();
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restCountDown.cancel();
                afterStartDuration.setVisibility(View.VISIBLE);
                afterStartWorkoutTitle.setVisibility(View.VISIBLE);
                readyTextLabel.setVisibility(View.INVISIBLE);
                restCountDownTimer.setVisibility(View.INVISIBLE);
                plusSecondButton.setVisibility(View.INVISIBLE);
                skipButton.setVisibility(View.INVISIBLE);
                finishExercise.setVisibility(View.VISIBLE);

                if (exercisePos == workoutTitleList.size() - 1) {
                    excerciseNextTitleWorkout.setText(workoutTitleList.get(exercisePos));
                } else {
                    excerciseNextTitleWorkout.setText(workoutTitleList.get(exercisePos + 1));
                }
                if (workoutDuration.get(exercisePos).contains(":")) {
                    reverseTimer(exerDuration, afterStartDuration);
                }
            }
        });

        plusSecondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restCountDown.cancel();
                plusDuration += 9;
                reverseTimerRest(plusDuration, restCountDownTimer, exerDuration,
                        afterStartDuration, workoutDuration.get(exercisePos).contains(":"));
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkoutActivity.this, MainActivity.class);
                intent.putExtra("fragmentPosition", 2);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
    }

    public void reverseTimer(int Seconds, final TextView tv) {

        exerciseCountDown = new CountDownTimer(Seconds * 1000 + 1000, 1000) {

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                tv.setText(String.format("%02d", minutes)
                        + ":" + String.format("%02d", seconds));
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onFinish() {
                setUpNextExercise();
            }
        }.start();
    }

    public void reverseTimerRest(int Seconds, final TextView tv, int duration, TextView textView, boolean isCountDownDuration) {

        restCountDown = new CountDownTimer(Seconds * 1000 + 1000, 1000) {

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                plusDuration = seconds;
                seconds = seconds % 60;
                tv.setText(String.format("%02d", minutes)
                        + ":" + String.format("%02d", seconds));
            }

            public void onFinish() {
                afterStartDuration.setVisibility(View.VISIBLE);
                afterStartWorkoutTitle.setVisibility(View.VISIBLE);
                readyTextLabel.setVisibility(View.INVISIBLE);
                restCountDownTimer.setVisibility(View.INVISIBLE);
                plusSecondButton.setVisibility(View.INVISIBLE);
                skipButton.setVisibility(View.INVISIBLE);
                finishExercise.setVisibility(View.VISIBLE);

                if (exercisePos == workoutTitleList.size() - 1) {
                    excerciseNextTitleWorkout.setText(workoutTitleList.get(exercisePos));
                } else {
                    excerciseNextTitleWorkout.setText(workoutTitleList.get(exercisePos + 1));
                }
                if (isCountDownDuration) {
                    reverseTimer(duration, textView);
                }
            }
        }.start();
    }

    @SuppressLint("SetTextI18n")
    private void setUpRestTime(int nextExerDuration, TextView nextExerTextView) {
        finishExercise.setVisibility(View.INVISIBLE);
        restCountDownTimer.setVisibility(View.VISIBLE);
        afterStartDuration.setVisibility(View.INVISIBLE);
        afterStartWorkoutTitle.setVisibility(View.INVISIBLE);
        readyTextLabel.setVisibility(View.VISIBLE);
        plusSecondButton.setVisibility(View.VISIBLE);
        skipButton.setVisibility(View.VISIBLE);
        readyTextLabel.setText("Take a Rest");
        int duration = 30;
        if (exercisePos == 1) {
            duration = 10;
        }
        if (workoutDuration.get(exercisePos).contains(":")) {
            reverseTimerRest(duration, restCountDownTimer, nextExerDuration, nextExerTextView, true);
        } else {
            reverseTimerRest(duration, restCountDownTimer, nextExerDuration, nextExerTextView, false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private void setUpNextExercise() {
        finishExercise.setVisibility(View.VISIBLE);
        if (exercisePos < workoutTitleList.size() - 1) {
            exercisePos++;
            exerciseCounter.setText("Exercise " + String.valueOf(exercisePos + 1) + "/" + workoutTitleList.size());
            afterStartWorkoutTitle.setText(workoutTitleList.get(exercisePos));

            loadingIcon.setVisibility(View.VISIBLE);

            Glide.with(WorkoutActivity.this)
                    .load(workoutUri.get(exercisePos))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            gifExerciseWorkout.setVisibility(View.VISIBLE);
                            loadingIcon.setVisibility(View.INVISIBLE);
                            return false;
                        }
                    })
                    .into(gifExerciseWorkout);

            if (workoutDuration.get(exercisePos).contains(":")) {
                finishExercise.setVisibility(View.INVISIBLE);
                String[] splitDuration = workoutDuration.get(exercisePos).split(":");
                if (Integer.parseInt(splitDuration[0]) > 0) {
                    exerDuration = Integer.parseInt(splitDuration[0]) * 60 + Integer.parseInt(splitDuration[1]);
                } else {
                    exerDuration = Integer.parseInt(splitDuration[1]);
                }

            } else {
                afterStartDuration.setAllCaps(false);
                afterStartDuration.setText(workoutDuration.get(exercisePos));
                finishExercise.setVisibility(View.VISIBLE);
            }

            setUpRestTime(exerDuration, afterStartDuration);
        } else {
            timerHandler.removeCallbacksAndMessages(null);
            endExercise();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void endExercise() {
        afterStartDuration.setVisibility(View.INVISIBLE);
        afterStartWorkoutTitle.setVisibility(View.INVISIBLE);
        readyTextLabel.setVisibility(View.INVISIBLE);
        restCountDownTimer.setVisibility(View.INVISIBLE);
        plusSecondButton.setVisibility(View.INVISIBLE);
        skipButton.setVisibility(View.INVISIBLE);
        finishExercise.setVisibility(View.INVISIBLE);
        gifExerciseWorkout.setVisibility(View.INVISIBLE);
        excerciseNextTitleWorkout.setVisibility(View.INVISIBLE);
        nextLabel.setVisibility(View.INVISIBLE);
        finishedLinear.setVisibility(View.VISIBLE);
        finishedTime.setText(exerciseTimeCounter.getText());
        gifWorkoutConstaint.setVisibility(View.INVISIBLE);
        totalFinishedExercise.setText(String.valueOf(workoutTitleList.size()));
        finishedTitle.setText(workoutTitle);

        doneWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkoutActivity.this, MainActivity.class);
                intent.putExtra("fragmentPosition", 2);
                startActivity(intent);
                Glide.with(WorkoutActivity.this).clear(gifExerciseWorkout);
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences(tempEmail, MODE_PRIVATE);
        String theTempWeight = sharedPreferences.getString("Weight", "");
        String theTempEmail = sharedPreferences.getString("Email", "");

        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(previousOrSame(MONDAY));

        if (!theTempWeight.equals("empty")) {
            float MET = 0f;
            String[] levelOfWorkout = workoutTitle.split("-");
            if (levelOfWorkout[1].equals(" Beginner")) {
                MET = 2.8f;
            } else if (levelOfWorkout[1].equals(" Advanced")) {
                MET = 8.0f;
            } else if (levelOfWorkout[1].equals(" Intermidiate")) {
                MET = 3.8f;
            }

            if (miniteOfWorkout > 0) {
                float kcalOfWorkout = (Float.parseFloat(theTempWeight) * MET) * miniteOfWorkout / 60;
                finishedKcal.setText(String.valueOf((int) kcalOfWorkout));
            } else {
                finishedKcal.setText("0");
            }
            addWeightLabel.setVisibility(View.GONE);
        } else {
            finishedKcal.setText("No Data");
            addWeightLabel.setVisibility(View.VISIBLE);
        }

        Runnable dataRunnable = new Runnable() {
            @Override
            public void run() {
                firestore = FirebaseFirestore.getInstance();

                String workoutTime = finishedTime.getText().toString();
                String workoutKcal = finishedKcal.getText().toString();
                String workoutDay = today.toString();
                String totalWorkout = totalFinishedExercise.getText().toString();

                Calendar cal = Calendar.getInstance();
                Date currentLocalTime = cal.getTime();
                DateFormat date = new SimpleDateFormat("HH:mm");
                String localTime = date.format(currentLocalTime);

                //set up date
                Date calendar = Calendar.getInstance().getTime();
                String day = (String) android.text.format.DateFormat.format("dd", calendar); // 20
                String monthString = (String) android.text.format.DateFormat.format("MMM", calendar); // Jun
                String todayFirebase = day + " " + monthString;

                long time= System.currentTimeMillis();

                Map<String, Object> workoutData = new HashMap<>();
                workoutData.put("workoutTime", workoutTime);
                workoutData.put("workoutDay", workoutDay);
                workoutData.put("workoutDate", todayFirebase);
                workoutData.put("firebaseTime", time);
                workoutData.put("workoutDayTime", localTime);
                workoutData.put("workoutTitle", workoutTitle);
                workoutData.put("workoutKcal", workoutKcal);
                workoutData.put("totalWorkout", totalWorkout);
                String theWorkoutImage = String.valueOf(workoutImage);
                workoutData.put("workoutImage",theWorkoutImage);

                firestore.collection("workoutHistory").
                        document(theTempEmail).collection("History").document().set(workoutData);

                workoutData.put("workoutDuration",bigWorkoutDuration);

                docRef = firestore.collection("workoutHistory").document(theTempEmail);
                docRef.set(workoutData);

                docRef =  firestore.collection("daily").
                        document("week-of-" + monday.toString()).
                        collection(today.toString()).
                        document(theTempEmail);

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                String tempNumOfExercise = document.getString("num_of_exercise");
                                String tempAmountOfKcal = document.getString("kcal_workout");
                                assert tempNumOfExercise != null;
                                docRef.update("num_of_exercise",String.valueOf(Integer.parseInt(tempNumOfExercise)+1));

                                if(!workoutKcal.equals("No Data")){
                                    docRef.update("kcal_workout",String.valueOf(Integer.parseInt(workoutKcal)+Integer.parseInt(tempAmountOfKcal)));
                                }
                            }
                        }
                    }
                });
            }
        };

        Thread uploadDataThread = new Thread(dataRunnable);
        uploadDataThread.start();

        congratAnimation.setVisibility(View.VISIBLE);
        congratAnimation.playAnimation();
    }
}