package com.smart.student.attendance.vasanth.smartattendanceforstudent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class Branch extends AppCompatActivity {
    static String branch ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch);
        TextView branchTV = (TextView)findViewById(R.id.branchTV);
        branchTV.append(" Year - "+Year.year);
    }
    public void cse(View v){
        branch = "CSE";
        startActivity(new Intent(Branch.this, Sections.class));
    }
    public void it(View v){
        branch = "IT";
        startActivity(new Intent(Branch.this, Sections.class));
    }
    public void ece(View v){
        branch = "ECE";
        startActivity(new Intent(Branch.this, Sections.class));
    }
    public void eee(View v){
        branch = "EEE";
        startActivity(new Intent(Branch.this, Sections.class));
    }
    public void mechanical(View v){
        branch = "Mechanical";
        startActivity(new Intent(Branch.this, Sections.class));
    }
    public void civil(View v){
        branch = "Civil";
        startActivity(new Intent(Branch.this, Sections.class));
    }
    public void pharma(View v){
        branch = "Pharma";
        startActivity(new Intent(Branch.this, Sections.class));
    }
    public void chemical(View v){
        branch = "Chemical";
        startActivity(new Intent(Branch.this, Sections.class));
    }
}
