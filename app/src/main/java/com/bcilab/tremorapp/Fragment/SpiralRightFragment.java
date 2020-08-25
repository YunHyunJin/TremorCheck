package com.bcilab.tremorapp.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bcilab.tremorapp.R;

public class SpiralRightFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view ;
        if (getArguments() != null) {
//            Clinic_ID = getArguments().getString("Clinic_ID");
//            PatientName = getArguments().getString("PatientName");
//            path = getArguments().getString("path");
        }

        view = inflater.inflate(R.layout.fragment_spiral_right, container, false);


        return view ;
    }
}
