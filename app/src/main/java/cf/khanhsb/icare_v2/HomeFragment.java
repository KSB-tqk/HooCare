package cf.khanhsb.icare_v2;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.Objects;


public class HomeFragment extends Fragment {
    private LinearLayout waterCardview,stepCardView,caloCardView,sleepCardView,trainingCardView,progressBar_text;
    private ProgressBar progressBar;
    private ConstraintLayout setupStepGoal;
    private String userEmail;
    private FirebaseFirestore firestore;
    private String step_goal;
    private TextView statusOfProgressBar;
    private int numberOfStep;
    public HomeFragment() {
        // Required empty public constructor
    }
    public HomeFragment(String userEmail){
        this.userEmail = userEmail;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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

        firestore = FirebaseFirestore.getInstance();
        DocumentReference docRef = firestore.collection("users").document(userEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        step_goal = document.getString("step_goal");
                        Log.i("LOGGER","Here it is "+document.getString("step_goal"));
                        if("empty".equals(step_goal)) {
                            statusOfProgressBar.setText("0");
                        }
                        else {
                            setupStepGoal.setVisibility(View.GONE);
                            statusOfProgressBar.setText(step_goal);
                            numberOfStep = Integer.parseInt(step_goal);
                        }
                        progressBar.setMax(10000);
                        ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, 0, numberOfStep);
                        anim.setDuration(3000);
                        progressBar.startAnimation(anim);
                    } else {
                        Log.d("LOGGER", "No such document");
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }
        });

        waterCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WaterActivity.class);
                intent.putExtra("userEmail",userEmail);
                startActivity(intent);
                Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.slide_in_right, R.anim.hold_position);
            }
        });

        stepCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toStepData = new Intent(getActivity(),StepCountActivity.class);
                toStepData.putExtra("userEmail",userEmail);
                toStepData.putExtra("step_goal", step_goal);
                startActivity(toStepData);
                Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.slide_in_right, R.anim.hold_position);
            }
        });

        return rootView;
    }
    
}