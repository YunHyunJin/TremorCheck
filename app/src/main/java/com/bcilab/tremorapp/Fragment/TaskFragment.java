package com.bcilab.tremorapp.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bcilab.tremorapp.LineActivity;
import com.bcilab.tremorapp.R;
import com.bcilab.tremorapp.SpiralActivity;

public class TaskFragment extends Fragment {

    private String clinicID ;
    private TabLayout tabLayout ;
    private String patientName ;
    private String task ;
    private int right, left, taskNum ;
    private String both ;
    private TaskDetailFragment taskDetailFragment;
    private BothFragment bothFragment ;
    private FragmentTransaction fragmentTransaction ;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view ;
        if (getArguments() != null) {
            clinicID = getArguments().getString("clinicID");
            patientName = getArguments().getString("patientName");
            task = getArguments().getString("task");
            right = getArguments().getInt("right",0);
            left = getArguments().getInt("left",0);
            taskNum = right+left ;
        }
        view = inflater.inflate(R.layout.fragment_spiral, container, false);
        Log.v("TaskFragment", "TaskName"+task) ;
        ((TextView)view.findViewById(R.id.client_name)).setText(patientName);
        ((TextView)view.findViewById(R.id.task_count)).setText("총 "+taskNum+"번");
        ((TextView)view.findViewById(R.id.task_name)).setText(task.equals("Spiral")?"나선 그리기 검사" : "선 긋기 검사");
        tabLayout = (TabLayout) view.findViewById(R.id.handTab) ;
        tabLayout.addTab(tabLayout.newTab().setText("오른손 ("+right+")"));
        tabLayout.addTab(tabLayout.newTab().setText("왼손 ("+left+")"));
        tabLayout.addTab(tabLayout.newTab().setText("양손 모아보기"));
        tabLayout.setSelectedTabIndicatorHeight(0);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
                LayoutInflater inflater = getLayoutInflater();
                View dialog_view = inflater.inflate(R.layout.activity_popup, null);
                builder.setView(dialog_view);
                final Button select_right = (Button) dialog_view.findViewById(R.id.right);
                final Button select_left = (Button) dialog_view.findViewById(R.id.left);
                builder.setTitle(task.equals("Spiral") ? "나선 그리기 검사" : "선 긋기 검사");
                final AlertDialog dialog = builder.create() ;
                dialog.show() ;
                Intent intent ;
                if(task.equals("Spiral"))
                    intent = new Intent(getActivity(), SpiralActivity.class) ;
                else intent = new Intent(getActivity(), LineActivity.class) ;
                intent.putExtra("clinicID", clinicID);
                intent.putExtra("patientName", patientName);
                intent.putExtra("task", task) ;
                select_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        intent.putExtra("both","Right") ;
                        Context context = getActivity();
                        LayoutInflater inflater = getLayoutInflater();
                        View customToast = inflater.inflate(R.layout.toast_custom, null);
                        Toast customtoast = new Toast(context);
                        customtoast.setView(customToast);
                        customtoast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                        customtoast.setDuration(Toast.LENGTH_LONG);
                        customtoast.show();
                        startActivity(intent) ;
                        dialog.dismiss();
                    }
                });

                select_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        intent.putExtra("both","Left") ;
                        Context context = getActivity();
                        LayoutInflater inflater = getLayoutInflater();
                        View customToast = inflater.inflate(R.layout.toast_custom, null);
                        Toast customtoast = new Toast(context);
                        customtoast.setView(customToast);
                        customtoast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                        customtoast.setDuration(Toast.LENGTH_LONG);
                        customtoast.show();
                        startActivity(intent) ;
                        dialog.dismiss();
                    }
                });

            }
        });
        changeView(0);
        return view ;
    }

    public void changeView(int position) {// * 오른손, 왼손, 양손 모아보기 탭 실시
        Bundle bundle ;
        taskDetailFragment = new TaskDetailFragment() ;
        bothFragment = new BothFragment();
        switch (position){
            case 0 :
                bundle = new Bundle() ;
                both = "Right";
                bundle.putString("patientName", patientName) ;
                bundle.putString("clinicID", clinicID) ;
                bundle.putString("task", task) ;
                bundle.putString("both", "Right") ;
                taskDetailFragment.setArguments(bundle);
                fragmentTransaction = getChildFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.handside, taskDetailFragment);
                fragmentTransaction.commit();
                break;
            case 1 :
                bundle = new Bundle() ;
                both = "Left";
                bundle.putString("patientName", patientName) ;
                bundle.putString("clinicID", clinicID) ;
                bundle.putString("task", task) ;
                bundle.putString("both", "Left") ;
                taskDetailFragment.setArguments(bundle);
                fragmentTransaction = getChildFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.handside, taskDetailFragment);
                fragmentTransaction.commit();
                break ;
            case 2:
                bundle = new Bundle() ;
                both = "Both";
                bundle.putString("patientName", patientName) ;
                bundle.putString("clinicID", clinicID) ;
                bundle.putString("task", task) ;
                bundle.putString("both", both) ;
                bothFragment.setArguments(bundle);
                fragmentTransaction = getChildFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.handside, bothFragment);
                fragmentTransaction.commit();
                break;
        }
    }

}
