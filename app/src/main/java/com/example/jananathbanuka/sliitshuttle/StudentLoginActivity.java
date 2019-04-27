package com.example.jananathbanuka.sliitshuttle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class StudentLoginActivity extends AppCompatActivity {

    private Button login;
    private TextView signUp;
    private EditText email, password;
    private String emailText, passwordText;
    private FirebaseAuth firebaseAuth;
    private SessionManager sessionManager;

    private FirebaseUser firebaseUser;
    private DatabaseReference studentDatabaseReference;
    private String studentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_student);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Student Login");
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //buttons
        signUp = (TextView)findViewById(R.id.signup);
        login = (Button)findViewById(R.id.login);
        
        //firebase instance
        firebaseAuth = FirebaseAuth.getInstance();


        //session
        sessionManager = new SessionManager(StudentLoginActivity.this);



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

        

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), StudentSignup.class));
            }
        });

        //texts
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sessionManager.logoutUser2();

                System.out.println("email: " + email.getText().toString());
                System.out.println("password: " + password.getText().toString());

                emailText = email.getText().toString();
                passwordText = password.getText().toString().trim();

                final ProgressDialog progressDialog = ProgressDialog.show(StudentLoginActivity.this, "Please wait...", "Login in...", true);

                if(emailText.length() == 0 || passwordText.length() == 0){
                    Toast.makeText(StudentLoginActivity.this, "Empty values", Toast.LENGTH_LONG).show();
                }else {

                    (firebaseAuth.signInWithEmailAndPassword(emailText, passwordText)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();

                            if (task.isSuccessful()) { //if login success

                                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                studentID = firebaseUser.getUid();
                                studentDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Students").child(studentID);

                                studentDatabaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.exists()){

                                            String userType = dataSnapshot.child("userType").getValue().toString();
                                            if(userType.equals("student")){ //load driver interface
                                                Toast.makeText(StudentLoginActivity.this, "Login Success!", Toast.LENGTH_LONG).show();

                                                Intent intent = new Intent(getApplicationContext(), StudentProfile.class);

                                                //set the email and user type
                                                sessionManager.logTheUser(emailText, "student");

                                                startActivity(intent);
                                                finish();
                                            }else if(userType.equals("driver")){
                                                Toast.makeText(StudentLoginActivity.this, "It is a Driver!", Toast.LENGTH_LONG).show();
                                            }else{
                                                Toast.makeText(StudentLoginActivity.this, "An Alien!", Toast.LENGTH_LONG).show();
                                            }
                                        }else{
                                            Toast.makeText(StudentLoginActivity.this, "No Child!", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                                });



                            } else {
                                Log.e("ERROR", "Invalid Credentials");
                                Toast.makeText(StudentLoginActivity.this, "Login Failed!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
