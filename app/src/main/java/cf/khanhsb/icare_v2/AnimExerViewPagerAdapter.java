package cf.khanhsb.icare_v2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import pl.droidsonroids.gif.GifImageView;

public class AnimExerViewPagerAdapter extends RecyclerView.Adapter<AnimExerViewPagerAdapter.ViewHolder>{

    private int[] gifImage;
    private String[] videoId;
    private boolean isVideo = false;
    private Lifecycle lifecycle;

    public AnimExerViewPagerAdapter(int[] gif, String[] video,Lifecycle lifecycle){
        this.gifImage = gif;
        this.videoId = video;
        this.lifecycle = lifecycle;
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
            holder.youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    String videoId = "w0yjlVqfgyU";
                    youTubePlayer.loadVideo(videoId, 0);
                }
            });
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
            lifecycle.addObserver(youTubePlayerView);
            gifImageView = itemView.findViewById(R.id.gif_anim_view_viewpager);
        }
    }
}
