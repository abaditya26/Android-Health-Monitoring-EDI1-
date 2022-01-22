package ml.adityabodhankar.androidhealthmonitoring;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ml.adityabodhankar.androidhealthmonitoring.Services.SensorServiceInitializer;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void startSensor(View view) {
        startService(new Intent(this, SensorServiceInitializer.class));
    }

    public void stopSensor(View view) {
        stopService(new Intent(this, SensorServiceInitializer.class));
    }
}