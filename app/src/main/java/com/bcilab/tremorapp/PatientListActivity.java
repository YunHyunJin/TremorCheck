package com.bcilab.tremorapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.design.internal.BottomNavigationPresenter;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

    InputMethodManager imm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation) ;
        bottomNavigationView.setVisibility(View.GONE);
        Fragment fragment = new PatientListFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.patientList, fragment);

        transaction.commit();

    }
}


