package ml.adityabodhankar.androidhealthmonitoring;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import ml.adityabodhankar.androidhealthmonitoring.Services.SensorServiceInitializer;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void startSensor(View view) {
        //check if internet connection
//         if yes then fetch from cloud and then fetch from local db
    //         if local db> remote then remote = local and send the value to remote and service
    //         else local = remote and send the value to local and service
//         else fetch from local and send to service

        Intent intent = new Intent(this, SensorServiceInitializer.class);
        intent.putExtra("steps", "100");
        startService(intent);
    }

    public void stopSensor(View view) {
        stopService(new Intent(this, SensorServiceInitializer.class));
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}