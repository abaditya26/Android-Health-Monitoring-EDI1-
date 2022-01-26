package ml.adityabodhankar.androidhealthmonitoring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import ml.adityabodhankar.androidhealthmonitoring.Models.UserModel;
import ml.adityabodhankar.androidhealthmonitoring.Services.CommonData;

public class ProfileActivity extends AppCompatActivity {

    private EditText profileName, profilePhone, profileHeight, profileWeight;
    private TextView profileEmail;
    private Button saveBtn;
    private CircleImageView image;
    RadioButton maleButton, femaleButton;
    ProgressBar loading;
    String gender = "";
    boolean isEditEnabled = false;
    UserModel user;

    private  FirebaseAuth auth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        if(auth.getCurrentUser()==null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        user = CommonData.userData;
        profileEmail= findViewById(R.id.profile_email);
        profileName = findViewById(R.id.profile_name);
        profilePhone = findViewById(R.id.profile_phone);
        profileHeight = findViewById(R.id.profile_height);
        profileWeight = findViewById(R.id.profile_weight);
        image = findViewById(R.id.profile_icon);
        saveBtn = findViewById(R.id.profile_save_user_btn);
        maleButton = findViewById(R.id.male_radio_btn);
        femaleButton = findViewById(R.id.female_radio_btn);
        loading = findViewById(R.id.profile_loading);

        loading.setVisibility(View.GONE);
        saveBtn.setVisibility(View.VISIBLE);

        maleButton.setOnClickListener(view -> gender = "male");
        femaleButton.setOnClickListener(view -> gender = "female");

        if(CommonData.isNetworkAvailable(this)) {
            if(isEditEnabled) {
                saveBtn.setOnClickListener(view -> saveData());
            }else{
                // enable edit
                enableEdit();
                saveBtn.setText("SAVE");
            }
        }else{
            saveBtn.setText("NO INTERNET");
        }

        profileEmail.setText(Objects.requireNonNull(auth.getCurrentUser()).getEmail());
        // load data
        setData();
    }

    private void enableEdit() {
        isEditEnabled = true;
        //enable edit of all edit text
        profileName.setEnabled(true);
        profilePhone.setEnabled(true);
        profileHeight.setEnabled(true);
        profileWeight.setEnabled(true);
    }

    private void disableEdit() {
        isEditEnabled = false;
        //enable edit of all edit text
        profileName.setEnabled(false);
        profilePhone.setEnabled(false);
        profileHeight.setEnabled(false);
        profileWeight.setEnabled(false);
    }

    private void setData() {
        profileEmail.setText(user.getEmail());
        profileName.setText(user.getName());
        profilePhone.setText(user.getPhone());
        profileHeight.setText(user.getHeight());
        profileWeight.setText(user.getWeight());
        if(user.getImage().compareTo("default")!=0){
            Glide.with(this).load(user.getImage()).into(image);
        }
    }

    private void saveData() {
        String name = profileName.getText().toString();
        String phone = profilePhone.getText().toString();
        String height = profileHeight.getText().toString();
        String weight = profileWeight.getText().toString();

        if (!isDataValid(name, phone, gender, height, weight)) {
            return;
        }
        loading.setVisibility(View.VISIBLE);
        saveBtn.setVisibility(View.GONE);
        //update data
//        user.setName(name);
//        user.setPhone(phone);
//        user.setHeight(height);
//        user.setWeight(weight);
        Map<String, Object> userMap = new HashMap<>();

        reference.child("users").child(user.getUid()).updateChildren(userMap)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getApplicationContext(), "Profile Updated.", Toast.LENGTH_SHORT).show();
                    disableEdit();
                }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Profile Update Failed. Please reload.", Toast.LENGTH_SHORT).show();
                });
    }

    private boolean isDataValid(String name, String phone, String gender, String height, String weight) {
        if (name.length() == 0){
            profileName.setError("Required field");
            return false;
        }
        if (phone.length() == 0){
            profilePhone.setError("Required field");
            return false;
        }
        if (height.length() == 0){
            profileHeight.setError("Required field");
            return false;
        }
        if (weight.length() == 0){
            profileWeight.setError("Required field");
            return false;
        }
        if (gender.length() == 0){
            Toast.makeText(getApplicationContext(), "Gender Required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}