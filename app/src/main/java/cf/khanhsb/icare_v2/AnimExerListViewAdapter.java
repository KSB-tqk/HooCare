package cf.khanhsb.icare_v2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

public class AnimExerListViewAdapter extends BaseAdapter {
    private String[] title,textDetail,videoUri;
    private Context context;
    private View view;
    private int resource;
    private Holder holder;

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

        holder.exerciseTitle.setText(title[position]);
        holder.videoView.setVideoURI(Uri.parse(videoUri[position]));
        holder.videoView.start();
        holder.exerciseText.setText(textDetail[position]);

        return view;
    }
    public class Holder
    {
        TextView exerciseTitle,exerciseText;
        VideoView videoView;
    }
}
