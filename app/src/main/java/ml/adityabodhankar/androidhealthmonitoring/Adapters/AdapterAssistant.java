package ml.adityabodhankar.androidhealthmonitoring.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ml.adityabodhankar.androidhealthmonitoring.Models.ModelMessage;
import ml.adityabodhankar.androidhealthmonitoring.Models.UserModel;
import ml.adityabodhankar.androidhealthmonitoring.R;
import ml.adityabodhankar.androidhealthmonitoring.Services.CommonData;

public class AdapterAssistant extends RecyclerView.Adapter<AdapterAssistant.ViewHolder>{
    Context ctx;
    List<ModelMessage> messages;

    public AdapterAssistant(Context ctx, List<ModelMessage> messages) {
        this.ctx = ctx;
        this.messages = messages;
    }

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    @NonNull
    @Override
    public AdapterAssistant.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==MSG_TYPE_RIGHT){
            view = LayoutInflater.from(ctx).inflate(R.layout.message_item_right,parent,false);
        }else if (viewType==MSG_TYPE_LEFT){
            view = LayoutInflater.from(ctx).inflate(R.layout.message_item_left,parent,false);
        }else{
            view = null;
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAssistant.ViewHolder holder, int position) {
        holder.setData(messages,position);
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).isUserMessage()){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userIcon;
        TextView message, time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userIcon = itemView.findViewById(R.id.message_sender_icon);
            message = itemView.findViewById(R.id.message_text_view);
            time = itemView.findViewById(R.id.message_time);
        }

        public void setData(List<ModelMessage> messages, int position) {
            message.setText(messages.get(position).getText());
            time.setText(messages.get(position).getTimeStamp());
//            TODO:user icon
            UserModel user = CommonData.userData;
            if(user.getImage().compareTo("default")!=0){
                Glide.with(ctx).load(user.getImage()).into(userIcon);
            }
        }
    }

}
