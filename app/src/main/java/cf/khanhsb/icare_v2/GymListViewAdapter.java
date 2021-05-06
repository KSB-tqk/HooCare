package cf.khanhsb.icare_v2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GymListViewAdapter extends BaseAdapter {

    private String[] title;
    private int[] image;
    private Context context;
    private View view;
    private int resource;
    private Holder holder;

    public GymListViewAdapter(Context context,int resource,int[] workoutImage,String[] workoutTitle){
        super();
        this.context = context;
        this.image = workoutImage;
        this.title = workoutTitle;
        this.resource = resource;
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
        holder.workoutImage = (ImageView) view.findViewById(R.id.body_part_icon);

        holder.workoutTitle.setText(title[position]);
        holder.workoutImage.setImageResource(image[position]);
        return view;
    }
    public class Holder
    {
        TextView workoutTitle;
        ImageView workoutImage;
    }
}
