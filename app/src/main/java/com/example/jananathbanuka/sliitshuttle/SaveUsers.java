package com.example.jananathbanuka.sliitshuttle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SaveUsers {
    String email,password,userType;
    private DatabaseReference database;
    private String driverID,studentID;
    private FirebaseAuth firebaseAuth;

    public  SaveUsers(){}

    public SaveUsers(String email, String password, String userType){
        this.email = email;
        this.password = password;
        this.userType = userType;
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
    }

    public void saveDriver(String email,  String password, String userType){
        driverID = firebaseAuth.getCurrentUser().getUid();

        SaveUsers driver = new SaveUsers(email, password, userType);
        database.child("Users").child("Drivers").child(driverID).setValue(driver);
    }

    public void saveStudent(String email,  String password, String userType){
        studentID = firebaseAuth.getCurrentUser().getUid();

        SaveUsers student = new SaveUsers(email, password, userType);
        database.child("Users").child("Students").child(studentID).setValue(student);
    }
}
