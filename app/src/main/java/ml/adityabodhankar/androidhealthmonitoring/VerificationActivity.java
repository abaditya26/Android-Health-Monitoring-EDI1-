package ml.adityabodhankar.androidhealthmonitoring;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class VerificationActivity extends AppCompatActivity {

    Button sendLink, resendLink, continueBtn;
    LinearLayout sendLinkLayout, resendLinkLayout, loadingLayout;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser()==null){
            //no user present
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        if(auth.getCurrentUser().isEmailVerified()){
            //email already verified
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        //initialize variables
        sendLink = findViewById(R.id.send_link_btn);
        resendLink = findViewById(R.id.resend_link_btn);
        continueBtn = findViewById(R.id.continue_btn);
        sendLinkLayout = findViewById(R.id.send_link_section);
        resendLinkLayout = findViewById(R.id.link_sent_section);
        loadingLayout = findViewById(R.id.loading_section);

        sendLinkLayout.setVisibility(View.VISIBLE);
        resendLinkLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.GONE);

        sendLink.setOnClickListener(view -> sendLink());
        resendLink.setOnClickListener(view -> sendLink());
        continueBtn.setOnClickListener(view -> {
            //sign out user
            auth.signOut();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });

    }

    private void sendLink(){
        // send the verification link to user
        sendLinkLayout.setVisibility(View.GONE);
        resendLinkLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
        Objects.requireNonNull(auth.getCurrentUser()).sendEmailVerification().addOnSuccessListener(unused -> {
            Toast.makeText(getApplicationContext(), "Verification Link sent on mail", Toast.LENGTH_SHORT).show();
            sendLinkLayout.setVisibility(View.GONE);
            resendLinkLayout.setVisibility(View.VISIBLE);
            loadingLayout.setVisibility(View.GONE);
        }).addOnFailureListener(e -> {
            Toast.makeText(getApplicationContext(), "Mail not sent. ERR:-"+e.getMessage(), Toast.LENGTH_SHORT).show();
            sendLinkLayout.setVisibility(View.VISIBLE);
            resendLinkLayout.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.GONE);
        });
    }
}