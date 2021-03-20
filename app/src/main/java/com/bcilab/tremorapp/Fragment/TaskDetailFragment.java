package com.bcilab.tremorapp.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bcilab.tremorapp.Adapter.ItemClickSupport;
import com.bcilab.tremorapp.Adapter.ItemDecoration;
import com.bcilab.tremorapp.Adapter.RecyclerItemClickListener;
import com.bcilab.tremorapp.Adapter.TaskListViewAdapter;
import com.bcilab.tremorapp.Data.PatientItem;
import com.bcilab.tremorapp.Data.ResultData;
import com.bcilab.tremorapp.Data.TaskItem;
import com.bcilab.tremorapp.PersonalPatientActivity;
import com.bcilab.tremorapp.PersonalResultActivity;
import com.bcilab.tremorapp.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class TaskDetailFragment extends Fragment {

    private String clinicID ;
    private TabLayout tabLayout ;
    private String patientName ;
    private String task ;
    private String both ;
    private int taskNum ;
    private GraphView graphView ;
    private LineGraphSeries<DataPoint> series ;
    private ArrayList<ResultData> resultData = new ArrayList<>() ;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewLayoutManager;
    TaskListViewAdapter taskListViewAdapter;
    ArrayList<TaskItem> tasks = new ArrayList<>();
    ArrayList<TaskItem> selected_tasks = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view ;
        if (getArguments() != null) {
            clinicID = getArguments().getString("clinicID");
            patientName = getArguments().getString("patientName");
            task = getArguments().getString("task");
            both = getArguments().getString("both");
        }

        view = inflater.inflate(R.layout.fragment_spiral_right, container, false);
        File path = Environment.getExternalStoragePublicDirectory(// * path
                "/TremorApp/"+clinicID+"/"+task+both);
        String filename = clinicID+"_"+task+both+".csv";
        readCSV(path, filename);// * data 값 가져오기

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.measure) ;
        tabLayout.addTab(tabLayout.newTab().setText("1초당 떨림의 횟수"));
        tabLayout.addTab(tabLayout.newTab().setText("떨림의 세기"));
        tabLayout.addTab(tabLayout.newTab().setText("벗어난 거리"));
        tabLayout.addTab(tabLayout.newTab().setText("검사 수행 시간"));
        tabLayout.addTab(tabLayout.newTab().setText("검사 평균 속도"));
        graphView = (GraphView) view.findViewById(R.id.graph);
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMinX(0);
        changegGraph(0);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                changegGraph(tab.getPosition()) ;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //changeView(0) ;
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.personal_taskList);
        taskListViewAdapter = new TaskListViewAdapter(getActivity(), tasks, selected_tasks);
        recyclerView.addItemDecoration(new ItemDecoration(view.getContext()));
        recyclerViewLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(taskListViewAdapter);
        for (int i = 0 ; i<resultData.size() ; i++){
            String taskImage = path.toString()+"/"+clinicID+"_"+task+"_"+both+"_"+resultData.get(i).getCount()+".jpg" ;
            tasks.add(new TaskItem(resultData.get(i).getTimestamp(), String.valueOf(i + 1), taskImage, null));
        }
        taskListViewAdapter.notifyDataSetChanged();

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener(){// * 그림 list 선택 시 result activity로 이동

            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent intent ;
                intent = new Intent(getActivity(), PersonalResultActivity.class);
                intent.putExtra("clinicID", clinicID) ;
                intent.putExtra("patientName", patientName) ;
                intent.putExtra("task", task) ;
                intent.putExtra("both", both) ;
                intent.putExtra("taskDate", tasks.get(position).getTaskDate());
                intent.putExtra("taskNum", tasks.get(position).getTaskNum());
                startActivity(intent);
            }
        });
        return view ;
    }
    public int readCSV(File path, String file) {// * csv 파일 읽기
        int line_length = 0 ;
        String[] resultArr;
        BufferedReader br = null;
        File spiralCSV = new File(path, file);
        try{
            br = new BufferedReader(new FileReader(spiralCSV));
            String line = "";
            line=br.readLine();
            while((line = br.readLine()) != null){
                resultArr=line.split(",");
                resultData.add(new ResultData(Integer.parseInt(resultArr[0]),Double.parseDouble(resultArr[1]), Double.parseDouble(resultArr[2]),Double.parseDouble(resultArr[3]),Double.parseDouble(resultArr[4]),Double.parseDouble(resultArr[5]), resultArr[6].substring(2,4)+"."+resultArr[6].substring(4,6)+"."+resultArr[6].substring(6,8)+" "
                        +resultArr[6].substring(9,11)+":"+resultArr[6].substring(12, 14)));
            }
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try{
                if(br != null){
                    br.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return line_length;
    }
    public void changegGraph(int position){// * measure tab 선택 시에 그래프 바뀌기
        graphView.removeAllSeries();
        series = new LineGraphSeries<>();
        series.setColor(Color.parseColor("#285E9F"));
        switch (position){
            case 0 :
            {
                series.appendData(new DataPoint(0,0), true, 100);
                for (int i = 0 ; i<resultData.size() ;i++) {
                    series.appendData(new DataPoint(resultData.get(i).getCount(), resultData.get(i).getHz()), true, 100);

                }
                graphView.addSeries(series);
                break;
            }
            case 1 :
            {
                series.appendData(new DataPoint(0,0), true, 100);
                for (int i = 0 ; i<resultData.size() ;i++) {
                    series.appendData(new DataPoint(resultData.get(i).getCount(), resultData.get(i).getMagnitude()), true, 100);

                }
                graphView.addSeries(series);
                break;
            }
            case 2 :
            {
                series.appendData(new DataPoint(0,0), true, 100);
                for (int i = 0 ; i<resultData.size() ;i++) {
                    series.appendData(new DataPoint(resultData.get(i).getCount(), resultData.get(i).getDistance()), true, 100);
                    graphView.addSeries(series);
                }
                break;
            }
            case 3 :
            {
                series.appendData(new DataPoint(0,0), true, 100);
                for (int i = 0 ; i<resultData.size() ;i++) {
                    series.appendData(new DataPoint(resultData.get(i).getCount(), resultData.get(i).getTime()), true, 100);
                    graphView.addSeries(series);
                }
                break;
            }
            case 4 :
            {
                series.appendData(new DataPoint(0,0), true, 100);
                for (int i = 0 ; i<resultData.size() ;i++) {
                    series.appendData(new DataPoint(resultData.get(i).getCount(), resultData.get(i).getSpeed()), true, 100);
                    graphView.addSeries(series);
                }
                break;
            }
        }
    }
}
