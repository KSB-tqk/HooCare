package cf.khanhsb.icare_v2.Adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;

import cf.khanhsb.icare_v2.R;
import pl.droidsonroids.gif.GifImageView;

public class AnimExerViewPagerAdapter extends
        RecyclerView.Adapter<AnimExerViewPagerAdapter.ViewHolder>{

    private ArrayList<String> gifImageList;
    private ArrayList<String> videoIdList;
    private Context context;
    private int positionOfView;
    private MediaController mediaController;

    public AnimExerViewPagerAdapter(ArrayList<String> gif, ArrayList<String> video,int position, Context context){
        this.gifImageList = gif;
        this.videoIdList = video;
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
            holder.imageView.setVisibility(View.GONE);
            holder.youTubePlayerView.setVisibility(View.VISIBLE);
            holder.youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    String videoId = videoIdList.get(positionOfView);
                    youTubePlayer.loadVideo(videoId, 0);
                }
            });
        }
        else{
            holder.imageView.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(gifImageList.get(position))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(holder.imageView);
//            holder.videoView.setVisibility(View.GONE);
            holder.youTubePlayerView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        //initialize variable
        VideoView videoView;
        ImageView imageView;
        YouTubePlayerView youTubePlayerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            youTubePlayerView = itemView.findViewById(R.id.video_view_viewpager);
            imageView = itemView.findViewById(R.id.gif_anim_view_viewpager);
        }
    }
}
