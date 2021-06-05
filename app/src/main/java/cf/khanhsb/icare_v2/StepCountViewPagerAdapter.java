package cf.khanhsb.icare_v2;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.shawnlin.numberpicker.NumberPicker;

import pl.droidsonroids.gif.GifImageView;

public class StepCountViewPagerAdapter extends RecyclerView.Adapter<StepCountViewPagerAdapter.ViewHolder>{

    private String step_goal;
    public StepCountViewPagerAdapter(String step_goal) {
        this.step_goal = step_goal;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_step_count_setupgoal_viewpager,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position == 0){
            holder.numberPicker.setVisibility(View.GONE);
            holder.linearPicker.setVisibility(View.VISIBLE);

            if(!step_goal.equals("empty")) {
                switch (step_goal) {
                    case "2500":
                        setUpBA(holder,true);

                }
            }
        }
        else{
            holder.numberPicker.setVisibility(View.VISIBLE);
            holder.linearPicker.setVisibility(View.GONE);
            String[] stepPickerData = new String[40];
            for (int i = 0;i < stepPickerData.length;i ++){
                stepPickerData[i] = String.valueOf(1000+(500*i));
            }
            holder.numberPicker.setMinValue(1);
            holder.numberPicker.setMaxValue(stepPickerData.length);
            holder.numberPicker.setDisplayedValues(stepPickerData);
            holder.numberPicker.setValue(5000);
        }

    }

    @SuppressLint("ResourceAsColor")
    private void setUpBA(ViewHolder holder, boolean goalChecked) {
        if(goalChecked) {
            holder.baText.setTextColor(Color.WHITE);
            holder.baNum.setTextColor(Color.WHITE);
            holder.baTitle.setTextColor(Color.WHITE);
            holder.baImage.setVisibility(View.VISIBLE);
            Drawable constaintDrawable = holder.baConstaint.getBackground();
            constaintDrawable = DrawableCompat.wrap(constaintDrawable);
            DrawableCompat.setTint(constaintDrawable,Color.parseColor("#58C892"));
        }
        else {
            holder.baText.setTextColor(Color.BLACK);
            holder.baNum.setTextColor(R.color.lime_100);
            holder.baTitle.setTextColor(Color.BLACK);
            Drawable constaintDrawable = holder.baConstaint.getBackground();
            constaintDrawable = DrawableCompat.wrap(constaintDrawable);
            DrawableCompat.setTint(constaintDrawable,Color.parseColor("#F3F2F2"));
            holder.baImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //initialize variable
        NumberPicker numberPicker;
        LinearLayout linearPicker;
        ConstraintLayout baConstaint,kfConstraint,bmConstaint,lwConstaint;
        TextView baTitle,kfTitle,bmTitle,lwTitle
                ,baNum,kfNum,bmNum,lwNum
                ,baText,kfText,bmText,lwText;
        ImageView baImage,kfImage,bmImage,lwImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearPicker = (LinearLayout) itemView.findViewById(R.id.linear_picker_stepcount);
            numberPicker = (NumberPicker) itemView.findViewById(R.id.number_picker_stepcount);

            baConstaint = (ConstraintLayout) itemView.findViewById(R.id.ba_cons_background);
            kfConstraint = (ConstraintLayout) itemView.findViewById(R.id.kf_cons_background);
            bmConstaint = (ConstraintLayout) itemView.findViewById(R.id.bm_cons_background);
            lwConstaint = (ConstraintLayout) itemView.findViewById(R.id.lw_cons_background);

            baTitle = (TextView) itemView.findViewById(R.id.ba_title);
            baNum = (TextView) itemView.findViewById(R.id.ba_number);
            baText = (TextView) itemView.findViewById(R.id.ba_text);

            kfTitle = (TextView) itemView.findViewById(R.id.kf_title);
            kfNum = (TextView) itemView.findViewById(R.id.kf_number);
            kfText = (TextView) itemView.findViewById(R.id.kf_text);

            bmTitle = (TextView) itemView.findViewById(R.id.bm_title);
            bmNum = (TextView) itemView.findViewById(R.id.bm_number);
            bmText = (TextView) itemView.findViewById(R.id.bm_text);

            lwTitle = (TextView) itemView.findViewById(R.id.lw_title);
            lwNum = (TextView) itemView.findViewById(R.id.lw_number);
            lwText   = (TextView) itemView.findViewById(R.id.lw_text);

            baImage = (ImageView) itemView.findViewById(R.id.ba_image);
            kfImage = (ImageView) itemView.findViewById(R.id.kf_image);
            bmImage = (ImageView) itemView.findViewById(R.id.bm_image);
            lwImage = (ImageView) itemView.findViewById(R.id.lw_image);
        }
    }
}
