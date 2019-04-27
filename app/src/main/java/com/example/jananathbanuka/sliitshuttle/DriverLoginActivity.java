package com.example.jananathbanuka.sliitshuttle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverLoginActivity extends AppCompatActivity {

    private Button login;
    private TextView signUp;
    public EditText email, password;
    private FirebaseAuth firebaseAuth;
    private String emailText, passwordText;
    private SessionManager sessionManager;

    private FirebaseUser firebaseUser;
    private DatabaseReference driverDatabaseReference;
    private String driverID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_driver);


        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Vehicle Login");



        signUp = (TextView)findViewById(R.id.signup);
        login = (Button)findViewById(R.id.login);

        //firebase instance
        firebaseAuth = FirebaseAuth.getInstance();

        sessionManager = new SessionManager(DriverLoginActivity.this);


        String[] emailAndUserType = sessionManager.getUserEmailAndType();
        String userEmail = emailAndUserType[0];
        String userType = emailAndUserType[1];

        if(userEmail.equals("empty") && userType.equals("empty")){ //user not logged in
            System.out.println("=========================== Not logged in...");
        }else {
            System.out.println("================================== Logged In!");
            if(userType.equals("student")){
                sessionManager.checkLogin(StudentProfile.class);
            }else if(userType.equals("driver")){
                sessionManager.checkLogin(DriverProfile.class);
            }else {
                System.out.println("====================ALIEN!");
            }
        }


        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);



        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DriverSignup.class));
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sessionManager.logoutUser2();

                emailText = email.getText().toString().trim();
                passwordText = password.getText().toString().trim();

                final ProgressDialog progressDialog = ProgressDialog.show(DriverLoginActivity.this, "Please wait...", "Login in...", true);

                if(emailText.length() == 0 || passwordText.length() == 0){
                    Toast.makeText(DriverLoginActivity.this, "Empty values", Toast.LENGTH_LONG).show();
                }else {

                    (firebaseAuth.signInWithEmailAndPassword(emailText, passwordText)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();

                            if (task.isSuccessful()) {

                                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                driverID = firebaseUser.getUid();
                                driverDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverID);

                                driverDatabaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.exists()){

                                            String userType = dataSnapshot.child("userType").getValue().toString();
                                            if(userType.equals("driver")){ //load driver interface
                                                Toast.makeText(DriverLoginActivity.this, "Login Success!", Toast.LENGTH_LONG).show();

                                                Intent intent = new Intent(getApplicationContext(), DriverProfile.class);

                                                sessionManager.logTheUser(emailText, "driver");

                                                startActivity(intent);
                                                finish();
                                            }else if(userType.equals("student")){
                                                Toast.makeText(DriverLoginActivity.this, "It is a Student!", Toast.LENGTH_LONG).show();
                                            }else{
                                                Toast.makeText(DriverLoginActivity.this, "An Alien!", Toast.LENGTH_LONG).show();
                                            }
                                        }else{
                                            Toast.makeText(DriverLoginActivity.this, "No Child!", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                                });


                            } else {
                                Log.e("ERROR", "Invalid Credentials");
                                Toast.makeText(DriverLoginActivity.this, "Login Failed!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

    }

}
