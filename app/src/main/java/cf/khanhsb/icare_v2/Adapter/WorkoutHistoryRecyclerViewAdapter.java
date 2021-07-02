package cf.khanhsb.icare_v2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cf.khanhsb.icare_v2.Model.ExerciseHistoryItem;
import cf.khanhsb.icare_v2.R;

public class WorkoutHistoryRecyclerViewAdapter extends RecyclerView.Adapter<WorkoutHistoryRecyclerViewAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<ExerciseHistoryItem> workoutItem;

    public WorkoutHistoryRecyclerViewAdapter(Context mContext, ArrayList<ExerciseHistoryItem> workoutItem) {
        this.mContext = mContext;
        this.workoutItem = workoutItem;

    }

    @NonNull
    @Override
    public WorkoutHistoryRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View heroView = inflater.inflate(R.layout.item_workout_history_recycler_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(heroView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutHistoryRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.workoutKcal.setText(workoutItem.get(position).getWorkoutKcal());
        holder.workoutTitle.setText(workoutItem.get(position).getWorkoutTitle());
        holder.workoutTime.setText(workoutItem.get(position).getWorkoutTime());
        holder.workoutDuration.setText(workoutItem.get(position).getWorkoutDuration());
        holder.workoutDay.setText(workoutItem.get(position).getWorkoutDay());
        holder.imageView.setImageResource(workoutItem.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return workoutItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView workoutTitle, workoutDay, workoutTime, workoutDuration, workoutKcal;
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            workoutDay = itemView.findViewById(R.id.workoutDate);
            workoutTitle = itemView.findViewById(R.id.workout_title_history_recycler);
            workoutTime = itemView.findViewById(R.id.workout_time);
            workoutDuration = itemView.findViewById(R.id.duration_workout);
            workoutKcal = itemView.findViewById(R.id.kcal_workout);
            imageView = itemView.findViewById(R.id.exercise_history_icon);
        }
    }
}
