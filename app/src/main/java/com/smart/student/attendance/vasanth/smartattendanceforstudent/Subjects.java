package com.smart.student.attendance.vasanth.smartattendanceforstudent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Subjects extends AppCompatActivity {

    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceEmails;

    ArrayList<String> keysArrayList;
    ArrayList<String> namesForReference;
    ArrayList<CardClass> membersArrayList;
    static ArrayList<String> referenceForKeyArrayList;
    static ArrayList<String> referenceForNamesArrayList;

    LocationManager locationManager;
    FirebaseAuth firebaseAuthSub;

    String year,branch,section;

    ProgressBar pbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);

            firebaseAuthSub = FirebaseAuth.getInstance();
        pbar = findViewById(R.id.pBarInSubjects);

        ActivityCompat.requestPermissions(Subjects.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }


        databaseReferenceEmails = FirebaseDatabase.getInstance().getReference().child("emails").child(String.valueOf(firebaseAuthSub.getCurrentUser().getUid()));
        databaseReferenceEmails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                year = String.valueOf(dataSnapshot.child("Year").getValue());
                branch = String.valueOf(dataSnapshot.child("Branch").getValue());
                section = String.valueOf(dataSnapshot.child("Section").getValue());
                getData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void getData(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("addedSubjects").child(year).child(branch).child(section);

        ListView listView = (ListView) findViewById(R.id.listViewForMember);


        membersArrayList = new ArrayList<>();
        keysArrayList = new ArrayList<>();
        namesForReference = new ArrayList<>();
        referenceForKeyArrayList = new ArrayList<>();
        referenceForNamesArrayList= new ArrayList<>();




        final AdapterClassForSubjects adapterForMember = new AdapterClassForSubjects(this, R.layout.card_design_for_subjects, membersArrayList);
        listView.setAdapter(adapterForMember);

        databaseReference.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String addedMember = dataSnapshot.getValue(String.class);
                membersArrayList.add(new CardClass(1, addedMember));
                namesForReference.add(addedMember);
                String addedkey = dataSnapshot.getKey();
                keysArrayList.add(addedkey);
                adapterForMember.notifyDataSetChanged();

                referenceForKeyArrayList = keysArrayList;
                referenceForNamesArrayList = namesForReference;

                if (pbar != null) {
                    pbar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s){

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {


            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    public void signout(View v){
        firebaseAuthSub.signOut();
        startActivity(new Intent(Subjects.this,Login.class));
    }
    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        Toast.makeText(this,"Checking attendance", Toast.LENGTH_LONG).show();
        return true;
    }
}
