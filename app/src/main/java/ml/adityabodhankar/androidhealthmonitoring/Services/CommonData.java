package ml.adityabodhankar.androidhealthmonitoring.Services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import ml.adityabodhankar.androidhealthmonitoring.Models.UserModel;

public class CommonData {
    public static UserModel userData;

    //    method to check internet connection
    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
