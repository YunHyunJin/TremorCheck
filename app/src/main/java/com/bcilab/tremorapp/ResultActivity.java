package com.bcilab.tremorapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bcilab.tremorapp.Adapter.RecyclerViewAdapter;
import com.bcilab.tremorapp.Data.PatientItem;

import com.bcilab.tremorapp.Data.ResultData;
import com.bcilab.tremorapp.Fragment.PatientListFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultActivity extends AppCompatActivity {
    private String clinicID;
    private String patientName ;
    private String task ;
    private String both ;
    private String timestamp ;
    private String image_path ;
    private String[] spiralStr;
    private TabLayout tabLayout ;
    private AlertDialog.Builder builder ;
    private AlertDialog dialog ;
    private GraphView graphView ;
    private LineGraphSeries<DataPoint> series ;
    private boolean firstdate ;
    private ArrayList<ResultData> resultData = new ArrayList<>() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        RequestManager mGlideRequestManager;
        RequestManager mGlideRequestManager_pre;
        mGlideRequestManager = Glide.with(ResultActivity.this);
        mGlideRequestManager_pre = Glide.with(ResultActivity.this);
        Intent intent = getIntent() ;
        clinicID = intent.getExtras().getString("clinicID");
        patientName = intent.getExtras().getString("patientName");
        task = intent.getExtras().getString("task");
        both = intent.getExtras().getString("both");
        timestamp = intent.getExtras().getString("timestamp");
        image_path = intent.getExtras().getString("image_path");
        firstdate = intent.getExtras().getBoolean("firstdate");
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(clinicID+" "+ (both.equals("Right") ? "오른손 " : "왼손 ") +(task.equals("Spiral") ? "나선 검사" : "선 검사"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final double[] spiral_result = intent.getDoubleArrayExtra("spiral_result");
        tabLayout = (TabLayout) findViewById(R.id.measure) ;
        //[0]: TM , [1]: TF , [2]:time , [3]: ED , [4]:velocity
        //[0]: 떨림규모 , [1]: 떨림 , [2]:time , [3]: 거리 , [4]:속도
        tabLayout.addTab(tabLayout.newTab().setText("1초당 떨림의 횟수"));
        tabLayout.addTab(tabLayout.newTab().setText("떨림의 세기"));
        tabLayout.addTab(tabLayout.newTab().setText("벗어난 거리"));
        tabLayout.addTab(tabLayout.newTab().setText("검사 수행 시간"));
        tabLayout.addTab(tabLayout.newTab().setText("검사 평균 속도"));
        graphView = (GraphView) findViewById(R.id.graph);
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMinX(0);
//        ((TextView) findViewById(R.id.clinic_ID)).setText(clinicID+" "+patientName) ;
//        ((TextView) findViewById(R.id.testTitle)).setText(both.equals("Right")?"오른손" : "왼손" + (task.equals("Spiral")? " 나선 그리기 결과" : " 선 긋기 결과")) ;
//        ((TextView) findViewById(R.id.today_date)).setText(timestamp.substring(0,4)+"."+timestamp.substring(4,6)+"."+timestamp.substring(6,8)+" "
//                +timestamp.substring(9,11)+":"+timestamp.substring(12, 14)) ;


        File path = Environment.getExternalStoragePublicDirectory(// path
                "/TremorApp/"+clinicID+"/"+task+both);
        String filename = clinicID+"_"+task+both+".csv";// 파일 이름
        File[] foder = path.listFiles() ;
        boolean no_first = false ;
        for (File name : foder) {
            if(name.getName().equals(filename)) {// * 첫번째가 아님
                no_first = true ;
            }
        }

        final ImageView result_image = findViewById(R.id.result_image);
        result_image.post(new Runnable() {
            @Override
            public void run() {
                mGlideRequestManager.load(image_path)
                        .into(result_image);
            }
        });
        result_image.setOnClickListener(new View.OnClickListener() {// 현재 결과 이미지
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResultActivity.this, ImageViewActivity.class);
                intent.putExtra("path",image_path);
                startActivity(intent);
            }
        });
        if(spiral_result[1] == -1) {// 현재 결과값
            ((TextView) findViewById(R.id.pre_hz_result)).setText("떨림 횟수가 적음");
        }
        else {
            ((TextView) findViewById(R.id.pre_hz_result)).setText(String.format("%.2f",spiral_result[1])+" Hz") ;
        }
        ((TextView) findViewById(R.id.pre_mag_result)).setText(String.format("%.2f",spiral_result[0])+" cm") ;
        ((TextView) findViewById(R.id.pre_distance_result)).setText(String.format("%.2f",spiral_result[3])+" cm") ;
        ((TextView) findViewById(R.id.pre_time_result)).setText(String.format("%.2f",spiral_result[2])+" sec") ;
        ((TextView) findViewById(R.id.pre_speed_result)).setText(String.format("%.2f",spiral_result[4])+" cm/sec") ;
        ((TextView) findViewById(R.id.result_date)).setText(timestamp.substring(0,4)+"."+timestamp.substring(4,6)+"."+timestamp.substring(6,8)+" "
                +timestamp.substring(9,11)+":"+timestamp.substring(12, 14)) ;
        if(no_first==false){// 해당 검사에서 처음일 때
            StringBuilder result = new StringBuilder();
            result.append("Count,Hz,Magnitude,Distance,Time,Speed, TimeStamp");// * 결과 값 csv 파일에 저장
            result.append("\n"+1+","+spiral_result[1]+","+spiral_result[0]+","+spiral_result[3]+","+spiral_result[2]+","+spiral_result[4]+","+timestamp);
            File spiralCSV = new File(path, filename) ;
            try{
                FileWriter write = new FileWriter(spiralCSV, false);
                PrintWriter csv = new PrintWriter(write);
                csv.println(result);
                csv.close();
                String date = timestamp.substring(2,4)+"."+timestamp.substring(4,6)+"."+timestamp.substring(6,8);

                try {
                    if(firstdate==true) {// 모든 검사 중에 처음
                        // * 모든 검샂 중에 처음이니까 환자 처음 검사에 해당 날짜 등록(patient.csv 파일에 first date 항목)
                        updateCSV(String.valueOf(Environment.getExternalStoragePublicDirectory("/TremorApp/"+clinicID)),date,1,3);
                    }
                    updateCSV(String.valueOf(Environment.getExternalStoragePublicDirectory("/TremorApp/"+clinicID)),date,1,4);
                } catch (CsvException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        else {
            File spiralCSV = new File(path, filename) ;
            BufferedWriter bufWriter = null;
            int count = readCSV(path, filename) ;// 처음 검사가 아니니까 이전에 데이터 가져오기
            try{
                bufWriter = new BufferedWriter(new FileWriter(spiralCSV, true));
                bufWriter.append(count+","+spiral_result[1]+","+spiral_result[0]+","+spiral_result[3]+","+spiral_result[2]+","+spiral_result[4]+","+timestamp);
                bufWriter.newLine();
                String date = spiralStr[6].substring(2,4)+"."+spiralStr[6].substring(4,6)+"."+spiralStr[6].substring(6,8);// 바로 직전 데이터
                updateCSV(String.valueOf(Environment.getExternalStoragePublicDirectory("/TremorApp/"+clinicID)),date,1,4);
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            } catch (CsvException e) {
                e.printStackTrace();
            } finally{
                try{
                    if(bufWriter != null){
                        bufWriter.close();
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }

            }
            String pre_image_path = path.toString()+"/"+clinicID+"_"+task+"_"+both+"_"+String.valueOf(count-1)+".jpg";
            final ImageView pre_result_image = findViewById(R.id.pre_result_image);// 직전 데이터
            pre_result_image.post(new Runnable() {
                @Override
                public void run() {
                    mGlideRequestManager.load(pre_image_path)
                            .into(pre_result_image);
                }
            });
            pre_result_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ResultActivity.this, ImageViewActivity.class);
                    intent.putExtra("path",pre_image_path);
                    startActivity(intent);
                }
            });
            if(Double.parseDouble(spiralStr[1])==-1) {// 직전 데이터
                ((TextView) findViewById(R.id.hz_result)).setText("떨림 횟수가 적음");
            }
            else {
                ((TextView) findViewById(R.id.hz_result)).setText(String.format("%.2f",Double.parseDouble(spiralStr[1]))+" Hz") ;
            }
            ((TextView) findViewById(R.id.mag_result)).setText(String.format("%.2f",Double.parseDouble(spiralStr[2]))+" cm") ;
            ((TextView) findViewById(R.id.distance_result)).setText(String.format("%.2f",Double.parseDouble(spiralStr[3]))+" cm") ;
            ((TextView) findViewById(R.id.time_result)).setText(String.format("%.2f",Double.parseDouble(spiralStr[4]))+" sec") ;
            ((TextView) findViewById(R.id.speed_result)).setText(String.format("%.2f",Double.parseDouble(spiralStr[5]))+" cm/sec") ;
            ((TextView) findViewById(R.id.pre_result_date)).setText(spiralStr[6].substring(0,4)+"."+spiralStr[6].substring(4,6)+"."+spiralStr[6].substring(6,8)+" "
                    +spiralStr[6].substring(9,11)+":"+spiralStr[6].substring(12, 14)) ;

        }
        resultData = new ArrayList<>() ;
        readCSV(path, filename);// * 현재 결과값을 저장한 csv 파일을 모든 데이터를 가져와서 measure graph에 적용하기
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
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.result_toolbar, menu);

        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add :// * add test
            {
                add_test();
                return true;
            }
            case R.id.list: {// * 환자 리스트로 바로 이동
                Intent intent = new Intent(getApplicationContext(), PatientListActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            case android.R.id.home: {// * 해당 환자 상세 정보로 이동
                onBackPressed();
                finish();
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void add_test(){
        builder = new android.app.AlertDialog.Builder(ResultActivity.this, R.style.AlertDialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        View dialog_view = inflater.inflate(R.layout.activity_popup, null);
        builder.setView(dialog_view);
        final Button select_right = (Button) dialog_view.findViewById(R.id.right);
        final Button select_left = (Button) dialog_view.findViewById(R.id.left);
        builder.setTitle(task.equals("Spiral") ? "나선 그리기 검사" : "선 긋기 검사");
        dialog = builder.create() ;
        dialog.show() ;
        Intent intent ;
        if(task.equals("Spiral"))
            intent = new Intent(ResultActivity.this, SpiralActivity.class) ;
        else intent = new Intent(ResultActivity.this, LineActivity.class) ;
        intent.putExtra("clinicID", clinicID);
        intent.putExtra("patientName", patientName);
        intent.putExtra("task", task) ;
        select_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("both","Right") ;
                Context context = ResultActivity.this;
                LayoutInflater inflater = getLayoutInflater();
                View customToast = inflater.inflate(R.layout.toast_custom, null);
                Toast customtoast = new Toast(context);
                customtoast.setView(customToast);
                customtoast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                customtoast.setDuration(Toast.LENGTH_LONG);
                customtoast.show();
                startActivity(intent) ;
                dialog.dismiss();
                finish();
            }
        });

        select_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("both","Left") ;
                Context context = ResultActivity.this;
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

    public static void updateCSV(String fileToUpdate, String replace, int row, int col) throws IOException, CsvException {// * csv 파일에 지정한 raw와 column 수정

        File inputFile = new File(fileToUpdate, "patient.csv");
        // Read existing file
        CSVReader reader = new CSVReader(new FileReader(inputFile));
        List<String[]> csvBody = reader.readAll();
// get CSV row column  and replace with by using row and column
        csvBody.get(row)[col] = replace;
        reader.close();

// Write to CSV file which is open
        CSVWriter writer = new CSVWriter(new FileWriter(inputFile));
        writer.writeAll(csvBody);
        writer.flush();
        writer.close();
    }
    public int readCSV(File path, String file) {// * 파일 읽기
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

    public void changegGraph(int position){// * change graph
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
