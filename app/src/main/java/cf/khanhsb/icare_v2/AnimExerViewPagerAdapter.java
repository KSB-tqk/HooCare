package cf.khanhsb.icare_v2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import pl.droidsonroids.gif.GifImageView;

public class AnimExerViewPagerAdapter extends RecyclerView.Adapter<AnimExerViewPagerAdapter.ViewHolder>{

    private int[] gifImage;
    private String[] videoId;
    private boolean isVideo = false;

    public AnimExerViewPagerAdapter(int[] gif, String[] video){
        this.gifImage = gif;
        this.videoId = video;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //initialize view
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_anim_exer_viewpager,parent,false);
        //return the view
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position == 1){
            holder.gifImageView.setVisibility(View.GONE);
            holder.youTubePlayerView.setVisibility(View.VISIBLE);
        }
        else{
            holder.gifImageView.setVisibility(View.VISIBLE);
            holder.gifImageView.setImageResource(gifImage[position]);
            holder.youTubePlayerView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //initialize variable
        YouTubePlayerView youTubePlayerView;
        GifImageView gifImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            youTubePlayerView = itemView.findViewById(R.id.youtube_player_view_viewpager);
            gifImageView = itemView.findViewById(R.id.gif_anim_view_viewpager);
        }
    }
}
