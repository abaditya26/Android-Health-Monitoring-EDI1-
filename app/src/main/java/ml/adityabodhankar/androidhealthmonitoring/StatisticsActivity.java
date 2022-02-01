package ml.adityabodhankar.androidhealthmonitoring;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ml.adityabodhankar.androidhealthmonitoring.Adapters.StatStepAdapter;
import ml.adityabodhankar.androidhealthmonitoring.Models.StepModel;
import ml.adityabodhankar.androidhealthmonitoring.Services.LocalDatabase;

public class StatisticsActivity extends AppCompatActivity {
    BarChart chart;
    LocalDatabase localDatabase;
    FirebaseAuth auth;
    List<StepModel> stepList;
    RecyclerView table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        localDatabase = new LocalDatabase(this);
        auth = FirebaseAuth.getInstance();
        chart = findViewById(R.id.bar_chart);
        table = findViewById(R.id.table_steps);

        table.setLayoutManager(new LinearLayoutManager(this));

        stepList = localDatabase.getOldSteps(Objects.requireNonNull(auth.getCurrentUser()).getUid(), true);

        table.setAdapter(new StatStepAdapter(this, stepList));

        stepList = localDatabase.getOldSteps(Objects.requireNonNull(auth.getCurrentUser()).getUid(), false);
        setChart();
    }

    private void setChart() {
        chart.setDrawGridBackground(false);
        chart.setDrawBorders(false);
        chart.getDescription().setEnabled(false);
        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);

        chart.getAxisRight().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        // add a nice and smooth animation
        chart.animateY(800);
        chart.getLegend().setEnabled(false);

        setBarData();

    }

    private void setBarData() {
        ArrayList<BarEntry> dataValues = new ArrayList<>();

        ArrayList<String> xAxisLables = new ArrayList<>();

        for (int i = 0; i< stepList.size();i++){
            dataValues.add(new BarEntry(i, Float.parseFloat(stepList.get(i).getSteps())));
            xAxisLables.add(""+stepList.get(i).getDate());
        }

        //set chart data
        BarDataSet BarDataSet = new BarDataSet(dataValues, "Steps Set");
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(BarDataSet);
        BarData data = new BarData(dataSets);
        data.setBarWidth(0.5f);
        chart.setData(data);

        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLables));

        chart.invalidate();

    }
    public void back_btn(View view) {
        finish();
    }
}