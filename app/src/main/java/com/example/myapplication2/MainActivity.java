package com.example.myapplication2;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import android.content.DialogInterface;
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


public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button submitButton;
    //private TextView alertView;
    private EditText activityName;
    private EditText activityDescription;
    private EditText activityLocation;
    private EditText activityStartTime;
    private EditText activityEndTime;
    private EditText activityTag;
    private EditText activityMaxPeople;
    private EditText activityCost;
    private Switch activityIsPublic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        submitButton = (Button) findViewById(R.id.createActivitySubmit);
        activityName = (EditText) findViewById(R.id.editActivityName);
        activityDescription = (EditText) findViewById(R.id.editActivityDescription);
        activityStartTime = (EditText) findViewById(R.id.editActivityStartTime);
        activityEndTime = (EditText) findViewById(R.id.editActivityEndTime);
        activityTag = (EditText) findViewById(R.id.editActivityTag);
        activityMaxPeople = (EditText) findViewById(R.id.editActivityPeople);
        activityCost = (EditText) findViewById(R.id.editActivityCost);
        activityLocation = (EditText) findViewById(R.id.editActivityLocation);
        activityIsPublic = (Switch) findViewById(R.id.activityIsPublic);
        //alertView = (TextView) findViewById(R.id.alertTextView);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(true);
                builder.setTitle("");
                builder.setMessage("");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //do something
                    }
                });
                builder.show();
                sumbitCreateActivity(view);
            }

        });
    }

    public void sumbitCreateActivity(View view)
    {

        String activityName = ((EditText)findViewById(R.id.editActivityName)).getText().toString();
        String activityDescription = ((EditText)findViewById(R.id.editActivityDescription)).getText().toString();
        String activityStartTime = ((EditText)findViewById(R.id.editActivityStartTime)).getText().toString();
        Map<String, Object> activity = new HashMap<>();
        activity.put("name", activityName);
        activity.put("description", activityDescription);
        activity.put("start_time", activityStartTime);
//
        // Add a new document with a generated ID
        db.collection("activity")
                .add(activity)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public String checkAllFilled(){

        return "Good";
    }

}