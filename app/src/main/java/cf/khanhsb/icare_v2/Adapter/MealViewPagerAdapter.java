package cf.khanhsb.icare_v2.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cf.khanhsb.icare_v2.Model.MealPlanData;
import cf.khanhsb.icare_v2.R;

public class MealViewPagerAdapter extends RecyclerView.Adapter {
    //initialize variable
    private MealPlanData mealPlanData;


    //create constructor
    public MealViewPagerAdapter(MealPlanData mealPlanData){
        this.mealPlanData = mealPlanData;
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
        viewHolderClass.titles.setText(mealPlanData.getTitles().get(position));
        viewHolderClass.detailOfMeal.setText(mealPlanData.getDetailOfMeal().get(position));
        viewHolderClass.imageView.setImageResource(mealPlanData.getIcons().get(position));
        viewHolderClass.backgroundView.setBackground(mealPlanData.getBackgrounds().get(position));

    }

    @Override
    public int getItemCount() {
        return mealPlanData.getTitles().size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {

        //initialize variable in the item container
        ImageView imageView;
        TextView titles,detailOfMeal;
        LinearLayout backgroundView;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.meal_icon);
            backgroundView = itemView.findViewById(R.id.meal_detail_background_view);
            titles = itemView.findViewById(R.id.meal_title);
            detailOfMeal = itemView.findViewById(R.id.meal_detail);
        }
    }
}