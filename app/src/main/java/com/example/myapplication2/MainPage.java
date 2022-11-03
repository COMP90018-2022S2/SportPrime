package com.example.myapplication2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicMarkableReference;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainPage extends AppCompatActivity {
    private TabLayout mTabLayout;
    RecyclerView recyclerView;
    FirebaseFirestore db;
    MyAdapter myAdapter;
    ArrayList<Activity> activityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        recyclerView = findViewById(R.id.activity_list);
        db = FirebaseFirestore.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        activityList = new ArrayList<>();
        myAdapter = new MyAdapter(this,activityList);
        recyclerView.setAdapter(myAdapter);

        db.collection("activity")
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
                                a.activityDate = getDocumentByKey(document, "end_time");
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

        mTabLayout = findViewById(R.id.mTabLayout);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if("Find Gym".equals(tab.getText())){
                    if (ActivityCompat.checkSelfPermission(MainPage.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainPage.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainPage.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                        return;
                    }
                    Intent intent = new Intent(MainPage.this,ShowNearbyGym.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (ActivityCompat.checkSelfPermission(MainPage.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainPage.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

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
