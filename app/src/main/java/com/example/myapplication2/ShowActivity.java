package com.example.myapplication2;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicMarkableReference;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.w3c.dom.Text;

public class ShowActivity extends AppCompatActivity{
    private double latitude;
    private double longitude;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView name;
    private TextView cost;
    private TextView description;
    private TextView date;
    private TextView tag;
    private TextView start_time;
    private TextView end_time;
    private TextView max_people;
    private TextView host;
    private TextView location;
    private String activityId;
    private Button joinButton;
    private String hostId;
    private String hostName;
    private FirebaseAuth mAuth;
    private String userList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        getHost();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_showactivity);
        name = (TextView) findViewById(R.id.showActivityName);
        cost = (TextView) findViewById(R.id.showActivityCost);
        description = (TextView) findViewById(R.id.showActivityDescription);
        date = (TextView) findViewById(R.id.showActivityDate);
        tag = (TextView) findViewById(R.id.showActivityTag);
        start_time = (TextView) findViewById(R.id.showActivityStartTime);
        end_time = (TextView) findViewById(R.id.showActivityEndTime);
        max_people = (TextView) findViewById(R.id.showActivityMaxPeople);
        host = (TextView) findViewById(R.id.showActivityHostId);
        location = (TextView) findViewById(R.id.showActivityLoaction);
        location.setTextSize(10);
        joinButton = (Button) findViewById(R.id.joinActivityButton);
        activityId = getIntent().getStringExtra("id");
        Display();
    }
    public void getHost(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> documentData = document.getData();
                                    String email = (String) documentData.get("Email");

                                    if (userEmail != null && userEmail.equals(email)) {
                                        hostId = document.getId();
                                        hostName = document.get("FullName").toString();

                                        return;
                                    }
                                }
                                //Toast.makeText(EditProfile.this, "Can not get user information from database, no matched email address with login email", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        }
    }
    private void Display(){
        DocumentReference docRef = db.collection("activity").document(activityId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    name.setText(document.get("name").toString());
                    cost.setText(document.get("cost").toString());
                    date.setText(document.get("date").toString());
                    start_time.setText(document.get("start_time").toString());
                    end_time.setText(document.get("end_time").toString());
                    location.setText(document.get("location").toString());
                    tag.setText(document.get("tag").toString());
                    host.setText("HOST: " + document.get("host_name").toString());
                    int available = Integer.parseInt(document.get("max_people").toString()) - Integer.parseInt(document.get("current_people").toString());
                    int max = Integer.parseInt(document.get("max_people").toString());
                    String str = Integer.toString(available) + "/" + Integer.toString(max) + " available";
                    max_people.setText(str);
                    description.setText((String)document.get("description"));
                    latitude = Double.parseDouble(document.get("location_latitude").toString());
                    longitude = Double.parseDouble(document.get("location_longitude").toString());
                    location.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(view.getContext(), ShowLocationWithMarker.class);
                            i.putExtra("LAT",latitude);
                            i.putExtra("LON",longitude);
                            startActivity(i);
                        }
                    });
                    //check if user already host or in the activity
                    if (hostId.equals(document.get("host_id").toString())){
                        joinButton.setText("Activity can not be cancelled");
                        joinButton.setTextSize(13);
                    } else {
                        userList = document.get("user_list").toString();

                        String user[] = userList.split(",");
                        Log.d(TAG, "this is zero string " +hostId);
                        List<String> names = new ArrayList<>(Arrays.asList(userList.split(",")));
                        if (names.contains(hostId)){
                            joinButton.setText("Leave Activity");
                            joinButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //leave
                                    names.remove(hostId);
                                    String str = names.toString();
                                    str = str.substring(1,str.length() -1);
                                    Map<String, Object> update =new HashMap<>();
                                    update.put("user_list", str);
                                    update.put("current_people", String.valueOf(max-available - 1));
                                    db.collection("activity").document(activityId).set(update,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(ShowActivity.this, "Successfully leave", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                                }
                            });
                        } else {
                            joinButton.setText("Join Activity");
                            joinButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //Join
                                    names.add(hostId);
                                    String str = names.toString();
                                    Log.d(TAG, "this is first string " +str);
                                    str = str.substring(1,str.length() -1).trim();
                                    Log.d(TAG, "this is second string " +str);
                                    Map<String, Object> update =new HashMap<>();
                                    update.put("user_list", str);
                                    update.put("current_people", String.valueOf(max-available + 1));
                                    db.collection("activity").document(activityId).set(update, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(ShowActivity.this, "Successfully join", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                                }
                            });
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


}
