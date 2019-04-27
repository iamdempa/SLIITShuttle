package com.example.jananathbanuka.sliitshuttle;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverProfile extends AppCompatActivity {

    private TextView welcome;
    private SessionManager sessionManager;
    private Button logout, getData, map;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);

        getSupportActionBar().hide();
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //block landscape view

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(".MainActivity");

        welcome = (TextView)findViewById(R.id.welcome);
        map = (Button)findViewById(R.id.map);


        sessionManager = new SessionManager(DriverProfile.this);



        logout = (Button)findViewById(R.id.logout);
        getData = (Button)findViewById(R.id.getdata);


        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DriversMap.class);
                startActivity(intent);
            }
        });

        getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String driverID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference driverDatBaseReference = FirebaseDatabase.getInstance()
                        .getReference().child("Shuttles On the Move");

                GeoFire geoFire = new GeoFire(driverDatBaseReference);
                geoFire.removeLocation(driverID, new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        System.out.println("============================Driver Removed");
                    }
                });

                sessionManager.logoutUser(MainActivity.class);

            }
        });

    }

}
