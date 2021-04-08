package com.example.eventsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventsapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import static android.text.TextUtils.isEmpty;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";

    private ProgressBar progressBar;
    private EditText editTextUsername, editTextEmail, editTextPassword, editTextConfirmPassword;

    // Declare an instance of FirebaseAuth
    //private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextUsername = (EditText) findViewById(R.id.etUsername);
        editTextEmail = (EditText) findViewById(R.id.etEmail);
        editTextPassword = (EditText) findViewById(R.id.etPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        findViewById(R.id.btnRegister).setOnClickListener(this);

        // Initialize the FirebaseAuth instance
        //mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        hideSoftKeyboard();
    }

    /**
     * Register a new email and password to Firebase Authentication
     * @param email
     * @param password
     */
    public void registerNewEmail(final String email, String password){

        showDialog();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                    if (task.isSuccessful()){
                        Log.d(TAG, "onComplete: AuthState: " + FirebaseAuth.getInstance().getCurrentUser().getUid());

                        //insert some default data
                        User user = new User();
                        user.setEmail(email);
                        user.setUsername(editTextUsername.getText().toString().trim());
                        user.setUser_id(FirebaseAuth.getInstance().getUid());

                        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().build();
                        mDb.setFirestoreSettings(settings);

                        DocumentReference newUserRef = mDb
                                .collection(getString(R.string.collection_users))
                                .document(FirebaseAuth.getInstance().getUid());

                        newUserRef.set(user).addOnCompleteListener(task1 -> {
                            hideDialog();

                            if(task1.isSuccessful()){
                                redirectLoginScreen();
                            }else{
                                View parentLayout = findViewById(android.R.id.content);
                                Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
                            }
                        });

                    }
                    else {
                        View parentLayout = findViewById(android.R.id.content);
                        Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
                        hideDialog();
                    }
                });
    }

    /**
     * Redirects the user to the login screen
     */
    private void redirectLoginScreen(){
        Log.d(TAG, "redirectLoginScreen: redirecting to login screen.");

        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showDialog(){
        progressBar.setVisibility(View.VISIBLE);
    }
    private void hideDialog(){
        if(progressBar.getVisibility() == View.VISIBLE){
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    // Handle all onClick requests
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.banner:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.btnRegister:
                //registerUser();
                registerUser2();
                break;

        }
    }

    public boolean doStringsMatch(String s1, String s2){
        return s1.equals(s2);
    }

    //previous registeruser()
    /*
    private void registerUser() {

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String fullName = editTextUsername.getText().toString().trim();


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
                            "Failed to register :(, Please try again.", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
     */

    private void registerUser2() {
        Log.d(TAG, "onClick: attempting to register.");

        //check for null valued EditText fields
        if(!isEmpty(editTextEmail.getText().toString())
                && !isEmpty(editTextPassword.getText().toString())
                && !isEmpty(editTextConfirmPassword.getText().toString())){

            //check if passwords match
            if(doStringsMatch(editTextPassword.getText().toString(), editTextConfirmPassword.getText().toString())){

                //Initiate registration task
                registerNewEmail(editTextEmail.getText().toString(), editTextPassword.getText().toString());
            }else{
                Toast.makeText(RegisterActivity.this, "Passwords do not Match", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(RegisterActivity.this, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
        }
    }
}