package cf.khanhsb.icare_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import pl.droidsonroids.gif.GifImageView;

public class List_Exercises_Activity extends AppCompatActivity {
    private GifImageView gifImageView;
    private TextView exerciseTitle,exerciseDuration,durationText,closeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_exercises);

        gifImageView = (GifImageView) findViewById(R.id.exercises_gif_image);
        exerciseTitle = (TextView) findViewById(R.id.title_animation_exercise);
        exerciseDuration = (TextView) findViewById(R.id.text_animation_exercise);
        closeButton = (TextView) findViewById(R.id.close_button_animation_exercise);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(List_Exercises_Activity.this,List_Data_Activity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        Intent intent = getIntent();

        exerciseTitle.setText(intent.getStringExtra("exerciseTitle"));
        exerciseDuration.setText(intent.getStringExtra("exerciseDuration"));
    }
}