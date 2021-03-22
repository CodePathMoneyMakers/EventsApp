package com.example.eventsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity{

    public static final int PASSWORD_MIN_LENGTH = 6;
    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword;
    private TextView tvEmail, tvPassword;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        btnRegister = (Button) findViewById(R.id.btnRegister);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);

        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvPassword = (TextView) findViewById(R.id.tvPassword);



        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
           public void onClick(View v) {
            registerUser();
            }
        });

    }


    private void registerUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if(email.isEmpty()){
            etEmail.setError("Username is required");
            etEmail.requestFocus();
            return;
        }
        else if(password.isEmpty()){
            etPassword.setError("Password is required." );
            etPassword.requestFocus();
            return;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("Please provide a valid email");
            etEmail.requestFocus();
            return;
        }
        else if(password.length() < PASSWORD_MIN_LENGTH) {
            etPassword.setError("Minimum password length should be 6 characters");
            etPassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    User user = new User(email);

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "user has been registered Successfully!", Toast.LENGTH_LONG).show();
                                // Progress bar here
                            } else {
                                Toast.makeText(RegisterActivity.this, "Failed to register :(, Please try agian.", Toast.LENGTH_LONG).show();
                                // Progress bar here
                            }
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "Failed to register :(, Please try agian.", Toast.LENGTH_LONG).show();
                    // Progress bar here
                }
            }
        });

    }
}