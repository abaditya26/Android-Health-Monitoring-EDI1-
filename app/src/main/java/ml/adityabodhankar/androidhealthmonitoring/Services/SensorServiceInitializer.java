package ml.adityabodhankar.androidhealthmonitoring.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import ml.adityabodhankar.androidhealthmonitoring.MainActivity;
import ml.adityabodhankar.androidhealthmonitoring.R;

public class SensorServiceInitializer extends Service implements SensorEventListener {
    public static String CHANNEL_ID = "AndroidHealthMonitoring";
    public static String CHANNEL_NAME = "Android Health Monitoring";
    FirebaseUser user;
    DatabaseReference reference;
    String today;

    public static long steps = 0;

//    sensors
    Sensor accelerometer;
    SensorManager sensorManager;

    public SensorServiceInitializer() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        today = formatter.format(date);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

//        initialize sensors
        sensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel channel = new  NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("CHANNEL_DESC");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        Intent mainIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , mainIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Step Counter")
                .setContentText("Counting")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        sensorManager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //sensor event listeners

    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentTime =System.currentTimeMillis();
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        System.out.println("###### x:- "+x+" ### y:- "+y+" ### z:- "+z+" ######");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}