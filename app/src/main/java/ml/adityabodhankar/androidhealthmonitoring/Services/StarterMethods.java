package ml.adityabodhankar.androidhealthmonitoring.Services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import ml.adityabodhankar.androidhealthmonitoring.Models.StepModel;

public class StarterMethods {
    private Context ctx;
    private LocalDatabase localDatabase;
    private FirebaseUser user;
    private DatabaseReference reference;

    public StarterMethods(Context ctx) {
        this.ctx = ctx;
        localDatabase = new LocalDatabase(ctx);
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
    }

    public void startSensor(){
        String steps = "0";

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String today = formatter.format(date);
        if(CommonData.isNetworkAvailable(ctx)){
            //get data from cloud and send it to service
            reference.child("users").child(user.getUid()).child("steps").child(today).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String steps = localDatabase.getSteps(user.getUid(),today);
                    if(snapshot.exists()){
                        StepModel stepModel = snapshot.getValue(StepModel.class);
                        if (stepModel != null) {
                            if(Integer.parseInt(stepModel.getSteps()) > Integer.parseInt(steps)){
                                //remote steps are greater
                                steps = stepModel.getSteps();
                                localDatabase.insertSteps(steps, user.getUid());
                            }else{
                                //local steps are greater
                                reference.child("users").child(user.getUid()).child("steps").child(today).setValue(stepModel);
                            }
                        }else{
                            //steps not present in remote db set them
                            stepModel = new StepModel(today,steps,user.getUid());
                            reference.child("users").child(user.getUid()).child("steps").child(today).setValue(stepModel);
                        }
                    }
                    Intent intent = new Intent(ctx, SensorServiceInitializer.class);
                    intent.putExtra("steps", steps);
                    ctx.startService(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            //get data from local database
            steps = localDatabase.getSteps(user.getUid(),today);
            Intent intent = new Intent(ctx, SensorServiceInitializer.class);
            intent.putExtra("steps", steps);
            ctx.startService(intent);
        }
    }
}
