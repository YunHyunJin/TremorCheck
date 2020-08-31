package com.bcilab.tremorapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bcilab.tremorapp.R;
import com.bcilab.tremorapp.TaskSelectActivity;

public class NonTaskFragment extends Fragment {

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view ;
        if (getArguments() != null) {
//            Clinic_ID = getArguments().getString("Clinic_ID");
//            PatientName = getArguments().getString("PatientName");
//            path = getArguments().getString("path");
        }

        view = inflater.inflate(R.layout.fragment_non_task, container, false);

        Button addTask = (Button) view.findViewById(R.id.spiral_add);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TaskSelectActivity.class);
                startActivity(intent);
            }
        });

        return view ;
    }
}
