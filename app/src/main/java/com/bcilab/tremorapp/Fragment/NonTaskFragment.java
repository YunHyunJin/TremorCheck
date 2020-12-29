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
import android.widget.Toast;

import com.bcilab.tremorapp.LineActivity;
import com.bcilab.tremorapp.R;
import com.bcilab.tremorapp.SpiralActivity;
import com.bcilab.tremorapp.TaskSelectActivity;

public class NonTaskFragment extends Fragment {
    private String clinicID ;
    private TabLayout tabLayout ;
    private String patientName ;
    private String task ;
    private int right, left, taskNum ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view ;
        clinicID = getArguments().getString("clinicID");
        patientName = getArguments().getString("patientName");
        task = getArguments().getString("task");
        right = getArguments().getInt("right",0);
        left = getArguments().getInt("left",0);
        taskNum = right+left ;
        Log.v("NonTastk", "NonTasssk"+task);
        view = inflater.inflate(R.layout.fragment_non_task, container, false);

        Button addTask = (Button) view.findViewById(R.id.spiral_add);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
                LayoutInflater inflater = getLayoutInflater();
                View dialog_view = inflater.inflate(R.layout.activity_popup, null);
                builder.setView(dialog_view);
                final Button select_right = (Button) dialog_view.findViewById(R.id.right);// * 오른손, 왼손 검사 선택
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

                select_left.setOnClickListener(new View.OnClickListener() {// * 안내문
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

        return view ;
    }
}
