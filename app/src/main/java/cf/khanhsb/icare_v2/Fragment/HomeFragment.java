package cf.khanhsb.icare_v2.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import cf.khanhsb.icare_v2.MainActivity;
import cf.khanhsb.icare_v2.Model.ProgressBarAnimation;
import cf.khanhsb.icare_v2.R;
import cf.khanhsb.icare_v2.SleepTimeActivity;
import cf.khanhsb.icare_v2.StepCountActivity;
import cf.khanhsb.icare_v2.StepCounter.StepDetector;
import cf.khanhsb.icare_v2.StepCounter.StepListener;
import cf.khanhsb.icare_v2.UsageStatisticActivity;
import cf.khanhsb.icare_v2.WaterActivity;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.SENSOR_SERVICE;
import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

public class HomeFragment extends Fragment implements SensorEventListener, StepListener {
    private LinearLayout waterCardview, stepCardView, caloCardView,
            sleepCardView, trainingCardView, progressBar_text, timeOnScreenCardView;
    private ProgressBar progressBar;
    private ConstraintLayout setupStepGoal, setupWaterGoal;
    private String userEmail, theTempEmail;
    private FirebaseFirestore firestore;
    private DocumentReference docRef;
    private String step_goal, drink_goal, sleepTime, stepGoal;
    private TextView statusOfProgressBar, numOfWater, sleepTimeTextView, timeOnScreenTextView;

    private FirebaseAuth mAuth;
    private static final String tempEmail = "tempEmail";
    private TextView userName, numOfExercise;


    private StepDetector simpleStepDetector;
    private static final String TEXT_NUM_STEPS = "";
    private int numStepsHomeFrag;
    private TextView home_step_count;


    public HomeFragment() {
        // Required empty public constructor
    }

    public void callParentMethod() {
        getActivity().onBackPressed();
    }

    public HomeFragment(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        home_step_count = (TextView) rootView.findViewById(R.id.home_step_count);
        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        sensorManager.registerListener(HomeFragment.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
      
        waterCardview = (LinearLayout) rootView.findViewById(R.id.water_card_view_linear);
        stepCardView = (LinearLayout) rootView.findViewById(R.id.step_count_cardview_linear);
        caloCardView = (LinearLayout) rootView.findViewById(R.id.calo_card_view_linear);
        sleepCardView = (LinearLayout) rootView.findViewById(R.id.sleep_card_view_linear);
        trainingCardView = (LinearLayout) rootView.findViewById(R.id.training_card_view_linear);
        timeOnScreenCardView = (LinearLayout) rootView.findViewById(R.id.time_on_screen_card_view_linear);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar_homefrag);
        progressBar_text = (LinearLayout) rootView.findViewById(R.id.progressBar_homefrag_linear);
        setupStepGoal = (ConstraintLayout) rootView.findViewById(R.id.setup_steps_constraint);
        statusOfProgressBar = (TextView) rootView.findViewById(R.id.status_of_progressbar_homefrag);
        setupWaterGoal = (ConstraintLayout) rootView.findViewById(R.id.setup_water_constraint);
        numOfWater = (TextView) rootView.findViewById(R.id.num_of_water);
        sleepTimeTextView = (TextView) rootView.findViewById(R.id.sleep_time_text_view);
        timeOnScreenTextView = (TextView) rootView.findViewById(R.id.time_on_screen_text_view);
        userName = (TextView) rootView.findViewById(R.id.username_text_view);
        numOfExercise = (TextView) rootView.findViewById(R.id.num_of_exercise_home_frag);

        SharedPreferences sharedPreferences = this.getActivity().
                getSharedPreferences(tempEmail, MODE_PRIVATE);
        theTempEmail = sharedPreferences.getString("Email", "");

        waterCardview.setClickable(false);
        stepCardView.setClickable(false);
        sleepCardView.setClickable(false);

        Runnable homeBackGroundRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    SetUpFirebase(theTempEmail);

                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        };

        Thread backgroundThread = new Thread(homeBackGroundRunnable);
        backgroundThread.start();

        Runnable stepData = new Runnable() {
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
                                if (temp != null) {
                                    if (!"empty".equals(temp)) {

                                        home_step_count.setText(String.valueOf(temp));
                                        numStepsHomeFrag = Integer.parseInt(temp);


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

        Thread stepDataThread = new Thread(stepData);
        stepDataThread.start();





        waterCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WaterActivity.class);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold_position);
            }
        });

        stepCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toStepData = new Intent(getActivity(), StepCountActivity.class);
                toStepData.putExtra("userEmail", userEmail);
                toStepData.putExtra("step_goal", step_goal);
                startActivity(toStepData);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold_position);
            }
        });

        caloCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).replaceFragment(1);
            }
        });

        trainingCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).replaceFragment(2);
            }
        });

        timeOnScreenCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toUsageStatistic = new Intent(getActivity(), UsageStatisticActivity.class);
                startActivity(toUsageStatistic);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold_position);
            }
        });

        sleepCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSleepTime = new Intent(getActivity(), SleepTimeActivity.class);
                toSleepTime.putExtra("sleepTime", sleepTime);
                startActivity(toSleepTime);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold_position);
            }
        });

        return rootView;


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void SetUpFirebase(String userEmail) {
        //set up shareRef
        SharedPreferences sharedPreferences = this.getActivity().
                getSharedPreferences(tempEmail, MODE_PRIVATE);

        //set up database date
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(previousOrSame(MONDAY));

        //set up firestore
        firestore = FirebaseFirestore.getInstance();
        docRef = firestore.collection("daily").
                document("week-of-" + monday.toString()).
                collection(today.toString()).
//                collection("2021-06-28").
                document(userEmail);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        Log.d("LOGGER", "got the document");
                        SharedPreferences.Editor editor;
                        editor = sharedPreferences.edit();
                        editor.putString("Email", userEmail);
                        editor.apply();

                        String tempNumOfExercise = document.getString("num_of_exercise");
                        int tempNum = Integer.parseInt(tempNumOfExercise);
                        if (tempNum == 0) {
                            numOfExercise.setText("No Workout");
                        } else if (tempNum == 1) {
                            numOfExercise.setText(tempNumOfExercise + " Workout");
                        } else {
                            numOfExercise.setText(tempNumOfExercise + " Workouts");
                        }

                        SetUpStepCountCard(userEmail);
                        SetUpWaterCard(userEmail);
                        SetUpSleepCard(userEmail);
                        SetUpTimeCard(userEmail);
                    } else {
                        //check if goal exist or not
                        docRef = firestore.collection("users").document(userEmail);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null) {
                                        String temp = document.getString("drink_goal");
                                        String time = document.getString("sleep_goal");
                                        String weight = document.getString("weight");
                                        String height = document.getString("height");
                                        String step = document.getString("step_goal");
                                        String mName = document.getString("name");

                                        userName.setText(mName);
                                        numOfExercise.setText("No Workout");

                                        //create dailyData
                                        docRef = firestore.collection("daily").
                                                document("week-of-" + monday.toString()).
                                                collection(today.toString()).
                                                document(userEmail);
                                        Map<String, Object> dailyGoal = new HashMap<>();

                                        if (temp.equals("empty")) {
                                            dailyGoal.put("drink", "empty");
                                        } else {
                                            dailyGoal.put("drink", "0");
                                        }

                                        if (time.equals("empty")) {
                                            dailyGoal.put("sleep_time", "empty");
                                        } else {
                                            dailyGoal.put("sleep_time", "0");
                                        }

                                        if (weight.equals("empty")) {
                                            dailyGoal.put("weight", "empty");
                                        } else {
                                            dailyGoal.put("weight", weight);
                                        }

                                        if (height.equals("empty")) {
                                            dailyGoal.put("height", "empty");
                                        } else {
                                            dailyGoal.put("height", height);
                                        }

                                        if (step.equals("empty")) {
                                            dailyGoal.put("steps", "empty");
                                        } else {
                                            dailyGoal.put("steps", step);
                                        }


                                        dailyGoal.put("time_on_screen", "0");
                                        dailyGoal.put("num_of_exercise", "0");
                                        dailyGoal.put("userEmail",userEmail);

                                        //update data to firestore
                                        firestore = FirebaseFirestore.getInstance();
                                        firestore.collection("daily").
                                                document("week-of-" + monday.toString()).
                                                collection(today.toString()).
                                                document(userEmail).set(dailyGoal);

                                        SharedPreferences.Editor editor;
                                        editor = sharedPreferences.edit();
                                        editor.putString("Email", userEmail);
                                        editor.apply();
                                        SetUpStepCountCard(userEmail);
                                        SetUpWaterCard(userEmail);
                                    } else {
                                        Log.d("LOGGER", "No such document");
                                    }
                                } else {
                                    Log.d("LOGGER", "get failed with ", task.getException());
                                }
                            }
                        });
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }
        });
    }

    private void SetUpStepCountCard(String theTempEmail) {
        firestore = FirebaseFirestore.getInstance();
        if (userEmail == null) {
            docRef = firestore.collection("users").document(theTempEmail);
        } else {
            docRef = firestore.collection("users").document(userEmail);
        }
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        String mName = document.getString("name");
                        userName.setText(mName);

                        step_goal = document.getString("step_goal");

                        if ("empty".equals(step_goal)) {
                            statusOfProgressBar.setText("steps");
                            setupStepGoal.setVisibility(View.VISIBLE);
                        } else {
                            setupStepGoal.setVisibility(View.GONE);
                            statusOfProgressBar.setText("/" + step_goal);
                            if (!statusOfProgressBar.getText().equals("steps")) {

                                home_step_count.setText(TEXT_NUM_STEPS + numStepsHomeFrag);


                                String tempStepGoal = statusOfProgressBar.getText().toString().substring(1);
                                progressBar.setMax(Integer.parseInt(tempStepGoal));


                                ProgressBarAnimation anim = new ProgressBarAnimation(progressBar,
                                        numStepsHomeFrag - 1,
                                        numStepsHomeFrag);
                                anim.setDuration(100);
                                progressBar.startAnimation(anim);
                            }

                        }

                        drink_goal = document.getString("drink_goal");
                        if ("empty".equals(drink_goal)) {
                            setupWaterGoal.setVisibility(View.VISIBLE);
                        } else {
                            setupWaterGoal.setVisibility(View.GONE);
                        }

                        stepCardView.setClickable(true);
                    } else {
                        Log.d("LOGGER", "No such document");
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void SetUpWaterCard(String theTempEmail) {

        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(previousOrSame(MONDAY));
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
                        String temp = document.getString("drink");
                        if (!"empty".equals(temp)) {
                            float waterHadDrink = Float.parseFloat(temp) / 1000;
                            numOfWater.setText(String.valueOf(waterHadDrink));
                            waterCardview.setClickable(true);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void SetUpSleepCard(String theTempEmail) {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(previousOrSame(MONDAY));
        docRef = firestore.collection("daily").
                document("week-of-" + monday.toString()).
                collection(today.toString()).
                document(theTempEmail);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        String temp = document.getString("sleep_time");
                        if (!"empty".equals(temp)) {
                            String[] splitString = temp.split(":");
                            sleepTimeTextView.setText(splitString[0] + "h");
                            sleepTime = temp;
                            sleepCardView.setClickable(true);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void SetUpTimeCard(String theTempEmail) {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(previousOrSame(MONDAY));
        docRef = firestore.collection("daily").
                document("week-of-" + monday.toString()).
                collection(today.toString()).
                document(theTempEmail);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        String temp = document.getString("time_on_screen");
                        if (!"0".equals(temp)) {
                            String[] splitHourOnScreen = temp.split(" ");
                            timeOnScreenTextView.setText(splitHourOnScreen[0]);
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


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void step(long timeNs) {
        if (!statusOfProgressBar.getText().equals("steps")) {
            numStepsHomeFrag++;
            home_step_count.setText(TEXT_NUM_STEPS + numStepsHomeFrag);


            String tempStepGoal = statusOfProgressBar.getText().toString().substring(1);
            progressBar.setMax(Integer.parseInt(tempStepGoal));


            ProgressBarAnimation anim = new ProgressBarAnimation(progressBar,
                    numStepsHomeFrag - 1,
                    numStepsHomeFrag);
            anim.setDuration(100);
            progressBar.startAnimation(anim);

            Runnable stepCountRunnable = new Runnable() {
                @Override
                public void run() {
                    LocalDate today = LocalDate.now();
                    LocalDate monday = today.with(previousOrSame(MONDAY));
                    if (userEmail == null) {
                        docRef = firestore.collection("users").document(theTempEmail);
                    } else {
                        docRef = firestore.collection("users").document(userEmail);
                    }
                    docRef = firestore.collection("daily").

                            document("week-of-" + monday.toString()).

                            collection(today.toString()).

                            document(theTempEmail);
                    docRef.update("steps", String.valueOf(numStepsHomeFrag));

                }
            };
            Thread backgroundThread = new Thread(stepCountRunnable);
            backgroundThread.start();
        }
    }

}


