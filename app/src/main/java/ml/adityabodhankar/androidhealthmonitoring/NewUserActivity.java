package ml.adityabodhankar.androidhealthmonitoring;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import ml.adityabodhankar.androidhealthmonitoring.Models.UserModel;

public class NewUserActivity extends AppCompatActivity {

    TextView emailTextView;
    EditText nameEditText, phoneEditText, heightEditText, weightEditText;
    RadioButton maleButton, femaleButton;
    ProgressBar loading;
    Button saveBtn;
    String gender="";

    FirebaseAuth auth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        if(auth.getCurrentUser()==null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        emailTextView = findViewById(R.id.new_user_email);
        nameEditText = findViewById(R.id.new_user_name);
        phoneEditText = findViewById(R.id.new_user_phone);
        heightEditText = findViewById(R.id.new_user_height);
        weightEditText = findViewById(R.id.new_user_weight);
        saveBtn = findViewById(R.id.save_user_btn);
        maleButton = findViewById(R.id.male_radio_btn);
        femaleButton = findViewById(R.id.female_radio_btn);
        loading = findViewById(R.id.new_user_loading);

        loading.setVisibility(View.GONE);
        saveBtn.setVisibility(View.VISIBLE);

        maleButton.setOnClickListener(view -> gender = "male");
        femaleButton.setOnClickListener(view -> gender = "female");

        saveBtn.setOnClickListener(view -> saveData());

        emailTextView.setText(Objects.requireNonNull(auth.getCurrentUser()).getEmail());
    }

    private void saveData() {
        String name = nameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String height = heightEditText.getText().toString();
        String weight = weightEditText.getText().toString();

        if (!isDataValid(name, phone, gender, height, weight)){
            return;
        }
        loading.setVisibility(View.VISIBLE);
        saveBtn.setVisibility(View.GONE);
        UserModel user = new UserModel(Objects.requireNonNull(auth.getCurrentUser()).getUid(), name,
                auth.getCurrentUser().getEmail(),phone, gender, height, weight, "");
        reference.child("users").child(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                .setValue(user).addOnSuccessListener(unused -> {
                    loading.setVisibility(View.GONE);
                    saveBtn.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "User Data Saved.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                }).addOnFailureListener(e -> {
                    loading.setVisibility(View.GONE);
                    saveBtn.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Unable to save Data. ERR:- "+e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    private boolean isDataValid(String name, String phone, String gender, String height, String weight) {
        if (name.length() == 0){
            nameEditText.setError("Required field");
            return false;
        }
        if (phone.length() == 0){
            phoneEditText.setError("Required field");
            return false;
        }
        if (height.length() == 0){
            heightEditText.setError("Required field");
            return false;
        }
        if (weight.length() == 0){
            weightEditText.setError("Required field");
            return false;
        }
        if (gender.length() == 0){
            Toast.makeText(getApplicationContext(), "Gender Required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}