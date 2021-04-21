package cf.khanhsb.icare_v2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MealViewPagerAdapter extends RecyclerView.Adapter {
    //initialize variable
    private ArrayList<String> mealItemTitles,detailOfMealText,numberOfKcalText;
    private ArrayList<Integer> images;


    //create constructor
    public MealViewPagerAdapter(ArrayList<String> mealsTitles,
                                ArrayList<String> detailOfMealText,
                                ArrayList<String> numberOfKcalText){
        this.mealItemTitles = mealsTitles;
        this.detailOfMealText = detailOfMealText;
        this.numberOfKcalText = numberOfKcalText;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //initialize view
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.fragment_meal_item_of_viewpager,parent,false);
        ViewHolderClass viewHolderClass = new ViewHolderClass(view);
        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderClass viewHolderClass = (ViewHolderClass) holder;
        //error
        viewHolderClass.titles.setText(mealItemTitles.get(position));
        viewHolderClass.detailOfMeal.setText(detailOfMealText.get(position));
        viewHolderClass.numberOfKcal.setText(numberOfKcalText.get(position));

    }

    @Override
    public int getItemCount() {
        return mealItemTitles.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {

        //initialize variable in the item container
        ImageView imageView;
        TextView titles,numberOfKcal,detailOfMeal;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.meal_icon);
            titles = itemView.findViewById(R.id.meal_title);
            detailOfMeal = itemView.findViewById(R.id.meal_detail);
            numberOfKcal = itemView.findViewById(R.id.numberOfKcal_MealFrag_ViewPager);
        }
    }
}
