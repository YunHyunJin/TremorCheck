package com.bcilab.tremorapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bcilab.tremorapp.Data.ResultData;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class PersonalResultActivity extends AppCompatActivity {
    private String clinicID;
    private String patientName ;
    private String task ;
    private String both ;
    private String timestamp ;
    private int taskNum ;
    private String[] spiralStr;
    private TabLayout tabLayout ;
    private AlertDialog.Builder builder ;
    private AlertDialog dialog ;
    private GraphView graphView ;
    private LineGraphSeries<DataPoint> series ;
    private ArrayList<ResultData> resultData = new ArrayList<>() ;
    private String image_path ;
    private String rawdata ;
    private File path ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_result);

        Intent intent = getIntent() ;
        clinicID = intent.getExtras().getString("clinicID");
        patientName = intent.getExtras().getString("patientName");
        task = intent.getExtras().getString("task");
        both = intent.getExtras().getString("both");
        timestamp = intent.getExtras().getString("taskDate");
        taskNum = Integer.parseInt(intent.getExtras().getString("taskNum"));

        path = Environment.getExternalStoragePublicDirectory(
                "/TremorApp/"+clinicID+"/"+task+both);
        String filename = clinicID+"_"+task+both+".csv";
        rawdata = path.toString()+"/"+clinicID+"_"+task+"_"+both+"_"+taskNum+"_RawData.csv";
        image_path = path.toString()+"/"+clinicID+"_"+task+"_"+both+"_"+taskNum+".jpg";
        readCSV(path, filename);
        ImageView result_image = findViewById(R.id.pre_result_image);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(clinicID+" "+ (both.equals("Right") ? "오른손 " : "왼손 ") +(task.equals("Spiral") ? "나선 그리기 검사" : "선 긋기 검사"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.measure) ;
        tabLayout.addTab(tabLayout.newTab().setText("1초당 떨림의 횟수"));
        tabLayout.addTab(tabLayout.newTab().setText("떨림의 세기"));
        tabLayout.addTab(tabLayout.newTab().setText("벗어난 거리"));
        tabLayout.addTab(tabLayout.newTab().setText("검사 수행 시간"));
        tabLayout.addTab(tabLayout.newTab().setText("검사 평균 속도"));
        graphView = (GraphView) findViewById(R.id.graph);
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
        result_image.post(new Runnable() {
            @Override
            public void run() {
                Glide.with(getApplicationContext()).load(image_path)
                        .into(result_image);
            }
        });
        result_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonalResultActivity.this, ImageViewActivity.class);
                intent.putExtra("path",image_path);
                startActivity(intent);
            }
        });
        ((TextView) findViewById(R.id.pre_result_date)).setText(timestamp);
        if(resultData.get(taskNum-1).getHz() == -1) {
            ((TextView) findViewById(R.id.pre_hz_result)).setText("떨림 횟수가 적음");
        }
        else {
            ((TextView) findViewById(R.id.pre_hz_result)).setText(String.format("%.2f",resultData.get(taskNum-1).getHz())+" Hz") ;
        }
        ((TextView) findViewById(R.id.pre_mag_result)).setText(String.format("%.2f",resultData.get(taskNum-1).getMagnitude())+" cm") ;
        ((TextView) findViewById(R.id.pre_distance_result)).setText(String.format("%.2f",resultData.get(taskNum-1).getDistance())+" cm") ;
        ((TextView) findViewById(R.id.pre_time_result)).setText(String.format("%.2f",resultData.get(taskNum-1).getTime())+" sec") ;
        ((TextView) findViewById(R.id.pre_speed_result)).setText(String.format("%.2f",resultData.get(taskNum-1).getSpeed())+" cm/sec") ;

    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    public int readCSV(File path, String file) {
        int line_length = 0 ;
        BufferedReader br = null;
        File spiralCSV = new File(path, file);
        try{
            br = new BufferedReader(new FileReader(spiralCSV));
            String line = "";
            while((line = br.readLine()) != null){
                line_length++;
                spiralStr=line.split(",");
                if (!spiralStr[0].equals("Count")) {
                    resultData.add(new ResultData(Integer.parseInt(spiralStr[0]),Double.parseDouble(spiralStr[1]), Double.parseDouble(spiralStr[2]),Double.parseDouble(spiralStr[3]),Double.parseDouble(spiralStr[4]),Double.parseDouble(spiralStr[5]), spiralStr[6].substring(2,4)+"."+spiralStr[6].substring(4,6)+"."+spiralStr[6].substring(6,8)));
                }
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
    public void changegGraph(int position){
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
