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

import com.bcilab.tremorapp.Adapter.ItemClickSupport;
import com.bcilab.tremorapp.Adapter.ItemDecoration;
import com.bcilab.tremorapp.Adapter.TaskListViewAdapter;
import com.bcilab.tremorapp.Data.PatientItem;
import com.bcilab.tremorapp.Data.ResultData;
import com.bcilab.tremorapp.Data.TaskItem;
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
public class BothFragment extends Fragment {// * 양손 모아보기 tab
    private String clinicID ;
    private TabLayout tabLayout ;
    private String patientName ;
    private String task ;
    private String both ;
    private ArrayList<ResultData> resultData = new ArrayList<>() ;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewLayoutManager;
    TaskListViewAdapter taskListViewAdapter;
    ArrayList<TaskItem> tasks = new ArrayList<>();
    ArrayList<TaskItem> selected_tasks = new ArrayList<>();

    RecyclerView recyclerView2;
    RecyclerView.LayoutManager recyclerViewLayoutManager2;
    TaskListViewAdapter taskListViewAdapter2;
    ArrayList<TaskItem> tasks2 = new ArrayList<>();
    ArrayList<TaskItem> selected_tasks2 = new ArrayList<>();
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
        String right = "Right";
        String left = "Left";
        view = inflater.inflate(R.layout.fragment_both, container, false);
        File path = Environment.getExternalStoragePublicDirectory(
                "/TremorApp/"+clinicID+"/"+task+right);
        String filename = clinicID+"_"+task+right+".csv";
        readCSV(path, filename);

        recyclerView = (RecyclerView) view.findViewById(R.id.spiral_RightRectangle);
        taskListViewAdapter = new TaskListViewAdapter(getActivity(), tasks, selected_tasks);
        recyclerView.addItemDecoration(new ItemDecoration(view.getContext()));
        recyclerViewLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.setAdapter(taskListViewAdapter);
        for (int i = 0 ; i<resultData.size() ; i++){
            String taskImage = path.toString()+"/"+clinicID+"_"+task+"_"+right+"_"+resultData.get(i).getCount()+".jpg" ;
            tasks.add(new TaskItem(resultData.get(i).getTimestamp(), String.valueOf((i+1)), taskImage, right.substring(0,1)));
        }
        taskListViewAdapter.notifyDataSetChanged();
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener(){

            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent intent ;
                intent = new Intent(getActivity(), PersonalResultActivity.class);
                intent.putExtra("clinicID", clinicID) ;
                intent.putExtra("patientName", patientName) ;
                intent.putExtra("task", task) ;
                intent.putExtra("both", right) ;
                int taskNum = (Integer.parseInt(tasks.get(position).getTaskNum()));
                intent.putExtra("taskDate", tasks.get(position).getTaskDate());
                intent.putExtra("taskNum", String.valueOf(taskNum));
                startActivity(intent);
            }
        });
        resultData = new ArrayList<>() ;
        path = Environment.getExternalStoragePublicDirectory(
                "/TremorApp/"+clinicID+"/"+task+left);
        filename = clinicID+"_"+task+left+".csv";
        readCSV(path, filename);

        recyclerView2 = (RecyclerView) view.findViewById(R.id.spiral_LeftRectangle);
        taskListViewAdapter2 = new TaskListViewAdapter(getActivity(), tasks2, selected_tasks2);
        recyclerView2.addItemDecoration(new ItemDecoration(view.getContext()));
        recyclerViewLayoutManager2 = new GridLayoutManager(getActivity(), 1);
        recyclerView2.setLayoutManager(recyclerViewLayoutManager2);
        recyclerView2.setAdapter(taskListViewAdapter2);
        for (int i = 0 ; i<resultData.size() ; i++){
            String taskImage = path.toString()+"/"+clinicID+"_"+task+"_"+left+"_"+resultData.get(i).getCount()+".jpg" ;
            tasks2.add(new TaskItem(resultData.get(i).getTimestamp(), String.valueOf((i+1)), taskImage, left.substring(0,1)));
        }
        taskListViewAdapter2.notifyDataSetChanged();
        ItemClickSupport.addTo(recyclerView2).setOnItemClickListener(new ItemClickSupport.OnItemClickListener(){

            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent intent ;
                intent = new Intent(getActivity(), PersonalResultActivity.class);
                intent.putExtra("clinicID", clinicID) ;
                intent.putExtra("patientName", patientName) ;
                intent.putExtra("task", task) ;
                intent.putExtra("both", left) ;
                int taskNum = Integer.parseInt(tasks.get(position).getTaskNum());
                Log.v("DDDDDDDDDDDDDDDss", "EEEEEEE"+taskNum+Integer.parseInt(tasks.get(position).getTaskNum())/2);
                intent.putExtra("taskDate", tasks.get(position).getTaskDate());
                intent.putExtra("taskNum", String.valueOf(taskNum));
                startActivity(intent);
            }
        });
        return view ;
    }
    public int readCSV(File path, String file) {
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

}
