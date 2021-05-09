package cf.khanhsb.icare_v2;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;


public class HomeFragment extends Fragment {
    private LinearLayout waterCardview,stepCardView,caloCardView,sleepCardView,trainingCardView;
    private ProgressBar progressBar;
    public HomeFragment() {
        // Required empty public constructor
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
        TextView statusOfProgressBar = (TextView) rootView.findViewById(R.id.status_of_progressbar_homefrag);
        int value = Integer.parseInt(statusOfProgressBar.getText().toString());

        progressBar.setMax(10000);
        ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, 0, value);
        anim.setDuration(3000);
        progressBar.startAnimation(anim);

        waterCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WaterActivity.class);
                startActivity(intent);
                Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.hold_position,R.anim.slide_in_bottom);
            }
        });

        stepCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toStepData = new Intent(getActivity(),StepCountActivity.class);
                startActivity(toStepData);
                Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.hold_position,R.anim.slide_in_bottom);
            }
        });

        return rootView;
    }
    
}