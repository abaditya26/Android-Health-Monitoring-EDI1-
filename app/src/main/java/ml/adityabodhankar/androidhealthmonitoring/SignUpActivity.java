package ml.adityabodhankar.androidhealthmonitoring;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, cPasswordEditText;

    private FirebaseAuth auth;
    private ProgressBar loginProgress;
    private Button signUpBtn;
    private static final int RC_SIGN_IN = 6006;
    private GoogleSignInClient mGoogleSignInClient;

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
        ImageButton googleSignInBtn = findViewById(R.id.google_sign_up_btn);

        signUpBtn.setOnClickListener(v -> signUp());
        loginBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("542624866044-itdfdkns6a7ijimc40575kcns71fmf7l.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInBtn.setOnClickListener(view -> {
            //google sign in code
            loginProgress.setVisibility(View.VISIBLE);
            signUpBtn.setVisibility(View.GONE);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
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
                loginProgress.setVisibility(View.GONE);
                signUpBtn.setVisibility(View.VISIBLE);
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
                        signUpBtn.setVisibility(View.VISIBLE);
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(getApplicationContext(), "Google Sign in Error.", Toast.LENGTH_SHORT).show();
                        loginProgress.setVisibility(View.GONE);
                        signUpBtn.setVisibility(View.VISIBLE);
                    }
                });
    }
}