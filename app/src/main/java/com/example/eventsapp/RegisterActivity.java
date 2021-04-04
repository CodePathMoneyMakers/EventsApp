package com.example.eventsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventsapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    // Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;

    private TextView banner, registerUser;
    private EditText editTextUsername, editTextAge, editTextEmail, editTextPassword;
    private ProgressBar progressBar;

    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        banner = (TextView) findViewById(R.id.banner);
        banner.setOnClickListener(this);

        // Register User
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        // Initialize the edit text view variables
        editTextUsername = (EditText) findViewById(R.id.etUsername);
        editTextAge = (EditText) findViewById(R.id.etAge);
        editTextEmail = (EditText) findViewById(R.id.etEmail);
        editTextPassword = (EditText) findViewById(R.id.etPassword);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    // Handle all onClick requests
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.banner:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.btnRegister:
                registerUser();
                break;

        }
    }


    private void registerUser() {

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String fullName = editTextUsername.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();


        if (fullName.isEmpty()) {
            editTextUsername.setError("Full name is required.");
            // refocus on the field
            editTextUsername.requestFocus();
            return;
        }
        else if (age.isEmpty()) {
            editTextAge.setError("Age is required.");
            editTextAge.requestFocus();
            return;
        }
        else if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please provide a valid email address.");
            editTextEmail.requestFocus();
            return;
        }
        else if (password.isEmpty() || password.length() < 6) {
            editTextPassword.setError("Please provide a password of at least 6 characters.");
            editTextPassword.requestFocus();
            return;
        }

        // Set visibility of progress bar to true
        progressBar.setVisibility(View.VISIBLE);

        // firebase object authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                // check if user is registered
                if (task.isSuccessful()) {

                    // create an android studio User object
                    User user = new User(fullName, age, email);

                    // create a firebase User Object
                    FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();

                    // send user object to real time database
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.VISIBLE);
                                fbUser.sendEmailVerification();
                                Toast.makeText(RegisterActivity.this,
                                        "You have been registered Successfully!", Toast.LENGTH_LONG).show();
                                Toast.makeText(RegisterActivity.this,
                                        "Please check your email to complete sign up!", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            } else {
                                Toast.makeText(RegisterActivity.this,
                                        "Failed to register :( Please try again.", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this,
                            "Failed to register :(, Please try agian.", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}