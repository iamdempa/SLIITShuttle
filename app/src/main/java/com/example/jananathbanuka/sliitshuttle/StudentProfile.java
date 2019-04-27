package com.example.jananathbanuka.sliitshuttle;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

public class StudentProfile extends Activity {

    private Button logout, map;
    private SessionManager sessionManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        logout = (Button)findViewById(R.id.logout);
        sessionManager = new SessionManager(StudentProfile.this);
        map = (Button)findViewById(R.id.map);


        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentProfile.this, StudentsMap.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logoutUser(MainActivity.class);
                finish();
            }
        });


    }

}
