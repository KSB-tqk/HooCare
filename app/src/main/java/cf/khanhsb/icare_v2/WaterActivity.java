package cf.khanhsb.icare_v2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.john.waveview.WaveView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cf.khanhsb.icare_v2.Adapter.BarChartAdapter;
import cf.khanhsb.icare_v2.Model.ProgressBarAnimation;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

public class WaterActivity extends AppCompatActivity {

    private ArrayList<BarEntry> dataValue;
    private int waterHadDrink = 0, numOfCup = 8, waterHaveToDrink = 0, i;
    private WaveView waveView;
    private ImageView backButton, moreButton, plusButton, minusButton, minusWaterButton;
    private AppCompatButton drinkWater;
    private ConstraintLayout bottomSheetContainer;
    private ProgressBar progressBar;
    private ColorStateList def_color;
    private TextView backToPreviusWeek, nextToFollowingWeek,
            numberOfCups_text_view, doneButton, dailyWaterGoal_text_view, waterCupDailyGoal,
            waterCountText, waterCupCountText, noData, dateTimeLabel, weekLabel;
    private FirebaseFirestore firestore;
    private DocumentReference docRef;
    private static final String tempEmail = "tempEmail";
    private ViewPager2 verticalViewPager2;
    private BarChartAdapter adapter;
    private LocalDate tempDate;
    private ArrayList<String> daylist;
    private String joinDate, beginDay, endDay;
    private String[] beginDaySplit, endDaySplit;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);

        /**create bardata and add bardataset to bardata*/
        waveView = findViewById(R.id.wave_view_water);
        backButton = findViewById(R.id.button_backtohomefrag);
        drinkWater = findViewById(R.id.drink_water_button);
        moreButton = findViewById(R.id.more_menu_waterfrag);
        bottomSheetContainer = findViewById(R.id.bottom_sheet_container_water_frag);
        dailyWaterGoal_text_view = findViewById(R.id.water_daily_goal_text);
        waterCupDailyGoal = findViewById(R.id.water_cup_daily_goal_text);
        waterCountText = findViewById(R.id.water_count_text);
        minusWaterButton = findViewById(R.id.minus_water_button);
        waterCupCountText = findViewById(R.id.water_cup_count_text);
        noData = findViewById(R.id.no_data_text_label_water);
        dateTimeLabel = findViewById(R.id.date_time_water);
        weekLabel = findViewById(R.id.week_label_water);

        backToPreviusWeek = (TextView) findViewById(R.id.back_to_last_week_button);
        nextToFollowingWeek = (TextView) findViewById(R.id.next_to_next_week_button);
        verticalViewPager2 = (ViewPager2) findViewById(R.id.water_barchart_viewPager2);
//        def_color = week_tab.getTextColors();

        //set up date
        Date calendar = Calendar.getInstance().getTime();
        System.out.println("Current time => " + calendar);
        String day = (String) DateFormat.format("dd", calendar); // 20
        String monthString = (String) DateFormat.format("MMM", calendar); // Jun
        String realDate = day + " " + monthString;

        dateTimeLabel.setText("Today, " + realDate);

        SharedPreferences sharedPreferences = getSharedPreferences(tempEmail, MODE_PRIVATE);
        String theTempEmail = sharedPreferences.getString("Email", "");

        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(previousOrSame(MONDAY));

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
                            minusWaterButton.setVisibility(View.GONE);
                            noData.setVisibility(View.VISIBLE);
                            verticalViewPager2.setVisibility(View.GONE);
                        } else {
                            setUpWeekData(monday, today);
                            noData.setVisibility(View.GONE);
                            numOfCup = Integer.parseInt(temp);
                            float amountOfWater = Float.parseFloat(String.valueOf(numOfCup));
                            dailyWaterGoal_text_view.setText("of " + (amountOfWater * 250 / 1000) + "L goal");
                            waterHaveToDrink = numOfCup * 250;
                            minusWaterButton.setVisibility(View.VISIBLE);
                        }

                        docRef = firestore.collection("daily").
                                document("week-of-" + monday.toString()).
                                collection(today.toString()).
                                document(theTempEmail);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null) {
                                        String waterHadDrinkOnDataBase = document.getString("drink");
                                        assert waterHadDrinkOnDataBase != null;
                                        if (waterHadDrinkOnDataBase.equals("empty")) {
                                            drinkWater.setText("Set a Goal");
                                            waterCupDailyGoal.setVisibility(View.GONE);
                                            dailyWaterGoal_text_view.setVisibility(View.GONE);
                                        } else {
                                            waterHadDrink = Integer.parseInt(waterHadDrinkOnDataBase);
                                            waterCupDailyGoal.setVisibility(View.VISIBLE);
                                            dailyWaterGoal_text_view.setVisibility(View.VISIBLE);
                                            drinkWater.setText("drink");
                                            waterCountText.setText(String.valueOf(waterHadDrink));
                                            minusWaterButton.setVisibility(View.VISIBLE);

                                            float hadDrink = waterHadDrink, haveToDrink = waterHaveToDrink;

                                            if (waterHadDrink <= waterHaveToDrink && waterHadDrink != 0) {
                                                String numOfCupHasToDrink = String.valueOf((int) Math.floor((double) ((haveToDrink / 250) - (hadDrink / 250))));
                                                waterCupCountText.setText(numOfCupHasToDrink);
                                            } else if (waterHadDrink == 0) {
                                                waterCupCountText.setText(String.valueOf(numOfCup));
                                            } else {
                                                waterCupCountText.setText("0");
                                            }
                                            float ans = hadDrink / haveToDrink * 100;
                                            waveView.setProgress((int) ans);
                                        }
                                    } else {
                                        Log.d("LOGGER", "No such document");
                                    }
                                } else {
                                    Log.d("LOGGER", "get failed with ", task.getException());
                                }
                            }
                        });
                    } else {
                        Log.d("LOGGER", "No such document");
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }
        });

        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMain = new Intent(WaterActivity.this, MainActivity.class);
                startActivity(toMain);
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        drinkWater.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                minusWaterButton.setVisibility(View.VISIBLE);
                if (drinkWater.getText().equals("Set a Goal")) {
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
                    doneButton = (TextView) bottomSheetView.findViewById(R.id.close_button_water);

                    progressBar.setMax(12000);
                    progressBar.setProgress(numOfCup * 1000);
                    numberOfCups_text_view.setText(String.valueOf(numOfCup));

                    plusButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            numOfCup = Integer.parseInt(numberOfCups_text_view.getText().toString());
                            if (numOfCup == 11) {
                                plusButton.setVisibility(View.GONE);
                            }
                            if (numOfCup < 12) {
                                numOfCup += 1;
                                if (numOfCup == 5) {
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

                                float amountOfWater = Float.parseFloat(String.valueOf(numOfCup));
                                dailyWaterGoal_text_view.setText("of " + (amountOfWater * 250 / 1000) + "L goal");

                                //update value
                                waterHaveToDrink = numOfCup * 250;
                                firestore.collection("users").document(theTempEmail).
                                        update("drink_goal", String.valueOf(numOfCup));
                            }
                        }
                    });

                    minusButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            numOfCup = Integer.parseInt(numberOfCups_text_view.getText().toString());
                            if (numOfCup == 5) {
                                minusButton.setVisibility(View.GONE);
                            }
                            if (numOfCup > 4) {
                                numOfCup -= 1;
                                if (numOfCup == 11) {
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

                                float amountOfWater = Float.parseFloat(String.valueOf(numOfCup));
                                dailyWaterGoal_text_view.setText("of " + (amountOfWater * 250 / 1000) + "L goal");

                                //update value
                                waterHaveToDrink = numOfCup * 250;
                                firestore.collection("users").document(theTempEmail).
                                        update("drink_goal", String.valueOf(numOfCup));
                            }
                        }
                    });

                    doneButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (waterHaveToDrink != 0) {
                                waterCupDailyGoal.setVisibility(View.VISIBLE);
                                dailyWaterGoal_text_view.setVisibility(View.VISIBLE);
                                drinkWater.setText("drink");
                                firestore.collection("daily").
                                        document("week-of-" + monday.toString()).
                                        collection(today.toString()).
                                        document(theTempEmail).update("drink", "0");
                                bottomSheetDialog.dismiss();
                            } else {
                                Toast.makeText(WaterActivity.this, "You must choose a goal", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    bottomSheetDialog.setContentView(bottomSheetView);
                    bottomSheetDialog.show();
                } else {

                    waterHadDrink += waterHaveToDrink / numOfCup;

                    LocalDate today = LocalDate.now();
                    LocalDate monday = today.with(previousOrSame(MONDAY));

                    firestore.collection("daily").
                            document("week-of-" + monday.toString()).
                            collection(today.toString()).
                            document(theTempEmail).
                            update("drink", String.valueOf(waterHadDrink));

                    waterCountText.setText(String.valueOf(waterHadDrink));
                    float hasDrink = waterHadDrink, haveToDrink = waterHaveToDrink, ans = hasDrink / haveToDrink * 100;

                    waveView.setProgress((int) ans);
                    if (waterHadDrink <= waterHaveToDrink) {
                        String numOfCupHasToDrink = String.valueOf((waterHaveToDrink / 250) - (waterHadDrink / 250));
                        waterCupCountText.setText(numOfCupHasToDrink);
                    } else {
                        waterCupCountText.setText("0");
                    }
                }
            }
        });

        minusWaterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int tempWaterAmount = Integer.parseInt(waterCountText.getText().toString());
                if (tempWaterAmount > 0) {
                    if (tempWaterAmount == (waterHaveToDrink - (waterHaveToDrink - 250))) {
                        minusWaterButton.setVisibility(View.GONE);
                    }
                    waterHadDrink -= waterHaveToDrink / numOfCup;

                    LocalDate today = LocalDate.now();
                    LocalDate monday = today.with(previousOrSame(MONDAY));

                    firestore.collection("daily").
                            document("week-of-" + monday.toString()).
                            collection(today.toString()).
                            document(theTempEmail).
                            update("drink", String.valueOf(waterHadDrink));

                    waterCountText.setText(String.valueOf(waterHadDrink));
                    float hasDrink = waterHadDrink, haveToDrink = waterHaveToDrink, ans = hasDrink / haveToDrink * 100;

                    waveView.setProgress((int) ans);

                    if (waterHadDrink <= waterHaveToDrink) {
                        String numOfCupHasToDrink = String.valueOf((waterHaveToDrink / 250) - (waterHadDrink / 250));
                        waterCupCountText.setText(numOfCupHasToDrink);
                    } else {
                        waterCupCountText.setText("0");
                    }
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
                doneButton = (TextView) bottomSheetView.findViewById(R.id.close_button_water);

                progressBar.setMax(12000);
                progressBar.setProgress(numOfCup * 1000);
                numberOfCups_text_view.setText(String.valueOf(numOfCup));

                plusButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        numOfCup = Integer.parseInt(numberOfCups_text_view.getText().toString());
                        if (numOfCup == 11) {
                            plusButton.setVisibility(View.GONE);
                        }
                        if (numOfCup < 12) {
                            numOfCup += 1;
                            if (numOfCup == 5) {
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

                            float amountOfWater = Float.parseFloat(String.valueOf(numOfCup));
                            dailyWaterGoal_text_view.setText("of " + (amountOfWater * 250 / 1000) + "L goal");

                            //update value
                            waterHaveToDrink = numOfCup * 250;
                            firestore.collection("users").document(theTempEmail).
                                    update("drink_goal", String.valueOf(numOfCup));
                        }
                    }
                });

                minusButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        numOfCup = Integer.parseInt(numberOfCups_text_view.getText().toString());
                        if (numOfCup == 5) {
                            minusButton.setVisibility(View.GONE);
                        }
                        if (numOfCup > 4) {
                            numOfCup -= 1;
                            if (numOfCup == 11) {
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

                            float amountOfWater = Float.parseFloat(String.valueOf(numOfCup));
                            dailyWaterGoal_text_view.setText("of " + (amountOfWater * 250 / 1000) + "L goal");

                            //update value
                            waterHaveToDrink = numOfCup * 250;
                            firestore.collection("users").document(theTempEmail).
                                    update("drink_goal", String.valueOf(numOfCup));
                        }
                    }
                });

                doneButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        float hasDrink = waterHadDrink, haveToDrink = waterHaveToDrink, ans = hasDrink / haveToDrink * 100;
                        waveView.setProgress((int) ans);
                        if (waterHadDrink <= waterHaveToDrink) {
                            String numOfCupHasToDrink = String.valueOf((waterHaveToDrink / 250) - (waterHadDrink / 250));
                            waterCupCountText.setText(numOfCupHasToDrink);
                        } else {
                            waterCupCountText.setText("0");
                        }
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });

        backToPreviusWeek.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable backDataOfWeekRunnable = new Runnable() {
                    @Override
                    public void run() {
                        docRef = firestore.collection("users").document(theTempEmail);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null) {
                                        joinDate = document.getString("join_date");
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                        formatter = formatter.withLocale(Locale.ENGLISH);  // Locale specifies human language for translating, and cultural norms for lowercase/uppercase and abbreviations and such. Example: Locale.US or Locale.CANADA_FRENCH
                                        LocalDate theJoinDate = LocalDate.parse(joinDate, formatter);


                                        if (weekLabel.getText().toString().equals("This Week")) {
                                            LocalDate beginOfWeek = monday.minusDays(7);
                                            LocalDate endOfWeek = monday.minusDays(1);

                                            if (theJoinDate.isBefore(beginOfWeek) && theJoinDate.isBefore((endOfWeek))) {
                                                setUpWeekData(beginOfWeek, endOfWeek);

                                                beginDaySplit = monday.minusDays(7).toString().split("-");
                                                beginDay = beginDaySplit[2] + "/" + beginDaySplit[1];
                                                endDaySplit = monday.minusDays(1).toString().split("-");
                                                endDay = endDaySplit[2] + "/" + endDaySplit[1];

                                                weekLabel.setText(beginDay + " - " + endDay);
                                                nextToFollowingWeek.setVisibility(View.VISIBLE);
                                            } else {
                                                Toast.makeText(WaterActivity.this, "No Data", Toast.LENGTH_SHORT).show();
                                            }

                                        } else {
                                            LocalDate beginDayOfData = LocalDate.parse(
                                                    beginDaySplit[0] + "-" + beginDaySplit[1] + "-" + beginDaySplit[2], formatter);
                                            LocalDate endDayOfData = LocalDate.parse(
                                                    endDaySplit[0] + "-" + endDaySplit[1] + "-" + endDaySplit[2], formatter);
                                            if (theJoinDate.isBefore(beginDayOfData)) {
                                                setUpWeekData(beginDayOfData.minusDays(7), endDayOfData.minusDays(7));

                                                beginDaySplit = beginDayOfData.minusDays(7).toString().split("-");
                                                beginDay = beginDaySplit[2] + "/" + beginDaySplit[1];
                                                endDaySplit = endDayOfData.minusDays(7).toString().split("-");
                                                endDay = endDaySplit[2] + "/" + endDaySplit[1];

                                                weekLabel.setText(beginDay + " - " + endDay);
                                            } else {
                                                Toast.makeText(WaterActivity.this, "No Data", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }
                };
                Thread backDataOfWeekThread = new Thread(backDataOfWeekRunnable);
                backDataOfWeekThread.start();
            }
        });

        nextToFollowingWeek.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!weekLabel.getText().toString().equals("This Week")) {

                    String[] splitBeginAndEnd = weekLabel.getText().toString().split("-");
                    String[] beginDaySplit = splitBeginAndEnd[0].trim().split("/");
                    String[] endDaySplit = splitBeginAndEnd[1].trim().split("/");

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    formatter = formatter.withLocale(Locale.ENGLISH);

                    LocalDate beginDay = LocalDate.parse("2021-" + beginDaySplit[1] + "-" + beginDaySplit[0], formatter);
                    LocalDate endDay = LocalDate.parse("2021-" + endDaySplit[1] + "-" + endDaySplit[0], formatter);

                    if (beginDay.plusDays(7).isEqual(monday)) {
                        setUpWeekData(monday, today);
                        weekLabel.setText("This Week");
                        nextToFollowingWeek.setVisibility(View.INVISIBLE);
                    } else {
                        setUpWeekData(beginDay.plusDays(7), endDay.plusDays(7));

                        String[] plusBeginDaySplit = beginDay.plusDays(7).toString().split("-");
                        String plusBeginDay = plusBeginDaySplit[2] + "/" + plusBeginDaySplit[1];
                        String[] plusEndDaySplit = endDay.plusDays(7).toString().split("-");
                        String plusEndDay = plusEndDaySplit[2] + "/" + plusEndDaySplit[1];

                        weekLabel.setText(plusBeginDay + " - " + plusEndDay);
                    }
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUpWeekData(LocalDate thisWeekMonday, LocalDate thisWeekToday) {
        daylist = new ArrayList<String>();
        dataValue = new ArrayList<BarEntry>();
        daylist.add("Mon");
        daylist.add("Tue");
        daylist.add("Wed");
        daylist.add("Thur");
        daylist.add("Fri");
        daylist.add("Sat");
        daylist.add("Sun");

        SharedPreferences sharedPreferences = getSharedPreferences(tempEmail, MODE_PRIVATE);
        String theTempEmail = sharedPreferences.getString("Email", "");

        firestore = FirebaseFirestore.getInstance();

        ArrayList<String> tempDrink = new ArrayList<>();
        ArrayList<BarDataSet> chartBarDataSetList = new ArrayList<>();
        ArrayList<String> dayInWeek = new ArrayList<String>();
        ArrayList<String> dateInWeek = new ArrayList<String>();
        ArrayList<String> dayThatHasValue = new ArrayList<String>();
        ArrayList<String> realDrinkValue = new ArrayList<String>();

        Runnable getWaterListPerDay = new Runnable() {
            @Override
            public void run() {
                long diffInDays = ChronoUnit.DAYS.between(thisWeekMonday, thisWeekToday);

                tempDate = thisWeekMonday;
                for (i = 0; i <= diffInDays; i++) {
                    dateInWeek.add(tempDate.toString());
                    realDrinkValue.add("0");

                    firestore.collection("daily").
                            document("week-of-" + thisWeekMonday.toString()).
                            collection(tempDate.toString())
                            .whereEqualTo("userEmail", theTempEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String drink = document.getString("drink");
                                    String dayOnFirebase = document.getString("datetime");
                                    dayThatHasValue.add(dayOnFirebase);
                                    if (drink.equals("empty")) {
                                        tempDrink.add("0");
                                    } else {
                                        tempDrink.add(drink);
                                    }

                                    assert dayOnFirebase != null;
                                    if (dayOnFirebase.equals(thisWeekToday.toString())) {
                                        final Handler handler = new Handler(Looper.getMainLooper());
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                for (int j = 0; j < dateInWeek.size(); j++) {
                                                    for (int z = 0; z < dayThatHasValue.size(); z++) {
                                                        if (dateInWeek.get(j).equals(dayThatHasValue.get(z))) {
                                                            realDrinkValue.set(j, tempDrink.get(z));
                                                        }
                                                    }
                                                }

                                                for (int k = 0; k < realDrinkValue.size(); k++) {
                                                    dataValue.add(new BarEntry(k, Float.parseFloat(realDrinkValue.get(k))));
                                                    dayInWeek.add(daylist.get(k));
                                                }

                                                int allDayContain = dayInWeek.size();

                                                if (allDayContain < 7) {
                                                    while (allDayContain < 7) {
                                                        dataValue.add(new BarEntry(allDayContain, 0));
                                                        dayInWeek.add(daylist.get(allDayContain));
                                                        allDayContain++;
                                                    }
                                                }

                                                //initialize testadapter
                                                adapter = new BarChartAdapter(dataValue, dayInWeek);
                                                //setting adapter on to the viewpager2
                                                verticalViewPager2.setUserInputEnabled(false);
                                                verticalViewPager2.setAdapter(adapter);
                                            }
                                        }, 100);
                                    }
                                }
                            } else {

                            }
                        }
                    });
                    tempDate = tempDate.plusDays(1);
                }

            }
        };

        Thread getDailyDrinkThread = new Thread(getWaterListPerDay);
        getDailyDrinkThread.start();
    }
}