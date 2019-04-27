package com.example.jananathbanuka.sliitshuttle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.renderscript.Sampler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    Context context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "PREF";
    private static final String EMAIL = "email";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String USER_TYPE = "UserType";


    public SessionManager(Context context){
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void logTheUser(String email, String  userType){
        editor.putString(EMAIL, email);
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(USER_TYPE, userType);
        editor.commit();
    }

    public String[] getUserEmailAndType(){
         String email = pref.getString(EMAIL, null);
         String  userType = pref.getString(USER_TYPE,null);

         String returnThisValue[] = {email, userType};
         String ifEmpty[] = {"empty","empty"};
         if(returnThisValue[0] == null){
             System.out.println("Empty Email");
             return ifEmpty;
         }else {
             System.out.println("User Logged!");
             return returnThisValue;
         }
    }

    public void checkLogin(Class cla){
        boolean value = isLoggedIn();
        System.out.println("Value of login is: "+String.valueOf(value));

        if(this.isLoggedIn()){ //if user is logged in
            System.out.println("Logged in!");
            Intent intent = new Intent(context, cla);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else{
            System.out.println("It is false"); //user is not logged in
        }
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false); //return false if value is no set
    }


    public void logoutUser(Class cla){
        editor.clear();
        editor.commit();

        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(context, cla);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    public void logoutUser2(){
        editor.clear();
        editor.commit();
    }

}
