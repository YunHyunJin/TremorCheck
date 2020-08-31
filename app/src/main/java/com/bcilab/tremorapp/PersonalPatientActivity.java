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

import com.bcilab.tremorapp.Data.PatientItem;
import com.bcilab.tremorapp.Fragment.NonTaskFragment;
import com.bcilab.tremorapp.Fragment.PatientListFragment;
import com.bcilab.tremorapp.Fragment.SpiralFragment;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class PersonalPatientActivity extends AppCompatActivity {

    private TabLayout tabLayout ;
    private NonTaskFragment nonTaskFragment ;
    private SpiralFragment spiralFragment ;
    private FragmentTransaction fragmentTransaction ;
    private String task ;
    private int spiral, line ;
    private String patientName, clinicID ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_patient);
        String dateFirst, dateFinal ;

        Intent intent = getIntent() ;
        tabLayout = (TabLayout) findViewById(R.id.taskTab) ;
        clinicID = intent.getExtras().getString("clinicID");
        patientName = intent.getExtras().getString("patientName");
        task = intent.getExtras().getString("task");

        spiralFragment = new SpiralFragment() ;
        nonTaskFragment = new NonTaskFragment() ;

        if(task.equals("Spiral")) changeView(0);
        else changeView(1);


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

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                changeView(tab.getPosition()) ;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //changeView(0) ;
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        ((TextView) findViewById(R.id.patient)).setText(clinicID+" "+patientName) ;
        ((TextView) findViewById(R.id.date)).setText(PatientLoad(clinicID)) ;



    }

    public String PatientLoad(String clinic_id) {
        File path = Environment.getExternalStoragePublicDirectory(
                "/TremorApp/"+clinic_id);
        spiral = 0 ;
        line = 0 ;
        //File spiralDirectory =new File(String.valueOf(path)+"/SpiralLeft") ;
        spiral+= new File(String.valueOf(path)+"/SpiralLeft").listFiles().length;
        spiral+=new File(String.valueOf(path)+"/SpiralRight").listFiles().length;

        line+=new File(String.valueOf(path)+"/LineLeft").listFiles().length;
        line+=new File(String.valueOf(path)+"/LineRight").listFiles().length;

        String date = null ;
            try {
                File patientCSV = new File(path, "patient.csv");

                BufferedReader buffer = new BufferedReader(new FileReader(patientCSV));
                String str = buffer.readLine();
                while (str!=null) {
                    str = buffer.readLine();
                    String[] patientStr= str.split(",");
                    date = (patientStr[3].equals("null")? "" : patientStr[3]) +" - "+  (patientStr[4].equals("null")? "" : patientStr[4]) ;
                }
                buffer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return date ;
    }

    public void changeView(int position) {
        switch (position){
            case 0 :
                Bundle bundle = new Bundle() ;
//                if(spiral==0) {
//                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                    fragmentTransaction.replace(R.id.task, nonTaskFragment);
//                    fragmentTransaction.commit();
//                }
//                else{
//                    bundle.putString("patientName", patientName) ;
//                    bundle.putString("clinicID", clinicID) ;
//                    bundle.putString("task", "Spiral") ;
//                    bundle.putInt("taskNum", spiral) ;
//                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                    fragmentTransaction.replace(R.id.task, spiralFragment);
//                    fragmentTransaction.commit();
//                }
                bundle.putString("patientName", patientName) ;
                bundle.putString("clinicID", clinicID) ;
                bundle.putString("task", "Spiral") ;
                bundle.putInt("taskNum", spiral) ;
                spiralFragment.setArguments(bundle);
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.task, spiralFragment);
                fragmentTransaction.commit();
                break;
            case 1 :

                break ;
        }
    }
}
