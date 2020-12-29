package com.bcilab.tremorapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class PatientListActivity extends AppCompatActivity {// * 환자 페이지

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1 ; // * 저장소 권한 요청에 대한 응답 코드
    private long lastTimeBackPressed;// * 뒤로 가기 종료 시간
    private BottomNavigationView bottomNavigationView ;//  * 삭제 모드 일 때 취소 삭제 navigation view
    private PatientListFragment patientListFragment ;// * fragment
    private TextView selectNum ;// * 삭제 모드 일때 선택한 삭제 사용자 수
    private Toolbar toolbar ;// * toolbar
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        selectNum = (TextView) findViewById(R.id.patient_number) ;
        Fragment fragment = new PatientListFragment();// * 환자 프레그먼트
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();// * 작동
        transaction.replace(R.id.patientList, fragment);
        transaction.commit();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation) ;
        bottomNavigationView.setVisibility(View.GONE);// * 삭제 모드일 때만 보여지도록.
        patientListFragment = (PatientListFragment) getSupportFragmentManager().findFragmentById(R.id.patientList);

        checkVerify();
        folderCreate();


        ((Button) findViewById(R.id.button_cancel)).setOnClickListener(new View.OnClickListener() {// * 삭제 모드 일때 삭제 navigation view에서 취소 버튼 누를 시에 삭제 모드 취소
            @Override
            public void onClick(View view) {
                ((PatientListFragment) getSupportFragmentManager().findFragmentById(R.id.patientList)).delete_exit();
            }
        });
        ((Button) findViewById(R.id.button_delete)).setOnClickListener(new View.OnClickListener() {// * 삭제 모드 일때 삭제 navigation view에서 삭제 버튼 누를 시에 선택한 사용자 삭제.
            @Override
            public void onClick(View view) {
                ((PatientListFragment) getSupportFragmentManager().findFragmentById(R.id.patientList)).patient_delete();
            }
        });


    }

    public void visibleBottom(int visible){// * 삭제 navigation view hide or visible
        Log.v("PatientList", "BottomVisible" +visible) ;
        bottomNavigationView.setVisibility(visible);
    }
    public void selectNum(String size){
        selectNum.setText(size);
    }// * 선택한 만큼 삭제 사용자 수가 변경
    public void patientNum(int size){// * 툴바에 총 사용자 수 반영
        toolbar.setTitle("사용자 목록 : "+size+"명");
        setSupportActionBar(toolbar);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.patient_list_toolbar, menu);

        return true;
    }
    @Override public boolean onOptionsItemSelected(MenuItem item) {// * 툴바에서 오른쪽 사용자 추가 버튼 누를시에 사용자 추가 다이어로그 생성
        switch (item.getItemId()) {
            default :
                {
                    if(((PatientListFragment) getSupportFragmentManager().findFragmentById(R.id.patientList)).getDeleteMode()==false) ((PatientListFragment) getSupportFragmentManager().findFragmentById(R.id.patientList)).addPatient();
                    return true;
                }
        }
        //return super.onOptionsItemSelected(item);
    }

    public void checkVerify() {// * 권한 요청 메세지 띄우기
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) { }
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {// * 권한 요청에 대한 응답 event
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {// * 권한 허용했다면 창 다시 시작.



                } else {// * 권한 허용해야 앱을 사용할 수 있다고 전달.

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "저장 권한을 허용해야 앱을 사용하실 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
                startActivity(new Intent(this, PatientListActivity.class));
                finish();
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void folderCreate(){// * 내부 저장소에 폴더 생성

        File path = Environment.getExternalStoragePublicDirectory(
                "/TremorApp");
        File deleteFolder = Environment.getExternalStoragePublicDirectory(
                "/TremorApp/RemovePatient");

        if (!path.mkdirs()&&!deleteFolder.mkdirs()) {
            Log.e("FILE", "Directory not created");
        }else{
            Toast.makeText(this, "내부 저장소 생성", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {// * 뒤로 가기
        PatientListFragment patientListFragment = (PatientListFragment) getSupportFragmentManager().findFragmentById(R.id.patientList);
        if (System.currentTimeMillis() - lastTimeBackPressed < 2200) {
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



