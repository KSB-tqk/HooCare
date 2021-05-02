package cf.khanhsb.icare_v2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GymFragment extends Fragment {
    private FloatingActionButton viewButtonExercises;
    private TextView workoutHeadline,workoutTitle;
    private Boolean gotPlan = false;
    private ImageView clockIcon;

    public GymFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gym, container, false);

        viewButtonExercises = (FloatingActionButton) rootView.findViewById(R.id.fab_exercises);
        workoutHeadline = (TextView) rootView.findViewById(R.id.workout_headline);
        workoutTitle = (TextView) rootView.findViewById(R.id.workout_title);
        clockIcon = (ImageView) rootView.findViewById(R.id.clockIcon);

        clockIcon.setColorFilter(Color.WHITE);
        viewButtonExercises.setColorFilter(getResources().getColor(R.color.lime_200));

        viewButtonExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotPlan = true;
            }
        });

        if(!gotPlan) {
            workoutHeadline.setText("Set Up plan");
        }
        else {
            viewButtonExercises.setVisibility(View.VISIBLE);
            workoutHeadline.setText("Next workout");
        }

        return rootView;
    }
}