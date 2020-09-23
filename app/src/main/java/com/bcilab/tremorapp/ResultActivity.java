package com.bcilab.tremorapp;

import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcilab.tremorapp.Adapter.RecyclerViewAdapter;
import com.bcilab.tremorapp.Data.PatientItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        RequestManager mGlideRequestManager;
        RequestManager mGlideRequestManager_pre;
        mGlideRequestManager = Glide.with(ResultActivity.this);
        mGlideRequestManager_pre = Glide.with(ResultActivity.this);
        Intent intent = getIntent() ;

        final double[] spiral_result = intent.getDoubleArrayExtra("spiral_result");
        clinicID = intent.getExtras().getString("clinicID");
        patientName = intent.getExtras().getString("patientName");
        task = intent.getExtras().getString("task");
        both = intent.getExtras().getString("both");
        timestamp = intent.getExtras().getString("timestamp");
        image_path = intent.getExtras().getString("image_path");
        tabLayout = (TabLayout) findViewById(R.id.measure) ;
        //[0]: TM , [1]: TF , [2]:time , [3]: ED , [4]:velocity
        //[0]: 떨림규모 , [1]: 떨림 , [2]:time , [3]: 거리 , [4]:속도
        tabLayout.addTab(tabLayout.newTab().setText("Hz"));
        tabLayout.addTab(tabLayout.newTab().setText("Magnitude"));
        tabLayout.addTab(tabLayout.newTab().setText("Distance"));
        tabLayout.addTab(tabLayout.newTab().setText("Time"));
        tabLayout.addTab(tabLayout.newTab().setText("Speed"));
        ((TextView) findViewById(R.id.clinic_ID)).setText(clinicID+" "+patientName) ;
        ((TextView) findViewById(R.id.testTitle)).setText(both.equals("Right")?"오른손" : "왼손" + (task.equals("Spiral")? " 나선 그리기 결과" : " 선 긋기 결과")) ;
        ((TextView) findViewById(R.id.today_date)).setText(timestamp.substring(0,4)+"."+timestamp.substring(4,6)+"."+timestamp.substring(6,8)+" "
                +timestamp.substring(9,11)+":"+timestamp.substring(12, 14)) ;


        File path = Environment.getExternalStoragePublicDirectory(
                "/TremorApp/"+clinicID+"/"+task+both);
        String filename = clinicID+"_"+task+both+".csv";
        File[] foder = path.listFiles() ;
        boolean first = false ;
        for (File name : foder) {
            if(name.getName().equals(filename)) {
                first = true ;
            }
        }

        ((TextView) findViewById(R.id.result_date)).setText(timestamp.substring(0,4)+"."+timestamp.substring(4,6)+"."+timestamp.substring(6,8)+" "
                +timestamp.substring(9,11)+":"+timestamp.substring(12, 14)) ;

        final ImageView result_image = findViewById(R.id.result_image);
        result_image.post(new Runnable() {
            @Override
            public void run() {
                mGlideRequestManager.load(image_path)
                        .into(result_image);
            }
        });
        ((TextView) findViewById(R.id.pre_hz_result)).setText(String.format("%.2f",spiral_result[1])+" Hz") ;
        ((TextView) findViewById(R.id.pre_mag_result)).setText(String.format("%.2f",spiral_result[0])+" cm") ;
        ((TextView) findViewById(R.id.pre_distance_result)).setText(String.format("%.2f",spiral_result[3])+" cm") ;
        ((TextView) findViewById(R.id.pre_time_result)).setText(String.format("%.2f",spiral_result[2])+" sec") ;
        ((TextView) findViewById(R.id.pre_speed_result)).setText(String.format("%.2f",spiral_result[4])+" cm/sec") ;
        if(first==false){
            StringBuilder result = new StringBuilder();
            result.append("Count,Hz,Magnitude,Distance,Time,Speed, TimeStamp");
            result.append("\n"+1+","+spiral_result[1]+","+spiral_result[0]+","+spiral_result[3]+","+spiral_result[2]+","+spiral_result[4]+","+timestamp);
            File spiralCSV = new File(path, filename) ;

            try{
                FileWriter write = new FileWriter(spiralCSV, false);
                PrintWriter csv = new PrintWriter(write);
                csv.println(result);
                csv.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            File spiralCSV = new File(path, filename) ;
            BufferedWriter bufWriter = null;
            int count = readCSV(path, filename) ;
            try{
                bufWriter = new BufferedWriter(new FileWriter(spiralCSV, true));
                bufWriter.append(count+","+spiral_result[1]+","+spiral_result[0]+","+spiral_result[3]+","+spiral_result[2]+","+spiral_result[4]+","+timestamp);
                bufWriter.newLine();
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }finally{
                try{
                    if(bufWriter != null){
                        bufWriter.close();
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }

            }
            String pre_image_path = path.toString()+"/"+clinicID+"_"+task+both+"_"+String.valueOf(count-1)+".jpg";
            final ImageView pre_result_image = findViewById(R.id.pre_result_image);
            pre_result_image.post(new Runnable() {
                @Override
                public void run() {
                    mGlideRequestManager.load(pre_image_path)
                            .into(pre_result_image);
                }
            });
            ((TextView) findViewById(R.id.hz_result)).setText(String.format("%.2f",Double.parseDouble(spiralStr[1]))+" Hz") ;
            ((TextView) findViewById(R.id.mag_result)).setText(String.format("%.2f",Double.parseDouble(spiralStr[2]))+" cm") ;
            ((TextView) findViewById(R.id.distance_result)).setText(String.format("%.2f",Double.parseDouble(spiralStr[3]))+" cm") ;
            ((TextView) findViewById(R.id.time_result)).setText(String.format("%.2f",Double.parseDouble(spiralStr[4]))+" sec") ;
            ((TextView) findViewById(R.id.speed_result)).setText(String.format("%.2f",Double.parseDouble(spiralStr[5]))+" cm/sec") ;
            ((TextView) findViewById(R.id.pre_result_date)).setText(spiralStr[6].substring(0,4)+"."+spiralStr[6].substring(4,6)+"."+spiralStr[6].substring(6,8)+" "
                    +spiralStr[6].substring(9,11)+":"+spiralStr[6].substring(12, 14)) ;

        }
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
