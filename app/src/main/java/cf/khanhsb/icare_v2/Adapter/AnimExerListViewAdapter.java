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
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import cf.khanhsb.icare_v2.R;

public class AnimExerListViewAdapter extends BaseAdapter {
    private String[] title,textDetail,videoUri;
    private Context context;
    private View view;
    private int resource;
    private Holder holder;
    private FirebaseFirestore firestore;
    private DocumentReference docRef;

    public AnimExerListViewAdapter(Context context,int resource,String[] Image,
                                   String[] exerciseTitle,
                                   String[] exerciseText){
        super();
        this.context = context;
        this.videoUri = Image;
        this.title = exerciseTitle;
        this.textDetail = exerciseText;
        this.resource = resource;
    }


    @Override
    public int getCount() {
        return title.length;
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
        holder.videoView = (VideoView) view.findViewById(R.id.exercises_video);
        holder.frameLayout = (FrameLayout) view.findViewById(R.id.exercises_video_frame);

        docRef = firestore.collection("exercise").document();
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
//                        workoutTitleList.add(document.getString("name"));
                    } else {
                        Log.d("LOGGER", "No such document");
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }
        });

        holder.exerciseTitle.setText(title[position]);
        holder.videoView.requestFocus();
        holder.videoView.setVideoURI(Uri.parse(videoUri[position]));
        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                holder.videoView.setVideoURI(Uri.parse(videoUri[position]));
            }
        });
        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        holder.videoView.start();
        holder.exerciseText.setText(textDetail[position]);

        return view;
    }
    public class Holder
    {
        TextView exerciseTitle,exerciseText;
        VideoView videoView;
        FrameLayout frameLayout;
    }
}
