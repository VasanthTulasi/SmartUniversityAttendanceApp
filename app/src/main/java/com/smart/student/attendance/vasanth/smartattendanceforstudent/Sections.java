package com.smart.student.attendance.vasanth.smartattendanceforstudent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class Sections extends AppCompatActivity {

    static String section = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sections);

        TextView sectionNameTV = (TextView)findViewById(R.id.sectionNameTV);
        sectionNameTV.append(" "+Branch.branch);
    }

    public void secA(View v){
        section = "A";
        startActivity(new Intent(Sections.this, Subjects.class));
    }
    public void secB(View v){
        section = "B";
        startActivity(new Intent(Sections.this, Subjects.class));
    }
    public void secC(View v){
        section = "C";
        startActivity(new Intent(Sections.this, Subjects.class));
    }
    public void secD(View v){
        section = "D";
        startActivity(new Intent(Sections.this, Subjects.class));
    }
}
