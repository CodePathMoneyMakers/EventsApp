package com.example.eventsapp.fragments;
import android.content.Context;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.eventsapp.Event;
import com.example.eventsapp.EventLocation;
import com.example.eventsapp.PolylineData;
import com.example.eventsapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
//    private ImageButton location_btn;
    private DatabaseReference EventsRef;
    private double currentLat = 0.0;
    private double currentLong = 0.0;

    private GeoApiContext geoApiContext = null;
    private ArrayList<PolylineData> polylineData = new ArrayList<>();
    private Marker selectedMarker = null;

    // Global Event location object
    //private EventLocation eventPosition;
    //private ArrayList<EventLocation> eventLocations = new ArrayList<>();



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

//        location_btn = view.findViewById(R.id.set_image);
//        location_btn.setOnClickListener(v -> onMapReady());

        etLocationTitle = view.findViewById(R.id.etLocationTitle);

        EventsRef = FirebaseDatabase.getInstance().getReference().child("Events");

        geoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.google_maps_key)).build();

        ImageView locationBtn = view.findViewById(R.id.myLocationBtn);

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
//
//                try {
//                    final Task location = mFusedLocationProviderClient.getLastLocation();
//                    location.addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "onComplete: found location!");
//                            Location currentLocation = (Location) task.getResult();
//
//                            currentLat = currentLocation.getLatitude();
//                            currentLong = currentLocation.getLongitude();
//
//                            // try to update the Map View, prevent an error crash
//                            try {
//                                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
//                            } catch (Exception e) {
//                                Toast.makeText(getActivity(), "unable to get current location", Toast.LENGTH_SHORT).show();
//                            }
//
//                        } else {
//                            Log.d(TAG, "onComplete: current location is null");
//                            Toast.makeText(getActivity(), "unable to get current location", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                } catch (SecurityException e) {
//                    Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
//                }
                getMyLocation();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public void init() {
        Log.d(TAG, "init: initializing");

        etLocationTitle.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                closeKeyboard();
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
//        MarkerOptions options = new MarkerOptions()
//                .position(latLng)
//                .title(title);
//        mMap.addMarker(options);
        //   }
    }

    public void zoomRoute(List<LatLng> lstLatLngRoute) {
        if (mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);
        int routePadding = 120;
        LatLngBounds latLngBounds = boundsBuilder.build();
        mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
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

        try{
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.mapstyle));

            if(!success){
                Log.e("SearchFragment", "Style parsing failed");
            }

        } catch (Resources.NotFoundException e){
            Log.e("SearchFragment", "Can't find style");
        }

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
                        if(event.getEventGenre().equals("Sports")){
                            mMap.addMarker(new MarkerOptions().position(location).title(event.getEventTitle()).icon(bitmapDescriptor(getContext(), R.drawable.ic_sports_mappin1)));
                        }else if(event.getEventGenre().equals("Music")){
                            mMap.addMarker(new MarkerOptions().position(location).title(event.getEventTitle()).icon(bitmapDescriptor(getContext(), R.drawable.ic_music_mappin)));
                        }else{
                            mMap.addMarker(new MarkerOptions().position(location).title(event.getEventTitle()).icon(bitmapDescriptor(getContext(), R.drawable.ic_user_group)));
                        }

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

        //Disable Map Toolbar:
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMyLocationEnabled(false);
        mMap.setPadding(0,220,20,0);
        mMap.setOnPolylineClickListener(this);


        init();

    }

    private void getMyLocation() {
        LatLng latLng = new LatLng(Double.parseDouble(String.valueOf(currentLat)), Double.parseDouble(String.valueOf(currentLong)));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
        mMap.animateCamera(cameraUpdate);
    }

    private BitmapDescriptor bitmapDescriptor(Context context, int vectorResId){
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
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

//    private void setEventLocation() {
//        for (EventLocation events : eventLocations) {
//            if (events.getUser().getUser_id().equals(FirebaseAuth.getInstance().getUid())) {
//                eventPosition = events;
//            }
//        }
//    }

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

                if(polylineData.size() > 0) {
                   for(PolylineData data: polylineData) {
                       data.getPolyline().remove();
                   }
                   polylineData.clear();
                   polylineData = new ArrayList<>();
                }

                double duration = 999999999;
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

                    polylineData.add(new PolylineData(polyline, route.legs[0]));

                    // find fastest route
                    double tempDuration = route.legs[0].duration.inSeconds;
                    if(tempDuration < duration) {
                        duration = tempDuration;
                        onPolylineClick(polyline);
                        zoomRoute(polyline.getPoints());
                    }

                    selectedMarker.setVisible(false);
                }
            }
        });
    }

    @Override
    public void onInfoWindowClick(Marker marker)  {
        if(marker.getTitle().contains("Navigate to ")){
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Open Turn by Turn Directions?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        String latitude = String.valueOf(marker.getPosition().latitude);
                        String longitude = String.valueOf(marker.getPosition().longitude);
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");

                        try{
                            if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(mapIntent);
                            }
                        }catch (NullPointerException e){
                            Log.e(TAG, "onClick: NullPointerException: Couldn't open map." + e.getMessage() );
                            Toast.makeText(getActivity(), "Couldn't open map", Toast.LENGTH_SHORT).show();
                        }

                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage("Find routes to " + marker.getTitle() + "?");
            builder.setCancelable(true);
            builder.setPositiveButton("Yes", (dialog, id) -> {
                selectedMarker = marker;
                calculateDirections(marker.getPosition());
                dialog.dismiss();
            });
            builder.setNegativeButton("No", (dialog, id) -> {
                dialog.cancel();
            });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPolylineClick(Polyline polyline) {

        for(PolylineData data: polylineData){
            Log.d(TAG, "onPolylineClick: toString: " + data.toString());
            if(polyline.getId().equals(data.getPolyline().getId())){
                data.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.blue1));
                data.getPolyline().setZIndex(1);

                String title;
                LatLng endLocation = new LatLng(
                        data.getLeg().endLocation.lat,
                        data.getLeg().endLocation.lng
                );

                Marker marker = mMap.addMarker(new MarkerOptions()
                .position(endLocation)
                .title("Navigate to " + selectedMarker.getTitle())
                .snippet("Duration: " + data.getLeg().duration));

                marker.showInfoWindow();
            }
            else{
                data.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
                data.getPolyline().setZIndex(0);
            }
        }
    }
}