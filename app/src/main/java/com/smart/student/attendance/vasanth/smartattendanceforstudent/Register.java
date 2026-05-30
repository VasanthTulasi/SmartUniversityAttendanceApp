package com.smart.student.attendance.vasanth.smartattendanceforstudent;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {
    EditText firstNameTV;
    EditText lastNameTV;
    EditText emailForRegTV;
    EditText passwordForRegTV;
    EditText confirmPasswordTV;
    EditText registrationPinTV;
    EditText rollNumberTV;

    FirebaseAuth fAuthReg;

    Spinner yearSpi;
    Spinner branchSpi;
    Spinner secSpi;

    String year="",branch="",section="",rollno="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstNameTV = findViewById(R.id.firstNameTV);
        lastNameTV = findViewById(R.id.lastNameTV);
        emailForRegTV = findViewById(R.id.emailForRegTV);
        passwordForRegTV = findViewById(R.id.passwordForRegTV);
        confirmPasswordTV = findViewById(R.id.confirmPasswordTV);
        registrationPinTV = findViewById(R.id.registrationPinTV);
        rollNumberTV = findViewById(R.id.rollNumberTV);

        fAuthReg = FirebaseAuth.getInstance();


        //Spinner for Year
        String[] arraySpinnerForYear = {"Year 1", "Year 2", "Year 3", "Year 4"};
        yearSpi = (Spinner) findViewById(R.id.yearSpi);
        ArrayAdapter<String> adapterForYear = new ArrayAdapter<String>(this, R.layout.spinner_item, arraySpinnerForYear);
        adapterForYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpi.setAdapter(adapterForYear);
        yearSpi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if(yearSpi.getItemAtPosition(i).equals("Year 1"))
                        year = "1";
                    else if(yearSpi.getItemAtPosition(i).equals("Year 2"))
                        year = "2";
                    else if(yearSpi.getItemAtPosition(i).equals("Year 3"))
                        year = "3";
                    else if(yearSpi.getItemAtPosition(i).equals("Year 4"))
                        year = "4";



                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        //Spinner for Branch
        String[] arraySpinnerForBranch = {"CSE", "IT", "ECE", "EEE","Mechanical","Civil","Pharma","Chemical"};
        branchSpi = (Spinner) findViewById(R.id.branchSpi);
        ArrayAdapter<String> adapterForBranch = new ArrayAdapter<String>(this, R.layout.spinner_item, arraySpinnerForBranch);
        adapterForBranch.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branchSpi.setAdapter(adapterForBranch);
        branchSpi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                branch = (String) branchSpi.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //Spinner for Section
        String[] arraySpinnerForSection = {"Section A", "Section B", "Section C", "Section D"};
        secSpi = (Spinner) findViewById(R.id.secSpi);
        ArrayAdapter<String> adapterForSec = new ArrayAdapter<String>(this, R.layout.spinner_item, arraySpinnerForSection);
        adapterForSec.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        secSpi.setAdapter(adapterForSec);
        secSpi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(secSpi.getItemAtPosition(i).equals("Section A"))
                    section = "A";
                else if(secSpi.getItemAtPosition(i).equals("Section B"))
                    section = "B";
                else if(secSpi.getItemAtPosition(i).equals("Section C"))
                    section = "C";
                else if(secSpi.getItemAtPosition(i).equals("Section D"))
                    section = "D";
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }



    public void register(View v){


        DatabaseReference db  = FirebaseDatabase.getInstance().getReference().child("pinForRegistration");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String fName="",lName="",email="",pass="",conPass="",regisPin="";
                fName=firstNameTV.getText().toString();
                lName=lastNameTV.getText().toString();
                email=emailForRegTV.getText().toString();
                pass=passwordForRegTV.getText().toString();
                conPass=confirmPasswordTV.getText().toString();
                regisPin=registrationPinTV.getText().toString();

                String s = String.valueOf(dataSnapshot.child(year+branch+section).getValue());

                    if(!TextUtils.isEmpty(fName) && !(TextUtils.isEmpty(lName)) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(conPass) && !TextUtils.isEmpty(regisPin)) {
                        if (pass.equals(conPass)) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                                if(s.equals(regisPin)) {
                                    builder.setMessage("Please confirm the details:\nYou belong to Year " + year + " - " + branch + " - " + section + ".")
                                            .setCancelable(false)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(final DialogInterface dialog, final int id) {
                                                    createUserOnCloud();
                                                }
                                            })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                public void onClick(final DialogInterface dialog, final int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    final AlertDialog alert = builder.create();
                                    alert.show();
                                }else {
                                    Toast.makeText(Register.this, "Registration failed! Incorrect registration pin entered.", Toast.LENGTH_LONG).show();
                                }
                        } else {
                            Toast.makeText(Register.this, "Registration failed! Passwords do not match", Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(Register.this, "Registration failed! Fields cannot be empty", Toast.LENGTH_LONG).show();

                    }


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    public void createUserOnCloud(){

                fAuthReg.createUserWithEmailAndPassword(emailForRegTV.getText().toString(), passwordForRegTV.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            DatabaseReference emailRegdb = FirebaseDatabase.getInstance().getReference().child("emails").child(String.valueOf(fAuthReg.getCurrentUser().getUid()));
                            emailRegdb.child("Year").setValue(year);
                            emailRegdb.child("Branch").setValue(branch);
                            emailRegdb.child("Section").setValue(section);
                            emailRegdb.child("Name").setValue(firstNameTV.getText().toString() + " " + lastNameTV.getText().toString());
                            emailRegdb.child("Email").setValue(fAuthReg.getCurrentUser().getEmail());
                            emailRegdb.child("RollNumber").setValue(rollNumberTV.getText().toString());
                        }
                        if (!task.isSuccessful()) {
                            FirebaseAuthException e = (FirebaseAuthException) task.getException();
                            Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
