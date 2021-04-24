package com.example.eventsapp.fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.eventsapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;

public class ComposeFragment<p> extends Fragment implements OnMapReadyCallback{
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;
    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    public static final String TAG = "ComposeFragment";
    public int counter;
    public Boolean switchState;
    private GoogleMap mMap;
    private MapView mapView;
    public FirebaseAuth mAuth;
    public DatabaseReference UsersRef, EventsRef, GroupMessageKeyRef;
    public String currentGroupName, currentUserID, currentUserName, currentDate, currentTime,
            eventDescription, eventDate,eventTimeStart,eventTimeEnd, eventTitle, eventPrivacy, eventFee, eventMusic, eventImage;
    private TextView tvDate;
    private ImageButton calendar_btn;
    private ImageButton time_btn;
    private TextView tvTime;
    private TextView tvTime1;
    private TextView till;
    private TextView from;
    private ImageButton music_btn;
    private ImageButton fee_btn;
    private TextView tvMusic;
    private TextView tvFee;
    private ImageView visibility;
    private Switch aSwitch;
    private EditText etOrganization;
    private ImageButton location_btn;
    private EditText etLocation;
    private ImageView selectedImage;
    private ImageButton picture_btn;
    private ImageButton post_btn;
    private EditText etMultiline, etEventTitle;

    DatabaseReference Dayaref;
    public StorageReference Storageref;


    Uri selectedImageUri;
    boolean isImageAdded = false;

    int t1Hour, t1Minute, t2Hour, t2Minute;

    public ComposeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragments_compose, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvDate = view.findViewById(R.id.tvDate);
        calendar_btn = view.findViewById(R.id.calender_btn);
        time_btn = view.findViewById(R.id.time_btn);
        tvTime = view.findViewById(R.id.tvTime);
        tvTime1 = view.findViewById(R.id.tvTime1);
        till = view.findViewById(R.id.till);
        from = view.findViewById(R.id.from);
        music_btn = view.findViewById(R.id.music_btn);
        fee_btn = view.findViewById(R.id.fee_btn);
        tvMusic = view.findViewById(R.id.tvMusic);
        tvFee = view.findViewById(R.id.tvFee);
        visibility = view.findViewById(R.id.visibility);
        aSwitch = view.findViewById(R.id.switch1);
        etOrganization = view.findViewById(R.id.etOrganization);
        location_btn = view.findViewById(R.id.set_btn);
        etLocation = view.findViewById(R.id.etLocation);
        picture_btn = view.findViewById(R.id.picture_btn);
        selectedImage = view.findViewById(R.id.selectedImage);
        post_btn = view.findViewById(R.id.post_btn);
        etMultiline = view.findViewById(R.id.etMultiline);
        etEventTitle = view.findViewById(R.id.etEventTitle);

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        mAuth = FirebaseAuth.getInstance();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        EventsRef = FirebaseDatabase.getInstance().getReference().child("Events");
        Storageref = FirebaseStorage.getInstance().getReference().child("EventImage");

        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateEvent();
            }
        });



        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();

        CalendarConstraints.Builder constraintBuilder = new CalendarConstraints.Builder();
        constraintBuilder.setValidator(DateValidatorPointForward.now());

        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select a Date");
        builder.setCalendarConstraints(constraintBuilder.build());
        MaterialDatePicker materialDatePicker = builder.build();

        long today = materialDatePicker.todayInUtcMilliseconds();
        builder.setSelection(today);

        calendar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity main = new MainActivity();
                materialDatePicker.show(getFragmentManager(), "DatePicker");
            }
        });


        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {

                Date date = new Date(materialDatePicker.getHeaderText());
                String dayOfTheWeek = (String) android.text.format.DateFormat.format("EEEE", date);

                String month = "";
                String day = "";
                StringTokenizer stringTokenizer = new StringTokenizer(materialDatePicker.getHeaderText());

                while (stringTokenizer.countTokens() != 1) {
                    month = stringTokenizer.nextToken();
                    day = stringTokenizer.nextToken();
                }
                tvDate.setText(dayOfTheWeek + " " + day + " " + month);

                eventDate = tvDate.getText().toString().trim(); //save event date as string
            }
        });

        from.setText("");
        till.setText("");
        time_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                        Calendar calendar1 = Calendar.getInstance();
//                        calendar1.set(Calendar.HOUR_OF_DAY, hourOfDay);
//                        calendar1.set(Calendar.MINUTE, minute);
//                        calendar1.setTimeZone(TimeZone.getDefault());
//                        SimpleDateFormat format = new SimpleDateFormat("k:mm a");
//                        String time = format.format(calendar1.getTime());

                        t1Hour = hourOfDay;
                        t1Minute = minute;

                        Calendar c = Calendar.getInstance();
                        c.set(0, 0, 0, t1Hour, t1Minute);

                        if (counter % 2 == 0) {
                            tvTime1.setText(DateFormat.format("hh:mm aa", c));
                            till.setText("till");

                            eventTimeStart = tvTime.getText().toString().trim(); // saves event time start as a string
                            eventTimeEnd = tvTime1.getText().toString().trim(); // saves event end time as a string
                        } else {
                            tvTime.setText(DateFormat.format("hh:mm aa", c));
                            from.setText("from");
                        }

                    }
                }, 12, 0, false);
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });

        music_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.music_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                EditText etMusicType = dialog.findViewById(R.id.etMusicType);
                Button save_btn = dialog.findViewById(R.id.save_btn);

                save_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str = etMusicType.getText().toString();
                        tvMusic.setText(str);
                        dialog.dismiss();

                        eventMusic = etMusicType.getText().toString(); // saves music as a string
                    }
                });

                dialog.show();
            }
        });

        fee_btn.setOnClickListener(v -> {

            Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.fee_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            EditText etFee = dialog.findViewById(R.id.etFee);
            Button save_btn = dialog.findViewById(R.id.save_btn);

            save_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str = etFee.getText().toString();
                    tvFee.setText(str);
                    dialog.dismiss();

                    eventFee = etFee.getText().toString(); // save everthing from edit text as a string eventFee
                }
            });

            dialog.show();
        });

        location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMapReady(location_btn);
            }
        });

        picture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera(picture_btn);
            }
        });

        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(aSwitch.isChecked()){
                    visibility.setImageDrawable(getResources().getDrawable(R.drawable.ic_invisible));
                    switchState = true;
                }else{
                    visibility.setImageDrawable(getResources().getDrawable(R.drawable.ic_visibility));
                    switchState = false;
                }
                eventPrivacy = switchState.toString();
            }
        });

        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateEvent();
            }
        });
    }

    private void CreateEvent() {
        eventDescription = etMultiline.getText().toString().trim();
        eventTitle = etEventTitle.getText().toString().trim();

        // save all event details in a hashmap
        HashMap<String, String> profileMap = new HashMap<>();
        profileMap.put("eventTitle", eventTitle);
        profileMap.put("eventDescription", eventDescription);
        profileMap.put("eventDate", String.valueOf(eventDate));
        profileMap.put("eventTimeStart", String.valueOf(eventTimeStart));
        profileMap.put("eventTimeEnd", String.valueOf(eventTimeEnd));
        profileMap.put("eventPrivacy", eventPrivacy);
        profileMap.put("eventFee", String.valueOf(eventFee));
        profileMap.put("eventMusic", eventMusic);
        profileMap.put("eventImage", eventImage);
        profileMap.put("userID", currentUserID);

        // push everything to firebase through eventsref
        EventsRef.push().setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(),
                            "Profile updated.", Toast.LENGTH_LONG).show();
                } else {
                    String message = task.getException().toString();
                    Toast.makeText(getContext(), "Error" + message, Toast.LENGTH_LONG).show();
                }
            }
        });
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
                selectedImage.setImageURI(selectedImageUri);
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
                        eventImage = uri.toString();

                    }
                });
            }
        });
    }

    public String getCallerFragment() {
        FragmentManager fm = getFragmentManager();
        int count = getFragmentManager().getBackStackEntryCount();
        return fm.getBackStackEntryAt(count - 2).getName();
    }

    public void onMapReady(View view) {
        String location = etLocation.getText().toString();
        List<Address> addressList = null;

        if(etLocation != null || !etLocation.equals("")){
            Geocoder geocoder = new Geocoder(getContext());
            try{
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(location));
            //mMap.animateCamera(CameraUpdateFactory.newLatLng());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

}