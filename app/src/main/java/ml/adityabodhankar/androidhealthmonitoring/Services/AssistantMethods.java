package ml.adityabodhankar.androidhealthmonitoring.Services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ml.adityabodhankar.androidhealthmonitoring.GoalActivity;
import ml.adityabodhankar.androidhealthmonitoring.HomeActivity;
import ml.adityabodhankar.androidhealthmonitoring.MainActivity;
import ml.adityabodhankar.androidhealthmonitoring.Models.ModelOpenPage;
import ml.adityabodhankar.androidhealthmonitoring.ProfileActivity;

public class AssistantMethods{

    Context context;
    Activity activity;

    String replyString = null;

    public AssistantMethods(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void initializeAssistant() {
        List<ModelOpenPage> openPages = new ArrayList<>();
        openPages.add(new ModelOpenPage("main",true ,MainActivity.class));
        openPages.add(new ModelOpenPage("home",true , MainActivity.class));
//        add stop service, initialize service,diet planner , logout
        CommonData.openPageList = openPages;

    }



    public String getReply(String inputString){

        replyString = "Unable to understand your request";

        if (inputString.toLowerCase().contains("open") || inputString.toLowerCase().contains("goto") || inputString.toLowerCase().contains("go to")){

            for (ModelOpenPage pages : CommonData.openPageList){
                if (inputString.toLowerCase().contains(pages.getPage())){
                    context.startActivity(new Intent(context, (Class<?>) pages.getActivity()));
                    return "opening";
                }
            }
            replyString = "no match found for your query";

        }else if (inputString.toLowerCase().contains("show") || inputString.toLowerCase().contains("view") || inputString.toLowerCase().contains("give")){
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String today = formatter.format(date);
            String s = new LocalDatabase(context).getSteps(FirebaseAuth.getInstance().getCurrentUser().getUid(),today);
            long steps = Math.max(Long.parseLong(s), CommonData.steps);
            if (inputString.toLowerCase().contains("steps")){
                replyString = "Today's steps count is "+ Math.max(Long.parseLong(s), CommonData.steps);
            }else if (inputString.toLowerCase().contains("calories")){
                replyString = "Today's calories count is "+ Math.max(Long.parseLong(s), CommonData.steps) * 0.04;
            }else {
                replyString = "No such data present.";
            }

        }else if (inputString.toLowerCase().contains("update") || inputString.toLowerCase().contains("change") || inputString.toLowerCase().contains("set")){

            if (inputString.toLowerCase().contains("goal")){
                context.startActivity(new Intent(context, GoalActivity.class));
            }else if (inputString.toLowerCase().contains("profile")){
                context.startActivity(new Intent(context, ProfileActivity.class));
            }else{
                replyString = "No Such Data Present";
            }

        }else {
//            basic data
            if (inputString.toLowerCase().contains("hi") || inputString.toLowerCase().contains("hello")){
                replyString = "Hello! How Can I Help?";
            } else if (inputString.toLowerCase().contains("logout") || inputString.toLowerCase().contains("signout") || inputString.toLowerCase().contains("sign out")){
                CommonData.logout(context);
                replyString = "OK";
            }else if (inputString.toLowerCase().contains("service")){
                if (inputString.toLowerCase().contains("stop") ){
                    replyString = "Stopping Service...";
                    activity.stopService(new Intent(context, SensorServiceInitializer.class));
                    LocalBroadcastManager.getInstance(context).unregisterReceiver(HomeActivity.broadcastReceiver);
                }else if(inputString.toLowerCase().contains("start")){
                    replyString = "Starting Service...";
                    new StarterMethods(context).startSensor();
                }
            }
        }

        return replyString;
    }
}
