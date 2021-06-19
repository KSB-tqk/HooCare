package cf.khanhsb.icare_v2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import cf.khanhsb.icare_v2.Adapter.AnimExerListViewAdapter;
import cf.khanhsb.icare_v2.Adapter.AnimExerViewPagerAdapter;
import cf.khanhsb.icare_v2.Model.NonScrollListView;

public class List_Data_Activity extends YouTubeBaseActivity {
    private ImageView workoutImage, backButton, moreButton, favButton;
    private TextView workoutTitle, workoutTime, exerciseTitle, exerciseDurationValue,
            exerciseDurationText, animTitle, videoTitle, focusArea, workoutBigTitle,
            animCloseBtn, exerciseCount,startWorkoutButton;
    private LinearLayout startButtonAnimExer;
    private ConstraintLayout bottomSheetContainer, selectedBackground;
    private ViewPager2 viewPager2;
    private AnimExerViewPagerAdapter animExerViewPagerAdapter;
    private FirebaseFirestore firestore;
    private DocumentReference docRef;
    private String exercise_contain,currentExercise,lastExercise;
    private String[] exerciseList;
    private ArrayList<String> workoutTitleList, workoutDuration, workoutUri,
            workoutDurationValue,workoutDurationType,workoutVideoUrl;
    private NonScrollListView listView;

    /*animation exercise viewpager*/


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data);

        Intent intent = getIntent();

        workoutTitle = (TextView) findViewById(R.id.gym_list_workout_text);
        workoutBigTitle = (TextView) findViewById(R.id.title_list_data);
        workoutTime = (TextView) findViewById(R.id.time_title_list_data);
        exerciseCount = (TextView) findViewById(R.id.exercises_count_list_data);
        focusArea = (TextView) findViewById(R.id.area_focus_title_list_data);
        workoutImage = (ImageView) findViewById(R.id.gym_list_image_view);
        backButton = (ImageView) findViewById(R.id.back_button_list_data);
        bottomSheetContainer = (ConstraintLayout) findViewById(R.id.bottom_sheet_container_exer_anim);
        startButtonAnimExer = (LinearLayout) findViewById(R.id.start_button_anim_exer_linear);
        startWorkoutButton = findViewById(R.id.start_button_exercise_list);

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

                                    lastExercise = exerciseList[exerciseList.length-1];

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
                                                    String tempDuration = exerciseDocument.getString("duration");
                                                    workoutDuration.add(tempDuration);
                                                    String tempUri = exerciseDocument.getString("url");
                                                    workoutUri.add(tempUri);
                                                    String tempValue = exerciseDocument.getString("duration_value");
                                                    workoutDurationValue.add(tempValue);
                                                    String tempType = exerciseDocument.getString("duration_type");
                                                    workoutDurationType.add(tempType);
                                                    String tempVideoUrl = exerciseDocument.getString("video_url");
                                                    workoutVideoUrl.add(tempVideoUrl);

                                                    if (currentExercise.equals(lastExercise)) {
                                                        /**setting up animation exercise listview*/
                                                        listView = findViewById(R.id.list_view_list_data);
                                                        AnimExerListViewAdapter animExerListViewAdapter = new AnimExerListViewAdapter(List_Data_Activity.this,
                                                                R.layout.item_exercises_list_data, workoutTitleList, workoutDuration, workoutUri);
                                                        listView.setAdapter(animExerListViewAdapter);
                                                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                            @Override
                                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                                                                        List_Data_Activity.this, R.style.BottomSheetDialogTheme);
                                                                View bottomSheetView = LayoutInflater.from(getApplicationContext())
                                                                        .inflate(
                                                                                R.layout.bottom_sheet_dialog_anim_exer,
                                                                                bottomSheetContainer,
                                                                                false
                                                                        );
                                                                exerciseTitle = bottomSheetView.findViewById(R.id.title_animation_exercise);
                                                                exerciseDurationText = bottomSheetView.findViewById(R.id.text_animation_exercise);
                                                                exerciseDurationValue = bottomSheetView.findViewById(R.id.duration_value_text);
                                                                animCloseBtn = bottomSheetView.findViewById(R.id.close_button_animation_exercise);
                                                                animTitle = bottomSheetView.findViewById(R.id.animation_title);
                                                                videoTitle = bottomSheetView.findViewById(R.id.video_title);
                                                                selectedBackground = bottomSheetView.findViewById(R.id.tab_animation_view);

                                                                /**setting up viewpager in animaiton exercise*/
                                                                animExerViewPagerAdapter = new AnimExerViewPagerAdapter(workoutUri, workoutVideoUrl
                                                                        , position
                                                                        , List_Data_Activity.this);
                                                                viewPager2 = bottomSheetView.findViewById(R.id.animation_exercise_viewPager);
                                                                viewPager2.setAdapter(animExerViewPagerAdapter);

                                                                viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                                                                    @Override
                                                                    public void onPageSelected(int position) {
                                                                        super.onPageSelected(position);
                                                                        if (position == 1) {
                                                                            int size = videoTitle.getWidth();
                                                                            videoTitle.setTypeface(videoTitle.getTypeface(), Typeface.BOLD);
                                                                            animTitle.setTypeface(null, Typeface.NORMAL);
                                                                            selectedBackground.animate().x(size).setDuration(200);
                                                                        } else {
                                                                            videoTitle.setTypeface(null, Typeface.NORMAL);
                                                                            animTitle.setTypeface(animTitle.getTypeface(), Typeface.BOLD);
                                                                            selectedBackground.animate().x(0).setDuration(200);
                                                                        }
                                                                    }
                                                                });

                                                                videoTitle.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        int size = videoTitle.getWidth();
                                                                        videoTitle.setTypeface(videoTitle.getTypeface(), Typeface.BOLD);
                                                                        animTitle.setTypeface(null, Typeface.NORMAL);
                                                                        selectedBackground.animate().x(size).setDuration(200);
                                                                        viewPager2.setCurrentItem(1);
                                                                    }
                                                                });

                                                                animTitle.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        videoTitle.setTypeface(null, Typeface.NORMAL);
                                                                        animTitle.setTypeface(animTitle.getTypeface(), Typeface.BOLD);
                                                                        selectedBackground.animate().x(0).setDuration(200);
                                                                        viewPager2.setCurrentItem(0);
                                                                    }
                                                                });

                                                                animCloseBtn.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        bottomSheetDialog.dismiss();
                                                                    }
                                                                });

                                                                exerciseTitle.setText(workoutTitleList.get(position));
                                                                exerciseDurationText.setText(workoutDurationType.get(position));
                                                                exerciseDurationValue.setText(workoutDurationValue.get(position));
                                                                exerciseCount.setText("(" + workoutTitleList.size() + ")");
                                                                bottomSheetDialog.setContentView(bottomSheetView);
                                                                bottomSheetDialog.show();
                                                            }
                                                        });
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
                } catch(Exception err) {
                    err.printStackTrace();
                }
            }
        };

        Thread backgroundThread = new Thread(homeBackGroundRunnable);
        backgroundThread.start();

        startWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(List_Data_Activity.this, WorkoutActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        startButtonAnimExer.bringToFront();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(List_Data_Activity.this, MainActivity.class);
                intent.putExtra("fragmentPosition", 3);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        /**setting up intent from gym fragment*/

        workoutTitle.setText(intent.getStringExtra("workoutTitle"));
        workoutImage.setImageResource(intent.getIntExtra("workoutImage", 0));
        workoutTime.setText(intent.getStringExtra("workoutTime"));
        focusArea.setText(intent.getStringExtra("focusBodyPart"));
        workoutBigTitle.setText(intent.getStringExtra("focusBodyPart"));



        /**app bar animation*/
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.gym_list_app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    workoutTitle.setAlpha(1f);
                } else if (verticalOffset == 0) {
                    workoutTitle.setAlpha(0f);
                } else {
                    workoutTitle.setAlpha(0.3f);
                }
            }
        });
    }
}