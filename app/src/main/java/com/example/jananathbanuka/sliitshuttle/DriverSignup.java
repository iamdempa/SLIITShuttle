package com.example.jananathbanuka.sliitshuttle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverSignup extends AppCompatActivity {

    private EditText email, password;
    private Button signUp;
    private FirebaseAuth firebaseAuth;
    private SessionManager sessionManager;
    private SaveUsers driver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_signup);

        sessionManager = new SessionManager(DriverSignup.this);
        String loggedInEmail[] = sessionManager.getUserEmailAndType();
        Toast.makeText(DriverSignup.this, "Email is: "+loggedInEmail[0], Toast.LENGTH_LONG).show();
//        sessionManager.checkLogin(DriverLoginActivity.class);

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        signUp = (Button)findViewById(R.id.signup);
        firebaseAuth =  FirebaseAuth.getInstance();

        driver = new SaveUsers(email.getText().toString(), password.getText().toString(), "driver");


        signUp.setOnClickListener(new View.OnClickListener() { //creating new driver
            @Override
            public void onClick(View v) {
                createNewDriver();
            }
        });
    }

    private void createNewDriver() {
        final ProgressDialog progressDialog = ProgressDialog.show(DriverSignup.this, "Please wait...", "Registering a new Shuttle...", true);
        (firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();

                if(task.isSuccessful()){

                    //save driver
                    driver.saveDriver(email.getText().toString(), password.getText().toString(),"driver");

                    Toast.makeText(DriverSignup.this, "Successfully Registered!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), DriverProfile.class);

                    startActivity(intent);

                }else{
                    Log.e("ERROR", task.getException().toString());
                    Toast.makeText(DriverSignup.this, "Shuttle already exists!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
