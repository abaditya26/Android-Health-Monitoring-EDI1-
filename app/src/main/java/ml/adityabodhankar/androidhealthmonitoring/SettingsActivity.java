package ml.adityabodhankar.androidhealthmonitoring;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import ml.adityabodhankar.androidhealthmonitoring.Models.UserModel;
import ml.adityabodhankar.androidhealthmonitoring.Services.CommonData;
import ml.adityabodhankar.androidhealthmonitoring.Services.SensorServiceInitializer;
import ml.adityabodhankar.androidhealthmonitoring.Services.StarterMethods;

public class SettingsActivity extends AppCompatActivity {

    LinearLayout profile, logout, goal, stepCounting;
    CircleImageView userIcon;
    TextView userName, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profile = findViewById(R.id.settings_profile);
        logout = findViewById(R.id.settings_logout);
        goal = findViewById(R.id.settings_goal);
        stepCounting = findViewById(R.id.settings_steps_counting);

        userIcon = findViewById(R.id.settings_profile_icon);
        userName = findViewById(R.id.settings_name);
        userEmail = findViewById(R.id.settings_email);

        UserModel user = CommonData.userData;
        userName.setText(user.getName());
        userEmail.setText(user.getEmail());
        if(user.getImage().compareTo("default")!=0){
            Glide.with(this).load(user.getImage()).into(userIcon);
        }

        profile.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), ProfileActivity.class)));
        logout.setOnClickListener(view -> CommonData.logout(getApplicationContext()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserModel user = CommonData.userData;
        userName.setText(user.getName());
        userEmail.setText(user.getEmail());
        if(user.getImage().compareTo("default")!=0){
            Glide.with(this).load(user.getImage()).into(userIcon);
        }
    }

    public void startSensor(View view) {
        StarterMethods s = new StarterMethods(this);
        s.startSensor();
    }

    public void stopSensor(View view) {
        stopService(new Intent(this, SensorServiceInitializer.class));
    }


    public void back_btn(View view) {
        finish();
    }
}