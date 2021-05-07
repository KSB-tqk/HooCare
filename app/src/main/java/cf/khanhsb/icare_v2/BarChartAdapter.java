package cf.khanhsb.icare_v2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class BarChartAdapter extends RecyclerView.Adapter<BarChartAdapter.ViewHolder>{
    //initialize variable
    private ArrayList<BarEntry> barEntryArrayList;
    private ArrayList<String> barLabels;

    //create constructor
    public BarChartAdapter(ArrayList<BarEntry> theBarEntryList, ArrayList<String> theLabels){
        this.barEntryArrayList = theBarEntryList;
        this.barLabels = theLabels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //initialize view
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.fragment_step_count_item_of_viewpager2,parent,false);
        //return the view
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //set image on image_view
        holder.barChart.setData(new BarData(new BarDataSet(this.barEntryArrayList,"")));

        holder.barChart.setDescription(null);
        holder.barChart.getLegend().setEnabled(false);
        holder.barChart.getAxisRight().setEnabled(false);
        holder.barChart.setDrawGridBackground(false);
        holder.barChart.setTouchEnabled(true);
        holder.barChart.setScaleEnabled(true);
        holder.barChart.setPinchZoom(false);
        holder.barChart.setDoubleTapToZoomEnabled(false);
        holder.barChart.getXAxis().setDrawGridLines(false);

        holder.barChart.animateXY(1000,1000);

        holder.barChart.getAxisLeft().setTextSize(15f);
        holder.barChart.getXAxis().setTextSize(15f);
        holder.barChart.getXAxis().setSpaceMin(0.5f);
        holder.barChart.setVisibleXRangeMaximum(6);
        holder.barChart.setViewPortOffsets(140f,0f,0f,50f);
        holder.barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        holder.barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return barLabels.get((int) value);
            }
        });
    }

    @Override
    public int getItemCount() {
        //return array length
        return 3;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //initialize variable
        BarChart barChart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            barChart = (BarChart) itemView.findViewById(R.id.bar_chart_test);
        }
    }
}
