package cf.khanhsb.icare_v2.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.VideoView;

public class UsageStatisticListViewAdapter extends BaseAdapter {

    private Context context;
    private int resource;
    private View view;
    private Holder holder;

    public UsageStatisticListViewAdapter(Context context,int resource,String[] Image,
                                   String[] exerciseTitle,
                                   String[] exerciseText){
        super();
        this.context = context;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(resource,parent,false);



        return view;
    }

    public class Holder
    {
        TextView exerciseTitle,exerciseText;
        VideoView videoView;
        FrameLayout frameLayout;
    }
}
