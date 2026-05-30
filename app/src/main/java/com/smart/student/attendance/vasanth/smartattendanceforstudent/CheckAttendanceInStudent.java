package com.smart.student.attendance.vasanth.smartattendanceforstudent;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class CheckAttendanceInStudent extends AppCompatActivity {
    long attendedClasses = 0L,totalClasses = 0L;
    TextView dbTV3,workingClassesTV,attendedClassesTV;

    DatabaseReference databaseReferenceEmail,databaseReferenceForTotalClasses;
    String year,branch,section,userName;
    FirebaseAuth firebaseAuthSub;

    DatabaseReference dbForCheckingAttendance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_attendance_in_student);


        dbTV3 = (TextView) findViewById(R.id.dbTV3);
        workingClassesTV = findViewById(R.id.workingClassesTV);
        attendedClassesTV = findViewById(R.id.attendedClassesTV);

        firebaseAuthSub  = FirebaseAuth.getInstance();
        databaseReferenceEmail  = FirebaseDatabase.getInstance().getReference().child("emails").child(String.valueOf(firebaseAuthSub.getCurrentUser().getUid()));
        databaseReferenceEmail.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userName = String.valueOf(dataSnapshot.child("Name").getValue());
                year = String.valueOf(dataSnapshot.child("Year").getValue());
                branch = String.valueOf(dataSnapshot.child("Branch").getValue());
                section = String.valueOf(dataSnapshot.child("Section").getValue());


                dbForCheckingAttendance = FirebaseDatabase.getInstance().getReference().child("attendance").child(year).child(branch).child(section);
                dbForCheckingAttendance.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){ // ds : Each subject
                            final DatabaseReference dbForCheckingAttendance1 = dbForCheckingAttendance.child(ds.getKey()); // Inside a particular Subject
                            dbForCheckingAttendance1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    DatabaseReference dbForCheckingAttendance2 = dbForCheckingAttendance1.child(userName); // Inside a particular Student
                                    dbForCheckingAttendance2.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds1:dataSnapshot.getChildren())//Getting the dates the Student
                                                if(!ds1.getValue().equals("false") && !ds1.getValue().equals("true"))
                                                    if(ds1.getValue(Long.class) != null)
                                                        attendedClasses += ds1.getValue(Long.class);
                                               attendedClassesTV.setText("Number of attended classes: "+String.valueOf(attendedClasses));
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        countTotalClasses();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void countTotalClasses(){
        databaseReferenceForTotalClasses = FirebaseDatabase.getInstance().getReference().child("attendance").child(year).child(branch).child(section);
        databaseReferenceForTotalClasses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    final DatabaseReference dbForCheckingAttendance1 = dbForCheckingAttendance.child(ds.getKey());
                    dbForCheckingAttendance1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(userName)) {
                                DatabaseReference dbForCheckingAttendance2 = dbForCheckingAttendance1.child("classesConducted");
                                dbForCheckingAttendance2.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds1 : dataSnapshot.getChildren())
                                            if(!ds1.getValue().equals("false") && !ds1.getValue().equals("true"))
                                                if (ds1.getValue(Long.class) != null)
                                                totalClasses += ds1.getValue(Long.class);
                                             workingClassesTV.setText("Number of conducted classes: "+String.valueOf(totalClasses));
                                            double finalAttendance = (double)(attendedClasses*100)/totalClasses;
                                             dbTV3.setText(String.valueOf(new DecimalFormat("##.##").format(finalAttendance))+"%");
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

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void onBackPressed(){
        super.onBackPressed();
        attendedClasses = 0L;
        totalClasses = 0L;
    }
    public void subjectWiseAttendance(View v){
        startActivity(new Intent(CheckAttendanceInStudent.this,SubjectWiseAttendance.class));
    }

}
