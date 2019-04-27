package com.example.jananathbanuka.sliitshuttle;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

     private Button student, driver;
     private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //block landscape view

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(".MainActivity");

        student = (Button)findViewById(R.id.student);
        driver = (Button)findViewById(R.id.driver);

        sessionManager = new SessionManager(MainActivity.this);



        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] emailAndUserType = sessionManager.getUserEmailAndType();
                String userEmail = emailAndUserType[0];
                String userType = emailAndUserType[1];

                if(!userEmail.equals("empty") && !userType.equals("empty")) { //if email exists; user has already logged in
                    System.out.println("user Type is: " + userType);
                    if(userType.equals("driver") || userType == "driver"){ //and if the user is a driver
//                        sessionManager.checkLogin(DriverProfile.class);
                        showAlertBox2();
//                        finish();
                        System.out.println("Start Vehicle Profile");
                    }else if(userType.equals("student") || userType == "student"){
                        System.out.println("Start Student Activity");
                        //call student profile here
                        sessionManager.checkLogin(StudentProfile.class);
//                        finish();
                    }else{
                        System.out.println("another thing");
                    }
                }else{
                    Intent intent = new Intent(MainActivity.this, StudentLoginActivity.class);
                    startActivity(intent);
                }

            }
        });

        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] emailAndUserType = sessionManager.getUserEmailAndType();
                String userEmail = emailAndUserType[0];
                String userType = emailAndUserType[1];

                if(!userEmail.equals("empty") && !userType.equals("empty")){ //if email exists; user has already logged in
                    System.out.println("user Type is: " + userType);
                    if(userType.equals("driver") || userType == "driver"){ //and if the user is a driver
                        sessionManager.checkLogin(DriverProfile.class);
//                        finish();
                        System.out.println("Start Vehicle Profile");
                    }else if(userType.equals("student") || userType == "student"){
                        System.out.println("Start Student Activity");
                        //call student profile here

                        showAlertBox();


//                        finish();
                    }else{
                        System.out.println("another thing");
                    }
                }else{ //starts Vehicle login UI
                    System.out.println("2");
                    Intent intent = new Intent(getApplicationContext(), DriverLoginActivity.class);
                    startActivity(intent);
                }
            }
        });


    }

    private void showAlertBox() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == DialogInterface.BUTTON_POSITIVE){ //Yes
                    sessionManager.logoutUser(DriverLoginActivity.class);
//                    sessionManager.checkLogin(DriverLoginActivity.class);
                }else if(which == DialogInterface.BUTTON_NEGATIVE){ //No
                    sessionManager.checkLogin(StudentProfile.class);
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("You are Already Logged in as a Student, Do you need to logout and continue as a Driver?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void showAlertBox2() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == DialogInterface.BUTTON_POSITIVE){ //Yes
                    sessionManager.logoutUser(StudentLoginActivity.class);
//                    sessionManager.checkLogin(DriverLoginActivity.class);
                }else if(which == DialogInterface.BUTTON_NEGATIVE){ //No
                    sessionManager.checkLogin(DriverProfile.class);
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("You are Already Logged in as a Driver, Do you need to logout and continue as a Student?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }


}
