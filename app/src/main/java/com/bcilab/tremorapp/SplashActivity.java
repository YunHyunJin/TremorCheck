package com.bcilab.tremorapp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {// * 가장 처음 화면, splash 화면.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {// * 1000ms 동안 splash 화면 뛰워지고 patientlistactivity로 이동.
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, PatientListActivity.class);
                startActivity(intent);
                finish();
            }
        },  1000);
    }
}
