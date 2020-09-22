package com.bcilab.tremorapp;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.bcilab.tremorapp.Adapter.RecyclerViewAdapter;
import com.bcilab.tremorapp.Data.PatientItem;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent() ;

        final double[] spiral_result = intent.getDoubleArrayExtra("spiral_result");
        clinicID = intent.getExtras().getString("clinicID");
        patientName = intent.getExtras().getString("patientName");
        task = intent.getExtras().getString("task");
        both = intent.getExtras().getString("both");
        timestamp = intent.getExtras().getString("timestamp");
        //[0]: TM , [1]: TF , [2]:time , [3]: ED , [4]:velocity
        //[0]: 떨림규모 , [1]: 떨림 , [2]:time , [3]: 거리 , [4]:속도
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
            try{
                bufWriter = new BufferedWriter(new FileWriter(spiralCSV, true));
                bufWriter.append(readCSV(path, filename)+","+spiral_result[1]+","+spiral_result[0]+","+spiral_result[3]+","+spiral_result[2]+","+spiral_result[4]+","+timestamp);
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
        }

        Log.v("ResultActivity", "spiral_result"+"  "+spiral_result[0]+"  "+spiral_result[1]+"  "+spiral_result[2]+"  "+spiral_result[3]+"  "+spiral_result[4]) ;
    }

    public static int readCSV(File path, String file) {
        int line_length = 0 ;
        BufferedReader br = null;
        File spiralCSV = new File(path, file);

        try{
            br = new BufferedReader(new FileReader(spiralCSV));
            String line = "";
            while((line = br.readLine()) != null){
                line_length++;
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
