package cf.khanhsb.icare_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.john.waveview.WaveView;

public class WaterActivity extends AppCompatActivity {

    private int waterHasDrink = 0;
    private int numOfCup = 8;
    private WaveView waveView;
    private ImageView backButton,moreButton,plusButton,minusButton;
    private AppCompatButton drinkWater;
    private ConstraintLayout bottomSheetContainer;
    private ProgressBar progressBar;
    private TextView numberOfCups_textview,doneButton;
    private static final String realNumOfCup = "MyGoal";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);

        waveView = findViewById(R.id.wave_view_water);
        backButton = findViewById(R.id.button_backtohomefrag);
        drinkWater = findViewById(R.id.drink_water_button);
        moreButton = findViewById(R.id.more_menu_waterfrag);
        bottomSheetContainer = findViewById(R.id.bottom_sheet_container_water_frag);

        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMain = new Intent(WaterActivity.this,MainActivity.class);
                startActivity(toMain);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });


        drinkWater.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = waterHasDrink; i < waterHasDrink + 12; i++){
                    waveView.setProgress(i);
                }
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
                numberOfCups_textview = (TextView) bottomSheetView.findViewById(R.id.num_of_cup_progress);
                doneButton = (TextView) bottomSheetView.findViewById(R.id.close_button_animation_exercise);

                progressBar.setMax(12000);
                progressBar.setProgress(8000);

                plusButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        numOfCup = Integer.parseInt(numberOfCups_textview.getText().toString());
                        if( numOfCup < 12 ){
                            numOfCup += 1;
                            numberOfCups_textview.setText(String.valueOf(numOfCup));
                            float progress = Float.parseFloat(String.valueOf(numOfCup)) * 1000;
                            ProgressBarAnimation anim = new ProgressBarAnimation(progressBar,
                                    progress-1000,
                                    progress);
                            anim.setDuration(1000);
                            progressBar.startAnimation(anim);
                            SharedPreferences sharedPreferences = getSharedPreferences(
                                   realNumOfCup,MODE_PRIVATE);
                            SharedPreferences.Editor editor;
                            if(!sharedPreferences.contains("dailyWaterGoal")) {
                                editor = sharedPreferences.edit();
                                editor.putInt("dailyWaterGoal", numOfCup);
                                editor.apply();
                            }
                        }
                    }
                });

                minusButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        numOfCup = Integer.parseInt(numberOfCups_textview.getText().toString());
                        if( numOfCup > 4 ){
                            numOfCup -= 1;
                            numberOfCups_textview.setText(String.valueOf(numOfCup));
                            float progress = Float.parseFloat(String.valueOf(numOfCup)) * 1000;
                            ProgressBarAnimation anim = new ProgressBarAnimation(progressBar,
                                    progress+1000,
                                    progress);
                            anim.setDuration(1000);
                            progressBar.startAnimation(anim);
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