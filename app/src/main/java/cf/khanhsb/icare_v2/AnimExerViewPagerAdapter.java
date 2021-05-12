package cf.khanhsb.icare_v2;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import pl.droidsonroids.gif.GifImageView;

public class AnimExerViewPagerAdapter extends
        RecyclerView.Adapter<AnimExerViewPagerAdapter.ViewHolder>{

    private int[] gifImage;
    private String[] videoId;
    private Context context;
    private int positionOfView;

    public AnimExerViewPagerAdapter(int[] gif, String[] video,int position, Context context){
        this.gifImage = gif;
        this.videoId = video;
        this.context = context;
        this.positionOfView = position;
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
            holder.videoView.setVisibility(View.VISIBLE);
            holder.videoView.setVideoURI(Uri.parse(videoId[positionOfView]));
            holder.videoView.start();
        }
        else{
            holder.gifImageView.setVisibility(View.VISIBLE);
            holder.gifImageView.setImageResource(gifImage[positionOfView]);
            holder.videoView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        //initialize variable
        VideoView videoView;
        GifImageView gifImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.video_view_viewpager);
            gifImageView = itemView.findViewById(R.id.gif_anim_view_viewpager);
        }
    }
}
