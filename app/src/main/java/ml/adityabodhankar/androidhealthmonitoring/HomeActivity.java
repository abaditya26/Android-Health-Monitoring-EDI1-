package ml.adityabodhankar.androidhealthmonitoring;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import ml.adityabodhankar.androidhealthmonitoring.Models.UserModel;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference reference;
    private LinearLayout loading, mainSection;
    private CircleImageView settingsIcon;
    private UserModel userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser()==null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        if(!auth.getCurrentUser().isEmailVerified()){
            startActivity(new Intent(this, VerificationActivity.class));
            finish();
            return;
        }

        reference = FirebaseDatabase.getInstance().getReference();

        //initialize locals
        loading = findViewById(R.id.loading_section);
        mainSection = findViewById(R.id.main_section);
        settingsIcon = findViewById(R.id.user_icon);

        loading.setVisibility(View.VISIBLE);
        mainSection.setVisibility(View.GONE);

        settingsIcon.setOnClickListener(view -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });

        reference.child("users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loading.setVisibility(View.GONE);
                mainSection.setVisibility(View.VISIBLE);
                if (!snapshot.exists()) {
                    // user not exists
                    startActivity(new Intent(getApplicationContext(), NewUserActivity.class));
                    finish();
                    return;
                }
                userData = snapshot.getValue(UserModel.class);
                setView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loading.setVisibility(View.GONE);
                mainSection.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Error :- "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setView() {
        if(userData.getImage().compareTo("default")!=0 && userData.getImage().length() != 0){
            Glide.with(getApplicationContext()).load(userData.getImage()).into(settingsIcon);
        }
    }
}