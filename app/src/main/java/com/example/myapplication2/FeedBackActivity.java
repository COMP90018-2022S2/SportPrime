package com.example.myapplication2;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FeedBackActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String imageUrl = "";
    private TextView editActivityName;
    private TextView editActivityPeople;
    private TextView editActivityLocation;
    private EditText etContent;
    private RatingBar ratingBar;
    private ImageView ivImg;
    String[] requestPermission = new String[]{Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE};
    List<String> permissionList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Uri imageUri;
    private String id;
    private String name;
    private String location, people;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        receiveInfo();
        progressDialog = new ProgressDialog(this);
//        editActivityName = findViewById(R.id.editActivityName);
//        editActivityName.setText(name);
//        editActivityPeople = findViewById(R.id.editActivityPeople);
//        editActivityPeople.setText(people);
//        editActivityLocation = findViewById(R.id.editActivityLocation);
//        editActivityLocation.setText(location);
        etContent = findViewById(R.id.etContent);
        ivImg = findViewById(R.id.ivImg);
        ratingBar = findViewById(R.id.ratingBar);
        findViewById(R.id.btSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
        ivImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPic();
            }
        });
    }
    private void receiveInfo(){
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        name = intent.getStringExtra("name");
        location = intent.getStringExtra("location");
        people = intent.getStringExtra("people");

    }

    private void getPic() {
        permissionList.clear();
        for (int i = 0; i < requestPermission.length; i++) {
            if (ContextCompat.checkSelfPermission(FeedBackActivity.this, requestPermission[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(requestPermission[i]);
            }
        }
        if (permissionList.isEmpty()) {
            showDialog();
        } else {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(FeedBackActivity.this, permissions, 1);
        }
    }
    private void showDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Select a picture")
                .setPositiveButton("album", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(intent, 2);
                        }
                    }
                }).setNegativeButton("photograph", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        File outputImage = new File(getExternalCacheDir(), System.currentTimeMillis()+".jpg");
                        try {
                            if (outputImage.exists()) {
                                outputImage.delete();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            imageUri = FileProvider.getUriForFile(FeedBackActivity.this, "com.example.myapplication2.fileprovider", outputImage);
                        } else {
                            imageUri = Uri.fromFile(outputImage);
                        }

                        //启动相机程序
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //MediaStore.ACTION_IMAGE_CAPTURE = android.media.action.IMAGE_CAPTURE
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, 3);
                    }
                }).create().show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 2) {
                final Uri uri = data.getData();
                uploadImage(uri);
                ivImg.setImageURI(uri);
            }
            if (requestCode == 3) {
                uploadImage(imageUri);
                ivImg.setImageURI(imageUri);
            }
        }

    }

    private void uploadImage(Uri uri) {
        progressDialog.show();
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://comp90018-project-sport-p.appspot.com");
        StorageReference storageReference = reference.child(System.currentTimeMillis()+".jpg");
        UploadTask uploadTask = storageReference.putFile(uri);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();


                }
            }
        });
    }

    public void submit() {
        String data = checkAllFilled();
        if (!TextUtils.isEmpty(data)){
            return;
        }
        Map<String, Object> objectHashMap = new HashMap<>();
        objectHashMap.put("name", name);
        objectHashMap.put("people",people);
        objectHashMap.put("location", location);
        objectHashMap.put("content",etContent.getText().toString());
        objectHashMap.put("rating",ratingBar.getRating());
        objectHashMap.put("image",imageUrl);
        objectHashMap.put("id", id);
        db.collection("feedback")
                .add(objectHashMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        new AlertDialog.Builder(FeedBackActivity.this)
                                .setCancelable(false)
                                .setTitle("Tip")
                                .setMessage("FeedBack successfully")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        finish();
                                        Intent switchActivityIntent = new Intent(FeedBackActivity.this, MainPage.class);
                                        startActivity(switchActivityIntent);
                                    }
                                }).create().show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(FeedBackActivity.this);
                        builder.setCancelable(true);
                        builder.setTitle("Something went wrong -.-!");
                        builder.setMessage("Please try again");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();
                    }
                });
    }

    public String checkAllFilled(){
        if (TextUtils.isEmpty(editActivityName.getText().toString().trim())){
            return "name";
        }
        if (TextUtils.isEmpty(editActivityPeople.getText().toString().trim())){
            return "people";
        }
        if (TextUtils.isEmpty(editActivityLocation.getText().toString().trim())){
            return "location";
        }
        if (TextUtils.isEmpty(etContent.getText().toString().trim())){
            return "feedback";
        }
        if (TextUtils.isEmpty(imageUrl)){
            return "image";
        }
        return "";
    }


}