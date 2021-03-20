package com.bcilab.tremorapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TaskSelectActivity extends AppCompatActivity {// * 이거 사용안함

    private String clinicID;
    private String patientName ;
    private String task ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_select);
        Intent intent = getIntent() ;
        clinicID = intent.getExtras().getString("clinicID");
        patientName = intent.getExtras().getString("patientName");
        task = intent.getExtras().getString("task");

        Button right_select = (Button) findViewById(R.id.right_select);
        right_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent ;
                if(task.equals("Spiral"))
                    intent = new Intent(getApplicationContext(), SpiralActivity.class) ;
                else intent = new Intent(getApplicationContext(), LineActivity.class) ;
                intent.putExtra("clinicID", clinicID);
                intent.putExtra("patientName", patientName);
                intent.putExtra("task", task) ;
                intent.putExtra("both","Right") ;
                Context context = getApplicationContext();
                LayoutInflater inflater = getLayoutInflater();
                View customToast = inflater.inflate(R.layout.toast_custom, null);
                Toast customtoast = new Toast(context);
                customtoast.setView(customToast);
                customtoast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                customtoast.setDuration(Toast.LENGTH_LONG);
                customtoast.show();
                startActivity(intent) ;
            }
        });

        Button left_select = (Button) findViewById(R.id.left_select);
        left_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent ;
                if(task.equals("Spiral"))
                    intent = new Intent(getApplicationContext(), SpiralActivity.class) ;
                else intent = new Intent(getApplicationContext(), LineActivity.class) ;
                intent.putExtra("clinicID", clinicID);
                intent.putExtra("patientName", patientName);
                intent.putExtra("task", task) ;
                intent.putExtra("both","Left") ;
                Context context = getApplicationContext();
                LayoutInflater inflater = getLayoutInflater();
                View customToast = inflater.inflate(R.layout.toast_custom, null);
                Toast customtoast = new Toast(context);
                customtoast.setView(customToast);
                customtoast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                customtoast.setDuration(Toast.LENGTH_LONG);
                customtoast.show();
                startActivity(intent) ;
            }
        });
    }
}
