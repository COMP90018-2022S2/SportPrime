package com.example.myapplication2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;



public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<Activity> list;

    public MyAdapter(Context context, ArrayList<Activity> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.activity_item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Activity activity = list.get(position);
        holder.activityName.setText(activity.getActivityName());
        holder.activityCost.setText(activity.getActivityCost());
        holder.activityLocation.setText(activity.getActivityLocation());
        holder.activityDate.setText(activity.getActivityDate());
        holder.activityTime.setText(activity.getActivityTime());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView activityName, activityCost, activityLocation, activityDate, activityTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            activityName = itemView.findViewById(R.id.tvActivityName);
            activityCost = itemView.findViewById(R.id.tvCost);
            activityLocation = itemView.findViewById(R.id.tvLocation);
            activityDate = itemView.findViewById(R.id.tvDate);
            activityTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
