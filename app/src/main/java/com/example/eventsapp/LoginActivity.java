package com.example.eventsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.eventsapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import static android.text.TextUtils.isEmpty;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG ="Login Activity";

    // Firebase Auth Listener (auto login)
    private FirebaseAuth.AuthStateListener mAuthListener;
    //private FirebaseAuth mAuth;

    private EditText editTextEmail, editTextPassword;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = (EditText) findViewById(R.id.etEmail);
        editTextPassword = (EditText) findViewById(R.id.etPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //mAuth = FirebaseAuth.getInstance();
        setupFirebaseAuth();
        findViewById(R.id.tvForgotPassword).setOnClickListener(this);
        findViewById(R.id.tvCreateAccount).setOnClickListener(this);
        findViewById(R.id.btnLogin).setOnClickListener(this);
    }

    // Firebase Setup
    private void setupFirebaseAuth(){

        Log.d(TAG, "setupFirebaseAuth: started.");

        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                Toast.makeText(LoginActivity.this, "Authenticated with: " + user.getEmail(), Toast.LENGTH_SHORT).show();

/*              FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().build();
                db.setFirestoreSettings(settings);

                DocumentReference userRef = db.collection(getString(R.string.collection_users)).document(user.getUid());

                userRef.get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: successfully set the user client.");
                        User user1 = task.getResult().toObject(User.class);
                        ((UserClient)(getApplicationContext())).setUser(user1);
                    }
                });
                */

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            // Take us to register Activity Layout
            case R.id.tvCreateAccount:
                startActivity(new Intent(this, RegisterActivity.class));
                break;

            case R.id.tvForgotPassword:
                startActivity(new Intent(this, ForgotPassword.class));
                break;

            case R.id.btnLogin:
                //userLogin();
                signIn();
                break;
        }
    }

    private void showDialog(){
        progressBar.setVisibility(View.VISIBLE);

    }
    private void hideDialog(){
        if(progressBar.getVisibility() == View.VISIBLE){
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    //previous userLogin()
    /*
    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // validate email
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please provide a valid email address.");
            editTextEmail.requestFocus();
            return;
        }
        // validate password
        if (password.isEmpty() || password.length() < 6) {
            editTextPassword.setError("Please provide a password of at least 6 characters.");
            editTextPassword.requestFocus();
            return;
        }

        // keep progress bar spinning until user is logged in.
        progressBar.setVisibility(View.VISIBLE);

        // object to sign user in
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    // verify email first
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user.isEmailVerified()){
                        // redirect to user profile
                        Toast.makeText(LoginActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        // send an email notification link
                        user.sendEmailVerification();
                        Toast.makeText(LoginActivity.this,
                                "Please check your email to verify your account.", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(LoginActivity.this,
                            "Cannot Log In.\nPlease check your credentials.", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
     */

    private void signIn(){
        //check if the fields are filled out
        if(!isEmpty(editTextEmail.getText().toString()) && !isEmpty(editTextPassword.getText().toString())){
            Log.d(TAG, "onClick: attempting to authenticate.");

            showDialog();

            FirebaseAuth.getInstance().signInWithEmailAndPassword(editTextEmail.getText().toString(),
                    editTextPassword.getText().toString())
                    .addOnCompleteListener(task -> hideDialog()).addOnFailureListener(e -> {
                        Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        hideDialog();
                    });
        }else{
            Toast.makeText(LoginActivity.this, "Please ensure no fields are empty.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }
}