package cf.khanhsb.icare_v2.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cf.khanhsb.icare_v2.R;

public class GymListViewAdapter extends BaseAdapter {

    private String[] title,time;
    private int[] image;
    private Context context;
    private View view;
    private int resource;
    private Holder holder;

    public GymListViewAdapter(Context context,int resource,int[] workoutImage,String[] workoutTitle,String[] time){
        super();
        this.context = context;
        this.image = workoutImage;
        this.title = workoutTitle;
        this.resource = resource;
        this.time = time;
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public String getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(resource,parent,false);

        holder = new Holder();
        holder.workoutTitle = (TextView) view.findViewById(R.id.body_part_workout_title);
        holder.workoutTime = (TextView) view.findViewById(R.id.body_part_workout_time);
        holder.workoutImage = (ImageView) view.findViewById(R.id.body_part_icon);

        holder.workoutTitle.setText(title[position]);
        holder.workoutImage.setImageResource(image[position]);
        holder.workoutTime.setText(time[position]);
        return view;
    }
    public class Holder
    {
        TextView workoutTitle,workoutTime;
        ImageView workoutImage;
    }
}
