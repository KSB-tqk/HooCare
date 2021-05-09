package cf.khanhsb.icare_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import pl.droidsonroids.gif.GifImageView;

public class List_Exercises_Activity extends AppCompatActivity {
    private GifImageView gifImageView;
    private TextView exerciseTitle,exerciseDuration,closeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_exercises);

        gifImageView = (GifImageView) findViewById(R.id.exercises_gif_image);
        exerciseTitle = (TextView) findViewById(R.id.exercises_workout_title);
        exerciseDuration = (TextView) findViewById(R.id.exercises_workout_duration);
        closeButton = (TextView) findViewById(R.id.close_button_animation_exercise);

        Intent intent = getIntent();

        gifImageView.setImageResource(intent.getIntExtra("gifImage",0));
        exerciseTitle.setText(intent.getStringExtra("exerciseTitle"));
        exerciseDuration.setText(intent.getStringExtra("exerciseDuration"));
    }
}