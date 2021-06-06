package cf.khanhsb.icare_v2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.john.waveview.WaveView;

import java.util.ArrayList;

public class WaterActivity extends AppCompatActivity {

    private ArrayList<BarEntry> dataValue = new ArrayList<BarEntry>();
    private int waterHasDrink = 0;
    private int numOfCup = 8;
    private WaveView waveView;
    private ImageView backButton, moreButton, plusButton, minusButton;
    private AppCompatButton drinkWater;
    private ConstraintLayout bottomSheetContainer;
    private ProgressBar progressBar;
    private ColorStateList def_color;
    private TextView statusOfProgressBar, day_tab, week_tab, month_tab, select_background;
    private TextView numberOfCups_text_view, doneButton, dailyWaterGoal_text_view;
    private FirebaseFirestore firestore;
    private DocumentReference docRef;
    private SeekBar seekBar;
    private static final String tempEmail = "tempEmail";
    private ViewPager2 verticalViewPager2;
    BarChartAdapter adapter;

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

        day_tab = (TextView) findViewById(R.id.text_item1_water_act);
        week_tab = (TextView) findViewById(R.id.text_item2_water_act);
        month_tab = (TextView) findViewById(R.id.text_item3_water_act);
        select_background = (TextView) findViewById(R.id.selected_background_tab_water_act);
        verticalViewPager2 = (ViewPager2) findViewById(R.id.water_barchart_viewPager2);
        def_color = week_tab.getTextColors();

        SharedPreferences sharedPreferences = getSharedPreferences(tempEmail, MODE_PRIVATE);
        String theTempEmail = sharedPreferences.getString("Email", "");

        firestore = FirebaseFirestore.getInstance();
        docRef = firestore.collection("users").document(theTempEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        String temp = document.getString("drink_goal");
                        assert temp != null;
                        if (temp.equals("empty")) {
                            dailyWaterGoal_text_view.setText("Set up your goal");
                        } else {
                            numOfCup = Integer.parseInt(temp);
                            dailyWaterGoal_text_view.setText("of " + (numOfCup * 250 / 1000) + "L goal");
                        }
                    } else {
                        Log.d("LOGGER", "No such document");
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }
        });

        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                waveView.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMain = new Intent(WaterActivity.this, MainActivity.class);
                startActivity(toMain);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        final Handler handler = new Handler();


        drinkWater.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0;i < 25;i++){
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ProgressBarAnimation anim = new ProgressBarAnimation(progressBar,
                                    0,
                                    25);
                            anim.setDuration(1000);
                            waveView.setAnimation(anim);
                        }
                    }, 300);
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
                numberOfCups_text_view = (TextView) bottomSheetView.findViewById(R.id.num_of_cup_progress);
                doneButton = (TextView) bottomSheetView.findViewById(R.id.close_button_animation_exercise);

                progressBar.setMax(12000);
                progressBar.setProgress(numOfCup * 1000);
                numberOfCups_text_view.setText(String.valueOf(numOfCup));

                plusButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        numOfCup = Integer.parseInt(numberOfCups_text_view.getText().toString());
                        if(numOfCup == 11) {
                            plusButton.setVisibility(View.GONE);
                        }
                        if (numOfCup < 12) {
                            numOfCup += 1;
                            if(numOfCup == 5){
                                minusButton.setVisibility(View.VISIBLE);
                            }
                            numberOfCups_text_view.setText(String.valueOf(numOfCup));

                            //progress bar animation
                            float progress = Float.parseFloat(String.valueOf(numOfCup)) * 1000;
                            ProgressBarAnimation anim = new ProgressBarAnimation(progressBar,
                                    progress - 1000,
                                    progress);
                            anim.setDuration(1000);
                            progressBar.startAnimation(anim);

                            dailyWaterGoal_text_view.setText("of " + numOfCup * 250 / 1000 + "L goal");

                            //update value
                            firestore.collection("users").document(theTempEmail).
                                    update("drink_goal", String.valueOf(numOfCup));
                        }
                    }
                });

                minusButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        numOfCup = Integer.parseInt(numberOfCups_text_view.getText().toString());
                        if(numOfCup == 5){
                            minusButton.setVisibility(View.GONE);
                        }
                        if (numOfCup > 4) {
                            numOfCup -= 1;
                            if(numOfCup == 11){
                                plusButton.setVisibility(View.VISIBLE);
                            }
                            numberOfCups_text_view.setText(String.valueOf(numOfCup));

                            //progress bar animation
                            float progress = Float.parseFloat(String.valueOf(numOfCup)) * 1000;
                            ProgressBarAnimation anim = new ProgressBarAnimation(progressBar,
                                    progress + 1000,
                                    progress);
                            anim.setDuration(1000);
                            progressBar.startAnimation(anim);

                            dailyWaterGoal_text_view.setText("of " + numOfCup * 250 / 1000 + "L goal");

                            //update value
                            firestore.collection("users").document(theTempEmail).
                                    update("drink_goal", String.valueOf(numOfCup));
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

        day_tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                select_background.animate().x(0).setDuration(200);
                day_tab.setTextColor(Color.WHITE);
                verticalViewPager2.setCurrentItem(0);
                week_tab.setTextColor(def_color);
                month_tab.setTextColor(def_color);
            }
        });

        week_tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = week_tab.getWidth();
                select_background.animate().x(size).setDuration(200);
                week_tab.setTextColor(Color.WHITE);
                verticalViewPager2.setCurrentItem(1);
                day_tab.setTextColor(def_color);
                month_tab.setTextColor(def_color);
            }
        });

        month_tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int size0fMonthTab = month_tab.getWidth() * 2;
                select_background.animate().x(size0fMonthTab).setDuration(200);
                month_tab.setTextColor(Color.WHITE);
                verticalViewPager2.setCurrentItem(2);
                day_tab.setTextColor(def_color);
                week_tab.setTextColor(def_color);
            }
        });

        ArrayList<BarDataSet> chartBarDataSetList = new ArrayList<>();

        /**add data to barchart*/
        dataValue.add(new BarEntry(0, 3000));
        dataValue.add(new BarEntry(1, 4000));
        dataValue.add(new BarEntry(2, 6000));
        dataValue.add(new BarEntry(3, 1000));
        dataValue.add(new BarEntry(4, 1200));
        dataValue.add(new BarEntry(5, 7200));
        dataValue.add(new BarEntry(6, 125));

        /**create bardata and add bardataset to bardata*/
        ArrayList<String> daylist = new ArrayList<String>();
        daylist.add("Mon");
        daylist.add("Tue");
        daylist.add("Wed");
        daylist.add("Thur");
        daylist.add("Fri");
        daylist.add("Sat");
        daylist.add("Sun");

        //initialize testadapter
        adapter = new BarChartAdapter(dataValue, daylist);
        //setting adapter on to the viewpager2
        verticalViewPager2.setUserInputEnabled(false);
        verticalViewPager2.setAdapter(adapter);
    }
}