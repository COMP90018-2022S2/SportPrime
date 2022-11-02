package com.user.a2;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {
    private String documentId;
    private String email;
    private String fullName;

    private EditText txtAge, txtGender, txtMobile, txtDescription;
    private ImageView userImageView;
    private Uri filePath;
    private ProgressDialog pd;
    private FirebaseAuth mAuth;
    private Button saveButton;
    private String storagePath = "gs://comp90018-project-sport-p.appspot.com";
    private Bitmap bitmap;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        //creating reference to firebase storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(storagePath);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


        mAuth = FirebaseAuth.getInstance();
        txtAge = findViewById(R.id.edtage);
        txtGender = findViewById(R.id.edtgender);
        txtMobile = findViewById(R.id.edtmobile);
        txtDescription = findViewById(R.id.edtdescription);
        userImageView = findViewById(R.id.uploadImage);
        saveButton = findViewById(R.id.save);
        if (SystemInfo.getImageBitmap()!=null){
            userImageView.setImageBitmap(SystemInfo.getImageBitmap());
        }

        pd = new ProgressDialog(this);
        pd.setMessage("Uploading profile image to firebase....");

        setUserData();
        userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), 111);

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentUser!=null){
                    saveUserInfoToDB();
                }
                if (filePath != null&& currentUser != null) {
                    pd.show();
                    String imagePath=currentUser.getEmail()+"/profile-image.jpg";
                    StorageReference childRef = storageRef.child(imagePath);
                    //uploading the image
                    UploadTask uploadTask = childRef.putFile(filePath);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            Toast.makeText(EditProfile.this, "Upload successful", Toast.LENGTH_SHORT).show();
                            SystemInfo.setImageBitmap(null);
                            Intent intent = new Intent(EditProfile.this, Profile.class);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(EditProfile.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else{
                    pd.dismiss();
                    Intent intent = new Intent(EditProfile.this, Profile.class);
                    startActivity(intent);
                    finish();

                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                //getting image from gallery
               bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting image to ImageView
                userImageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void saveUserInfoToDB(){
        Map<String, Object> user=new HashMap<>();
        user.put("FullName", fullName);
        user.put("Email", email);
        user.put("MobileNumber",txtMobile.getText().toString().trim());
        user.put("Age", txtAge.getText().toString().trim());
        user.put("Gender", txtGender.getText().toString().trim());
        user.put("Description", txtDescription.getText().toString().trim());

        db.collection("users").document(documentId)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

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
                                    email = (String) documentData.get("Email");

                                    if (userEmail != null && userEmail.equals(email)) {
                                        documentId=document.getId();
                                        String mobileNumber = (String) documentData.get("MobileNumber");
                                        txtMobile.setText(mobileNumber);

                                        fullName = (String) documentData.get("FullName");
                                        return;
                                    }
                                }
                                Toast.makeText(EditProfile.this, "Can not get user information from database, no matched email address with login email", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        }


    }
}
