package ml.adityabodhankar.androidhealthmonitoring;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
            saveBtn.setOnClickListener(view -> {
                if(isEditEnabled) {
                    //edit complete save data
                    saveData();
                }else{
                    // enable edit
                    enableEdit();
                }
            });

        }else{
            saveBtn.setText("NO INTERNET");
        }

        profileEmail.setText(Objects.requireNonNull(auth.getCurrentUser()).getEmail());
        // load data
        setData();
        disableEdit();

        image.setOnClickListener(view -> {
            if(isEditEnabled){
                //update image
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 11);
            }else{
                Toast.makeText(getApplicationContext(), "Enable Edit First.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enableEdit() {
        isEditEnabled = true;
        saveBtn.setText("SAVE");
        //enable edit of all edit text
        profileName.setEnabled(true);
        profilePhone.setEnabled(true);
        profileHeight.setEnabled(true);
        profileWeight.setEnabled(true);
        maleButton.setEnabled(true);
        femaleButton.setEnabled(true);
    }

    private void disableEdit() {
        isEditEnabled = false;
        saveBtn.setText("EDIT");
        //enable edit of all edit text
        profileName.setEnabled(false);
        profilePhone.setEnabled(false);
        profileHeight.setEnabled(false);
        profileWeight.setEnabled(false);
        maleButton.setEnabled(false);
        femaleButton.setEnabled(false);
        loading.setVisibility(View.GONE);
        saveBtn.setVisibility(View.VISIBLE);
    }

    private void setData() {
        profileEmail.setText(user.getEmail());
        profileName.setText(user.getName());
        profilePhone.setText(user.getPhone());
        profileHeight.setText(user.getHeight());
        profileWeight.setText(user.getWeight());
        gender = user.getGender();
        if(user.getImage().compareTo("default")!=0){
            Glide.with(this).load(user.getImage()).into(image);
        }
        if(gender.compareTo("male")==0){
            maleButton.setChecked(true);
        }else{
            femaleButton.setChecked(true);
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
        user.setName(name);
        user.setPhone(phone);
        user.setHeight(height);
        user.setWeight(weight);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("phone", phone);
        userMap.put("height", height);
        userMap.put("weight", weight);
        userMap.put("image", user.getImage());

        reference.child("users").child(user.getUid()).updateChildren(userMap)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getApplicationContext(), "Profile Updated.", Toast.LENGTH_SHORT).show();
                    disableEdit();
                    CommonData.userData = user;
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

    public void back_btn(View view) {
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==11 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            Uri imageUri = data.getData();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Image Uploading");
            progressDialog.setCancelable(false);
            progressDialog.show();

            StorageReference uploadImage = FirebaseStorage.getInstance().getReference().child("images/"+auth.getCurrentUser().getUid());

            uploadImage.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Image Uploaded. Click Save data.", Toast.LENGTH_SHORT).show();
                        Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        task.addOnSuccessListener(uri -> {
                            user.setImage(uri.toString());
                            Glide.with(this).load(imageUri).into(image);
                        });
                    })
                    .addOnFailureListener(exception -> {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed to upload"+exception, Toast.LENGTH_SHORT).show();
                    }).addOnProgressListener(snapshot -> {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setMessage("Uploading :- "+(int)progressPercent+" %");
            });
        }else{
            Toast.makeText(this, "Operation Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}