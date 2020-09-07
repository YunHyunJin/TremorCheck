package com.bcilab.tremorapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent() ;

        final double[] spiral_result = intent.getDoubleArrayExtra("spiral_result");
        //[0]: TM , [1]: TF , [2]:time , [3]: ED , [4]:velocity
        //[0]: 떨림규모 , [1]: 떨림 , [2]:time , [3]: 거리 , [4]:속도
        Log.v("ResultActivity", "spiral_result"+"  "+spiral_result[0]+"  "+spiral_result[1]+"  "+spiral_result[2]+"  "+spiral_result[3]+"  "+spiral_result[4]) ;
    }
}
