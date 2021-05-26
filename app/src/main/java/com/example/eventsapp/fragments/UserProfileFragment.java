package com.example.eventsapp.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventsapp.Event;
import com.example.eventsapp.EventsAdapter;
import com.example.eventsapp.LoginActivity;
import com.example.eventsapp.R;
import com.example.eventsapp.RequestsActivity;
import com.example.eventsapp.adapters.RSVPRecyclerAdapter;
import com.example.eventsapp.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class UserProfileFragment extends Fragment  {
    private boolean showingFirst = true;
    private FirebaseUser user;
    FirebaseRecyclerOptions<User> options2;
    FirebaseRecyclerAdapter<User, RSVPRecyclerAdapter> adapter2;
    RecyclerView profileRecycler;
    RecyclerView rsvpRecyclerView;
    RSVPRecyclerAdapter rsvpRecyclerAdapter;
    private DatabaseReference reference;
    private String userID, test;
    private ImageButton logOut;
    private ImageView ivProfileImage;
    private Uri imageUri;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    public DatabaseReference UsersRef, DataRef, EventsRef, eventID, rsvpRef;
    DatabaseReference requestsRef;
    private ImageButton btnEdit, btnSettings;
    private EditText etBio;
    private TextView numAttending, numCreated;
    private String fullName, email, age, bio, userImage, currentUserID, event;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;
    FirebaseRecyclerOptions<Event> options;
    FirebaseRecyclerAdapter<Event, EventsAdapter> adapter;
    boolean isImageAdded = false;
    ArrayList<String> keyList, idList;

    private ImageButton settings;
    String TAG = "UserProfileFragment";
    public StorageReference Storageref;

    Uri selectedImageUri;

    // Required empty public constructor
    public UserProfileFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.options, menu);
         super.onCreateOptionsMenu(menu, inflater);
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


        ImageView settings = view.findViewById(R.id.Settings);
        ImageButton logout = view.findViewById(R.id.logout);
        ImageButton edit = view.findViewById(R.id.edit);
        ImageButton rsvp = view.findViewById(R.id.rsvp);


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showingFirst == true){
                    ObjectAnimator rotate = ObjectAnimator.ofFloat(settings, "rotation", 0f, 180f);
                    ObjectAnimator animateY = ObjectAnimator.ofFloat(logout, "y", 1330f);
                    ObjectAnimator animateY2 = ObjectAnimator.ofFloat(edit, "y", 1200f);
                    ObjectAnimator animateY3 = ObjectAnimator.ofFloat(rsvp, "y", 1070f);
                    rotate.setDuration(1000);
                    animateY.setDuration(500);
                    animateY2.setDuration(500);
                    animateY3.setDuration(500);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(rotate, animateY, animateY2, animateY3);
                    animatorSet.start();
                    showingFirst = false;
                }else{
                    ObjectAnimator rotate = ObjectAnimator.ofFloat(settings, "rotation", 180f, 0f);
                    ObjectAnimator animateY = ObjectAnimator.ofFloat(logout, "y", 1465f);
                    ObjectAnimator animateY2 = ObjectAnimator.ofFloat(edit, "y", 1465f);
                    ObjectAnimator animateY3 = ObjectAnimator.ofFloat(rsvp, "y", 1465f);
                    rotate.setDuration(1000);
                    animateY.setDuration(500);
                    animateY2.setDuration(500);
                    animateY3.setDuration(500);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(rotate, animateY, animateY2, animateY3);
                    animatorSet.start();
                    showingFirst = true;
                }

            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get an instance of firebase auth
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        rsvp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.rsvp_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                rsvpRecyclerView = dialog.findViewById(R.id.rsvp_recycler);
                rsvpRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                rsvpRecyclerView.setHasFixedSize(true);

//                rsvpRecyclerView = dialog.findViewById(R.id.rsvp_recycler);
//                rsvpRecyclerAdapter = new RSVPRecyclerAdapter();
//                rsvpRecyclerView.setAdapter(rsvpRecyclerAdapter);

                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                itemTouchHelper.attachToRecyclerView(rsvpRecyclerView);

                LoadData();
                dialog.show();

            }
        });

        // eventID = getActivity().getIntent().getStringExtra("EventID");
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        DataRef = FirebaseDatabase.getInstance().getReference().child("Events");
        keyList = new ArrayList<>();
        idList = new ArrayList<>();


        userID = user.getUid();
        currentUserID = mAuth.getCurrentUser().getUid();
        // TODO FIX ME
        rsvpRef = FirebaseDatabase.getInstance().getReference().child("RSVP");
        requestsRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        EventsRef = FirebaseDatabase.getInstance().getReference().child("Users");
        //      test = "hello";

        //  final TextView greetingTextView = (TextView) view.findViewById(R.id.welcome);
        final TextView fullNameTextView = (TextView) view.findViewById(R.id.tvFullName);
        final TextView emailTextView = (TextView) view.findViewById(R.id.tvEmail);
        final TextView ageTextView = (TextView) view.findViewById(R.id.tvAge);
        profileRecycler = view.findViewById(R.id.profileRecycler);
        profileRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        profileRecycler.setHasFixedSize(true);


        Storageref = FirebaseStorage.getInstance().getReference().child("UserImage");

        ivProfileImage = view.findViewById(R.id.ivProfileImage);

        etBio = (EditText) view.findViewById(R.id.etBio);
        numAttending = (TextView) view.findViewById(R.id.numAttending);
        numCreated = (TextView) view.findViewById(R.id.numCreated);

        //btnEdit = view.findViewById(R.id.btnEdit);


//        btnEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                UpdateSettings();
//            }
//        });
//        fab2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(getContext(), LoginActivity.class));
//            }
//        });
//        logOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(getContext(), LoginActivity.class));
//            }
//        });
        RetrieveUserInfo();
        LoadData("");
        // storage = FirebaseStorage.getInstance();
        // storageReference = storage.getReference().child("Profile Images");

        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Images");

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // choosePicture();
                openCamera(ivProfileImage);
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
                    fullNameTextView.setText(fullName + ", " + age);
                    emailTextView.setText(email);
                  //  ageTextView.setText(age);
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

    private void LoadData() {
        Query query = requestsRef.child(currentUserID);

        options2 = new FirebaseRecyclerOptions.Builder<User>().setQuery(query, User.class).build();
        adapter2 = new FirebaseRecyclerAdapter<User, RSVPRecyclerAdapter>(options2) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull RSVPRecyclerAdapter rsvpRecyclerAdapter, int i, @NonNull @NotNull User user) {
                String eventID = getRef(i).getKey();
                keyList.add(eventID);
                Log.d(TAG, "Look here: " + i);

                assert eventID != null;
                requestsRef.child(currentUserID).child(eventID).addValueEventListener(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            String name = snapshot.child("name").getValue().toString();
                            String event = snapshot.child("event").getValue().toString();
                            String image = snapshot.child("image").getValue().toString();
                            String email = snapshot.child("email").getValue().toString();
                            String uid = snapshot.child("UID").getValue().toString();

                            rsvpRecyclerAdapter.tvFullName.setText(name);
                            rsvpRecyclerAdapter.tvEmail.setText(email);
                            rsvpRecyclerAdapter.tvEventTitle.setText(event);
                            Picasso.get().load(image).into(rsvpRecyclerAdapter.profileImage);

                            idList.add(uid);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public RSVPRecyclerAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rsvp_recycler_layout, parent, false);
                return new RSVPRecyclerAdapter(v);
            }
        };

        adapter2.startListening();
        rsvpRecyclerView.setAdapter(adapter2);
    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    int position = viewHolder.getAdapterPosition();

                    switch (direction){
                        case ItemTouchHelper.LEFT:
                            Toast.makeText(getContext(), "swiped left", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "ArrayList + " + keyList);
                            requestsRef.child(currentUserID).child(keyList.get(position)).removeValue();

                            keyList.remove(position);
                            idList.remove(position);

                            Log.d(TAG, "KeyList + " + keyList);
                            Log.d(TAG, "IDList + " + idList);


                            break;
                        case ItemTouchHelper.RIGHT:
                            Toast.makeText(getContext(), "swiped right", Toast.LENGTH_SHORT).show();
                            //rsvpRef.child(keyList.get(position)).child(idList.get(position)).setValue(idList.get(position));
                            EventsRef.child(keyList.get(position)).child("Attendees").child(idList.get(position)).setValue(idList.get(position));
                            requestsRef.child(currentUserID).child(keyList.get(position)).removeValue();

                            keyList.remove(position);
                            idList.remove(position);
                            break;
                    }

                }
            };

        public void LoadData (String s){
            Query query = DataRef.orderByChild("userID").equalTo(currentUserID);

            options = new FirebaseRecyclerOptions.Builder<Event>().setQuery(query, Event.class).build();
            adapter = new FirebaseRecyclerAdapter<Event, EventsAdapter>(options) {
                @Override
                protected void onBindViewHolder(@NonNull EventsAdapter eventsAdapter, int i, @NonNull Event event) {
                    eventsAdapter.eventTitle.setText(event.getEventTitle());
                    eventsAdapter.eventGenre.setText(event.getEventGenre());
                    eventsAdapter.eventFee.setText(event.getEventFee());
                    eventsAdapter.eventDay.setText(event.getEventDay());
                    eventsAdapter.eventMonth.setText(event.getEventMonth());
                    Picasso.get().load(event.getEventImage()).into(eventsAdapter.eventImage);
                    eventsAdapter.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent (getContext(), RequestsActivity.class);
                            intent.putExtra("EventID", getRef(i).getKey());
                            startActivity(intent);
                        }
                    });

                }

                @NonNull
                @Override
                public EventsAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
                    return new EventsAdapter(v);
                }
            };

            adapter.startListening();
            profileRecycler.setAdapter(adapter);
        }


    public void showPopup(View view){
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) this);
        popupMenu.inflate(R.menu.options);
        popupMenu.show();
    }

    private void RetrieveUserInfo() {
        UsersRef.child(userID).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()  && (snapshot.hasChild("bio")) && (snapshot.hasChild("image"))){
                    String retrieveUserName = snapshot.child("bio").getValue().toString();
                    String retrieveProfileImage = snapshot.child("image").getValue().toString();
                    String userImage2 = snapshot.child("userImage").getValue().toString();

                    Picasso.get().load(userImage2).placeholder(R.drawable.ic_person).into(ivProfileImage);


                    etBio.setHint(retrieveUserName);
                    Long value = snapshot.child("Attending").getChildrenCount();
                    Long created = snapshot.child("Created").getChildrenCount();
                    numAttending.setText(String.valueOf(value));
                    numCreated.setText(String.valueOf(created));
                }
                else if(snapshot.exists()  && (snapshot.hasChild("userImage"))){
                    String retrieveUserName = snapshot.child("bio").getValue().toString();

                    String userImage2 = snapshot.child("userImage").getValue().toString();
                    Picasso.get().load(userImage2).placeholder(R.drawable.ic_person).into(ivProfileImage);

                    etBio.setHint(retrieveUserName);
                    Long value = snapshot.child("Attending").getChildrenCount();
                    numAttending.setText(String.valueOf(value));
                    Long created = snapshot.child("Created").getChildrenCount();

                        numCreated.setText(String.valueOf(created));

                }
                else if(snapshot.exists() && (snapshot.child("bio").exists())){
                   String retrieveUserName = snapshot.child("bio").getValue().toString();
                    etBio.setHint(retrieveUserName);
                    Long value = snapshot.child("Attending").getChildrenCount();
                    numAttending.setText(String.valueOf(value));
                    Long created = snapshot.child("Created").getChildrenCount();
                    numCreated.setText(String.valueOf(created));
                }
                else{
                    Long value = snapshot.child("Attending").getChildrenCount();
                    numAttending.setText(String.valueOf(value));
                    Long created = snapshot.child("Created").getChildrenCount();
                    numCreated.setText(String.valueOf(created));
                  //  String userImage2 = snapshot.child("userImage").getValue().toString();
                  //  Picasso.get().load(userImage2).placeholder(R.drawable.ic_person).into(ivProfileImage);

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
                profileMap.put("userImage", userImage);

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

    public void openCamera(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
            if(resultCode == RESULT_OK) {
                selectedImageUri = data.getData();
                InputStream inputStream = null;
                try {
                    assert selectedImageUri != null;
                    inputStream = getActivity().getContentResolver().openInputStream(selectedImageUri);
                    isImageAdded = true;

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                BitmapFactory.decodeStream(inputStream);
                ivProfileImage.setImageURI(selectedImageUri);
                Toast.makeText(getContext(), "Cover Image selected", Toast.LENGTH_SHORT).show();
                uploadImage(currentUserID);
            }
        }
    }

    private void uploadImage(String currentUserID) {
        final String key = UsersRef.push().getKey();

        Storageref.child(key +".jpg").putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Storageref.child(key +".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        userImage = uri.toString();

                    }
                });
            }
        });
    }


}
