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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
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
    private int spiral_right, spiral_left, line_right, line_left ;
    private String patientName, clinicID ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_patient);
        String dateFirst, dateFinal ;

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        Intent intent = getIntent() ;
        tabLayout = (TabLayout) findViewById(R.id.taskTab) ;
        tabLayout.addTab(tabLayout.newTab().setText("Spiral"));
        tabLayout.addTab(tabLayout.newTab().setText("Line"));
        clinicID = intent.getExtras().getString("clinicID");
        patientName = intent.getExtras().getString("patientName");
        task = intent.getExtras().getString("task");
        Log.v("PatientList","PPPPPPPPPPP"+clinicID+" "+ patientName+" "+task);
        toolbar.setTitle(clinicID+" "+patientName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spiralFragment = new SpiralFragment() ;
        nonTaskFragment = new NonTaskFragment() ;

    }
    @Override
    public void onStart() {
        super.onStart();
        PatientLoad(clinicID);
        if(task.equals("Spiral")) changeView(0);
        else changeView(1);
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
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.personal_patient_toolbar, menu);

        return true;
    }
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_patient :
            {
                PopupMenu popupMenu = new PopupMenu(PersonalPatientActivity.this, (View) findViewById(R.id.edit_patient));
                popupMenu.getMenuInflater().inflate(R.menu.patient_edit, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
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
                return true;
            }
            case android.R.id.home: {
                onBackPressed();
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    public String PatientLoad(String clinic_id) {
        File path = Environment.getExternalStoragePublicDirectory(
                "/TremorApp/"+clinic_id);
        spiral_left = 0 ;
        spiral_right = 0 ;
        line_left = 0 ;
        line_right = 0 ;
        //File spiralDirectory =new File(String.valueOf(path)+"/SpiralLeft") ;

        spiral_left = taskCount(new File(String.valueOf(path)+"/SpiralLeft").listFiles()) ;
        spiral_right = taskCount(new File(String.valueOf(path)+"/SpiralRight").listFiles()) ;
        line_left = taskCount(new File(String.valueOf(path)+"/LineLeft").listFiles()) ;
        line_right = taskCount(new File(String.valueOf(path)+"/LineRight").listFiles()) ;

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
    public int taskCount(File[] folder){
        int task = 0;
        for(int i = 0 ; i<folder.length ; i++) {
            if(folder[i].getName().contains(".jpg")){
                task++ ;
            }
        }
        return task ;
    }
    public void changeView(int position) {
        Bundle bundle ;
        spiralFragment = new SpiralFragment() ;
        nonTaskFragment = new NonTaskFragment() ;
        switch (position){
            case 0 :
                bundle = new Bundle() ;
                task = "Spiral";
                bundle.putString("patientName", patientName) ;
                bundle.putString("clinicID", clinicID) ;
                bundle.putString("task", "Spiral") ;
                bundle.putInt("left", spiral_left) ;
                bundle.putInt("right", spiral_right) ;
                if((spiral_left+spiral_right)==0) {
                    nonTaskFragment.setArguments(bundle);
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.task, nonTaskFragment);
                    fragmentTransaction.commit();
                }
                else{
                    spiralFragment.setArguments(bundle);
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.task, spiralFragment);
                    fragmentTransaction.commit();
                }
                break;
            case 1 :
                bundle = new Bundle() ;
                task="Line";
                bundle.putString("patientName", patientName);
                bundle.putString("clinicID", clinicID);
                bundle.putString("task", task);
                bundle.putInt("left", line_left);
                bundle.putInt("right", line_right);
                if((line_left+line_right)==0) {
                    nonTaskFragment.setArguments(bundle);
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.task, nonTaskFragment);
                    fragmentTransaction.commit();
                }
                else {
                    spiralFragment.setArguments(bundle);
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.task, spiralFragment);
                    fragmentTransaction.commit();
                }
                break;
        }
    }
}
