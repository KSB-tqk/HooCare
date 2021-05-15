package cf.khanhsb.icare_v2;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class leaderboard  extends RecyclerView.Adapter<leaderboard.ViewHolder>{
    List<String> item_leaderboard = new ArrayList<>();


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(leaderboard.ViewHolder holder, int position) {
        holder.userdata_leaderboard.setText(item_leaderboard.get(position));
    }

    @Override
    public int getItemCount() {
        return item_leaderboard.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userdata_leaderboard;
        public ViewHolder( View itemView) {
            super(itemView);
            userdata_leaderboard = itemView.findViewById(R.id.username_text_view_lb2);
        }
    }
}
