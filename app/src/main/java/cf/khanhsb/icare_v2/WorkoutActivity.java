package cf.khanhsb.icare_v2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
            excerciseNextTitleWorkout, afterStartWorkoutTitle, afterStartDuration, readyTextLabel;
    private ProgressBar progressBar;
    private int tempProgress = 15000, exercisePos,exerDuration;
    private FirebaseFirestore firestore;
    private DocumentReference docRef;
    private static final String tempEmail = "tempEmail";
    private String exercise_contain, currentExercise, lastExercise;
    private ArrayList<String> workoutTitleList, workoutDuration, workoutUri,
            workoutDurationValue, workoutDurationType, workoutVideoUrl;
    private String[] exerciseList;
    private boolean startTicking = true;
    private ConstraintLayout progressWorkoutConstaint;
    private CountDownTimer toNextExerciseCountDown;
    private long countDownDuration;
    private ImageView finishExercise;

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

        countDownDuration = TimeUnit.SECONDS.toMillis(15);
        progressBar.setMax(15000);

        SharedPreferences sharedPreferences = getSharedPreferences(tempEmail, MODE_PRIVATE);
        String theTempEmail = sharedPreferences.getString("Email", "");

        firestore = FirebaseFirestore.getInstance();

        String tempString = intent.getStringExtra("workoutTitle");

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
                                                    String tempName = exerciseDocument.getString("name");
                                                    workoutTitleList.add(tempName);
                                                    String tempUri = exerciseDocument.getString("url");
                                                    workoutUri.add(tempUri);
                                                    String tempType = exerciseDocument.getString("duration_type");
                                                    workoutDurationType.add(tempType);
                                                    String tempVideoUrl = exerciseDocument.getString("video_url");
                                                    workoutVideoUrl.add(tempVideoUrl);
                                                    String tempDuration = exerciseDocument.getString("duration");
                                                    workoutDuration.add(tempDuration);
                                                    String tempValue = exerciseDocument.getString("duration_value");
                                                    workoutDurationValue.add(tempValue);

                                                    if (workoutTitleList.size() == 1) {
                                                        exerciseTitleWorkout.setText(tempName);
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
                                                                        loadingIcon.setVisibility(View.GONE);
                                                                        return false;
                                                                    }
                                                                })
                                                                .into(gifExerciseWorkout);
                                                    } else if (workoutTitleList.size() == 2) {
                                                        excerciseNextTitleWorkout.setText(tempName);
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
                countDownTextView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                afterStartDuration.setVisibility(View.VISIBLE);
                afterStartWorkoutTitle.setVisibility(View.VISIBLE);
                readyTextLabel.setVisibility(View.INVISIBLE);
                exerciseTitleWorkout.setVisibility(View.INVISIBLE);

                if (workoutDuration.get(exercisePos).contains(":")) {
                    String[] splitDuration = workoutDuration.get(exercisePos).split(":");
                    if (Integer.parseInt(splitDuration[0]) > 0) {
                        exerDuration = Integer.parseInt(splitDuration[0]) * 60 + Integer.parseInt(splitDuration[1]);
                    } else {
                        exerDuration = Integer.parseInt(splitDuration[1]);
                    }
                    reverseTimer(exerDuration,afterStartDuration);
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
                setUpNextExercise();
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

    public void reverseTimer(int Seconds,final TextView tv){

        new CountDownTimer(Seconds* 1000+1000, 1000) {

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

    @SuppressLint("SetTextI18n")
    private void setUpNextExercise() {
        if(exercisePos < workoutTitleList.size()-1){
            exercisePos++;
            if (exercisePos == workoutTitleList.size()-1){
                excerciseNextTitleWorkout.setText(workoutTitleList.get(exercisePos));
            } else {
                excerciseNextTitleWorkout.setText(workoutTitleList.get(exercisePos + 1));
            }
        }
        exerciseCounter.setText("Exercise " + String.valueOf(exercisePos + 1) + "/" + workoutTitleList.size());
        afterStartWorkoutTitle.setText(workoutTitleList.get(exercisePos));

        if (workoutDuration.get(exercisePos).contains(":")) {
            finishExercise.setVisibility(View.INVISIBLE);
            String[] splitDuration = workoutDuration.get(exercisePos).split(":");
            if (Integer.parseInt(splitDuration[0]) > 0) {
                exerDuration = Integer.parseInt(splitDuration[0]) * 60 + Integer.parseInt(splitDuration[1]);
            } else {
                exerDuration = Integer.parseInt(splitDuration[1]);
            }
            reverseTimer(exerDuration,afterStartDuration);
        } else {
            afterStartDuration.setAllCaps(false);
            afterStartDuration.setText(workoutDuration.get(exercisePos));
            finishExercise.setVisibility(View.VISIBLE);
        }
    }
}