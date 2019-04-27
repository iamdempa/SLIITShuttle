package com.example.jananathbanuka.sliitshuttle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;

import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;


public class StudentsMap extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location lastLocation;
    LocationRequest locationRequest;
    Marker currentUserLocationMarker;
    private static final int REQUEST_USER_LOCATION_CODE = 99;

    private DatabaseReference driverDatabaseReference;
    private DatabaseReference driverDatabaseReference2;
    private Marker shuttleMarker;
    private String studentID;
    private DatabaseReference studentDatabaseReference;
    private DatabaseReference shuttleDatabaseReference;
    private LatLng studentPickUpLocation;
    private int searchRadius = 1;

    private Button pick;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private boolean flag = true;
    private boolean shuttleFound = false;
    private String shuttleFoundID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_map);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        studentID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        pick = (Button) findViewById(R.id.pick);

        driverDatabaseReference = FirebaseDatabase.getInstance()
                .getReference().child("Shuttles On the Move");

        driverDatabaseReference2 = FirebaseDatabase.getInstance()
                .getReference().child("Shuttles On the Move");

        studentDatabaseReference = FirebaseDatabase.getInstance()
                .getReference().child("Students On the Road");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkUserLocationPermission();
        }

        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //students on the road
                showStudentsOnTheRoad();
            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.location_map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }

    private void showMovingShuttles() {

        GeoFire geoFire = new GeoFire(driverDatabaseReference);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(studentPickUpLocation.latitude, studentPickUpLocation.longitude),searchRadius);

        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                if(!shuttleFound){
                    shuttleFound = true;
                    shuttleFoundID = key;

                    shuttleDatabaseReference = FirebaseDatabase
                            .getInstance().getReference()
                            .child("Users").child("Drivers")
                            .child(shuttleFoundID);
                    HashMap shuttlesMap = new HashMap();
                    shuttlesMap.put("StudentOnTheRoadID", studentID);
                    shuttleDatabaseReference.updateChildren(shuttlesMap);

                    GettingShuttleLocation();

                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

                if(!shuttleFound){
                    searchRadius = searchRadius + 1;
                    showMovingShuttles();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    //getting shuttle location
    private void GettingShuttleLocation() {
        driverDatabaseReference2
                .child(shuttleFoundID)
                .child("l").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){ //if any shuttles are on the wat
                    List<Object> shuttleLocationMap = (List<Object>)dataSnapshot.getValue();
                    double latitudes = 0;
                    double longitudes = 0;

                    if(shuttleLocationMap.get(0)!= null){
                        latitudes = Double.parseDouble(shuttleLocationMap.get(0).toString());

                    }

                    if(shuttleLocationMap.get(1)!= null){
                        longitudes = Double.parseDouble(shuttleLocationMap.get(1).toString());

                    }

                    LatLng shuttleLatLng = new LatLng(latitudes, longitudes);
                    if(shuttleMarker != null){
                        shuttleMarker.remove();
                    }

                    //getting shuttle long/lat
                    Location location1 = new Location("");
                    location1.setLatitude(shuttleLatLng.latitude);
                    location1.setLongitude(shuttleLatLng.longitude);

                    //getting student long/lat
                    Location location2 = new Location("");
                    location2.setLatitude(studentPickUpLocation.latitude);
                    location2.setLongitude(studentPickUpLocation.longitude);

                    //getting distance between the student and the shuttle
                    float distance = location2.distanceTo(location1);

                    //showing the driver
                    shuttleMarker = mMap.addMarker(new MarkerOptions().position(shuttleLatLng));

                    pick.setText("Distance is: " + distance + "m");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public boolean checkUserLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_USER_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_USER_LOCATION_CODE);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_USER_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else { //permission denied
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
//        locationRequest.setInterval(1000);
//        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onLocationChanged(Location location) {

        lastLocation = location;

        if(currentUserLocationMarker != null){ //set to some other location
            currentUserLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("You are here");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        currentUserLocationMarker = mMap.addMarker(markerOptions);

        if(flag){
            System.out.println("============================================ CHANGED");
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            flag = false;
        }


        
//        float zoomLevel = 16.0f;
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

    }

    private void showStudentsOnTheRoad() {
        GeoFire geoFire = new GeoFire(studentDatabaseReference);
        geoFire.setLocation(studentID,
                new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()),
                new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        System.out.println("======= I AM ON THE ROAD!!!");
                    }
                });
        studentPickUpLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(studentPickUpLocation).title("I am here"));
        showMovingShuttles();
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
