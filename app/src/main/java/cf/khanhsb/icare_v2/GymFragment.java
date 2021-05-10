package cf.khanhsb.icare_v2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class GymFragment extends Fragment {
    private FloatingActionButton viewButtonExercises;
    private TextView workoutHeadline, workoutTitle;
    private Boolean gotPlan = false;
    private ImageView clockIcon;

    /**
     * Gym listview
     */
    private NonScrollListView listView;
    private String[] gymListTitle = {"Abs - Beginner",
            "Chest - Beginner",
            "Arm - Beginner",
            "Leg - Beginner",
            "Shoulder & Back - Beginner"};
    private String[] gymListTime = {"15 min", "6 min", "16 min", "21 min", "14 min"};
    private String[] focusBodyPart = {"Abs","Chest","Arm","Leg","Shoulder & Back"};
    private int[] gymListImage = {R.drawable.workout_real_pic_item_list_icon,
            R.drawable.workout_real_pic_item_list_icon,
            R.drawable.workout_real_pic_item_list_icon,
            R.drawable.workout_real_pic_item_list_icon,
            R.drawable.workout_real_pic_item_list_icon};
    /**
     * Gym listview
     */

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

        listView = (NonScrollListView) rootView.findViewById(R.id.classic_workout_listview);
        GymListViewAdapter listViewAdapter = new GymListViewAdapter(getActivity(),
                R.layout.item_workout_list,
                gymListImage,
                gymListTitle,
                gymListTime);
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), List_Data_Activity.class);
                intent.putExtra("workoutTitle", gymListTitle[position]);
                intent.putExtra("workoutImage", gymListImage[position]);
                intent.putExtra("workoutTime",gymListTime[position]);
                intent.putExtra("focusBodyPart",focusBodyPart[position]);
                startActivity(intent);
                Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.slide_in_right, R.anim.hold_position);
            }
        });


        clockIcon.setColorFilter(Color.WHITE);
        viewButtonExercises.setColorFilter(getResources().getColor(R.color.lime_200));

        viewButtonExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotPlan = true;
            }
        });

        if (!gotPlan) {
            workoutHeadline.setText("Set Up plan");
        } else {
            viewButtonExercises.setVisibility(View.VISIBLE);
            workoutHeadline.setText("Next workout");
        }

        return rootView;
    }
}