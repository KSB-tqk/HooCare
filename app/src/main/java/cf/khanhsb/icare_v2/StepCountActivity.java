package cf.khanhsb.icare_v2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class StepCountActivity extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<BarEntry> dataValue = new ArrayList<BarEntry>();
    private ProgressBar progressBar;
    private ImageView backtohomefrag_button;
    private TextView statusOfProgressBar, day_tab, week_tab, month_tab, select_background;
    private ColorStateList def_color;
    /*private int realtimePBstatus = 0;
    private Handler handler = new Handler();*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);

        progressBar = (ProgressBar) findViewById(R.id.progressbar_stepcount_detail);
        statusOfProgressBar = (TextView) findViewById(R.id.progressbar_status);
        day_tab = (TextView) findViewById(R.id.text_item1);
        week_tab = (TextView) findViewById(R.id.text_item2);
        month_tab = (TextView) findViewById(R.id.text_item3);
        select_background = (TextView) findViewById(R.id.selected_background_tab);
        backtohomefrag_button = (ImageView) findViewById(R.id.button_backtohomefrag);

        //set tabview onclick listener
        day_tab.setOnClickListener(this);
        week_tab.setOnClickListener(this);
        month_tab.setOnClickListener(this);
        def_color = week_tab.getTextColors();

        int value = Integer.parseInt(statusOfProgressBar.getText().toString());

        progressBar.setMax(10000);
        ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, 0, value);
        anim.setDuration(3000);
        progressBar.startAnimation(anim);

        backtohomefrag_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMain = new Intent(StepCountActivity.this,MainActivity.class);
                startActivity(toMain);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        //add data to barchart
        dataValue.add(new BarEntry(0, 3));
        dataValue.add(new BarEntry(1, 4));
        dataValue.add(new BarEntry(2, 6));
        dataValue.add(new BarEntry(3, 10));
        dataValue.add(new BarEntry(4, 12));
        dataValue.add(new BarEntry(5, 14));
        dataValue.add(new BarEntry(6, 12));

        //create bardataset
        BarDataSet barDataSet = new BarDataSet(dataValue, "DataSet 1");

        //create bardata and add bardataset to bardata
        BarData barData = new BarData();
        barData.addDataSet(barDataSet);
        barData.setValueTextSize(15f);

        //create barchart and add bardata to barchart
        BarChart barChart = (BarChart) findViewById(R.id.mp_Barchart);
        barChart.setData(barData);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.animateXY(2000,2000);
        barChart.setViewPortOffsets(60, 50, 50, 60);
        barChart.getXAxis().setDrawLabels(true);


        //add column to mpbarchart
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("Mon");
        xAxisLabel.add("Tue");
        xAxisLabel.add("Wed");
        xAxisLabel.add("Thu");
        xAxisLabel.add("Fri");
        xAxisLabel.add("Sat");
        xAxisLabel.add("Sun");

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xAxisLabel.get((int) value);

            }
        });

        barChart.invalidate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_item1:
                select_background.animate().x(0).setDuration(200);
                day_tab.setTextColor(Color.WHITE);
                week_tab.setTextColor(def_color);
                month_tab.setTextColor(def_color);
                break;
            case R.id.text_item2:
                int size = week_tab.getWidth();
                select_background.animate().x(size).setDuration(200);
                week_tab.setTextColor(Color.WHITE);
                day_tab.setTextColor(def_color);
                month_tab.setTextColor(def_color);
                break;
            case R.id.text_item3:
                int size0fMonthTab = month_tab.getWidth()*2;
                select_background.animate().x(size0fMonthTab).setDuration(200);
                month_tab.setTextColor(Color.WHITE);
                day_tab.setTextColor(def_color);
                week_tab.setTextColor(def_color);
                break;

        }
    }
}