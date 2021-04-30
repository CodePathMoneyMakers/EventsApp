package com.example.eventsapp.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.eventsapp.Event;
import com.example.eventsapp.EventLocation;
import com.example.eventsapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment
        extends Fragment
        implements OnMapReadyCallback,
        View.OnClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnPolylineClickListener {

    private static final float DEFAULT_ZOOM = 15f;
    MapView mapView;
    private GoogleMap mMap;
    TextView textView;
    EditText etLocationTitle;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    public static final String TAG = "SearchFragment";
    private ImageButton location_btn;
    private DatabaseReference EventsRef;
    private double currentLat;
    private double currentLong;

    private GeoApiContext geoApiContext = null;

    // Global Event location object
    private EventLocation eventPosition;
    private ArrayList<EventLocation> eventLocations = new ArrayList<>();



    public SearchFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragments_search, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        location_btn = view.findViewById(R.id.set_image);
        location_btn.setOnClickListener(v -> onMapReady());

        etLocationTitle = view.findViewById(R.id.etLocationTitle);

        EventsRef = FirebaseDatabase.getInstance().getReference().child("Events");

        geoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.google_maps_key)).build();


    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    public void init() {
        Log.d(TAG, "init: initializing");

        etLocationTitle.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                //execute our method for searching
                geoLocate();
            }

            return false;
        });
    }
    public void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = etLocationTitle.getText().toString();

        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: found a location: " + address.toString());
            //Toast.makeText(getContext(), address.toString(), Toast.LENGTH_SHORT).show();

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));

            //eventLocation = address.getLatitude() + ", " + address.getLongitude();
        }
    }

    public void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));


        // if(!title.equals("My Location")){
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title);
        mMap.addMarker(options);
        //   }
    }

    public void onMapReady() {
        String location = etLocationTitle.getText().toString();
        List<Address> addressList = null;

        if (etLocationTitle != null || !etLocationTitle.equals("")) {
            Geocoder geocoder = new Geocoder(getContext());
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                Toast.makeText(getActivity(), "unable to get current location", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            //TODO: put an if else to prevent null pointer crash
            if (addressList == null || addressList.size() < 1) {
                Toast.makeText(getActivity(), "Unable to locate address.", Toast.LENGTH_SHORT).show();
            } else {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                //eventLocation = address.getLatitude() + ", " + address.getLongitude();
                mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            }
        }
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

        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) { return; }

        mMap.setOnInfoWindowClickListener(this);

        EventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event event;
                try {
                    for (DataSnapshot s : snapshot.getChildren()) {
                        event = s.getValue(Event.class);
                        LatLng location = new LatLng(event.latitude, event.longitude);
                        mMap.addMarker(new MarkerOptions().position(location).title(event.getEventTitle()));
                    }
                } catch (NullPointerException e) {
                    Toast.makeText(getActivity(), "An event was not able to load.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        getDeviceLocation();
        mMap.setMyLocationEnabled(true);
        mMap.setOnPolylineClickListener(this);
        //init();
    }

    public void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        try {
            final Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: found location!");
                    Location currentLocation = (Location) task.getResult();

                    currentLat = currentLocation.getLatitude();
                    currentLong = currentLocation.getLongitude();

                    // try to update the Map View, prevent an error crash
                    try {
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                DEFAULT_ZOOM, "My Location");
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "unable to get current location", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.d(TAG, "onComplete: current location is null");
                    Toast.makeText(getActivity(), "unable to get current location", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void setEventLocation() {
        for (EventLocation events : eventLocations) {
            if (events.getUser().getUser_id().equals(FirebaseAuth.getInstance().getUid())) {
                eventPosition = events;
            }
        }
    }

    private void calculateDirections(LatLng marker){
        Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng origin =
                new com.google.maps.model.LatLng(currentLat, currentLong);
        com.google.maps.model.LatLng destination =
                new com.google.maps.model.LatLng(marker.latitude, marker.longitude);

        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);
        directions.alternatives(true);
        directions.origin(origin);

        Log.d(TAG, "getDeviceLocation: getting the devices current location");


        Log.d(TAG, "calculateDirections: destination: " + destination.toString());

        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );
            }
        });
    }

    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);
                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
                    List<LatLng> newDecodedPath = new ArrayList<>();
                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){
                    // Log.d(TAG, "run: latlng: " + latLng.toString());
                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
                    polyline.setClickable(true);
                }
            }
        });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("Would you like to navigate to " + marker.getTitle() + "?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", (dialog, id) -> {
            calculateDirections(marker.getPosition());
            dialog.dismiss();
        });
        builder.setNegativeButton("No", (dialog, id) -> {
            dialog.cancel();
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        polyline.setColor(ContextCompat.getColor(getActivity(), R.color.blue1));
        polyline.setZIndex(1);
    }
}