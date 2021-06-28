package cf.khanhsb.icare_v2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import cf.khanhsb.icare_v2.Model.ProgressBarAnimation;

public class WorkoutActivity extends AppCompatActivity {

    private ImageView backButton, gifExerciseWorkout, loadingIcon;
    private TextView countDownTextView, exerciseCounter, exerciseTimeCounter, exerciseTitleWorkout,
            excerciseNextTitleWorkout, afterStartWorkoutTitle, afterStartDuration, readyTextLabel,
            workoutPartMainTitle, skipButton, plusSecondButton, restCountDownTimer,nextLabel;
    private ProgressBar progressBar;
    private int tempProgress = 15000, exercisePos, exerDuration;
    private FirebaseFirestore firestore;
    private DocumentReference docRef;
    private static final String tempEmail = "tempEmail";
    private String exercise_contain, currentExercise, lastExercise;
    private ArrayList<String> workoutTitleList, workoutDuration, workoutUri,
            workoutDurationValue, workoutDurationType, workoutVideoUrl;
    private String[] exerciseList;
    private boolean startTicking = true;
    private ConstraintLayout progressWorkoutConstaint;
    private CountDownTimer exerciseCountDown,restCountDown;
    private long countDownDuration;
    private ImageView finishExercise;
    private LottieAnimationView congratAnimation;
    long startTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        Intent intent = getIntent();

        countDownTextView = findViewById(R.id.count_down_text);
        backButton = findViewById(R.id.button_backtohomefrag_workout);
        progressBar = findViewById(R.id.progressbar_workout);
        exerciseCounter = findViewById(R.id.exercise_counter);
        exerciseTimeCounter = findViewById(R.id.exercise_time_counter);
        exerciseTitleWorkout = findViewById(R.id.exercise_title_workout);
        excerciseNextTitleWorkout = findViewById(R.id.exercise_next_title_workout);
        progressWorkoutConstaint = findViewById(R.id.progressbar_workout_constraint);
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

        //runs without a timer by reposting this handler at the end of the runnable
        Handler timerHandler = new Handler();
        Runnable timerRunnable = new Runnable() {

            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                exerciseTimeCounter.setText(String.format("%02d:%02d", minutes, seconds));

                timerHandler.postDelayed(this, 500);
            }
        };

        countDownDuration = TimeUnit.SECONDS.toMillis(15);
        progressBar.setMax(15000);

        SharedPreferences sharedPreferences = getSharedPreferences(tempEmail, MODE_PRIVATE);
        String theTempEmail = sharedPreferences.getString("Email", "");

        firestore = FirebaseFirestore.getInstance();

        String tempString = intent.getStringExtra("workoutTitle");
        String tempTotal = intent.getStringExtra("workoutTotalExercise");
        workoutPartMainTitle.setText(tempTotal + " Exercises");

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

        exerciseCountDown =  new CountDownTimer(Seconds * 1000 + 1000, 1000) {

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                tv.setText(String.format("%02d", minutes)
                        + ":" + String.format("%02d", seconds));
            }

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
            endExercise();
        }

    }

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

        congratAnimation.setVisibility(View.VISIBLE);
        congratAnimation.playAnimation();
    }
}