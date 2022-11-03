package com.example.myapplication2;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Register extends AppCompatActivity {

    TextView txtSignIn;
    EditText edtFullName, edtEmail, edtMobile, edtPassword, edtConfirmPassword;
    ProgressBar progressBar;
    Button registerBtn;
    String txtFullName, txtEmail, txtMobile, txtPassword, txtConfirmPassword;
    private FirebaseAuth mAuth;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtSignIn = findViewById(R.id.txtSignIn);
        edtFullName = findViewById(R.id.fullname);
        edtEmail= findViewById(R.id.email);
        edtMobile = findViewById(R.id.phone);
        edtPassword = findViewById(R.id.password);
        edtConfirmPassword = findViewById(R.id.conPassword);
        progressBar = findViewById(R.id.signUpProgressBar);
        registerBtn = findViewById(R.id.registerBtn);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();

            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtFullName = edtFullName.getText().toString();
                txtEmail = edtEmail.getText().toString().trim();
                txtMobile = edtMobile.getText().toString().trim();
                txtPassword = edtPassword.getText().toString().trim();
                txtConfirmPassword = edtConfirmPassword.getText().toString().trim();

                if (!TextUtils.isEmpty(txtFullName)) {
                    if (!TextUtils.isEmpty(txtEmail)) {
                            if (!TextUtils.isEmpty(txtMobile)) {
                                if (txtMobile!= null) {
                                    if (!TextUtils.isEmpty(txtPassword)) {
                                        if (!TextUtils.isEmpty(txtConfirmPassword)) {
                                            if (txtConfirmPassword.equals(txtPassword)) {
                                                SignUpUser();

                                            } else {
                                                edtConfirmPassword.setError("Confirm Password and password should be same");
                                            }

                                        } else {
                                            edtConfirmPassword.setError("Confirm Password Field can not be empty");

                                        }

                                    } else {
                                        edtPassword.setError("Password Field can not be empty");
                                    }

                                } else {
                                    edtMobile.setError("Enter a valid Mobile");
                                }

                            } else {
                                edtMobile.setError("Mobile Number Field can not be empty");
                            }



                    } else {
                        edtEmail.setError("Email Field can not be empty");
                    }


                } else {
                    edtFullName.setError("Full Name Field can not be empty");
                }

            }
        });

    }

    private void SignUpUser() {



        mAuth.createUserWithEmailAndPassword(txtEmail, txtPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user!=null){
                                Toast.makeText(Register.this, "Register new user success. User email:" +user.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                                saveUserInfoToDB();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });



            }

            private void saveUserInfoToDB(){
                Map<String, Object> user=new HashMap<>();
                user.put("FullName", txtFullName);
                user.put("Email", txtEmail);
                user.put("MobileNumber",txtMobile);

                db.collection("users")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(Register.this,"Data Stored Successfully!",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(Register.this, Login.class);
                                startActivity(intent);
                                finish();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Register.this, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
}










