package cf.khanhsb.icare_v2.Adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.shawnlin.numberpicker.NumberPicker;

import cf.khanhsb.icare_v2.R;

public class StepCountViewPagerAdapter extends RecyclerView.Adapter<StepCountViewPagerAdapter.ViewHolder>{

    private String step_goal;
    private String userEmail;
    private FirebaseFirestore firestore;

    public StepCountViewPagerAdapter(String step_goal,String userEmail) {
        this.step_goal = step_goal;
        this.userEmail = userEmail;
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
                        setUpBa(holder,true);
                        break;
                    case "5000":
                        setUpKf(holder,true);
                        break;
                    case "8000":
                        setUpBm(holder,true);
                        break;
                    case "15000":
                        setUpLw(holder,true);
                        break;
                }
            }

            holder.baConstaint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setUpBa(holder,true);
                    setUpBm(holder,false);
                    setUpLw(holder,false);
                    setUpKf(holder,false);
                    setUserStepGoalFirebase(userEmail,"2500");
                }
            });

            holder.bmConstaint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setUpBa(holder,false);
                    setUpBm(holder,true);
                    setUpLw(holder,false);
                    setUpKf(holder,false);
                    setUserStepGoalFirebase(userEmail,"8000");
                }
            });

            holder.kfConstraint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setUpBa(holder,false);
                    setUpBm(holder,false);
                    setUpLw(holder,false);
                    setUpKf(holder,true);
                    setUserStepGoalFirebase(userEmail,"5000");
                }
            });

            holder.lwConstaint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setUpBa(holder,false);
                    setUpBm(holder,false);
                    setUpLw(holder,true);
                    setUpKf(holder,false);
                    setUserStepGoalFirebase(userEmail,"15000");
                }
            });
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
            holder.numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    setUserStepGoalFirebase(userEmail,String.valueOf((newVal+1)*500));
                }
            });
        }

    }

    private void setUpBa(ViewHolder holder, boolean goalChecked) {
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
            holder.baNum.setTextColor(Color.parseColor("#2FA678"));
            holder.baTitle.setTextColor(Color.BLACK);
            Drawable constaintDrawable = holder.baConstaint.getBackground();
            constaintDrawable = DrawableCompat.wrap(constaintDrawable);
            DrawableCompat.setTint(constaintDrawable,Color.parseColor("#F3F2F2"));
            holder.baImage.setVisibility(View.GONE);
        }
    }
    private void setUpKf(ViewHolder holder, boolean goalChecked) {
        if(goalChecked) {
            holder.kfText.setTextColor(Color.WHITE);
            holder.kfNum.setTextColor(Color.WHITE);
            holder.kfTitle.setTextColor(Color.WHITE);
            holder.kfImage.setVisibility(View.VISIBLE);
            Drawable constaintDrawable = holder.kfConstraint.getBackground();
            constaintDrawable = DrawableCompat.wrap(constaintDrawable);
            DrawableCompat.setTint(constaintDrawable,Color.parseColor("#58C892"));
        }
        else {
            holder.kfText.setTextColor(Color.BLACK);
            holder.kfNum.setTextColor(Color.parseColor("#2FA678"));
            holder.kfTitle.setTextColor(Color.BLACK);
            Drawable constaintDrawable = holder.kfConstraint.getBackground();
            constaintDrawable = DrawableCompat.wrap(constaintDrawable);
            DrawableCompat.setTint(constaintDrawable,Color.parseColor("#F3F2F2"));
            holder.kfImage.setVisibility(View.GONE);
        }
    }
    private void setUpBm(ViewHolder holder, boolean goalChecked) {
        if(goalChecked) {
            holder.bmText.setTextColor(Color.WHITE);
            holder.bmNum.setTextColor(Color.WHITE);
            holder.bmTitle.setTextColor(Color.WHITE);
            holder.bmImage.setVisibility(View.VISIBLE);
            Drawable constaintDrawable = holder.bmConstaint.getBackground();
            constaintDrawable = DrawableCompat.wrap(constaintDrawable);
            DrawableCompat.setTint(constaintDrawable,Color.parseColor("#58C892"));
        }
        else {
            holder.bmText.setTextColor(Color.BLACK);
            holder.bmNum.setTextColor(Color.parseColor("#2FA678"));
            holder.bmTitle.setTextColor(Color.BLACK);
            Drawable constaintDrawable = holder.bmConstaint.getBackground();
            constaintDrawable = DrawableCompat.wrap(constaintDrawable);
            DrawableCompat.setTint(constaintDrawable,Color.parseColor("#F3F2F2"));
            holder.bmImage.setVisibility(View.GONE);
        }
    }
    private void setUpLw(ViewHolder holder, boolean goalChecked) {
        if(goalChecked) {
            holder.lwText.setTextColor(Color.WHITE);
            holder.lwNum.setTextColor(Color.WHITE);
            holder.lwTitle.setTextColor(Color.WHITE);
            holder.lwImage.setVisibility(View.VISIBLE);
            Drawable constaintDrawable = holder.lwConstaint.getBackground();
            constaintDrawable = DrawableCompat.wrap(constaintDrawable);
            DrawableCompat.setTint(constaintDrawable,Color.parseColor("#58C892"));
        }
        else {
            holder.lwText.setTextColor(Color.BLACK);
            holder.lwNum.setTextColor(Color.parseColor("#2FA678"));
            holder.lwTitle.setTextColor(Color.BLACK);
            Drawable constaintDrawable = holder.lwConstaint.getBackground();
            constaintDrawable = DrawableCompat.wrap(constaintDrawable);
            DrawableCompat.setTint(constaintDrawable,Color.parseColor("#F3F2F2"));
            holder.lwImage.setVisibility(View.GONE);
        }
    }

    private void setUserStepGoalFirebase(String userEmail,String newStepGoal) {
        firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(userEmail).update("step_goal",newStepGoal);
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
