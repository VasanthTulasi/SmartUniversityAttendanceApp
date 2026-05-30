package com.smart.student.attendance.vasanth.smartattendanceforstudent;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {


    EditText emailForPassResetTV;
    Button resetBtn;
    FirebaseAuth fa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailForPassResetTV = findViewById(R.id.emailForPassResetTV);
        fa = FirebaseAuth.getInstance();
        resetBtn = findViewById(R.id.resetBtn);
    }


    public void sendResetLink(View v){

        fa.sendPasswordResetEmail(emailForPassResetTV.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                 if(task.isSuccessful()){
                     Toast.makeText(ForgotPassword.this,"Reset link sent to the given email",Toast.LENGTH_LONG).show();
                 }
               }
           });

           resetBtn.setText("Resend link");
           resetBtn.setBackgroundColor(Color.parseColor("#BF360C"));
           resetBtn.setTextColor(Color.WHITE);
           resetBtn.setEnabled(false);

           new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                      Thread.sleep(60* 1000);//min secs millisecs
                    } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ForgotPassword.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resetBtn.setEnabled(true);
                        resetBtn.setBackgroundColor(Color.parseColor("#F7DC6F"));
                        resetBtn.setTextColor(Color.BLACK);

                    }
                });
            }
        }).start();

    }


}
