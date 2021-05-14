package cf.khanhsb.icare_v2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.john.waveview.WaveView;

public class WaterActivity extends AppCompatActivity {

    private int waterHasDrink = 0;
    private int numOfCup = 8;
    private WaveView waveView;
    private ImageView backButton, moreButton, plusButton, minusButton;
    private AppCompatButton drinkWater;
    private ConstraintLayout bottomSheetContainer;
    private ProgressBar progressBar;
    private TextView numberOfCups_text_view, doneButton, dailyWaterGoal_text_view;
    private static final String realNumOfCup = "MyGoal";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);

        waveView = findViewById(R.id.wave_view_water);
        backButton = findViewById(R.id.button_backtohomefrag);
        drinkWater = findViewById(R.id.drink_water_button);
        moreButton = findViewById(R.id.more_menu_waterfrag);
        bottomSheetContainer = findViewById(R.id.bottom_sheet_container_water_frag);
        dailyWaterGoal_text_view = findViewById(R.id.water_daily_goal_text);

        SharedPreferences sharedPreferences = getSharedPreferences(
                realNumOfCup, MODE_PRIVATE);

        int dailyWaterGoal = sharedPreferences.getInt("dailyWaterGoal", 0);
        if (dailyWaterGoal == 0) {
            dailyWaterGoal_text_view.setText("Set up your goal");
        } else {
            dailyWaterGoal_text_view.setText("of " +
                    String.valueOf(sharedPreferences.
                            getInt("dailyWaterGoal", 0) * 250 / 1000) +
                    "L goal");
        }

        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMain = new Intent(WaterActivity.this, MainActivity.class);
                startActivity(toMain);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        drinkWater.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressBarAnimation animWaterProgress = new ProgressBarAnimation(progressBar,
                        1,
                        25);
                animWaterProgress.setDuration(1000);
                waterHasDrink += 25;
            }
        });

        moreButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        WaterActivity.this,
                        R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.bottom_sheet_dialog_waterfrag,
                                bottomSheetContainer,
                                false
                        );
                plusButton = (ImageView) bottomSheetView.findViewById(R.id.plus_button_water_menu);
                minusButton = (ImageView) bottomSheetView.findViewById(R.id.minus_button_water_menu);
                progressBar = (ProgressBar) bottomSheetView.findViewById(R.id.progressbar_water);
                numberOfCups_text_view = (TextView) bottomSheetView.findViewById(R.id.num_of_cup_progress);
                doneButton = (TextView) bottomSheetView.findViewById(R.id.close_button_animation_exercise);

                if (!sharedPreferences.contains("dailyWaterGoal")) {
                    numberOfCups_text_view.setText("8");
                } else {
                    numberOfCups_text_view.setText(String.valueOf(sharedPreferences.getInt("dailyWaterGoal", 0)));
                }
                progressBar.setMax(12000);
                progressBar.setProgress(sharedPreferences.getInt("dailyWaterGoal", 0)*1000);

                plusButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        numOfCup = Integer.parseInt(numberOfCups_text_view.getText().toString());
                        if (numOfCup < 12) {
                            numOfCup += 1;
                            numberOfCups_text_view.setText(String.valueOf(numOfCup));
                            float progress = Float.parseFloat(String.valueOf(numOfCup)) * 1000;
                            ProgressBarAnimation anim = new ProgressBarAnimation(progressBar,
                                    progress - 1000,
                                    progress);
                            anim.setDuration(1000);
                            progressBar.startAnimation(anim);
                            SharedPreferences.Editor editor;
                            editor = sharedPreferences.edit();
                            editor.putInt("dailyWaterGoal", numOfCup);
                            editor.apply();
                            dailyWaterGoal_text_view.setText("of " + numOfCup * 250 / 1000 + "L goal");
                        }
                    }
                });

                minusButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        numOfCup = Integer.parseInt(numberOfCups_text_view.getText().toString());
                        if (numOfCup > 4) {
                            numOfCup -= 1;
                            numberOfCups_text_view.setText(String.valueOf(numOfCup));
                            float progress = Float.parseFloat(String.valueOf(numOfCup)) * 1000;
                            ProgressBarAnimation anim = new ProgressBarAnimation(progressBar,
                                    progress + 1000,
                                    progress);
                            anim.setDuration(1000);
                            progressBar.startAnimation(anim);
                            SharedPreferences.Editor editor;
                            editor = sharedPreferences.edit();
                            editor.putInt("dailyWaterGoal", numOfCup);
                            editor.apply();
                            dailyWaterGoal_text_view.setText("of " + numOfCup * 250 / 1000 + "L goal");
                        }
                    }
                });

                doneButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });
    }
}