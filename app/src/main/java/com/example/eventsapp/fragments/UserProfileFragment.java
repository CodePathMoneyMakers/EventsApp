package com.example.eventsapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.eventsapp.LoginActivity;
import com.example.eventsapp.R;
import com.example.eventsapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class UserProfileFragment extends Fragment {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private Button logOut;
    private ImageView ivProfileImage;
    private Uri imageUri;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private Button btnEdit;
    private CircleImageView userProfileImage;
    private EditText etBio;
    private String fullName, email, age, bio;

    // Required empty public constructor
    public UserProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.options, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

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
      //  setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragments_profile, container,false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        logOut = (Button) view.findViewById(R.id.btnSignOut);
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

        final TextView greetingTextView = (TextView) view.findViewById(R.id.welcome);
        final TextView fullNameTextView = (TextView) view.findViewById(R.id.tvFullName);
        final TextView emailTextView = (TextView) view.findViewById(R.id.tvEmail);
        final TextView ageTextView = (TextView) view.findViewById(R.id.tvAge);

        ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);

        etBio = (EditText) view.findViewById(R.id.etBio);

        btnEdit = (Button) view.findViewById(R.id.btnEdit);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });

        RetrieveUserInfo();

       // storage = FirebaseStorage.getInstance();
       // storageReference = storage.getReference().child("Profile Images");

        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Images");

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
                    greetingTextView.setText("Welcome, " + fullName + "!");
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

    private void RetrieveUserInfo() {
        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()  && (snapshot.hasChild("bio")) && (snapshot.hasChild("image"))){
                    String retrieveUserName = snapshot.child("bio").getValue().toString();
                    String retrieveProfileImage = snapshot.child("image").getValue().toString();

                    etBio.setHint(retrieveUserName);
                }
                else if(snapshot.exists()  && (snapshot.hasChild("bio"))){
                    String retrieveUserName = snapshot.child("bio").getValue().toString();

                    etBio.setHint(retrieveUserName);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData(); // gets file

            // start picker to get image for cropping and then use the image in cropping activity
          /*  CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start((Activity) getContext());


            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK){
                    Uri resultUri = result.getUri();

                    StorageReference filePath = storageReference.child(userID + ".jpg");

                    filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getContext(), "Image Uploaded Successfully.", Toast.LENGTH_LONG).show();
                            }
                            else{
                               String message= task.getException().toString();
                                Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
                 */
            uploadPicture();
          //  ivProfileImage.setImageURI(imageUri);


        }
    }

    private void uploadPicture() {

        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setTitle("Uploading Image...");
        pd.show();

        final String randomKey = UUID.randomUUID().toString();
       StorageReference riversRef = storageReference.child("images/" + randomKey);
       StorageReference filePath = storageReference.child(userID + ".jpg");

       filePath.putFile(imageUri)
               .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                   @Override
                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                       pd.dismiss();
                       Toast.makeText(getContext(), "Image Uploaded", Toast.LENGTH_LONG).show();
                    //   Snackbar.make(getView().findViewById(android.R.id.content), "Image Uploaded.", Snackbar.LENGTH_LONG).show();
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
}
