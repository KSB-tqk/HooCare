package cf.khanhsb.icare_v2.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;

import cf.khanhsb.icare_v2.MainActivity;
import cf.khanhsb.icare_v2.Model.ProgressBarAnimation;
import cf.khanhsb.icare_v2.R;
import cf.khanhsb.icare_v2.StepCountActivity;
import cf.khanhsb.icare_v2.WaterActivity;

import static android.content.Context.MODE_PRIVATE;
import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;


public class HomeFragment extends Fragment {
    private LinearLayout waterCardview, stepCardView, caloCardView, sleepCardView, trainingCardView, progressBar_text;
    private ProgressBar progressBar;
    private ConstraintLayout setupStepGoal, setupWaterGoal;
    private String userEmail;
    private FirebaseFirestore firestore;
    private String step_goal, drink_goal;
    private TextView statusOfProgressBar, numOfWater;
    private DocumentReference docRef;
    private int numberOfStep;
    private static final String tempEmail = "tempEmail";

    public HomeFragment() {
        // Required empty public constructor
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

        waterCardview = (LinearLayout) rootView.findViewById(R.id.water_card_view_linear);
        stepCardView = (LinearLayout) rootView.findViewById(R.id.step_count_cardview_linear);
        caloCardView = (LinearLayout) rootView.findViewById(R.id.calo_card_view_linear);
        sleepCardView = (LinearLayout) rootView.findViewById(R.id.sleep_card_view_linear);
        trainingCardView = (LinearLayout) rootView.findViewById(R.id.training_card_view_linear);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar_homefrag);
        progressBar_text = (LinearLayout) rootView.findViewById(R.id.progressBar_homefrag_linear);
        setupStepGoal = (ConstraintLayout) rootView.findViewById(R.id.setup_steps_constraint);
        statusOfProgressBar = (TextView) rootView.findViewById(R.id.status_of_progressbar_homefrag);
        setupWaterGoal = (ConstraintLayout) rootView.findViewById(R.id.setup_water_constraint);
        numOfWater = (TextView) rootView.findViewById(R.id.num_of_water);

        SharedPreferences sharedPreferences = this.getActivity().
                getSharedPreferences(tempEmail, MODE_PRIVATE);
        String theTempEmail = sharedPreferences.getString("Email", "");

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
                        step_goal = document.getString("step_goal");
                        Log.i("LOGGER", "Here it is " + document.getString("step_goal"));
                        if ("empty".equals(step_goal)) {
                            statusOfProgressBar.setText("");
                            setupStepGoal.setVisibility(View.VISIBLE);
                        } else {
                            setupStepGoal.setVisibility(View.GONE);
                            statusOfProgressBar.setText("/" + step_goal);
                            numberOfStep = Integer.parseInt("0");
                        }
                        progressBar.setMax(10000);
                        ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, 0, numberOfStep);
                        anim.setDuration(3000);
                        progressBar.startAnimation(anim);

                        drink_goal = document.getString("drink_goal");
                        if ("empty".equals(drink_goal)) {
                            setupWaterGoal.setVisibility(View.VISIBLE);
                        } else {
                            setupWaterGoal.setVisibility(View.GONE);
                        }
                    } else {
                        Log.d("LOGGER", "No such document");
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }
        });

        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(previousOrSame(MONDAY));
        docRef = firestore.collection("daily").
                document("week-of-" + monday.toString()).
                collection(today.toString()).
                document(theTempEmail);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
        }, 100);


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
                ((MainActivity)getActivity()).replaceFragment(2);
            }
        });

        trainingCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).replaceFragment(3);
            }
        });



        return rootView;
    }

}