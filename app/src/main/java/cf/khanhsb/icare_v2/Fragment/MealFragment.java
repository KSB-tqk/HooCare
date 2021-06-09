package cf.khanhsb.icare_v2.Fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cf.khanhsb.icare_v2.Adapter.MealViewPagerAdapter;
import cf.khanhsb.icare_v2.Model.MealPlanData;
import cf.khanhsb.icare_v2.R;

public class MealFragment extends Fragment {

    //initialize variable for recyclerview
    private RecyclerView recyclerView;
    private ArrayList<String> titles,detailOfMeals,kcalOfMeal;
    private ArrayList<Integer> icons;
    private ArrayList<Drawable> backgroundView;
    private MealViewPagerAdapter adapter;
    private MealPlanData mealPlanData;

    public MealFragment() {
        // Required empty public constructor
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

        mealPlanData = new MealPlanData(titles,detailOfMeals,kcalOfMeal,icons,backgroundView);

        /**setting up the recyclerview*/
        recyclerView = rootview.findViewById(R.id.meal_plan_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL,false));
        adapter = new MealViewPagerAdapter(mealPlanData);
        recyclerView.setAdapter(adapter);

        return rootview;
    }
}