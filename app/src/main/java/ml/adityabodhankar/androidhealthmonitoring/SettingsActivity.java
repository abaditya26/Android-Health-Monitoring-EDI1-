package ml.adityabodhankar.androidhealthmonitoring;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import ml.adityabodhankar.androidhealthmonitoring.Models.UserModel;
import ml.adityabodhankar.androidhealthmonitoring.Services.CommonData;
import ml.adityabodhankar.androidhealthmonitoring.Services.SensorServiceInitializer;
import ml.adityabodhankar.androidhealthmonitoring.Services.StarterMethods;

public class SettingsActivity extends AppCompatActivity {

    LinearLayout profile, logout, goal, stepCounting, changePassword;
    CircleImageView userIcon;
    TextView userName, userEmail, stepCountingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profile = findViewById(R.id.settings_profile);
        logout = findViewById(R.id.settings_logout);
        goal = findViewById(R.id.settings_goal);
        stepCounting = findViewById(R.id.settings_steps_counting);
        changePassword = findViewById(R.id.settings_change_password);

        userIcon = findViewById(R.id.settings_profile_icon);
        userName = findViewById(R.id.settings_name);
        userEmail = findViewById(R.id.settings_email);
        stepCountingTextView = findViewById(R.id.settings_steps_counting_text);

        if(isMyServiceRunning()){
            stepCountingTextView.setText("Stop Counting Steps");
        }else{
            stepCountingTextView.setText("Start Counting Steps");
        }

        UserModel user = CommonData.userData;
        userName.setText(user.getName());
        userEmail.setText(user.getEmail());
        if(user.getImage().compareTo("default")!=0){
            Glide.with(this).load(user.getImage()).into(userIcon);
        }

        profile.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), ProfileActivity.class)));
        logout.setOnClickListener(view -> CommonData.logout(getApplicationContext()));
        goal.setOnClickListener(view->startActivity(new Intent(getApplicationContext(), GoalActivity.class)));
        stepCounting.setOnClickListener(view -> {
            if(isMyServiceRunning()){
                //service running stop it
                stopSensor();
                stepCountingTextView.setText("Start Counting Steps");
            }else{
                //service stopped start it
                startSensor();
                stepCountingTextView.setText("Stop Counting Steps");
            }
        });
        changePassword.setOnClickListener(view -> FirebaseAuth.getInstance()
                .sendPasswordResetEmail(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail()))
                .addOnSuccessListener(unused -> {
                    Toast.makeText(SettingsActivity.this, "Password Reset Link send to your email.", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(SettingsActivity.this, "Failed to send password reset link.", Toast.LENGTH_SHORT).show();
                }));
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

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SensorServiceInitializer.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public void startSensor() {
        StarterMethods s = new StarterMethods(this);
        s.startSensor();
    }

    public void stopSensor() {
        stopService(new Intent(this, SensorServiceInitializer.class));
    }

    public void back_btn(View view) {
        finish();
    }
}