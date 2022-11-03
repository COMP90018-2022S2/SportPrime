package com.example.myapplication2;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;



public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<Activity> activityList;

    public MyAdapter(Context context, ArrayList<Activity> list) {
        this.context = context;
        this.activityList = list;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView activityName, activityCost, activityLocation, activityDate, activityTime, id;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
//            id = itemView.findViewById(R.id.idDisplay);
            activityName = itemView.findViewById(R.id.tvActivityName);
            activityCost = itemView.findViewById(R.id.tvCost);
            activityLocation = itemView.findViewById(R.id.tvLocation);
            activityDate = itemView.findViewById(R.id.tvDate);
            activityTime = itemView.findViewById(R.id.tvTime);

            itemView.findViewById(R.id.buttonDetail).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent tgt = new Intent (view.getContext(),ShowActivity.class);
                    tgt.putExtra("id", id.getText());
                    view.getContext().startActivity(tgt);
                }
            });


        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.activity_item,parent,false);
        return new MyViewHolder(v);
    }

    public void setActivityList(ArrayList<Activity> activityList){
        this.activityList = activityList;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Activity activity = activityList.get(position);
        holder.activityName.setText(activity.getActivityName());
        holder.activityCost.setText(activity.getActivityCost());
        holder.activityLocation.setText(activity.getActivityLocation());
        holder.activityDate.setText(activity.getActivityDate());
        holder.activityTime.setText(activity.getActivityTime());
//        holder.id.setText(activity.id);
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }


}
