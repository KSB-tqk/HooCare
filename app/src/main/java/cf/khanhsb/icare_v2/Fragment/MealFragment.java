package cf.khanhsb.icare_v2.Fragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

import cf.khanhsb.icare_v2.Adapter.MealViewPagerAdapter;
import cf.khanhsb.icare_v2.MainActivity;
import cf.khanhsb.icare_v2.Model.MealPlanData;
import cf.khanhsb.icare_v2.R;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.Integer.valueOf;
import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

public class MealFragment extends Fragment {

    //initialize variable for recyclerview
    private RecyclerView recyclerView;
    private ArrayList<String> titles, detailOfMeals, kcalOfMeal;
    private ArrayList<Integer> icons;
    private ArrayList<Drawable> backgroundView;
    private MealViewPagerAdapter adapter;
    private MealPlanData mealPlanData;
    private TextView weightTextView, heightTextView, setUpBMIButton, bmiTextView,
            bodyFatData, eatenKCal, burnedKcal, progressbarStatus, bmiTitle;

    private static final String tempEmail = "tempEmail";
    private FirebaseFirestore firestore;
    private DocumentReference docRef;
    private ConstraintLayout setUPContraint;
    private RelativeLayout bmiRelative;
    private ProgressBar remainKcalReal, remainKcalFake;

    ///MealInputVariables
    private EditText mCarbsBreakfast, mProteinBreakfast, mFatBreakfast,
            mCarbsLunch, mProteinLunch, mFatLunch,
            mCarbsSnack, mProteinSnack, mFatSnack,
            mCarbsDinner,mProteinDinner,mFatDinner;
    private Button mOpenMealInput,mConsumed;
    /////
    private Thread backgroundThread;
    private ConstraintLayout expandableView;
    private CardView mealCardview;
    private int eaten;
    public MealFragment() {
        // Required empty public constructor
    }

    public void callParentMethod(){
        getActivity().onBackPressed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_meal, container, false);

        weightTextView = rootview.findViewById(R.id.weight_detail);
        heightTextView = rootview.findViewById(R.id.body_height_data);
        setUPContraint = rootview.findViewById(R.id.setup_bmi_constraint);
        bmiRelative = rootview.findViewById(R.id.bmi_relative);
        setUpBMIButton = rootview.findViewById(R.id.setup_bmi_button);
        bmiTextView = rootview.findViewById(R.id.BMI_data);
        bodyFatData = rootview.findViewById(R.id.body_fat_data);
        mOpenMealInput = rootview.findViewById(R.id.btOpenMealInput);
        eatenKCal = rootview.findViewById(R.id.eaten_kcal_number_view);
        mealCardview = rootview.findViewById(R.id.MealCardView);
        expandableView = rootview.findViewById(R.id.inputmeallayout);
        burnedKcal = rootview.findViewById(R.id.burned_kcal_number);
        progressbarStatus = rootview.findViewById(R.id.progressbar_status);
        remainKcalFake = rootview.findViewById(R.id.fake_progressba_meal_frag);
        remainKcalReal = rootview.findViewById(R.id.progressbar_meal_frag);
        ////Meal Input
        mCarbsBreakfast = rootview.findViewById(R.id.et_carbs_breakfast);
        mCarbsLunch = rootview.findViewById(R.id.et_carbs_lunch);
        mCarbsSnack = rootview.findViewById(R.id.et_carbs_snack);
        mCarbsDinner = rootview.findViewById(R.id.et_carbs_dinner);
        mProteinBreakfast = rootview.findViewById(R.id.et_protein_breakfast);
        mProteinLunch = rootview.findViewById(R.id.et_protein_lunch);
        mProteinSnack = rootview.findViewById(R.id.et_protein_snack);
        mProteinDinner = rootview.findViewById(R.id.et_protein_dinner);
        mFatBreakfast = rootview.findViewById(R.id.et_fat_breakfast);
        mFatLunch = rootview.findViewById(R.id.et_fat_lunch);
        mFatSnack = rootview.findViewById(R.id.et_fat_snack);
        mFatDinner = rootview.findViewById(R.id.et_fat_dinner);
        mConsumed = rootview.findViewById(R.id.btConsume);
        ///
        bmiTitle = rootview.findViewById(R.id.BMI_title);

        mConsumed.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (expandableView.getVisibility()==View.GONE){
                    TransitionManager.beginDelayedTransition(mealCardview, new AutoTransition());
                    expandableView.setVisibility(View.VISIBLE);

                }else{
                    TransitionManager.beginDelayedTransition(mealCardview, new AutoTransition());
                    expandableView.setVisibility(View.GONE);
                }
            }
        });
        SharedPreferences sharedPreferences = this.getActivity().
                getSharedPreferences(tempEmail, MODE_PRIVATE);
        String theTempEmail = sharedPreferences.getString("Email", "");
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(previousOrSame(MONDAY));
        Runnable setUpDietRunnable = new Runnable() {
            @Override
            public void run() {
               firestore = FirebaseFirestore.getInstance();

                docRef = firestore.collection("daily").
                        document("week-of-" + monday.toString()).
                        collection(today.toString()).
                        document(theTempEmail);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document != null){
                                String kcal = document.getString("diet");
                                eatenKCal.setText(String.valueOf(kcal));
                                try {
                                    eaten = Integer.parseInt(String.valueOf(eatenKCal));
                                }catch (NumberFormatException e1){
                                    eaten = 0;
                                }

                            }
                        }
                    }
                });
            }
        };
        Thread setUpDiet = new Thread(setUpDietRunnable);
        setUpDiet.start();
        ////

        Runnable getBurnedKcalFromDB = new Runnable() {
            @Override
            public void run() {
                firestore = FirebaseFirestore.getInstance();
                docRef = firestore.collection("workoutHistory").document(theTempEmail);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document != null){
                                String burned = document.getString("workoutKcal");
                                String date = document.getString("workoutDay");
                                String todaystring = today.toString();
                                if (date == todaystring){
                                    burnedKcal.setText(String.valueOf(burned));
                                }else{
                                    burnedKcal.setText("0");
                                }

                            }else{
                                burnedKcal.setText("0");
                            }
                        }
                    }
                });
            }
        };
        Thread getBurned = new Thread(getBurnedKcalFromDB);
        getBurned.start();

        Runnable setUpBMIRunnable = new Runnable() {
            @Override
            public void run() {
                firestore = FirebaseFirestore.getInstance();
                docRef = firestore.collection("users").document(theTempEmail);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                String tempWeight = document.getString("weight");
                                String tempHeight = document.getString("height");
                                String tempGender = document.getString("gender");
                                String tempDate = document.getString("date_of_birth");

                                SharedPreferences.Editor editor;
                                editor = sharedPreferences.edit();
                                editor.putString("Weight", tempWeight);
                                editor.apply();

                                assert tempWeight != null;
                                if (!tempWeight.equals("empty")) {

                                    String bmiData = getBMI_And_getBodyFat(tempHeight, tempWeight , tempGender,tempDate);
                                    weightTextView.setText(tempWeight);
                                    heightTextView.setText(tempHeight + " cm");
                                    String[] splitString = bmiData.split("-");
                                    bmiTextView.setText(splitString[0] + " BMI");
                                    bodyFatData.setText(splitString[1]);

                                    float tempBmiStatus = Float.parseFloat(splitString[0]);
                                    if(tempBmiStatus < 18.5f){
                                        bmiTitle.setText("Underweight");
                                    } else if(tempBmiStatus >= 18.5f && tempBmiStatus < 24.9f) {
                                        bmiTitle.setText("Normal");
                                    } else if(tempBmiStatus >= 24.9f && tempBmiStatus < 29.9f) {
                                        bmiTitle.setText("Overweight");
                                    } else if(tempBmiStatus > 29.9f && tempBmiStatus < 34.9f) {
                                        bmiTitle.setText("Obese");
                                    } else {
                                        bmiTitle.setText("Extremely Obese");
                                    }

                                    if(splitString[1].equals("No Data")){
                                        bodyFatData.setTextSize(13);
                                        heightTextView.setTextSize(13);
                                        bmiTextView.setTextSize(13);

                                    }
                                    bmiRelative.setVisibility(View.VISIBLE);
                                    setUPContraint.setVisibility(View.GONE);
                                } else {
                                    bmiRelative.setVisibility(View.GONE);
                                    setUPContraint.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                });
            }
        };

        backgroundThread = new Thread(setUpBMIRunnable);
        backgroundThread.start();

        setUpBMIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).fillForm(Gravity.CENTER,1);
            }
        });
        mConsumed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String carb_breakfast = mCarbsBreakfast.getText().toString();
                String carb_lunch = mCarbsLunch.getText().toString();
                String carb_snack = mCarbsSnack.getText().toString();
                String carb_dinner = mCarbsDinner.getText().toString();
                String protein_breakfast = mProteinBreakfast.getText().toString();
                String protein_lunch = mProteinLunch.getText().toString();
                String protein_snack = mProteinSnack.getText().toString();
                String protein_dinner = mProteinDinner.getText().toString();
                String fat_breakfast = mFatBreakfast.getText().toString();
                String fat_lunch = mFatLunch.getText().toString();
                String fat_snack = mFatSnack.getText().toString();
                String fat_dinner = mFatDinner.getText().toString();
                String burn_string = burnedKcal.getText().toString();
                if(carb_breakfast.isEmpty()){carb_breakfast="0";}
                if(carb_lunch.isEmpty()){carb_lunch="0";}
                if(carb_snack.isEmpty()){carb_snack="0";}
                if(carb_dinner.isEmpty()){carb_dinner="0";}
                if(protein_breakfast.isEmpty()){protein_breakfast="0";}
                if(protein_lunch.isEmpty()){protein_lunch="0";}
                if(protein_snack.isEmpty()){protein_snack="0";}
                if(protein_dinner.isEmpty()){protein_dinner="0";}
                if(fat_breakfast.isEmpty()){fat_breakfast="0";}
                if(fat_lunch.isEmpty()){fat_lunch="0";}
                if(fat_snack.isEmpty()){fat_snack="0";}
                if(fat_dinner.isEmpty()){fat_dinner="0";}
                if (burn_string==null){burn_string="0";}
                int finalcarb1,finalcarb2,finalcarb3,finalcarb4,
                        finalprotein1,finalprotein2,finalprotein3,finalprotein4,
                        finalfat1,finalfat2,finalfat3,finalfat4,breakfast,lunch,snack,dinner,burned1,
                        sumary;
                try {
                    finalcarb1 = Integer.parseInt(carb_breakfast);
                    finalcarb2 = Integer.parseInt(carb_lunch);
                    finalcarb3 = Integer.parseInt(carb_snack);
                    finalcarb4 = Integer.parseInt(carb_dinner);
                    finalprotein1 = Integer.parseInt(protein_breakfast);
                    finalprotein2 = Integer.parseInt(protein_lunch);
                    finalprotein3 = Integer.parseInt(protein_snack);
                    finalprotein4 = Integer.parseInt(protein_dinner);
                    finalfat1 = Integer.parseInt(fat_breakfast);
                    finalfat2 = Integer.parseInt(fat_lunch);
                    finalfat3 = Integer.parseInt(fat_snack);
                    finalfat4 = Integer.parseInt(fat_dinner);
                    burned1 = Integer.parseInt(burn_string);

                }catch (NumberFormatException e){
                    finalcarb1=0;
                    finalcarb2=0;
                    finalcarb3=0;
                    finalcarb4=0;
                    finalprotein1=0;
                    finalprotein2=0;
                    finalprotein3=0;
                    finalprotein4=0;
                    finalfat1=0;
                    finalfat2=0;
                    finalfat3=0;
                    finalfat4=0;
                    burned1=0;
                }
                breakfast = finalcarb1*4 + finalprotein1*4 + finalfat1*9 ;
                lunch = finalcarb2*4 + finalprotein2*4 + finalfat2*9 ;
                snack = finalcarb3*4 + finalprotein3*4 + finalfat3*9 ;
                dinner = finalcarb4*4 + finalprotein4*4 + finalfat4*9 ;
                sumary = breakfast + lunch + snack + dinner;
                eaten = eaten + sumary;
                eatenKCal.setText(String.valueOf(eaten - burned1));
                firestore.collection("daily").
                        document("week-of-" + monday.toString()).
                        collection(today.toString()).
                        document(theTempEmail).update("diet", String.valueOf(eaten));
                TransitionManager.beginDelayedTransition(mealCardview, new AutoTransition());
                expandableView.setVisibility(View.GONE);

            }
        });


        //set up test data for recyclerview
        titles = new ArrayList<String>();
        detailOfMeals = new ArrayList<String>();
        kcalOfMeal = new ArrayList<String>();
        icons = new ArrayList<Integer>();
        backgroundView = new ArrayList<Drawable>();
        titles.add("BreakFast");
        titles.add("Lunch");
        titles.add("Snake");
        titles.add("Dinner");
        detailOfMeals.add("Bread, \nDimsum, \nNoodle");
        detailOfMeals.add("Rice, \nMeat, \nEgg");
        detailOfMeals.add("Rice, \nMeat, \nVegetable");
        detailOfMeals.add("Sushi, \nEnergy Drink, \nGrape");
        kcalOfMeal.add("525");
        kcalOfMeal.add("405");
        kcalOfMeal.add("615");
        kcalOfMeal.add("375");
        icons.add(R.drawable.breakfast_meal_icon);
        icons.add(R.drawable.lucnch_meal_icon);
        icons.add(R.drawable.snake_meal_icon);
        icons.add(R.drawable.dinner_meal_icon);
        backgroundView.add(getResources().getDrawable(R.drawable.meal_plan_detail_background_image));
        backgroundView.add(getResources().getDrawable(R.drawable.meal_plan_detail_background_lunch));
        backgroundView.add(getResources().getDrawable(R.drawable.meal_plan_detail_background_dinner));
        backgroundView.add(getResources().getDrawable(R.drawable.meal_plan_detail_background_snake));

        mealPlanData = new MealPlanData(titles, detailOfMeals, kcalOfMeal, icons, backgroundView);

        /**setting up the recyclerview*/
        recyclerView = rootview.findViewById(R.id.meal_plan_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));
        adapter = new MealViewPagerAdapter(mealPlanData);
        recyclerView.setAdapter(adapter);


        return rootview;
    }

    @SuppressLint("DefaultLocale")
    private String getBMI_And_getBodyFat(String tempHeight, String tempWeight,String tempGender,String tempDate) {
        String bmi,bodyFatAns;
        float height = Float.parseFloat(tempHeight) / 100;
        float weight = Float.parseFloat(tempWeight);
        float ans = weight / (height * height),bodyfat;

        bodyFatAns = "No Data";
        if(!tempGender.equals("empty")){
            String[] realDate = tempDate.split("/");
            float year = (float) Calendar.getInstance().get(Calendar.YEAR);
            float age = year - Float.parseFloat(realDate[2]);
            if(tempGender.equals("Male")){
                bodyfat = ((1.2f * ans) + (0.23f * age)) - 16.2f;
                bodyFatAns = String.format("%.0f", bodyfat) + "%";
            } else if(tempGender.equals("Female")){
                bodyfat = ((1.2f * ans) + (0.23f * age)) - 5.4f;
                bodyFatAns = String.format("%.0f", bodyfat) + "%";
            }
        }
        bmi = String.format("%.1f", ans);
        return bmi+"-"+bodyFatAns;
    }

    public void SetUpWeight() {
        backgroundThread.start();
    }
}