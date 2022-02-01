package ml.adityabodhankar.androidhealthmonitoring.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ml.adityabodhankar.androidhealthmonitoring.Models.StepModel;
import ml.adityabodhankar.androidhealthmonitoring.R;

public class StatStepAdapter extends RecyclerView.Adapter<StatStepAdapter.ViewHolder> {
    Context ctx;
    List<StepModel> steps;

    public StatStepAdapter(Context ctx, List<StepModel> steps) {
        this.ctx = ctx;
        this.steps = steps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.table_row,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(steps.get(position));
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, step, calorie;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.table_date);
            step = itemView.findViewById(R.id.table_step_count);
            calorie = itemView.findViewById(R.id.table_calories);
        }

        public void setData(StepModel stepData) {
            date.setText(stepData.getDate());
            step.setText(stepData.getSteps());
            calorie.setText((Long.parseLong(stepData.getSteps()) * 0.04)+"");
        }
    }
}
