package ml.adityabodhankar.androidhealthmonitoring;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ml.adityabodhankar.androidhealthmonitoring.Adapters.AdapterAssistant;
import ml.adityabodhankar.androidhealthmonitoring.Models.ModelMessage;
import ml.adityabodhankar.androidhealthmonitoring.Services.AssistantMethods;
import ml.adityabodhankar.androidhealthmonitoring.Services.CommonData;

public class AssistantActivity extends AppCompatActivity {

    EditText message;
    ImageView sendBtn;

    RecyclerView assistantRecycler;
    AdapterAssistant assistantAdapter;

    List<ModelMessage> messages;

    AssistantMethods assistantMethods;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant);

        assistantMethods = new AssistantMethods(this, this);

        message = findViewById(R.id.message);
        sendBtn = findViewById(R.id.btn_send_message);
        assistantRecycler = findViewById(R.id.assistant_recycler);

        if (CommonData.messages!=null){
            if (CommonData.messages.size()>0){
                messages = CommonData.messages;
            }else{
                messages = new ArrayList<>();
            }
        }else{
            messages = new ArrayList<>();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        assistantRecycler.setLayoutManager(linearLayoutManager);
        assistantAdapter = new AdapterAssistant(this,messages);
        assistantRecycler.setAdapter(assistantAdapter);

        assistantMethods.initializeAssistant();

        sendBtn.setOnClickListener(v -> sendMessage());


    }

    private void sendMessage() {
        if (message.getText().toString().equalsIgnoreCase("")){
            return;
        }
        messages.add(new ModelMessage(messages.size()+"", message.getText().toString(),getCurrentDate(),true));

        String output = assistantMethods.getReply(message.getText().toString());

        message.setText("");
        messages.add(new ModelMessage(messages.size()+"", output,getCurrentDate(),false));
        assistantAdapter = new AdapterAssistant(this,messages);
        assistantRecycler.setAdapter(assistantAdapter);
        CommonData.messages = messages;
    }

    private String getCurrentDate(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }

    public void back_btn(View view) {
        finish();
    }
}