package cf.khanhsb.icare_v2;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class ArchieveFragment extends Fragment {
    private Button dailyclaim_button;
            Button rankclaim_button;


    public ArchieveFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_archieve, container, false);

        dailyclaim_button=(Button) rootView.findViewById(R.id.dailyclaim_button);
        rankclaim_button=(Button) rootView.findViewById(R.id.rankclaim_button);
        rankclaim_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rankclaim_button.setBackgroundColor(Color.GRAY);
            }
        });
        dailyclaim_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailyclaim_button.setBackgroundColor(Color.GRAY);
            }
        });

        return rootView;
    }
}