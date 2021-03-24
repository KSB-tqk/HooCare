package cf.khanhsb.icare_v2;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;


public class HomeFragment extends Fragment {


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

        LinearLayout waterCardview = (LinearLayout) rootView.findViewById(R.id.water_card_view_linear);
        LinearLayout stepCardView = (LinearLayout) rootView.findViewById(R.id.step_count_cardview_linear);
        LinearLayout caloCardView = (LinearLayout) rootView.findViewById(R.id.calo_card_view_linear);
        LinearLayout sleepCardView = (LinearLayout) rootView.findViewById(R.id.sleep_card_view_linear);
        LinearLayout trainingCardView = (LinearLayout) rootView.findViewById(R.id.training_card_view_linear);

        waterCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WaterActivity.class);
                startActivity(intent);
                Toast.makeText(v.getContext(),"Water",Toast.LENGTH_SHORT).show();
            }
        });

        stepCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toStepData = new Intent(getActivity(),StepCountActivity.class);
                startActivity(toStepData);
                Toast.makeText(v.getContext(),"Step",Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
}