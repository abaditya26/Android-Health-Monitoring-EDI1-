package ml.adityabodhankar.androidhealthmonitoring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import ml.adityabodhankar.androidhealthmonitoring.Models.ModelGoal;
import ml.adityabodhankar.androidhealthmonitoring.Services.CommonData;
import ml.adityabodhankar.androidhealthmonitoring.Services.LocalDatabase;

public class GoalActivity extends AppCompatActivity {

    EditText steps, calories;
    Button submitBtn;

    ModelGoal goal;

    FirebaseAuth auth;
    DatabaseReference reference;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);


        //initialize firebase
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        if (auth.getCurrentUser()==null){
            Toast.makeText(this, "User Not Signed in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Fetching Your Data");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //initialize locals
        steps = findViewById(R.id.step_goal_text_view);
        calories = findViewById(R.id.calories_goal_text_view);
        submitBtn = findViewById(R.id.update_goal_btn);
        disableInputs();

        reference.child("Goals").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goal = snapshot.getValue(ModelGoal.class);
                if (goal==null) {
                    steps.setText("500");
                    calories.setText("200");
                    goal = new ModelGoal("500","200");
                    updateGoals();
                }else {
                    steps.setText(goal.getStepGoal());
                    calories.setText(goal.getCaloriesGoal());
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error => "+error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        submitBtn.setOnClickListener(v -> {
            if(submitBtn.getText().toString().equalsIgnoreCase("update goal")) {
                updateGoals();
            }else{
                enableInputs();
            }
        });
    }

    public void updateGoals(){
        if(!checkData()){
            return;
        }
        HashMap<String, Object> newGoal = new HashMap<>();
        newGoal.put("stepGoal",steps.getText().toString());
        newGoal.put("caloriesGoal",calories.getText().toString());
        if(CommonData.goal == null){
            CommonData.goal = new ModelGoal();
        }
        CommonData.goal.setCaloriesGoal(calories.getText().toString());
        CommonData.goal.setStepGoal(steps.getText().toString());
        reference.child("Goals").child(Objects.requireNonNull(auth.getCurrentUser()).getUid()).updateChildren(newGoal).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                new LocalDatabase(this).insertGoal(auth.getCurrentUser().getUid(),steps.getText().toString(),calories.getText().toString());
                Toast.makeText(this, "Data Updated", Toast.LENGTH_SHORT).show();
                disableInputs();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Error => "+e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void disableInputs() {
        submitBtn.setText("Edit Goal");
        calories.setEnabled(false);
        steps.setEnabled(false);
    }

    private void enableInputs() {
        submitBtn.setText("Update Goal");
        steps.setEnabled(true);
        calories.setEnabled(true);
    }

    private boolean checkData() {
        if (calories.getText().toString().equalsIgnoreCase("")){
            calories.setError("Required Field");
            return false;
        }
        if (steps.getText().toString().equalsIgnoreCase("")){
            steps.setError("Required Field");
            return false;
        }
        return true;
    }
    public void back_btn(View view) {
        finish();
    }
}