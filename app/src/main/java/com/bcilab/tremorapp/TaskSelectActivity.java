package com.bcilab.tremorapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bcilab.tremorapp.Function.LineActivity;

public class TaskSelectActivity extends AppCompatActivity {

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
                startActivity(intent) ;
            }
        });
    }
}
