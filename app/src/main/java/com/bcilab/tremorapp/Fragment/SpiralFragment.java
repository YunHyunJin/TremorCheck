package com.bcilab.tremorapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bcilab.tremorapp.PersonalPatientActivity;
import com.bcilab.tremorapp.R;
import com.bcilab.tremorapp.TaskSelectActivity;

public class SpiralFragment extends Fragment {

    private String clinicID ;
    private TabLayout tabLayout ;
    private String patientName ;
    private String task ;
    private int taskNum ;
    private String hand ;
    private SpiralRightFragment spiralRightFragment ;
    private FragmentTransaction fragmentTransaction ;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view ;
        if (getArguments() != null) {
            clinicID = getArguments().getString("clinicID");
            patientName = getArguments().getString("patientName");
            task = getArguments().getString("task");
            taskNum = getArguments().getInt("taskNum",0);
        }

        view = inflater.inflate(R.layout.fragment_spiral, container, false);
        Log.v("SpiralFragment", "TaskName"+task) ;
        ((TextView)view.findViewById(R.id.client_name)).setText(patientName);
        ((TextView)view.findViewById(R.id.task_count)).setText("총 "+taskNum+"번");
        ((TextView)view.findViewById(R.id.task_name)).setText(task.equals("Spiral")?"나선 그리기 검사" : "선 긋기 검사");
        tabLayout = (TabLayout) view.findViewById(R.id.handTab) ;
        tabLayout.addTab(tabLayout.newTab().setText("오른손"));
        tabLayout.addTab(tabLayout.newTab().setText("왼손"));
        tabLayout.addTab(tabLayout.newTab().setText("양손 모아보기"));
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


        Button addTask = (Button) view.findViewById(R.id.spiral_add);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TaskSelectActivity.class);
                intent.putExtra("clinicID", clinicID);
                intent.putExtra("patientName", patientName);
                intent.putExtra("task", task) ;
                startActivity(intent);
            }
        });
        changeView(0);
        return view ;
    }

    public void changeView(int position) {
        Bundle bundle ;
        spiralRightFragment = new SpiralRightFragment() ;
        //nonTaskFragment = new NonTaskFragment() ;
        switch (position){
            case 0 :
                bundle = new Bundle() ;
                hand = "Right";
                bundle.putString("patientName", patientName) ;
                bundle.putString("clinicID", clinicID) ;
                bundle.putString("task", task) ;
                bundle.putString("hand", "Right") ;
                spiralRightFragment.setArguments(bundle);
                fragmentTransaction = getChildFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.handside, spiralRightFragment);
                fragmentTransaction.commit();
                break;
            case 1 :
                bundle = new Bundle() ;
                hand = "Left";
                bundle.putString("patientName", patientName) ;
                bundle.putString("clinicID", clinicID) ;
                bundle.putString("task", task) ;
                bundle.putString("hand", "Left") ;
                spiralRightFragment.setArguments(bundle);
                fragmentTransaction = getChildFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.handside, spiralRightFragment);
                fragmentTransaction.commit();
                break ;
        }
    }

}
