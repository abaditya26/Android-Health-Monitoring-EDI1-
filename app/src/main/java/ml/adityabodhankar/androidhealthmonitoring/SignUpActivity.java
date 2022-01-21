package ml.adityabodhankar.androidhealthmonitoring;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, cPasswordEditText;

    private FirebaseAuth auth;
    private ProgressBar loginProgress;
    private Button signUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //initialize firebase
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            if(auth.getCurrentUser().isEmailVerified()){
                //nav to homepage
                startActivity(new Intent(this, HomeActivity.class));
            }else{
                //nav to email verification link
                startActivity(new Intent(this, VerificationActivity.class));
            }
            finish();
            return;
        }

        //initialize locals
        emailEditText = findViewById(R.id.signup_email);
        passwordEditText = findViewById(R.id.signup_password);
        cPasswordEditText = findViewById(R.id.c_signup_password);
        loginProgress = findViewById(R.id.signup_progress);
        signUpBtn = findViewById(R.id.signup_button);
        TextView loginBtn = findViewById(R.id.login_nav);

        signUpBtn.setOnClickListener(v -> signUp());
        loginBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void signUp() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String cPassword = cPasswordEditText.getText().toString();
        if(!isDataValid(email, password, cPassword)){
            return;
        }
        loginProgress.setVisibility(View.VISIBLE);
        signUpBtn.setVisibility(View.GONE);
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            Toast.makeText(getApplicationContext(), "User Created Successfully", Toast.LENGTH_SHORT).show();
            loginProgress.setVisibility(View.GONE);
            signUpBtn.setVisibility(View.VISIBLE);
            //go to verify page
            startActivity(new Intent(getApplicationContext(), VerificationActivity.class));
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(getApplicationContext(), "User Creation Error. ERR :- "+e.getMessage(), Toast.LENGTH_SHORT).show();
            loginProgress.setVisibility(View.GONE);
            signUpBtn.setVisibility(View.VISIBLE);
        });
    }

    private boolean isDataValid(String email, String password, String cPassword) {
        if(email.length()==0){
            emailEditText.setError("Field Required");
            return false;
        }
        if(password.length()==0){
            passwordEditText.setError("Field Required");
            return false;
        }
        if(cPassword.length()==0){
            cPasswordEditText.setError("Field Required");
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Enter Valid Email.");
            return false;
        }
        if(password.length()<6){
            passwordEditText.setError("Minimum password length is 6.");
            return false;
        }
        if(cPassword.length()<6){
            cPasswordEditText.setError("Minimum password length is 6.");
            return false;
        }
        if(password.compareTo(cPassword)!=0){
            passwordEditText.setError("Both Passwords not match.");
            cPasswordEditText.setError("Both Passwords not match");
            return false;
        }
        return true;
    }
}