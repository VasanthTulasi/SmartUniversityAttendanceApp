package com.smart.student.attendance.vasanth.smartattendanceforstudent;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    EditText emailTV;
    EditText passwordTV;
    EditText loginPinTV;
    DatabaseReference db;
    String email="",password="",loginPin="";
    boolean emailFound = false;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailTV = findViewById(R.id.emailTV);
        passwordTV = findViewById(R.id.passwordTV);
        loginPinTV = findViewById(R.id.loginPinTV);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null)
                    startActivity(new Intent(Login.this, Subjects.class));
            }
        };
    }


    public void signin(View v) {

        email = emailTV.getText().toString();
        password = passwordTV.getText().toString();
        loginPin = loginPinTV.getText().toString();


        db = FirebaseDatabase.getInstance().getReference().child("emails");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    DatabaseReference databaseReferenceForChild = db.child(ds.getKey());
                    databaseReferenceForChild.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(String.valueOf(dataSnapshot.child("Email").getValue()).equals(email)){
                                emailFound = true;
                                final String year = String.valueOf(dataSnapshot.child("Year").getValue());
                                final String branch = String.valueOf(dataSnapshot.child("Branch").getValue());
                                final String section = String.valueOf(dataSnapshot.child("Section").getValue());

                                DatabaseReference dbForPinChecking =FirebaseDatabase.getInstance().getReference().child("pinForRegistration");
                                dbForPinChecking.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String s = String.valueOf(dataSnapshot.child(year+branch+section).getValue());
                                        if(s.equals(loginPin)){
                                            login(email,password,loginPin);
                                        }
                                        else
                                            Toast.makeText(Login.this,"Incorrect login pin entered",Toast.LENGTH_LONG).show();
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                if(!emailFound)
                    Toast.makeText(Login.this,"Email not found",Toast.LENGTH_LONG).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void signup(View v){
       startActivity(new Intent(Login.this,Register.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    public void forgotPass(View v){
        startActivity(new Intent(Login.this,ForgotPassword.class));
    }

    public void login(String email,String password,String loginPin){
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(loginPin)){
            mAuth.signInWithEmailAndPassword(emailTV.getText().toString(), passwordTV.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful())
                        Toast.makeText(Login.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(Login.this,"Fields cannot be empty.",Toast.LENGTH_LONG).show();
        }
    }

}
