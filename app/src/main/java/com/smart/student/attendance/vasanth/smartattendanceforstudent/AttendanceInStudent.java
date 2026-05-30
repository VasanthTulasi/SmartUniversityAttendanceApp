package com.smart.student.attendance.vasanth.smartattendanceforstudent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class AttendanceInStudent extends AppCompatActivity {


    Date c = Calendar.getInstance().getTime();
    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
    String formattedDate = df.format(c);
    long noOfClasses=1L;

    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    double lattitude,longitude;
    String teacherLatitude,teacherLongitude;

    FirebaseAuth fa;
    String userName,year,branch,section;
    TextView welcomeText;

    ArrayList<String> studentNames;
    DatabaseReference studentNamesRef;


    boolean isButtonClicked = false;
    Button validateBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_in_student);

        validateBTN = (Button) findViewById(R.id.validate);
        welcomeText = findViewById(R.id.welcomeText);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        fa = FirebaseAuth.getInstance();

        DatabaseReference databaseReferenceEmails = FirebaseDatabase.getInstance().getReference().child("emails").child(String.valueOf(fa.getCurrentUser().getUid()));
        databaseReferenceEmails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = String.valueOf(dataSnapshot.child("Name").getValue());
                year = String.valueOf(dataSnapshot.child("Year").getValue());
                branch = String.valueOf(dataSnapshot.child("Branch").getValue());
                section = String.valueOf(dataSnapshot.child("Section").getValue());
                welcomeText.setText("Hi "+userName+"!\nEnter today's authentication code for this subject");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference databaseReferenceForNoOfClasses = FirebaseDatabase.getInstance().getReference().child("noOfClasses");
        databaseReferenceForNoOfClasses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                noOfClasses = dataSnapshot.child(year+branch+section+AdapterClassForSubjects.subjectName).getValue(Long.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void getStudentNames(){
        studentNames = new ArrayList<>();
        studentNamesRef = FirebaseDatabase.getInstance().getReference().child("addedStudents").child(year).child(branch).child(section).child(AdapterClassForSubjects.subjectName);
        studentNamesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String name = dataSnapshot.getValue(String.class);
                studentNames.add(name);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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

         getLocation();
    }

    public void buttonClicked(View v){
        isButtonClicked = true;
        checkIfAttendanceRecorder();
    }

    public void checkIfAttendanceRecorder(){
        DatabaseReference dbForAttendaceRecordedOrNot = FirebaseDatabase.getInstance().getReference().child("attendance").child(year).child(branch).child(section).child(AdapterClassForSubjects.subjectName).child(userName);
        dbForAttendaceRecordedOrNot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(isButtonClicked) {
                    String s = dataSnapshot.child("isAttendanceRecorded").getValue(String.class);
                    if (s.equals("false")) {
                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            buildAlertMessageNoGps();
                        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            getUserName();
                        }
                    } else {
                        Toast.makeText(AttendanceInStudent.this, "You attendance is already recorded", Toast.LENGTH_LONG).show();
                        isButtonClicked = false;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void validateCode(){
        TextView authCodeTV = (TextView) findViewById(R.id.authCode);
        final String val = authCodeTV.getText().toString();
        final String checkAuth = year+branch+section+AdapterClassForSubjects.subjectName;
        DatabaseReference authCheck = FirebaseDatabase.getInstance().getReference().child("AuthCodes");
        authCheck.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(isButtonClicked) {
                    String code = String.valueOf(dataSnapshot.child(checkAuth).getValue());
                    if (code.equals(val) && !val.equals("")){
                        getNoOfClasses();
                    }
                    else {
                        Toast.makeText(AttendanceInStudent.this, "Incorrect code", Toast.LENGTH_SHORT).show();
                        isButtonClicked=false;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void getNoOfClasses(){
        DatabaseReference databaseReferenceForNoOfClasses = FirebaseDatabase.getInstance().getReference().child("noOfClasses");
        databaseReferenceForNoOfClasses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                noOfClasses = dataSnapshot.child(year+branch+section+AdapterClassForSubjects.subjectName).getValue(Long.class);
                enterDataOnToCloud();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getUserName(){
        DatabaseReference databaseReferenceEmails = FirebaseDatabase.getInstance().getReference().child("emails").child(String.valueOf(fa.getCurrentUser().getUid()));
        databaseReferenceEmails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = String.valueOf(dataSnapshot.child("Name").getValue());
                year = String.valueOf(dataSnapshot.child("Year").getValue());
                branch = String.valueOf(dataSnapshot.child("Branch").getValue());
                section = String.valueOf(dataSnapshot.child("Section").getValue());

                getStudentNames();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void enterDataOnToCloud(){

        if(studentNames.contains(userName)) {
            final DatabaseReference databaseReferenceForDateEntry = FirebaseDatabase.getInstance().getReference().child("attendance").child(year).child(branch).child(section).child(AdapterClassForSubjects.subjectName).child(userName);
            databaseReferenceForDateEntry.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if(isButtonClicked) {
                        if (snapshot.hasChild(formattedDate)) {
                            getThePreviousValue();
                        } else {
                            changeButtonConditions();
                            Toast.makeText(AttendanceInStudent.this, "Your attendance is recorded", Toast.LENGTH_LONG).show();
                            databaseReferenceForDateEntry.child(formattedDate).setValue(noOfClasses);
                            isButtonClicked=false;
                            DatabaseReference dbForAttendaceRecord = databaseReferenceForDateEntry.child("isAttendanceRecorded");
                            dbForAttendaceRecord.setValue("true");

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            Toast.makeText(AttendanceInStudent.this,"You are not a student of this subject",Toast.LENGTH_SHORT).show();
        }

    }

    public void changeButtonConditions(){
        validateBTN.setBackgroundColor(Color.parseColor("#BF360C"));
        validateBTN.setTextColor(Color.WHITE);
        validateBTN.setEnabled(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(60* 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                AttendanceInStudent.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        validateBTN.setEnabled(true);
                        validateBTN.setBackgroundColor(Color.parseColor("#F7DC6F"));
                        validateBTN.setTextColor(Color.BLACK);
                    }
                });
            }
        }).start();
    }

    public void getThePreviousValue(){

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        final String formattedDate = df.format(c);

        final DatabaseReference preValRef = FirebaseDatabase.getInstance().getReference().child("attendance").child(year).child(branch).child(section).child(AdapterClassForSubjects.subjectName).child(userName).child(formattedDate);

        preValRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isButtonClicked) {
                    changeButtonConditions();
                    Toast.makeText(AttendanceInStudent.this, "Your attendance is recorded", Toast.LENGTH_LONG).show();
                    long previousValue = dataSnapshot.getValue(Long.class);
                    long presentVal = previousValue + noOfClasses;
                    preValRef.setValue(presentVal);
                    DatabaseReference dbForAttendaceRecord = FirebaseDatabase.getInstance().getReference().child("attendance").child(year).child(branch).child(section).child(AdapterClassForSubjects.subjectName).child(userName).child("isAttendanceRecorded");
                    dbForAttendaceRecord.setValue("true");

                    isButtonClicked=false;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(AttendanceInStudent.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (AttendanceInStudent.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(AttendanceInStudent.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                lattitude = latti;
                longitude = longi;

//                Toast.makeText(AttendanceInStudent.this,"Your current location is"+ "\n" + "Lattitude = " + lattitude
//                        + "\n" + "Longitude = " + longitude,Toast.LENGTH_SHORT).show();
                calculateDistanceBetweenCoordinates();

            } else  if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                lattitude = latti;
                longitude = longi;

//                Toast.makeText(AttendanceInStudent.this,"Your current location is"+ "\n" + "Lattitude = " + lattitude
//                        + "\n" + "Longitude = " + longitude,Toast.LENGTH_SHORT).show();
                calculateDistanceBetweenCoordinates();


            } else  if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                lattitude = latti;
                longitude = longi;

//                Toast.makeText(AttendanceInStudent.this,"Your current location is"+ "\n" + "Lattitude = " + lattitude
//                        + "\n" + "Longitude = " + longitude,Toast.LENGTH_SHORT).show();
                calculateDistanceBetweenCoordinates();

            }else{

                Toast.makeText(this,"Unable to Trace your location",Toast.LENGTH_SHORT).show();

            }
        }
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

    public void calculateDistanceBetweenCoordinates(){


        DatabaseReference dbForLocationCoordinates = FirebaseDatabase.getInstance().getReference().child("LocationCoordinates").child(year+branch+section+AdapterClassForSubjects.subjectName);
        dbForLocationCoordinates.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                teacherLatitude = String.valueOf(dataSnapshot.child("latitude").getValue());
                teacherLongitude = String.valueOf(dataSnapshot.child("longitude").getValue());
                float[] res = new float[10];
                Location.distanceBetween(lattitude, longitude,Double.valueOf(teacherLatitude),Double.valueOf(teacherLongitude),res);
                if(res[0] <= 5){
                    validateCode();
                }
                else{
                    Toast.makeText(AttendanceInStudent.this,"You are too far away from class",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }

}



