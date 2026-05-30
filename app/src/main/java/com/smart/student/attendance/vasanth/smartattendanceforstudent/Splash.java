package com.smart.student.attendance.vasanth.smartattendanceforstudent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TextView gello =(TextView)findViewById(R.id.gello);
        gello.setText(AdapterClassForSubjects.subjectName);
    }
}
