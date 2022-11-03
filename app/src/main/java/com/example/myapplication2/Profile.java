package com.example.myapplication2;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class Profile extends AppCompatActivity {
    private TextView nameTextView, ageTextView, genderTextView;
    private TextView emailTextView, mobileTextView, descriptionTextView;
    private ImageView userImageView, emailImageView, mobileImageView, descriptionImageView;
    private Button logout, edit;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String storagePath = "gs://comp90018-project-sport-p.appspot.com";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();


        nameTextView = findViewById(R.id.fullname);
        ageTextView = findViewById(R.id.age);
        genderTextView = findViewById(R.id.gender);
        emailTextView = findViewById(R.id.email_textview);
        mobileTextView = findViewById(R.id.mobile_textview);
        descriptionTextView = findViewById(R.id.description_textview);
        userImageView = findViewById(R.id.user_image);
        emailImageView = findViewById(R.id.email_imageview);
        mobileImageView = findViewById(R.id.mobile_imageview);
        descriptionImageView = findViewById(R.id.description_imageview);
        logout = findViewById(R.id.logoutBtn);
        edit = findViewById(R.id.edit);

        setUserData();

        setGraph();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    SystemInfo.setImageBitmap(null);
                    //根据需要更换
                    Intent intent = new Intent(Profile.this, Login.class);
                    startActivity(intent);
                    finish();
                }

            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, EditProfile.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void setGraph() {
        Bitmap bitImage = SystemInfo.getImageBitmap();
        if(bitImage!=null){
            userImageView.setImageBitmap(bitImage);
            return;
        }
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(storagePath);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            return;
        }
        String imagePath = currentUser.getEmail() + "/profile-image.jpg";
        StorageReference image = storageRef.child(imagePath);
        final long ONE_MEGABYTE = 1024 * 1024;

        image.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                userImageView.setImageBitmap(bmp);
                SystemInfo.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(Profile.this, exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUserData() {
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
                                        String mobileNumber = (String) documentData.get("MobileNumber");
                                        mobileTextView.setText(mobileNumber);
                                        String age=(String) documentData.get("Age");
                                        ageTextView.setText(age);
                                        String gender=(String) documentData.get("Gender");
                                        genderTextView.setText(gender);
                                        String description=(String) documentData.get("Description");
                                        descriptionTextView.setText(description);

                                        emailTextView.setText(email);
                                        String fullName = (String) documentData.get("FullName");
                                        nameTextView.setText(fullName);
                                        return;
                                    }
                                }
                                Toast.makeText(Profile.this, "Can not get user information from database, no matched email address with login email", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        }


    }
}