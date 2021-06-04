package cf.khanhsb.icare_v2;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;
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

    @Override
    public int getItemCount() {
        return 2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //initialize variable
        NumberPicker numberPicker;
        LinearLayout linearPicker;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearPicker = (LinearLayout) itemView.findViewById(R.id.linear_picker_stepcount);
            numberPicker = (NumberPicker) itemView.findViewById(R.id.number_picker_stepcount);
        }
    }
}
