package com.example.myapplication2;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ActivityList extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseFirestore db;
    MyAdapter myAdapter;
    ArrayList<Activity> activityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView = findViewById(R.id.activity_list);
        db = FirebaseFirestore.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        activityList = new ArrayList<>();
        myAdapter = new MyAdapter(this,activityList);
        recyclerView.setAdapter(myAdapter);

        //        db.collection("activity").get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        if (!queryDocumentSnapshots.isEmpty()){
//                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
//                            for(DocumentSnapshot d: list){
//                                Activity a = d.toObject(Activity.class);
//                                a.activityName = d.get("name").toString();
//                                a.activityCost = d.get("cost").toString();
//                                a.activityLocation = d.get("location").toString();
//                                a.activityDate = "1111";
//                                a.activityTime = d.get("start_time").toString();
//                                activityList.add(a);
//                                Log.d(TAG, "This is a print:" + a.getActivityCost());
//
//                            }
//                        }
//                    }
//                });

        db.collection("activity")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Activity a = document.toObject(Activity.class);
                                a.activityName = document.get("name").toString();
                                a.activityCost = document.get("cost").toString();
                                a.activityLocation = document.get("location").toString();
                                a.activityDate = document.get("end_time").toString();
                                a.activityTime = document.get("start_time").toString();
                                activityList.add(a);
                                Log.d(TAG, document.getId() + " => " + activityList);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        myAdapter.notifyDataSetChanged();


    }
}