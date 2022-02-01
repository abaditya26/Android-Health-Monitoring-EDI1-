package ml.adityabodhankar.androidhealthmonitoring;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import ml.adityabodhankar.androidhealthmonitoring.Models.ModelGoal;
import ml.adityabodhankar.androidhealthmonitoring.Models.StepModel;
import ml.adityabodhankar.androidhealthmonitoring.Models.UserModel;
import ml.adityabodhankar.androidhealthmonitoring.Services.CommonData;
import ml.adityabodhankar.androidhealthmonitoring.Services.LocalDatabase;

public class HomeActivity extends AppCompatActivity {

    static FirebaseAuth auth;
    private DatabaseReference reference;
    private LinearLayout loading;
    private RelativeLayout mainSection;
    private CircleImageView settingsIcon;
    private UserModel userData;
    @SuppressLint("StaticFieldLeak")
    static LocalDatabase localDatabase;
    private boolean flag = false;
    private FloatingActionButton chatFAB;

    String today;
    static BarChart chart;

    static ProgressBar stepCountProgress;
    static TextView stepCount,stepTextView, caloriesTextView, distanceTextView;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    //receiver for data sent from service
    public static BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int steps = Integer.parseInt(intent.getStringExtra("steps"));
            double calories = steps * 0.04;
            double height = Double.parseDouble(CommonData.userData.getHeight());
            //distance calculation

//            Distance travelled during a walk is calculated by multiplying the steps taken by the person's stride length.
            double multiplier = 0.414;
            double stride = multiplier * height;

            double distanceCm = steps * stride;
            double distance = distanceCm/100;
            updateUI(steps, calories, distance);
        }

        private void updateUI(int steps, double calories, double distance) {
            try{
                long stepGoal = 500;
                try {
                    stepGoal = Long.parseLong(CommonData.goal.getStepGoal());
                }catch (Exception ignored) {
//                    todo:get goal
                    FirebaseDatabase.getInstance().getReference().child("Goals").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ModelGoal goal = snapshot.getValue(ModelGoal.class);
                            if (goal!=null){
                                CommonData.goal = goal;
                                System.out.println(CommonData.goal.getStepGoal());
                                localDatabase.insertGoal(FirebaseAuth.getInstance().getCurrentUser().getUid(),goal.getStepGoal(),goal.getCaloriesGoal());
                            }else{
                                System.out.println("huiashdiusadh");
                                CommonData.goal = localDatabase.getGoal(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }


                double percentage = steps*100.0/stepGoal;
                stepCount.setText(steps+"");
                stepCountProgress.setProgress((int) percentage);

                stepTextView.setText(steps+"");
                caloriesTextView.setText(df.format(calories));
                distanceTextView.setText(df.format(distance));
                try{
                    setBarData();
                }catch (Exception er){
                    Log.e("CHART ERROR",er+"");
                }
            }catch (Exception e){
                Log.e("ERROR",e+"");
            }
        }

        private void setBarData() {
            ArrayList<BarEntry> dataValues = new ArrayList<>();

            List<StepModel> stepList = localDatabase.getOldSteps(Objects.requireNonNull(auth.getCurrentUser()).getUid());
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

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();

        flag = getIntent().getBooleanExtra("flag", false);

        if(auth.getCurrentUser()==null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        if(!auth.getCurrentUser().isEmailVerified()){
            startActivity(new Intent(this, VerificationActivity.class));
            finish();
            return;
        }

        reference = FirebaseDatabase.getInstance().getReference();
        localDatabase = new LocalDatabase(this);

        //initialize locals
        loading = findViewById(R.id.loading_section);
        mainSection = findViewById(R.id.main_section);
        settingsIcon = findViewById(R.id.user_icon);
        chatFAB = findViewById(R.id.chat_fab);

        loading.setVisibility(View.VISIBLE);
        mainSection.setVisibility(View.GONE);

        chatFAB.setOnClickListener(view -> startActivity(new Intent(this, AssistantActivity.class)));

        settingsIcon.setOnClickListener(view -> startActivity(new Intent(this, SettingsActivity.class)));

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        today = formatter.format(date);
//        set default variables
        stepCount = findViewById(R.id.stepCount);
        stepCountProgress = findViewById(R.id.stepsProgressBar);
        stepTextView = findViewById(R.id.step_count_view);
        caloriesTextView = findViewById(R.id.calories_count_view);
        distanceTextView = findViewById(R.id.distance_count_view);

        chart = findViewById(R.id.bar_chart);
        TextView dateTextView = findViewById(R.id.date_text_view);
        dateTextView.setText(today);

//        if internet connection then fetch from remote db and set the latest data to local db
//        else get data from local db
        if(CommonData.isNetworkAvailable(this)) {
            reference.child("users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    loading.setVisibility(View.GONE);
                    mainSection.setVisibility(View.VISIBLE);
                    if (!snapshot.exists()) {
                        // user not exists
                        startActivity(new Intent(getApplicationContext(), NewUserActivity.class));
                        finish();
                        return;
                    }
                    userData = snapshot.getValue(UserModel.class);
                    CommonData.userData = userData;
                    //set the data to local database
                    if (userData != null) {
                        if(flag) {
                            localDatabase.setUserData(userData);
                        } else {
                            localDatabase.createUser(userData);
                        }
                    }
                    setView();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    loading.setVisibility(View.GONE);
                    mainSection.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Error :- " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            // get data from local database
            userData = localDatabase.getUser(auth.getCurrentUser().getUid());
            loading.setVisibility(View.GONE);
            mainSection.setVisibility(View.VISIBLE);
            CommonData.userData = userData;
            setView();
        }
    }

    private void setView() {
        if(userData.getImage().compareTo("default")!=0 && userData.getImage().length() != 0){
            Glide.with(getApplicationContext()).load(userData.getImage()).into(settingsIcon);
        }

        setUIData();

        setChart();
    }

    private void setUIData() {

        long steps = CommonData.steps;
        double calories = steps * 0.04;


        long stepGoal = 0;
        double percentage = 0;
        try {
            stepGoal = Long.parseLong(CommonData.goal.getStepGoal());
        }catch (Exception ignored){
            stepGoal = 500;
        }
        percentage = CommonData.steps * 100.0 / stepGoal;

        double height = Double.parseDouble(CommonData.userData.getHeight());

//            Distance travelled during a walk is calculated by multiplying the steps taken by the person's stride length.
        double multiplier = 0.614;
        double stride = multiplier * height;

        double distanceCm = steps * stride;
        double distance = distanceCm/100;


        stepCount.setText(steps+"");
        stepCountProgress.setProgress((int) percentage);

        stepTextView.setText(steps+"");
        caloriesTextView.setText(df.format(calories));
        distanceTextView.setText(df.format(distance));

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

        List<StepModel> stepList = localDatabase.getOldSteps(Objects.requireNonNull(auth.getCurrentUser()).getUid());
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
        }catch (Exception ignored){}
        try {
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver, new IntentFilter("AndroidHealthMonitoring"));
        }catch (Exception ignored){}
    }

    public void displayDetails(View view) {
        startActivity(new Intent(this, StatisticsActivity.class));
    }
}