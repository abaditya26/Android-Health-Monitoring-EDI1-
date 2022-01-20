package ml.adityabodhankar.androidhealthmonitoring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
//    private TextView forgetPassBtn;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize firebase
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            if(auth.getCurrentUser().isEmailVerified()){
                //nav to homepage
            }else{
                //nav to email verification link
            }
        }

        //initialize locals
        Button loginBtn = findViewById(R.id.login_button);
        emailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);
//        forgetPassBtn = findViewById(R.id.forget_password_btn);

        //listener for login btn
        loginBtn.setOnClickListener(view -> loginUser());
    }

    private void loginUser(){
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if(!isDataValid(email, password)){
            return;
        }
        //sign in user
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            if(Objects.requireNonNull(authResult.getUser()).isEmailVerified()){
                //email verified. nav to homepage
            }else{
                //email not verified send email verification link.
                //nav to email verification link
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getApplicationContext(), "Login Failed. ERR :- "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
            passwordEditText.setError("Minimum password length is 6");
            return false;
        }
        return true;
    }

}