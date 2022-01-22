package ml.adityabodhankar.androidhealthmonitoring;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference reference;
    private LinearLayout loading;

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

        loading.setVisibility(View.VISIBLE);

        reference.child("users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loading.setVisibility(View.GONE);
                if (!snapshot.exists()) {
                    // user not exists
                    startActivity(new Intent(getApplicationContext(), NewUserActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}