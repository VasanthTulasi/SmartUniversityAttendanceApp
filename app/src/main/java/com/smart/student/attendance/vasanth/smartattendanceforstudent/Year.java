package com.smart.student.attendance.vasanth.smartattendanceforstudent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Year extends AppCompatActivity {
    static String year="";

    LocationManager locationManager;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year);

        fAuth = FirebaseAuth.getInstance();

        ActivityCompat.requestPermissions(Year.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

    }


    public void year1(View v){
        year = "1";
        startActivity(new Intent(Year.this, Branch.class));
    }
    public void year2(View v){
        year = "2";
        startActivity(new Intent(Year.this, Branch.class));
    }
    public void year3(View v){
        year = "3";
        startActivity(new Intent(Year.this, Branch.class));
    }
    public void year4(View v){
        year = "4";
        startActivity(new Intent(Year.this, Branch.class));
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
    public void signout(View v){
        fAuth.signOut();
        startActivity(new Intent(Year.this,Login.class));
    }
    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

}
