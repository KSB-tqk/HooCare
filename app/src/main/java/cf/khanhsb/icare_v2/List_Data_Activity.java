package cf.khanhsb.icare_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class List_Data_Activity extends AppCompatActivity {
    private ImageView workoutImage,backButton,moreButton,favButton;
    private TextView workoutTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list__data);

        workoutTitle = (TextView) findViewById(R.id.gym_list_workout_text);
        workoutImage = (ImageView) findViewById(R.id.gym_list_image_view);
        backButton = (ImageView) findViewById(R.id.back_button_list_data);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();

        workoutTitle.setText(intent.getStringExtra("workoutTitle"));
        workoutImage.setImageResource(intent.getIntExtra("workoutImage",0));
    }
}