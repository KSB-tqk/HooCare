package cf.khanhsb.icare_v2.Fragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

import cf.khanhsb.icare_v2.Adapter.MealViewPagerAdapter;
import cf.khanhsb.icare_v2.MainActivity;
import cf.khanhsb.icare_v2.Model.MealPlanData;
import cf.khanhsb.icare_v2.R;

import static android.content.Context.MODE_PRIVATE;

public class MealFragment extends Fragment {

    //initialize variable for recyclerview
    private RecyclerView recyclerView;
    private ArrayList<String> titles, detailOfMeals, kcalOfMeal;
    private ArrayList<Integer> icons;
    private ArrayList<Drawable> backgroundView;
    private MealViewPagerAdapter adapter;
    private MealPlanData mealPlanData;
    private TextView weightTextView, heightTextView, setUpBMIButton, bmiTextView, bodyFatData;
    private static final String tempEmail = "tempEmail";
    private FirebaseFirestore firestore;
    private DocumentReference docRef;
    private ConstraintLayout setUPContraint;
    private RelativeLayout bmiRelative;
    private Thread backgroundThread;

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

        SharedPreferences sharedPreferences = this.getActivity().
                getSharedPreferences(tempEmail, MODE_PRIVATE);
        String theTempEmail = sharedPreferences.getString("Email", "");

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
                                    if(splitString[1].equals("No Data")){
                                        bodyFatData.setTextSize(13);
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
                ((MainActivity) getActivity()).fillForm(Gravity.BOTTOM);
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
            if(tempGender.equals("male")){
                bodyfat = ((1.2f * ans) + (0.23f * age)) - 16.2f;
                bodyFatAns = String.format("%.0f", bodyfat) + "%";
            } else if(tempGender.equals("female")){
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