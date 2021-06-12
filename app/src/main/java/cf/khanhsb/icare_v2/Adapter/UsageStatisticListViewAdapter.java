package cf.khanhsb.icare_v2.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;

import cf.khanhsb.icare_v2.R;

public class UsageStatisticListViewAdapter extends BaseAdapter {

    private Context context;
    private int resource;
    private View view;
    private Holder holder;
    private ArrayList<String> appNameList, appUsageTimeList;
    private ArrayList<Drawable> appIconList;

    public UsageStatisticListViewAdapter(Context context, int resource,
                                         ArrayList<String> appNameList,
                                         ArrayList<String> appUsageTimeList,
                                         ArrayList<Drawable> appIconList){
        super();
        this.context = context;
        this.resource = resource;
        this.appIconList = appIconList;
        this.appNameList = appNameList;
        this.appUsageTimeList = appUsageTimeList;
    }

    @Override
    public int getCount() {
        return appNameList.size();
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

        holder = new UsageStatisticListViewAdapter.Holder();
        holder.appNameTextView = view.findViewById(R.id.app_name_title);
        holder.appUsageTimeTextView = view.findViewById(R.id.app_usage_time);
        holder.appIconView = view.findViewById(R.id.app_icon_image_view);
        holder.progressBar = view.findViewById(R.id.progressbar_time_usage);

        holder.appNameTextView.setText(appNameList.get(position));
        holder.appUsageTimeTextView.setText(appUsageTimeList.get(position));
        holder.appIconView.setImageDrawable(appIconList.get(position));

        String tempString = appUsageTimeList.get(0);
        if(tempString.contains("h")){
            String[] splitString = appUsageTimeList.get(0).split("h");
            String min = splitString[1].substring(1,3);
            long timeInForeground = (Long.parseLong( min)*60*1000) + (Long.parseLong( splitString[0])*60*60*1000);

            String[] splitPosString = appUsageTimeList.get(position).split("h");
            String minPos = splitPosString[1].substring(1,3);
            long timeInForegroundPos = (Long.parseLong( min)*60*1000) + (Long.parseLong( splitString[0])*60*60*1000);

            if(timeInForeground > timeInForegroundPos) {

            }
        }

        return view;
    }

    public class Holder
    {
        TextView appNameTextView,appUsageTimeTextView;
        ImageView appIconView;
        ProgressBar progressBar;
    }
}
