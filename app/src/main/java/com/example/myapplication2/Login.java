package com.example.myapplication2;

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

public class Login extends AppCompatActivity {
    TextView txtSignUp;
    EditText edtEmail, edtPassword;
    ProgressBar progressBar;
    Button loginBtn;
    String  txtEmail, txtPassword;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        txtSignUp=findViewById(R.id.txtSignUp);
        edtEmail = findViewById(R.id.email);
        edtPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.signInProgressBar);
        loginBtn= findViewById(R.id.loginBtn);


        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                //finish();
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtEmail = edtEmail.getText().toString().trim();
                txtPassword = edtPassword.getText().toString().trim();

                if (!TextUtils.isEmpty(txtEmail) && !TextUtils.isEmpty(txtPassword)) {
                    doLogin(txtEmail, txtPassword);
                } else {
                    edtEmail.setError("Email and password fields can not be empty");
                }
            }
        });
    }

    private void doLogin(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "User Login Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, Profile.class);
                            startActivity(intent);
                            finish();
                        } else {

                            Toast.makeText(Login.this, "Login failed, please check email and password.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public void onStart() {
         super.onStart();
         FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //根据需要更换Profile.class
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}