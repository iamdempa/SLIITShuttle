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

public class StudentSignup extends AppCompatActivity {

    private EditText email, password;
    private Button signUp;
    private FirebaseAuth firebaseAuth;
    private SessionManager sessionManager;
    private SaveUsers student;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_signup);

        sessionManager = new SessionManager(StudentSignup.this);
        String loggedInEmail[] = sessionManager.getUserEmailAndType();
        Toast.makeText(StudentSignup.this, "Email is: "+loggedInEmail[0], Toast.LENGTH_LONG).show();
//        sessionManager.checkLogin(DriverLoginActivity.class);

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        signUp = (Button)findViewById(R.id.signup);
        firebaseAuth =  FirebaseAuth.getInstance();


        student = new SaveUsers(email.getText().toString(), password.getText().toString(), "student");


        signUp.setOnClickListener(new View.OnClickListener() { //creating new driver
            @Override
            public void onClick(View v) {
                createNewStudent();
            }
        });
    }

    private void createNewStudent() {
        final ProgressDialog progressDialog = ProgressDialog.show(StudentSignup.this, "Please wait...", "Registering a new Student...", true);
        (firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();

                if(task.isSuccessful()){

                    //save driver
                    student.saveStudent(email.getText().toString(), password.getText().toString(),"student");

                    Toast.makeText(StudentSignup.this, "Successfully Registered!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), StudentProfile.class);

                    startActivity(intent);

                }else{
                    Log.e("ERROR", task.getException().toString());
                    Toast.makeText(StudentSignup.this, "Shuttle already exists!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
