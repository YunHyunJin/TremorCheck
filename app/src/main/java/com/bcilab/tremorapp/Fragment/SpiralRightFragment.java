package com.bcilab.tremorapp.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bcilab.tremorapp.R;

public class SpiralRightFragment extends Fragment {

    private String clinicID ;
    private TabLayout tabLayout ;
    private String patientName ;
    private String task ;
    private String hand ;
    private int taskNum ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view ;
        if (getArguments() != null) {
            clinicID = getArguments().getString("clinicID");
            patientName = getArguments().getString("patientName");
            task = getArguments().getString("task");
            hand = getArguments().getString("hand");
        }

        view = inflater.inflate(R.layout.fragment_spiral_right, container, false);

        Log.v("Spiral", "Spirallllll"+clinicID+hand) ;
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.measure) ;
        tabLayout.addTab(tabLayout.newTab().setText("떨림의 주파"));
        tabLayout.addTab(tabLayout.newTab().setText("떨림의 세기"));
        tabLayout.addTab(tabLayout.newTab().setText("벗어난 거리"));
        tabLayout.addTab(tabLayout.newTab().setText("검사 수행 시간"));
        tabLayout.addTab(tabLayout.newTab().setText("검사 평균 속도"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //changeView(tab.getPosition()) ;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //changeView(0) ;
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view ;
    }
}
