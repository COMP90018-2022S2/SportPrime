package com.example.myapplication2;


import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.lang.String;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;





import android.content.Intent;





import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    private Button mainPage;
    private Button camera;
    private Button createActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mainPage = (Button) findViewById(R.id.navi1);
        camera = (Button) findViewById(R.id.navi2);
        createActivity = (Button) findViewById(R.id.navi3);
        mainPage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent switchActivityIntent = new Intent(MainActivity.this, ShowNearbyGym.class);
                startActivity(switchActivityIntent);
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent switchActivityIntent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(switchActivityIntent);
            }
        });
        createActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent switchActivityIntent = new Intent(MainActivity.this, CreateActivity.class);
                startActivity(switchActivityIntent);
//                Intent intent = new Intent(this, MapActivity.class);
//                Bundle bundle = new Bundle();
//
//                bundle.putString(SimplePlacePicker.API_KEY,apiKey);
//                bundle.putString(SimplePlacePicker.COUNTRY,country);
//                bundle.putString(SimplePlacePicker.LANGUAGE,language);
//                bundle.putStringArray(SimplePlacePicker.SUPPORTED_AREAS,supportedAreas);
//
//                intent.putExtras(bundle);
//                startActivityForResult(intent, SimplePlacePicker.SELECT_LOCATION_REQUEST_CODE);
            }
        });
    }


}
