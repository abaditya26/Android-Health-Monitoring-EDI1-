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

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
//    private TextView forgetPassBtn;
    private FirebaseAuth auth;
    private ProgressBar loginProgress;
    private Button loginBtn;
    private TextView signUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        loginBtn = findViewById(R.id.login_button);
        emailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);
        loginProgress = findViewById(R.id.login_progress);
        signUpBtn = findViewById(R.id.sign_up_nav);
//        forgetPassBtn = findViewById(R.id.forget_password_btn);

        //listener for login btn
        loginBtn.setOnClickListener(view -> loginUser());
        signUpBtn.setOnClickListener(view -> {
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        });
    }

    private void loginUser(){
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if(!isDataValid(email, password)){
            return;
        }
        loginProgress.setVisibility(View.VISIBLE);
        loginBtn.setVisibility(View.GONE);
        //sign in user
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            if(Objects.requireNonNull(authResult.getUser()).isEmailVerified()){
                //nav to homepage
                startActivity(new Intent(this, HomeActivity.class));
            }else{
                //nav to email verification link
                startActivity(new Intent(this, VerificationActivity.class));
            }
            loginProgress.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(getApplicationContext(), "Login Failed. ERR :- "+e.getMessage(), Toast.LENGTH_SHORT).show();
            loginProgress.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        });
    }

    private boolean isDataValid(String email, String password) {
        if(email.compareTo("")==0){
            emailEditText.setError("Field Required");
            return false;
        }
        if(password.compareTo("")==0){
            passwordEditText.setError("Field Required");
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
        return true;
    }

}