package com.example.myapplication2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;


import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainPage extends AppCompatActivity {
    private TabLayout mTabLayout;
    RecyclerView recyclerView;
    FirebaseFirestore db;
    MyAdapter myAdapter;
    ArrayList<Activity> activityList;

    ImageButton btnFilter1;
    ImageButton btnFilter2;
    ImageButton btnFilter3;
    ImageButton btnFilter4;


    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    ActionBarDrawerToggle drawerToogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);


        btnFilter1 = (ImageButton)findViewById(R.id.imageButton);
        btnFilter2 = (ImageButton)findViewById(R.id.imageButton2);
        btnFilter3 = (ImageButton)findViewById(R.id.imageButton3);
        btnFilter4 = (ImageButton)findViewById(R.id.imageButton4);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        setupDrawerContent(nvDrawer);
        //drawerToogle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();


        mDrawer.addDrawerListener(drawerToggle);


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
                                a.longitude = Double.parseDouble(getDocumentByKey(document, "location_longitude"));
                                a.latitude = Double.parseDouble(getDocumentByKey(document, "location_latitude"));
                                a.id = document.getId();
                                a.activityLocation = getDocumentByKey(document, "location");
                                a.activityDate = getDocumentByKey(document, "end_time");
                                a.activityTime = getDocumentByKey(document, "start_time");
                                activityList.add(a);
                                //Log.d(TAG, document.getId() + " => " + a.getActivityCost());
                                Log.d(TAG, "this is id " + document.getId());
                            }
                            //Log.d(TAG, String.valueOf(activityList.size()));
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

        btnFilter1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent switchActivityIntent = new Intent(MainPage.this, Filter1Activity.class);
                startActivity(switchActivityIntent);
            }
        });
        btnFilter2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent switchActivityIntent = new Intent(MainPage.this, Filter2Activity.class);
                startActivity(switchActivityIntent);
            }
        });
        btnFilter3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent switchActivityIntent = new Intent(MainPage.this, Filter3Activity.class);
                startActivity(switchActivityIntent);
            }
        });
        btnFilter4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent switchActivityIntent = new Intent(MainPage.this, Filter4Activity.class);
                startActivity(switchActivityIntent);
            }
        });

    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }
    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }


    public void selectDrawerItem(MenuItem menuItem) {

        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_profile:
                fragmentClass = Profile.class;
                Log.d(TAG,"this is happening");

                break;
            case R.id.nav_createAct:
                fragmentClass = CreateActivity.class;

                break;
            case R.id.nav_actHistory:
                fragmentClass = MapsActivityCurrentPlace.class;

                break;
            case R.id.nav_main:
                fragmentClass = MainPage.class;

                break;
            default:
                fragmentClass = MainPage.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
