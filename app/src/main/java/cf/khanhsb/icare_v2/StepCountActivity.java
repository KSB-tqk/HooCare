package cf.khanhsb.icare_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class StepCountActivity extends AppCompatActivity implements View.OnClickListener {

    //initialize variable
    private ArrayList<BarEntry> dataValue = new ArrayList<BarEntry>();
    private ProgressBar progressBar;
    private ImageView backtohomefrag_button;
    private TextView statusOfProgressBar, day_tab, week_tab, month_tab, select_background;
    private ColorStateList def_color;
    private ViewPager2 verticalViewPager2;
    BarChartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);

        /**assign variable*/
        
        statusOfProgressBar = (TextView) findViewById(R.id.progressbar_status);
        day_tab = (TextView) findViewById(R.id.text_item1);
        week_tab = (TextView) findViewById(R.id.text_item2);
        month_tab = (TextView) findViewById(R.id.text_item3);
        select_background = (TextView) findViewById(R.id.selected_background_tab);
        backtohomefrag_button = (ImageView) findViewById(R.id.button_backtohomefrag);
        verticalViewPager2 = (ViewPager2) findViewById(R.id.step_count_barchart_viewPager2);



        /**set tabview onclick listener*/
        day_tab.setOnClickListener(this);
        week_tab.setOnClickListener(this);
        month_tab.setOnClickListener(this);
        def_color = week_tab.getTextColors();


        /**back button on the toolbar click event*/
        backtohomefrag_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMain = new Intent(StepCountActivity.this,MainActivity.class);
                startActivity(toMain);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        ArrayList<BarDataSet> chartBarDataSetList = new ArrayList<>();

        /**add data to barchart*/
        dataValue.add(new BarEntry(0, 3000));
        dataValue.add(new BarEntry(1, 4000));
        dataValue.add(new BarEntry(2, 6000));
        dataValue.add(new BarEntry(3, 1000));
        dataValue.add(new BarEntry(4, 1200));
        dataValue.add(new BarEntry(5, 7200));
        dataValue.add(new BarEntry(6, 125));

        /**create bardata and add bardataset to bardata*/
        ArrayList<String> daylist = new ArrayList<String>() ;
        daylist.add("mon");
        daylist.add("tue");
        daylist.add("wed");
        daylist.add("thur");
        daylist.add("fri");
        daylist.add("sat");
        daylist.add("sun");

        //initialize testadapter
        adapter = new BarChartAdapter(dataValue,daylist);
        //setting adapter on to the viewpager2
        verticalViewPager2.setUserInputEnabled(false);
        verticalViewPager2.setAdapter(adapter);
    }


    /**sliding navigation bar (day/week/month)*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_item1:
                verticalViewPager2.setCurrentItem(0);
                select_background.animate().x(0).setDuration(200);
                day_tab.setTextColor(Color.WHITE);
                week_tab.setTextColor(def_color);
                month_tab.setTextColor(def_color);
                break;
            case R.id.text_item2:
                verticalViewPager2.setCurrentItem(1);
                int size = week_tab.getWidth();
                select_background.animate().x(size).setDuration(200);
                week_tab.setTextColor(Color.WHITE);
                day_tab.setTextColor(def_color);
                month_tab.setTextColor(def_color);
                break;
            case R.id.text_item3:
                verticalViewPager2.setCurrentItem(2);
                int size0fMonthTab = month_tab.getWidth()*2;
                select_background.animate().x(size0fMonthTab).setDuration(200);
                month_tab.setTextColor(Color.WHITE);
                day_tab.setTextColor(def_color);
                week_tab.setTextColor(def_color);
                break;

        }
    }
}