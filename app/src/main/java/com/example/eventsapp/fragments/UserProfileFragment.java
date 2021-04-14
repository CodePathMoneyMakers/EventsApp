package com.example.eventsapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventsapp.LoginActivity;
import com.example.eventsapp.MainActivity;
import com.example.eventsapp.ProfileActivity;
import com.example.eventsapp.R;
import com.example.eventsapp.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class UserProfileFragment extends Fragment  {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private ImageButton logOut;
    private ImageView ivProfileImage;
    private Uri imageUri;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private ImageButton btnEdit;
    private EditText etBio;
    private String fullName, email, age, bio;

    // Required empty public constructor
    public UserProfileFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.options, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragments_profile, container,false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        logOut = view.findViewById(R.id.btnSignOut);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get an instance of firebase auth
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

    //  final TextView greetingTextView = (TextView) view.findViewById(R.id.welcome);
        final TextView fullNameTextView = (TextView) view.findViewById(R.id.tvFullName);
        final TextView emailTextView = (TextView) view.findViewById(R.id.tvEmail);
        final TextView ageTextView = (TextView) view.findViewById(R.id.tvAge);

        ivProfileImage = view.findViewById(R.id.ivProfileImage);

        etBio = (EditText) view.findViewById(R.id.etBio);

        btnEdit = view.findViewById(R.id.btnEdit);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });
        
        RetrieveUserInfo();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });



        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // create a user object
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null) {
                    // a user has these attributes
                    fullName = userProfile.fullName;
                    email = userProfile.email;
                    age = userProfile.age;


                    // set information to the layout
                    //greetingTextView.setText("Welcome, " + fullName + "!");
                    fullNameTextView.setText(fullName);
                    emailTextView.setText(email);
                    ageTextView.setText(age);
                }
            }

            // error handling
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),
                        "Something wrong happened.", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void showPopup(View view){
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) this);
        popupMenu.inflate(R.menu.options);
        popupMenu.show();
    }

    private void RetrieveUserInfo() {
        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()  && (snapshot.hasChild("bio")) && (snapshot.hasChild("image"))){
                    String retrieveUserName = snapshot.child("bio").getValue().toString();
                    String retrieveProfileImage = snapshot.child("image").getValue().toString();

                    etBio.setText(retrieveUserName);
                }
                else if(snapshot.exists()  && (snapshot.hasChild("bio"))){
                    String retrieveUserName = snapshot.child("bio").getValue().toString();
                }
                else{
                    Toast.makeText(getContext(), "Update profile here", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void UpdateSettings() {
        bio = etBio.getText().toString();

            HashMap<String, String> profileMap = new HashMap<>();
                profileMap.put("bio", bio);
                profileMap.put("fullName", String.valueOf(fullName));
                profileMap.put("email", String.valueOf(email));
                profileMap.put("age", String.valueOf(age));

                reference.child(userID).setValue(profileMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getContext(),
                                            "Profile updated.", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    String message = task.getException().toString();
                                    Toast.makeText(getContext(), "Error" + message, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
      //  }
    }

    private void choosePicture(){
        /*
        Create file variable, store image into file variable
         */
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData(); // gets file
            ivProfileImage.setImageURI(imageUri);
            uploadPicture();
        }
    }

    private void uploadPicture() {

        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setTitle("Uploading Image...");
        pd.show();

        final String randomKey = UUID.randomUUID().toString();
       StorageReference riversRef = storageReference.child("images/" + randomKey);

       riversRef.putFile(imageUri)
               .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){

                   @Override
                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       pd.dismiss();
                       Toast.makeText(getContext(), "Image Upload", Toast.LENGTH_SHORT).show();
                   }
               })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       pd.dismiss();
                       Toast.makeText(getContext(), "Failed to Upload.", Toast.LENGTH_LONG).show();
                   }
               })
               .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                       double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                       pd.setMessage("Percentage: " + (int) progressPercent + "%");
                   }
               });
    }

//    @Override
//    public boolean onMenuItemClick(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.itLogOut:
//                Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(getContext(), LoginActivity.class));
//                return true;
//            case R.id.itEdit:
//                Toast.makeText(getContext(), "Edit profile", Toast.LENGTH_SHORT).show();
//                UpdateSettings();
//                return true;
//            default:
//                return false;
//        }
//    }
}
