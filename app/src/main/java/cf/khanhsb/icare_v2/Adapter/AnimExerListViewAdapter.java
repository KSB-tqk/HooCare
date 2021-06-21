package cf.khanhsb.icare_v2.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import cf.khanhsb.icare_v2.R;

public class AnimExerListViewAdapter extends BaseAdapter {
    private ArrayList<String> title,textDetail,videoUri;
    private Context context;
    private View view;
    private int resource;
    private Holder holder;
    private FirebaseFirestore firestore;
    private DocumentReference docRef;
    private String[] exerciseContainer;

    public AnimExerListViewAdapter(Context context,int resource,
                                   ArrayList<String> title,
                                   ArrayList<String> textDetail,
                                   ArrayList<String> videoUri){
        super();
        this.context = context;
        this.title = title;
        this.textDetail = textDetail;
        this.videoUri = videoUri;
        this.resource = resource;
    }


    @Override
    public int getCount() {
        return title.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(resource,parent,false);

        holder = new AnimExerListViewAdapter.Holder();
        holder.exerciseTitle = (TextView) view.findViewById(R.id.exercises_workout_title);
        holder.exerciseText = (TextView) view.findViewById(R.id.exercises_workout_duration);
        holder.exerciseGif = (ImageView) view.findViewById(R.id.exercises_video);
        holder.frameLayout = (FrameLayout) view.findViewById(R.id.exercises_video_frame);

        holder.exerciseTitle.setText(title.get(position));
        holder.exerciseText.setText(textDetail.get(position));

        Glide.with(context)
                .load(videoUri.get(position))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(holder.exerciseGif);


        return view;
    }
    public class Holder
    {
        TextView exerciseTitle,exerciseText;
        ImageView exerciseGif;
        FrameLayout frameLayout;
    }
}
