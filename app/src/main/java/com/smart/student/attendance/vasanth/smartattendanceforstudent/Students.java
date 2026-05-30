package com.smart.student.attendance.vasanth.smartattendanceforstudent;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

public class Students extends AppCompatActivity {

    DatabaseReference databaseReferenceInStudents;

    ArrayList<String> keysArrayListInStudents;
    ArrayList<String> namesForReferenceInStudents;
    ArrayList<CardClass> membersArrayListInStudents;
    static ArrayList<String> referenceForKeyArrayListInStudents;
    static ArrayList<String> referenceForNamesArrayListInStudents;

    DatabaseReference databaseReferenceEmail;
    String year,branch,section,userName;
    FirebaseAuth firebaseAuthSub;
    ProgressBar pb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);

        firebaseAuthSub  = FirebaseAuth.getInstance();
        pb = findViewById(R.id.pBarInStudents);

        TextView studentsTV = (TextView)findViewById(R.id.studentsTV);
        studentsTV.append(" "+ AdapterClassForSubjects.subjectName);

        databaseReferenceEmail  = FirebaseDatabase.getInstance().getReference().child("emails").child(String.valueOf(firebaseAuthSub.getCurrentUser().getUid()));
        databaseReferenceEmail.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = String.valueOf(dataSnapshot.child("Name").getValue());
                year = String.valueOf(dataSnapshot.child("Year").getValue());
                branch = String.valueOf(dataSnapshot.child("Branch").getValue());
                section = String.valueOf(dataSnapshot.child("Section").getValue());
                getStudentList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final RelativeLayout ll = new RelativeLayout(this);
        final TextView dynamicTextView = new TextView(this);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(30* 1000);//min secs millisecs
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Students.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            pb.setVisibility(View.GONE);
                            if(namesForReferenceInStudents.isEmpty()) {
                             Toast.makeText(Students.this,"No students in this subject",Toast.LENGTH_LONG).show();
                            }
                    }
                });
            }
        }).start();

    }

    public void getStudentList(){
        databaseReferenceInStudents = FirebaseDatabase.getInstance().getReference().child("addedStudents").child(year).child(branch).child(section).child(AdapterClassForSubjects.subjectName);
        ListView listViewInStudents = (ListView) findViewById(R.id.listViewForMemberInStudentActivity);

        membersArrayListInStudents = new ArrayList<>();
        keysArrayListInStudents = new ArrayList<>();
        namesForReferenceInStudents = new ArrayList<>();
        referenceForKeyArrayListInStudents = new ArrayList<>();
        referenceForNamesArrayListInStudents= new ArrayList<>();

        final AdapterClassForStudents adapterForMemberInStudents = new AdapterClassForStudents(this, R.layout.card_design_for_students, membersArrayListInStudents);
        listViewInStudents.setAdapter(adapterForMemberInStudents);
        databaseReferenceInStudents.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    final String addedMember = dataSnapshot.getValue(String.class);

                    final DatabaseReference dbToFindRollNumber = FirebaseDatabase.getInstance().getReference().child("emails");
                    dbToFindRollNumber.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                DatabaseReference dbToFindRollNumberInsideEachKey = dbToFindRollNumber.child(ds.getKey());
                                dbToFindRollNumberInsideEachKey.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.child("Name").getValue().equals(addedMember)) {
                                            String rollNumber = String.valueOf(dataSnapshot.child("RollNumber").getValue());
                                            membersArrayListInStudents.add(new CardClass(1, addedMember, rollNumber));
                                            namesForReferenceInStudents.add(addedMember);
                                            String addedkey = dataSnapshot.getKey();
                                            keysArrayListInStudents.add(addedkey);
                                            adapterForMemberInStudents.notifyDataSetChanged();

                                            referenceForKeyArrayListInStudents = keysArrayListInStudents;
                                            referenceForNamesArrayListInStudents = namesForReferenceInStudents;

                                            if (pb != null) {
                                                pb.setVisibility(View.GONE);
                                            }
                                        }

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


    public void markAttendance(View v){
        if(namesForReferenceInStudents.contains(userName))
            startActivity(new Intent(Students.this, AttendanceInStudent.class));
        else
            Toast.makeText(Students.this,"You are not a student of this subject yet. Contact the admin for further details.",Toast.LENGTH_LONG).show();
    }

    public void checkAttendance(View v){
        startActivity(new Intent(Students.this,CheckAttendanceInStudent.class));
    }


}
