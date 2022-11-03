package com.example.myapplication2;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Filter4Activity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseFirestore db;
    MyAdapter myAdapter;
    ArrayList<Activity> activityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        recyclerView = findViewById(R.id.activity_filter);
        db = FirebaseFirestore.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        activityList = new ArrayList<>();
        myAdapter = new MyAdapter(this,activityList);
        recyclerView.setAdapter(myAdapter);


        db.collection("activity")
                .whereEqualTo("tag", "PINGPONG")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Activity a = document.toObject(Activity.class);
                                a.activityName = getDocumentByKey(document, "name");
                                a.activityCost = getDocumentByKey(document, "cost");
                                a.activityLocation = getDocumentByKey(document, "location");
                                a.activityDate = getDocumentByKey(document, "date");
                                a.activityTime = getDocumentByKey(document, "start_time");
                                activityList.add(a);
                                Log.d(TAG, document.getId() + " => " + a.getActivityCost());
                            }
                            Log.d(TAG, String.valueOf(activityList.size()));
                            myAdapter.setActivityList(activityList);
                            myAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });



    }

    private String getDocumentByKey(QueryDocumentSnapshot document, String key){
        String value = null;
        if (key!= null && key.length()>0){
            Object o = document.get(key);
            if (o != null) {
                value = String.valueOf(o);
            }
        }
        return value;
    }
}