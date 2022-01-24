package ml.adityabodhankar.androidhealthmonitoring;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import ml.adityabodhankar.androidhealthmonitoring.Services.CommonData;
import ml.adityabodhankar.androidhealthmonitoring.Services.SensorServiceInitializer;
import ml.adityabodhankar.androidhealthmonitoring.Services.StarterMethods;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void startSensor(View view) {
        StarterMethods s = new StarterMethods(this);
        s.startSensor();
    }

    public void stopSensor(View view) {
        stopService(new Intent(this, SensorServiceInitializer.class));
    }

    public void logout(View view) {
        CommonData.logout(this);
    }
}