package com.example.myapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
                Intent switchActivityIntent = new Intent(MainActivity.this, MainPage.class);
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
                Intent switchActivityIntent = new Intent(MainActivity.this, MapsActivityCurrentPlace.class);
                startActivity(switchActivityIntent);
            }
        });
    }
}
