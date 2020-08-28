package com.bcilab.tremorapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bcilab.tremorapp.Fragment.PatientListFragment;
import com.bcilab.tremorapp.Fragment.SpiralFragment;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

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

        ((Button) findViewById(R.id.backButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
        ((Button) findViewById(R.id.dot)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(PersonalPatientActivity.this, ((Button) findViewById(R.id.dot)));
                popupMenu.getMenuInflater().inflate(R.menu.patient_edit, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit:
                                //show(Clinic_ID);
                                return true;

                            case R.id.person_delete:
                                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PersonalPatientActivity.this);
                                dialogBuilder.setMessage("삭제 하시겠습니까?");
                                dialogBuilder.setPositiveButton("예",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                File source = Environment.getExternalStoragePublicDirectory(
                                                        "/TremorApp/"+clinicID);
                                                File dest = Environment.getExternalStoragePublicDirectory(
                                                        "/TremorApp/RemovePatient/"+clinicID);
                                                try {
                                                    FileUtils.copyDirectory(source, dest);
                                                    File[] deleteList = source.listFiles();
                                                    for(File file : deleteList) file.delete();
                                                    source.delete();
                                                    onBackPressed();
                                                    finish();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                dialogBuilder.setNegativeButton("아니오",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                dialogBuilder.create().show();
                                return true;

                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        tabLayout.addTab(tabLayout.newTab().setText("Spiral"));
        tabLayout.addTab(tabLayout.newTab().setText("Line"));

        ((TextView) findViewById(R.id.patient)).setText(clinicID+" "+patientName) ;

        Fragment fragment = new SpiralFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.task, fragment);transaction.commit();


    }
}
