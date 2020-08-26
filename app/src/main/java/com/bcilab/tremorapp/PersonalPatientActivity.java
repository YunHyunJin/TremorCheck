package com.bcilab.tremorapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.bcilab.tremorapp.Fragment.PatientListFragment;
import com.bcilab.tremorapp.Fragment.SpiralFragment;

public class PersonalPatientActivity extends AppCompatActivity {

    private TabLayout tabLayout ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_patient);
        String clinicID, patientName, dateFirst, dateFinal ;

        Intent intent = getIntent() ;
        tabLayout = (TabLayout) findViewById(R.id.taskTab) ;
        clinicID = intent.getExtras().getString("clinicID");
        patientName = intent.getExtras().getString("patientName");

        tabLayout.addTab(tabLayout.newTab().setText("Spiral"));
        tabLayout.addTab(tabLayout.newTab().setText("Line"));
        ((TextView) findViewById(R.id.patient)).setText(clinicID+" "+patientName) ;
        Fragment fragment = new SpiralFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.task, fragment);transaction.commit();



    }
}
