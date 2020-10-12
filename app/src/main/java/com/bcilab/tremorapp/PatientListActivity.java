package com.bcilab.tremorapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.design.internal.BottomNavigationPresenter;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcilab.tremorapp.Adapter.RecyclerViewAdapter;
import com.bcilab.tremorapp.Data.PatientItem;
import com.bcilab.tremorapp.Fragment.PatientListFragment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PatientListActivity extends AppCompatActivity {

    private long lastTimeBackPressed;
    private BottomNavigationView bottomNavigationView ;
    private PatientListFragment patientListFragment ;
    private TextView selectNum ;
    private Toolbar toolbar ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        selectNum = (TextView) findViewById(R.id.patient_number) ;
        Fragment fragment = new PatientListFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.patientList, fragment);
        transaction.commit();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation) ;
        bottomNavigationView.setVisibility(View.GONE);
        patientListFragment = (PatientListFragment) getSupportFragmentManager().findFragmentById(R.id.patientList);

        checkVerify();
        folderCreate();


        ((Button) findViewById(R.id.button_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PatientListFragment) getSupportFragmentManager().findFragmentById(R.id.patientList)).delete_exit();
            }
        });
    }

    public void visibleBottom(int visible){
        Log.v("PatientList", "BottomVisible" +visible) ;
        bottomNavigationView.setVisibility(visible);
    }
    public void selectNum(String size){
        selectNum.setText(size);
    }
    public void patientNum(int size){
        toolbar.setTitle("환자 목록 : "+size+"명");
        setSupportActionBar(toolbar);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.patient_list_toolbar, menu);

        return true;
    }
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default :
                {
                    ((PatientListFragment) getSupportFragmentManager().findFragmentById(R.id.patientList)).addPatient();
                    return true;
                }
        }
        //return super.onOptionsItemSelected(item);
    }

    public void checkVerify() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) { }
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
    }

    public void folderCreate(){

        File path = Environment.getExternalStoragePublicDirectory(
                "/TremorApp");
        File deleteFolder = Environment.getExternalStoragePublicDirectory(
                "/TremorApp/RemovePatient");

        if (!path.mkdirs()&&!deleteFolder.mkdirs()) {
            Log.e("FILE", "Directory not created");
        }else{
            Toast.makeText(this, "폴더 저장", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        PatientListFragment patientListFragment = (PatientListFragment) getSupportFragmentManager().findFragmentById(R.id.patientList);
        if (System.currentTimeMillis() - lastTimeBackPressed < 2000) {
            ActivityCompat.finishAffinity(this);
            return;
        }
        if (patientListFragment.getdeleteMode() == true) {
            patientListFragment.delete_exit();
        }
        else {
            lastTimeBackPressed = System.currentTimeMillis();
            Toast.makeText(this, "'뒤로' 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }
}



