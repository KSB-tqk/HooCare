package cf.khanhsb.icare_v2.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

import cf.khanhsb.icare_v2.R;

public class UsageStatisticListViewAdapter extends BaseAdapter {

    private Context context;
    private int resource;
    private View view;
    private Holder holder;
    private ArrayList<String> appNameList, appUsageTimeList;
    private ArrayList<Drawable> appIconList;
    private ArrayList<Long> appUsageTimeListInMilliSec;
    private long otherAppTimeUsage;

    public UsageStatisticListViewAdapter(Context context, int resource,
                                         ArrayList<String> appNameList,
                                         ArrayList<String> appUsageTimeList,
                                         ArrayList<Drawable> appIconList,
                                         ArrayList<Long> appUsageTimeListInMilliSec,
                                         long otherAppTimeUsage) {
        super();
        this.context = context;
        this.resource = resource;
        this.appIconList = appIconList;
        this.appNameList = appNameList;
        this.appUsageTimeList = appUsageTimeList;
        this.otherAppTimeUsage = otherAppTimeUsage;
        this.appUsageTimeListInMilliSec = appUsageTimeListInMilliSec;
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
        view = inflater.inflate(resource, parent, false);

        holder = new UsageStatisticListViewAdapter.Holder();
        holder.appNameTextView = view.findViewById(R.id.app_name_title);
        holder.appUsageTimeTextView = view.findViewById(R.id.app_usage_time);
        holder.appIconView = view.findViewById(R.id.app_icon_image_view);
        holder.progressBar = view.findViewById(R.id.progressbar_time_usage);
        holder.container = view.findViewById(R.id.time_usage_container);

        holder.appNameTextView.setText(appNameList.get(position));
        holder.appUsageTimeTextView.setText(appUsageTimeList.get(position));
        holder.appIconView.setImageDrawable(appIconList.get(position));

        if (position == appIconList.size() - 1) {
            holder.progressBar.setProgress(100);
        } else {
            float progress = (float) appUsageTimeListInMilliSec.get(position) / (float) otherAppTimeUsage * 100;
            if (progress <= 5) {
                holder.progressBar.setProgress(5);
            } else {
                holder.progressBar.setProgress((int) progress);
            }
        }
        return view;
    }

    public class Holder {
        TextView appNameTextView, appUsageTimeTextView;
        ImageView appIconView;
        ProgressBar progressBar;
        ConstraintLayout container;
    }
}
