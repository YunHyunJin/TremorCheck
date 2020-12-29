package com.bcilab.tremorapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bcilab.tremorapp.Fragment.NonTaskFragment;
import com.bcilab.tremorapp.Fragment.TaskFragment;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class PersonalPatientActivity extends AppCompatActivity {// * 상세 정보 페이지

    private TabLayout tabLayout ;// * 나선 그리기, 선 긋기 tab
    private NonTaskFragment nonTaskFragment ;// * 검사 안했을 시에 띄워지는 fragment
    private TaskFragment taskFragment;// * 나선, 선 긋기 fragment (선택한 tab에 따라 변수가 다르게 적용되어 하나의 fragment에서도 나선, 선긋기 분리 될 수 있도록 함
    private FragmentTransaction fragmentTransaction ;
    private String task ;// * 선택한 task 가 나선 그리기 인지, 선 긋기 인지 알려주는 변수
    private int spiral_right, spiral_left, line_right, line_left ;// * 각 항목별 검사 수
    private String patientName, clinicID ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_patient);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        Intent intent = getIntent() ;
        tabLayout = (TabLayout) findViewById(R.id.taskTab) ;
        tabLayout.addTab(tabLayout.newTab().setText("나선 그리기"));
        tabLayout.addTab(tabLayout.newTab().setText("선 긋기"));
        clinicID = intent.getExtras().getString("clinicID");
        patientName = intent.getExtras().getString("patientName");
        task = intent.getExtras().getString("task");

        toolbar.setTitle(clinicID+" "+patientName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// * 툴바 왼쪽에 뒤로가기 버튼
        taskFragment = new TaskFragment() ;
        nonTaskFragment = new NonTaskFragment() ;

    }
    @Override
    public void onStart() {
        super.onStart();
        PatientLoad(clinicID);
        if(task.equals("Spiral")) changeView(0);// * task 이름에 따라 나선 그리기 탭 실행
        else changeView(1);// * task 이름에 따라 선 긋기 탭 실행

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {// * tab 선택시 fragment 적용
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
    public boolean onCreateOptionsMenu(Menu menu)// * toolbar 에 menu 적용
    {
        getMenuInflater().inflate(R.menu.personal_patient_toolbar, menu);

        return true;
    }
    @Override public boolean onOptionsItemSelected(MenuItem item) {//
        switch (item.getItemId()) {
            case R.id.edit_patient :
            {// * 삭제 하기.
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

                                                    for(File file : deleteList) {
                                                        if(file.listFiles()!=null && file.listFiles().length!=0) {
                                                            for (File inner_file : file.listFiles()) {
                                                                inner_file.delete();
                                                            }

                                                        }
                                                        file.delete();
                                                    }

                                                    source.delete();
                                                    Intent intent = new Intent(PersonalPatientActivity.this, PatientListActivity.class);
                                                    startActivity(intent);
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
                Intent intent = new Intent(PersonalPatientActivity.this, PatientListActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PersonalPatientActivity.this, PatientListActivity.class);
        startActivity(intent);
        finish();
    }
    public String PatientLoad(String clinic_id) {
        File path = Environment.getExternalStoragePublicDirectory(
                "/TremorApp/"+clinic_id);
        spiral_left = 0 ;
        spiral_right = 0 ;
        line_left = 0 ;
        line_right = 0 ;
        //File spiralDirectory =new File(String.valueOf(path)+"/SpiralLeft") ;

        spiral_left = taskCount(new File(String.valueOf(path)+"/SpiralLeft").listFiles()) ;// * 검사 수 가져오기
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
            return date ;// * 이제 가져올 필요 없음 date 안쓰임
    }
    public int taskCount(File[] folder){// * .jpg 파일을 읽어서 검사 횟수 가져오기
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
        taskFragment = new TaskFragment() ;
        nonTaskFragment = new NonTaskFragment() ;
        switch (position){
            case 0 :
                bundle = new Bundle() ;
                task = "Spiral";// * task name에 따라 적용
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
                    taskFragment.setArguments(bundle);
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.task, taskFragment);
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
                if((line_left+line_right)==0) {// * 아무것도 안했을 시
                    nonTaskFragment.setArguments(bundle);
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.task, nonTaskFragment);
                    fragmentTransaction.commit();
                }
                else {
                    taskFragment.setArguments(bundle);
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.task, taskFragment);
                    fragmentTransaction.commit();
                }
                break;
        }
    }
}
