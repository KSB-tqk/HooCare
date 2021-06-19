package cf.khanhsb.icare_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import cf.khanhsb.icare_v2.Model.ProgressBarAnimation;

public class WorkoutActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView countDownTextView;
    private ProgressBar progressBar;
    private int tempProgress = 15000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        countDownTextView = findViewById(R.id.count_down_text);
        backButton = findViewById(R.id.button_backtohomefrag_workout);
        progressBar = findViewById(R.id.progressbar_workout);

        long duration = TimeUnit.SECONDS.toMillis(15);
        progressBar.setMax(15000);

        new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String temp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                String sDuration = String.format(Locale.ENGLISH,"%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                countDownTextView.setText(sDuration);
                int progress = Integer.parseInt(temp)*1000;
                ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, tempProgress, progress);
                anim.setDuration(1000);
                progressBar.startAnimation(anim);
                tempProgress = progress;
            }

            @Override
            public void onFinish() {

            }
        }.start();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkoutActivity.this, MainActivity.class);
                intent.putExtra("fragmentPosition", 3);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
    }
}