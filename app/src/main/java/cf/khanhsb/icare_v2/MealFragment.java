package cf.khanhsb.icare_v2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class MealFragment extends Fragment {

    //initialize variable for recyclerview
    private RecyclerView recyclerView;
    private ArrayList<String> titles,detailOfMeals,kcalOfMeal;
    private MealViewPagerAdapter adapter;

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
        titles.add("BreakFast");
        titles.add("Lunch");
        titles.add("Dinner");
        titles.add("Snake");
        detailOfMeals.add("Bread, \nDimsum, \nNoodle");
        detailOfMeals.add("Rice, \nMeat, \nEgg");
        detailOfMeals.add("Rice, \nMeat, \nVegetable");
        detailOfMeals.add("Sushi, \nEnergy Drink, \nGrape");
        kcalOfMeal.add("525");
        kcalOfMeal.add("405");
        kcalOfMeal.add("615");
        kcalOfMeal.add("375");

        /**setting up the recyclerview*/
        recyclerView = rootview.findViewById(R.id.meal_plan_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL,false));
        adapter = new MealViewPagerAdapter(titles,detailOfMeals,kcalOfMeal);
        recyclerView.setAdapter(adapter);

        return rootview;
    }
}