package com.example.myapplication2;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;



import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentChange.Type;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Query.Direction;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentChange.Type;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Query.Direction;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.String;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import org.w3c.dom.Text;


public class CreateActivity extends AppCompatActivity {
    private static final int REQUEST_GET_MAP_LOCATION = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button submitButton;
    //private TextView alertView;
    private EditText activityName;
    private EditText activityDescription;
    private TextView activityLocation;
    private EditText activityStartTime;
    private EditText activityEndTime;
    private EditText activityTag;
    private EditText activityMaxPeople;
    private EditText activityCost;
    private EditText activityDate;
    private Switch activityIsPublic;
    private Button selectLocation;
    private double latitude;
    private double longitude;
    private String activityLocationString;
    private String hostName;
    private String hostId;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_main);
        submitButton = (Button) findViewById(R.id.createActivitySubmit);
        activityName = (EditText) findViewById(R.id.editActivityName);
        activityDescription = (EditText) findViewById(R.id.editActivityDescription);
        activityStartTime = (EditText) findViewById(R.id.editActivityStartTime);
        activityEndTime = (EditText) findViewById(R.id.editActivityEndTime);
        activityTag = (EditText) findViewById(R.id.editActivityTag);
        activityMaxPeople = (EditText) findViewById(R.id.editActivityPeople);
        activityCost = (EditText) findViewById(R.id.editActivityCost);
        activityLocation = (TextView) findViewById(R.id.editActivityLocation);
        activityDate = (EditText) findViewById(R.id.editActivityDate);
        activityIsPublic = (Switch) findViewById(R.id.activityIsPublic);
        selectLocation = (Button) findViewById(R.id.selectLLocation1);
        //alertView = (TextView) findViewById(R.id.alertTextView);
        getHost();
        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = null;
                String result = checkAllFilled();
                if (result.matches( "good")){
                    submitCreateActivity(view);
                }  else {
                    message = "Please fill in " + result + " .";
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateActivity.this);
                    builder.setCancelable(true);
                    builder.setTitle("Informarion required");
                    builder.setMessage(message);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    builder.show();
                }


            }

        });
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

    public void submitCreateActivity(View view)
    {


        Map<String, Object> activity = new HashMap<>();
        activity.put("name", activityName.getText().toString());
        activity.put("description", activityDescription.getText().toString());
        activity.put("start_time", activityStartTime.getText().toString());
        activity.put("end_time",activityEndTime.getText().toString());
        activity.put("location",activityLocation.getText().toString());
        activity.put("cost",activityCost.getText().toString());
        activity.put("tag",activityTag.getText().toString());
        activity.put("max_people",activityMaxPeople.getText().toString());
        activity.put("current_people",1);
        activity.put("host_id",hostId);
        activity.put("host_name", hostName);
        activity.put("is_public", activityIsPublic.isChecked());
        activity.put("location_latitude", Double.toString(latitude));
        activity.put("location_longitude", Double.toString(longitude));
        activity.put("date",activityDate.getText().toString());
        activity.put("user_list", "");

        // Add a new document with a generated ID
        db.collection("activity")
                .add(activity)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        AlertDialog.Builder builder = new AlertDialog.Builder(CreateActivity.this);
                        builder.setCancelable(false);
                        builder.setTitle("Activity successfully created");
                        builder.setMessage("Redirecting to the main page");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(view.getContext(), MainPage.class);
                                startActivity(intent);
                                finish();
                                //heading to the main page
                            }
                        });
                        builder.show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        AlertDialog.Builder builder = new AlertDialog.Builder(CreateActivity.this);
                        builder.setCancelable(true);
                        builder.setTitle("Something went wrong -.-!");
                        builder.setMessage("Please try again");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //do nothing
                            }
                        });
                        builder.show();
                    }
                });
    }

    public String checkAllFilled(){
        if (TextUtils.isEmpty(activityName.getText().toString().trim())){
            return "activity name";
        }
        if (TextUtils.isEmpty(activityDescription.getText().toString().trim())){
            return "activity description";
        }
        if (TextUtils.isEmpty(activityStartTime.getText().toString().trim())){
            return "start time";
        }
        if (TextUtils.isEmpty(activityEndTime.getText().toString().trim())){
            return "end time";
        }
        if (TextUtils.isEmpty(activityLocation.getText().toString().trim())){
            return "activity location";
        }
        if (TextUtils.isEmpty(activityMaxPeople.getText().toString().trim())){
            return "max people";
        }
        if (TextUtils.isEmpty(activityDate.getText().toString().trim())){
            return "date";
        }
        return "good";
    }
    private void switchActivities() {
        Intent switchActivityIntent = new Intent(this, MapActivity.class);
        startActivityForResult(switchActivityIntent, REQUEST_GET_MAP_LOCATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GET_MAP_LOCATION && resultCode == Activity.RESULT_OK) {
            latitude = data.getDoubleExtra("latitude", 0.0);
            longitude = data.getDoubleExtra("longitude", 0.0);
            activityLocationString = data.getStringExtra("address");
            activityLocation.setText(activityLocationString);
            activityLocation.setTextSize(10);
        }
    }

}