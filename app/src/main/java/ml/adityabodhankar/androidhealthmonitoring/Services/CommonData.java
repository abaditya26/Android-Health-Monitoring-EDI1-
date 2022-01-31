package ml.adityabodhankar.androidhealthmonitoring.Services;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import ml.adityabodhankar.androidhealthmonitoring.MainActivity;
import ml.adityabodhankar.androidhealthmonitoring.Models.ModelGoal;
import ml.adityabodhankar.androidhealthmonitoring.Models.ModelMessage;
import ml.adityabodhankar.androidhealthmonitoring.Models.ModelOpenPage;
import ml.adityabodhankar.androidhealthmonitoring.Models.UserModel;

public class CommonData {
    public static UserModel userData;
    public static List<ModelOpenPage> openPageList;
    public static long steps;
    public static List<ModelMessage> messages;
    public static ModelGoal goal;

    //    method to check internet connection
    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void logout(Context context){
        FirebaseAuth.getInstance().signOut();
        try {
            GoogleSignInClient mGoogleSignInClient;
            GoogleSignInOptions gso = new GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("542624866044-itdfdkns6a7ijimc40575kcns71fmf7l.apps.googleusercontent.com")
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
            mGoogleSignInClient.signOut();
        }catch (Exception ignored){}
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
