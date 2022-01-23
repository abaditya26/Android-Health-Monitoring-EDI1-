package ml.adityabodhankar.androidhealthmonitoring;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import ml.adityabodhankar.androidhealthmonitoring.Services.LocalDatabase;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 6006;
    private EditText emailEditText, passwordEditText;
//    private TextView forgetPassBtn;
    private FirebaseAuth auth;
    private ProgressBar loginProgress;
    private Button loginBtn;
    private GoogleSignInClient mGoogleSignInClient;
    private LocalDatabase localDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize firebase
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            if(auth.getCurrentUser().isEmailVerified()){
                //nav to homepage
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("flag", true);
                startActivity(intent);
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
        ImageButton googleSignInBtn = findViewById(R.id.google_sign_in_btn);
        TextView signUpBtn = findViewById(R.id.sign_up_nav);
//        forgetPassBtn = findViewById(R.id.forget_password_btn);


        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("542624866044-itdfdkns6a7ijimc40575kcns71fmf7l.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        //listener for login btn
        loginBtn.setOnClickListener(view -> loginUser());
        signUpBtn.setOnClickListener(view -> {
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        });
        googleSignInBtn.setOnClickListener(view -> {
            //google sign in code
            loginProgress.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(getApplicationContext(), "Google sign in error. ERR:- "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            if(user.isEmailVerified()){
                                //nav to homepage
                                startActivity(new Intent(this, HomeActivity.class));
                            }else{
                                //nav to email verification link
                                startActivity(new Intent(this, VerificationActivity.class));
                            }
                        }else{
                            auth.signOut();
                            Toast.makeText(getApplicationContext(), "Login error.", Toast.LENGTH_SHORT).show();
                        }
                        loginProgress.setVisibility(View.GONE);
                        loginBtn.setVisibility(View.VISIBLE);
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(getApplicationContext(), "Google Sign in Error.", Toast.LENGTH_SHORT).show();
                        loginProgress.setVisibility(View.GONE);
                        loginBtn.setVisibility(View.VISIBLE);
                    }
                });
    }
}