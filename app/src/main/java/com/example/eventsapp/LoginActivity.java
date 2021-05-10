package com.example.eventsapp;

import android.content.Intent;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG ="Login Activity";

    private TextView register, forgotPassword;
    private EditText editTextEmail, editTextPassword;
    private Button signIn;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*if(mAuth.getCurrentUser().getUid() != null) {
            goMainActivity();
        }*/

        //setSharedElementEnterTransition(new ChangeBounds());

        forgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
        forgotPassword.setOnClickListener(this);

        // Create Account button is pressed
        register = (TextView) findViewById(R.id.tvCreateAccount);
        register.setOnClickListener(this);

        signIn = (Button) findViewById(R.id.btnLogin);
        signIn.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.etEmail);
        editTextPassword = (EditText) findViewById(R.id.etPassword);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
    }

    // Method to handle all on click listeners for the Login Activity
    @Override
    public void onClick(View v) {
        switch (v.getId()){

            // Take us to register Activity Layout
            case R.id.tvCreateAccount:
                startActivity(new Intent(this, RegisterActivity.class));
                break;

            case R.id.btnLogin:
                userLogin();
                break;

            case R.id.tvForgotPassword:
                startActivity(new Intent(this, ForgotPassword.class));
                break;
        }
    }

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
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        // verify email first
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        if(user.isEmailVerified()){
                            // redirect to user profile
                            Toast.makeText(LoginActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
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
                });
    }

    /*private void goMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }*/
}