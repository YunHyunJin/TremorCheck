package com.bcilab.tremorapp.Function;

import android.os.Environment;
import android.util.Log;

import com.bcilab.tremorapp.Data.Complex;
import com.opencsv.CSVWriter;

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
import java.util.List;

import Jampack.Print;
import edu.mines.jtk.dsp.BandPassFilter;
import marytts.util.math.ComplexArray;
import marytts.util.math.Hilbert;
import smile.math.Math;
import smile.math.matrix.DenseMatrix;
import smile.projection.PCA;

import static com.bcilab.tremorapp.Function.main.ctx;

public class LineTaskAnalyze {
    private static fft ft;
    // sampling rate : 60
    private static final int srate = 60;
    public final static double DPI = 88.18;

    //                                                              3/60(3Hz) ,15/60(15Hz), 다음 값들은 경험적.... 실험으로
    private static BandPassFilter BPF_line = new BandPassFilter(0.05, 0.25, 0.001, 0.0005608);
    private static BandPassFilter LPF_line = new BandPassFilter(0.0012, 0.05, 0.00001, 0.5605);
    private static BandPassFilter BPF_spiral = new BandPassFilter(0.05, 0.25, 0.1, 0.0005608);
    private static BandPassFilter LPF_spiral = new BandPassFilter(0.0012, 0.05, 0.00001, 0.5605);
    private static int counts;
    private static int countup;
    private static int set;

    private String Clinic_ID;
    static File file;
    private static String sspiral_count;


    //어레이리스트에서 어레이로 바꿔주기 (밴드패스 필터링 라이브러리 때문에)
    public double[] ArratlistToArray(ArrayList<Double> arr) {
        double[] return_value = new double[arr.size()];

        for (int i = 0; i < arr.size(); i++) {
            return_value[i] = arr.get(i);
        }

        return return_value;
    }

    //================================================================
    public float[] DoubleToFloat(double[] arr) {
        float[] return_value = new float[arr.length];

        for (int i = 0; i < arr.length; i++) {
            return_value[i] = (float) arr[i];
        }

        return return_value;
    }

    //================================================================
    public double[] FloatToDouble(float[] arr) {
        double[] return_value = new double[arr.length];

        for (int i = 0; i < arr.length; i++) {
            return_value[i] = (double) arr[i];
        }

        return return_value;
    }

//================================================================

    //어레이를 어레이리스트롤 바꿔주기
    public ArrayList<Double> ArrayToArraylist(double[] arr) {
        ArrayList<Double> return_val = new ArrayList<Double>();

        for (double temp : arr) {
            return_val.add(temp);
        }
        return return_val;
    }

    // *****************************BPF  *****************************
    public double[] myBPF(double[] data, boolean SpiralOrLine) {
        for (double a : data) Log.v("LineTask", "이거 맞냐" + a);
        float[] data_float = new float[data.length];
        float[] temp_float = new float[data.length];
        double[] temp_double = new double[data.length];
        double[] BPF_result = new double[data.length];

        data_float = DoubleToFloat(data);

        //SpiralOrLine :  true일시 스파이럴 테스트 , false 일시 라인 테스트
        if (SpiralOrLine == true) {
            BPF_spiral.setExtrapolation(BandPassFilter.Extrapolation.ZERO_SLOPE);

            //결과가 float라서 float형으로 써줌
            BPF_spiral.apply(data_float, temp_float);

            //firebase는 arraylist를 사용하는데 arraylist에서는 double형 밖에 안들어가는 걸로 기억.
            temp_double = FloatToDouble(temp_float);

            for (int i = 0; i < temp_double.length; i++) {
                BPF_result[i] = temp_double[i];
            }
        } else {
            BPF_line.setExtrapolation(BandPassFilter.Extrapolation.ZERO_SLOPE);
            BPF_line.apply(data_float, temp_float);

            temp_double = FloatToDouble(temp_float);

            for (int i = 0; i < temp_double.length; i++) {
                BPF_result[i] = temp_double[i];
            }
        }
        //System.out.println("BPF 결과~~~"+BPF_result);
        Log.d("결과~과정1", String.valueOf(BPF_result));

        return BPF_result;
    }

    // ***************************** LPF *****************************
    public double[] myLPF(double[] data, boolean SpiralOrLine) {
        float[] data_float = new float[data.length];
        float[] temp_float = new float[data.length];

        double[] temp_double = new double[data.length];
        double[] LPF_result = new double[data.length];

        data_float = DoubleToFloat(data);

        //SpiralOrLine true일시 스파이럴 테스트
        if (SpiralOrLine == true) {
            LPF_spiral.setExtrapolation(BandPassFilter.Extrapolation.ZERO_SLOPE);
            LPF_spiral.apply(data_float, temp_float);

            temp_double = FloatToDouble(temp_float);

            for (int i = 0; i < temp_double.length; i++) {
                LPF_result[i] = temp_double[i];
            }
        } else {
            LPF_line.setExtrapolation(BandPassFilter.Extrapolation.ZERO_SLOPE);
            LPF_line.apply(data_float, temp_float);

            temp_double = FloatToDouble(temp_float);

            for (int i = 0; i < temp_double.length; i++) {
                LPF_result[i] = temp_double[i];
            }
        }
        Log.d("test1", "잘돌고있음 LPF");
        // System.out.println("LPF 결과~~~"+LPF_result);
        return LPF_result;
    }


    // ***************************** PCA *****************************

    public double[] myPCA(double[] x, double[] y) {
        DenseMatrix pca_prime;
        double[][] temp = new double[x.length][2];
        double[][] temp2 = new double[2][x.length];
        double[][] cong = new double[1][2];
        double[] re_pca = new double[x.length];
        double x_sum = 0.0;

//        Csig = Filterdata' * Filterdata; % Covariance
//                [V,D] = eig(Csig); % eigenvector
//                W = V(:,end); % weight
//        new = W'*Filterdata';  % projection'
        //위 매틀랩 코드를 따라 만든 코드

        for (int i = 0; i < x.length; i++) {
            temp[i][0] = x[i];
            temp[i][1] = y[i];

            temp2[0][i] = x[i];
            temp2[1][i] = y[i];

        }

        PCA pca = new PCA(temp);
        pca_prime = pca.getProjection();

        for (int i = 0; i < x.length; i++) {
            x_sum += x[i];
        }


        if (x_sum > 0) {
            cong[0][0] = -pca_prime.get(0, 0);
            cong[0][1] = -pca_prime.get(1, 0);

        } else {
            cong[0][0] = pca_prime.get(0, 0);
            cong[0][1] = pca_prime.get(1, 0);

        }

        Log.d("test1_pca", cong[0][0] + " " + cong[0][1]);

        for (int i = 0; i < x.length; i++) {
            re_pca[i] = cong[0][0] * temp2[0][i] + cong[0][1] * temp2[1][i];
        }

        Log.d("test1", "잘돌고있음 PCA");
        Log.d("결과~과정2", String.valueOf(re_pca));

        return re_pca;
    }

    // ***************************** Hilbert *****************************

    public double[] MyHilbert(double[] data) {
        ComplexArray complexArray = Hilbert.transform(data);
        //hilbert transform library 사용해서 real part . imagine part 나눠서 사용
        double[] hilbert_real = complexArray.real;
        double[] hilbert_imag = complexArray.imag;
        double[] temp = new double[data.length];
//        double envel = 0;
        double[] envel = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            temp[i] = Math.sqrt(Math.pow(hilbert_real[i], 2) + Math.pow(hilbert_imag[i], 2));
            Log.v("test12", String.valueOf(temp[i]));
        }


        for (int i = 0; i < data.length; i++) {
            envel[i] = Math.abs(temp[i]);
//            envel += Math.abs(temp[i]);
        }

//        envel = envel/data.length;
        Log.d("test1", "잘돌고있음 Hilbert");
        return envel;
    }

    // ***************************** DrawingLength *****************************

    public double myDrawingLength(double[] x, double[] y) {
        double result = 0.0;
//        // 실제로 그린 길이 만큼 나옴
//        for (int i = 10; i < x.length-6; i++) {
////            double d = x[i] - x[i-1];
////            result += Math.sqrt(d*d);
//            double d_x = x[i] - x[i - 1];
//            result += Math.sqrt((d_x * d_x));
//
//        }

        for (int i = 10; i < x.length - 6; i++) {
            double d_x = x[i] - x[i - 1];
            double d_y = y[i] - y[i - 1];
            // 변경사항

            result += Math.sqrt((d_x * d_x) + (d_y * d_y));
        }
         System.out.println("길이결과~~~"+result/DPI);

        return result / DPI;////
    }

    // ***************************** EUD *****************************

    public double myEUD(double[] x, double[] y) {
        double result = 0.0;

        for (int i = 0; i < y.length; i++) {
            Log.d("결과~baseX_i: ", String.valueOf(i));
            Log.d("결과~DrawX: ", String.valueOf(x[i]));
        }
        //유클리드 거리 측정.
        int count = 0;
        for (int i = 10; i < x.length - 6; i++) {
            double d = x[i] - y[i];
            Log.d("결과~gap: ", String.valueOf(d));

//            if(Math.abs(d) < 65){
//                count++;
//                continue;
//            }
            d = d / DPI;

            result += Math.sqrt(d * d);
        }
        Log.d("결과~count: ", String.valueOf(count));

        return result / (x.length - 15);
    }

    // ***************************** Spiral ED *****************************
//    public double MyED(double[] x, double[] y) {
//        System.out.println("x_length: "+ x.length);
//        System.out.println("y_length: "+ y.length);
//        count=2;
//        countup=0;
//        set =0;
//
//        double org_X = 571;
//        double org_Y = 924;
////        double last_point_length = 520;
////
////        double[] angle_atan = new double[x.length];
////        double[] draw_length = new double[x.length];
////
////        double[] baseline_x = new double[x.length];
////        double[] baseline_y = new double[x.length];
////        int count1 = 0;
////        int count2 = 0;
////
////        for(int i=0 ; i<x.length ; i++) {
////            count1++;
////            angle_atan[i] = Math.atan2(y[i], x[i]); // 그린 검사 선의 각도
////            draw_length[i] = Math.sqrt(Math.pow(x[i], 2) + Math.pow(y[i], 2)); // 그린 선의 길
////        }
////
////        double split_baselength = 520/x.length;
////        double plus = 0;
////        for(int i=0 ; i<520 ; i++){
////            count2++;
////            baseline_x[i] = plus* Math.cos(angle_atan[i]);
////            baseline_y[i] = plus* Math.sin(angle_atan[i]);
////            plus+= split_baselength;
////
////        }
////        Log.v("베이스 길이", "1: "+String.valueOf(count1)+"2: "+String.valueOf(count2));
//
////
//        double result = 0.0;
//        int idx = 0;
//        double[] angle_atan = new double[x.length];
//        double[] angle_pos = new double[x.length];
//
//        //get the angle of spiral
//        for (int i = 0; i < x.length; i++) {
//            angle_atan[i] = Math.atan2( (y[i]-), (org_X-x[i]) ); // ==> 라디안 값
//            Log.d("결과~angle_atan", String.valueOf((angle_atan[i]* (180/Math.PI) )));
//        }
//
//        for (int i = 0; i < x.length; i++) {
//            if (i == 0)
//                angle_pos[i] = unwrap(0, angle_atan[i]);
//            else
//                angle_pos[i] = unwrap(angle_atan[i - 1], angle_atan[i]);
//            Log.d("결과~angle_pos", "i: "+i+"and "+String.valueOf(angle_pos[i]));
//
//        }
//
//        //calculate base point
//        double v = 600;
//        double w = 10 * Math.PI;
//        double t = angle_pos[0] / w;
//        double r = v * t;
//        double[] pair_pos_x = new double[x.length];
//        double[] pair_pos_y = new double[x.length];
//
//
//        for (int i = 0; i < x.length; i++) {
//            if (i == 0) {
//                pair_pos_x[i] = r * Math.cos(angle_pos[i]); // base_밑변
//                pair_pos_y[i] = r * Math.sin(angle_pos[i]); // base_높이
//            } else {
//                t = angle_pos[i] / w;
//                r = v * t;
//                pair_pos_x[i] = r * Math.cos(angle_pos[i]);
//                pair_pos_y[i] = r * Math.sin(angle_pos[i]);
//            }
//
//
////            Log.v("LineTaskAnalyze", "linetaskanalyzzz position x " + x[i] + " y " + y[i]);
////            Log.v("LineTaskAnalyze", "linetaskanalyzzz angleee angle_atan : " + angle_atan[i] + " angle_pos : " + angle_pos[i]);
////            Log.v("LineTaskAnalyze", "linetaskanalyzzz pairpos x " + pair_pos_x[i] + " pairpos y " + pair_pos_y[i]);
//        }
//        //calculate error distance
//
//        for (int i = 0; i < x.length; i++) {
//            double[] length = new double[x.length];
//
//            length[i] = Math.sqrt(Math.pow((x[i]-pair_pos_x[i]), 2) + Math.pow((y[i]-pair_pos_y[i]), 2));
//            Log.v("길이", "x,y " + length[i]);
//
//        }
//
//        double sum = 0;
//        for (int i = 0; i < x.length; i++) {
//            //            sum += Math.sqrt(Math.pow((x[i] - pair_pos_x[i]), 2) + Math.pow((y[i] - pair_pos_y[i]), 2));
//            sum += Math.sqrt(Math.pow((x[i] - pair_pos_x[i]), 2) + Math.pow((y[i] - pair_pos_x[i]), 2));
//
//        }
//
//        return sum / x.length;
//    }
    public double MyED(double[] x, double[] y, double[] time, String Clinic_ID,
                       String task, String both, String count, String startXX, String startYY) {
        // 태블릿이 변경되었을때
        // 1. DPI 변경 - 태블릿 화면에 1cm당 좌표값
        // 2. 6PI:cm 길이 = radian : x 에서 단위 파이 대비 cm 길이를 구해서 바꿔준다.
        double[] thetax = new double[x.length];
        double[] thetay = new double[x.length];
        double[] theta = new double[x.length];
        double[] base_angle = new double[x.length];
        double[] ed = new double[x.length];
        double length = 0.0;
        double sum = 0.0;
        double additional_angle = 0.0;
        double angle_gap = 0.0;

        double[] base_x = new double[x.length];
        double[] base_y= new double[x.length];

        double start_X = Double.parseDouble(startXX);
        double start_Y = Double.parseDouble(startYY);

        //get the angle of spiral
        for (int i = 0; i < x.length; i++) {

            thetax[i] = x[i] - start_X; //확인용
            thetay[i] = y[i] - start_Y;
            Log.v("화긴해보자", "X: "+start_X+ "Y: "+start_Y);

            theta[i] = Math.atan2(thetay[i], thetax[i]);

            if(i==0){
                base_angle[i] = theta[i]+(Math.PI*2);
            }else{
                angle_gap = theta[i]-theta[i-1];
                if((angle_gap < -1.5*Math.PI)){ // (+) --> (-) 로 넘어갔을 경우
                    counts++;
                    additional_angle =  counts*(2*Math.PI);
                }else if((angle_gap > 1.5*Math.PI)){ // (-) --> (+) 로 넘어갔을 경우
                    counts--;
                    additional_angle =  counts*(2*Math.PI);
                }
                base_angle[i] = theta[i]+ additional_angle+(Math.PI*2);
            }
            length = ( Math.sqrt( Math.pow(thetax[i],2) + Math.pow(thetay[i],2) ) );

            Log.v("LineTaskAnalyze", "(x) " + thetax[i] + " (y) " + thetay[i]
                    + " (theta) "+ theta[i] + " (count) " + count + " (base_angle) " + base_angle[i]);

            Log.v("LineTaskAnalyze", "(r)" + length/DPI+"cm");

            ed[i] = ( Math.abs((length/DPI) - (base_angle[i]* (5/(Math.PI*6))) ) );

            Log.v("ed: ", String.valueOf(ed[i]));

            sum += ed[i];

            base_x[i] = (base_angle[i]*(5/(Math.PI*6))*DPI) * Math.cos(base_angle[i])+start_X ;
            base_y[i] = (base_angle[i]*(5/(Math.PI*6))*DPI) * Math.sin(base_angle[i])+start_Y;
            Log.v("베이스나와라: ", "x: "+base_x[i]+" y: "+base_y[i]);
            Log.v("원래거나와라: ", "x: "+x[i]+" y: "+y[i]);

        }
        File filePath = Environment.getExternalStoragePublicDirectory("/TremorApp/"+Clinic_ID+"/"+task+both);
        try {
            csvmaker(base_x,base_y,x,y, time, filePath, Clinic_ID, task, both, count);
        } catch (IOException e) {
            Log.v("하야야ㅇㅇ: ", "처음오류");
            e.printStackTrace();
        }

        Log.v("sum: ", String.valueOf(sum));
        Log.v("sumq: ", String.valueOf(sum/x.length));

        return sum/x.length;
    }


    public static void csvmaker(double[] base_x, double[] base_y,double[] x, double[] y,double[] time, File filePath,
                                String Clinic_ID, String task, String both, String count) throws IOException {
//        StringBuilder baseData = new StringBuilder();
//        baseData.append("baseX,baseY");
//
//        for(int i=0 ; i<base_x.length ; i++){
//            baseData.append("\n"+base_x+","+base_y);
//        }
//        Log.v("하 이것봐라","하야야");
//
//        try{
//            File file = new File("/TremorApp/Real_base.csv");
//            FileWriter fw = new FileWriter(file, true);
//
//            for(int i=0 ; i<base_x.length ; i++){
//                fw.write("\n"+base_x+","+base_y);
//            }
//            Log.v("하 이것봐라","저장완료");
//
//            fw.flush();
//            fw.close();
//        }catch (IOExcepion e){
//            e.printStackTrace();
//        }
        Log.v("하야야ㅇㅇcount: ", "j: "+count);
        StringBuilder baseData = new StringBuilder();
        baseData.append("baseX,baseY,positionX,positionY,time");

        for(int i = 0 ; i<base_x.length ; i++){
            baseData.append("\n"+base_x[i]+","+base_y[i]+","+x[i]+","+y[i]+","+time[i]);
        }

        File baseCsv = new File(filePath, Clinic_ID+"_"+task+"_"+both+"_"+count+"_Final"+".csv");

        try {
            FileWriter write = new FileWriter(baseCsv,false);
            Log.v("하야야ㅇㅇ: ", "왔다");

            PrintWriter csv = new PrintWriter(write);
            csv.println(baseData);
            csv.close();
        }catch (IOException e){
            Log.v("하야야ㅇㅇ: ", "오류");
            e.printStackTrace();
        }
    }

    public static double unwrap(double reference, double wrapped) {
        double po = reference;
        double dp = wrapped - reference;


//        if (dp > Math.PI ) { // 각도가 -에서 +로 넘어갈때
//            while (dp > Math.PI) {
//                po -= 2 * Math.PI;
//                dp -= 2 * Math.PI;
//            }
//        }
//        if (dp < -Math.PI) { //각도가 +에서 -로 넘어갈떄
//            while (dp < -Math.PI) {
//                po += 2 * Math.PI;
//                dp += 2 * Math.PI;
//            }
//        }
        // 뒤집어 지는 것을 찾으면 2pi를 더해줌으로서
//
//        if (dp < -Math.PI ) {
//            countup++;      // 각도가 +에서 -로 넘어갈때
//            set = 1;         // 아랫부분 on
//
//            if(countup >=3) count+=2;
//
//        }else if(dp > Math.PI){//각도가 -에서 +로 넘어갈때
//            set = 2;            // 윗부분 on
//            countup++;
//        }
//
//
//        if (set == 1) {
//            po += count * Math.PI;
//        }
//        if (set ==2) {
//            po += count * Math.PI;
//        }

        Log.d("결과~upwrap", String.valueOf(wrapped + po));
        return wrapped + po;
    }


    //x축을 구하긴 하지만 사용하는거는 y축밖에 없음
    public double myFFT(double[] pca_data) {


        int n = pca_data.length;
        Complex[] temp_com = new Complex[n];

        List<Integer> slice = new ArrayList<Integer>();

        Dataslice ds = new Dataslice();
        slice = ds.Dataslice(n);

        Log.d("sdsd1", "n is " + n);
        int m = slice.size();


        for (int i = 0; i < n; i++) {
            temp_com[i] = new Complex(pca_data[i], 0);
        }


        int padlen = ds.calN(n); //padlen 사이즈 체크

        Complex[] pca_i = new Complex[padlen];
        pca_i = ds.zeropadding(temp_com, padlen);

        Complex[] fft_pca = ft.fft(pca_i);
        double result_fft = 0;

        // change the data type: Complex ---> double
        double[] absfft_pca = new double[padlen / 2];

        Complex inter;
        Complex nm = new Complex(padlen, 0);

        inter = fft_pca[0].divides(nm);
        absfft_pca[0] = inter.abs();

        for (int i = 1; i < padlen / 2; i++) {
            inter = fft_pca[i].divides(nm);
            absfft_pca[i] = 2 * inter.abs();
            Log.d("test1_fft", "fft  " + i + " " + absfft_pca[i]);
        }
        float[] index = new float[padlen / 2];

        for (int j = 0; j < padlen / 2; j++) {
            index[j] = (float) srate * (float) j / (float) padlen;
            Log.d("test1_fft", "fft index  " + j + " " + index[j]);
        }

        result_fft = FFT_PeakFind(absfft_pca, index);
        Log.d("결과~과정3", String.valueOf(result_fft));

        return result_fft;
    }

    public double FFT_PeakFind(double[] result, float[] index) {
        double mean = 0;
        double std = 0;
        double hz = 0;
        double max_amp = 0;
        double[] session = new double[result.length];
        double[] temp1 = new double[result.length]; //temp1 : 10~25Hz 사이의 값을 추출해서 저장하는 변수
        double[] temp2 = new double[result.length]; //temp2 : temp1에서 mean + 70*std 값보다 큰 값을 저장하는 변수

        // 10~25Hz 사이의 정보만 받기위해
        int a = 0;
        for (int i = 0; i < result.length; i++) {
            if (index[i] >= 10 && index[i] <= 25) {
                temp1[a++] = result[i];

            }
        }
        Log.d("test2", "필터링된 주파수 갯수1:  " + a);

        // 10~25Hz 사이의 정보의 표준편차와 평균을 구함
        mean = Math.mean(temp1);
        std = Math.var(temp1);


        // 10~25Hz의 표준편차와 평균을 구해서  X = mean + std * A 의 식을 만듦
        // X의 값이 주파수 대역의 데이터가 하나의 peak점만 찾게하기 위한 A의 값을 정하면됨
        // 실험 해봤을땐 40이 적당했음
        int b = 0;
        for (int i = 0; i < result.length; i++) {
            if (result[i] >= (mean + 40 * std)) { //몇 배 할껀지 정하기
                temp2[b++] = result[i];

            }
        }

        Log.d("test2", "필터링된 주파수 갯수2:  " + b);

        max_amp = Math.max(temp2);

        Log.d("test2", "주파수 맥시멈:  " + max_amp);

        for (int i = 0; i < result.length; i++) {
            if (max_amp == result[i]) {
                hz = index[i];
                break;
            }
        }
        //간혹 가다가 peak점은 찾았는데 떨림이 없는 데이터면 안되니까 3.2Hz이하면 안쳐줌
        if (hz < 3.2) {
            hz = -1;
        }

        System.out.println("주파수는"+hz);
        return hz;
    }

    public double rmsValue(double[] arr, int n) {
        double square = 0;
        double mean = 0;
        double root = 0;

        // Calculate square.
        for (int i = 0; i < n; i++) {
            square += Math.pow(arr[i], 2);
        }

        // Calculate Mean.
        mean = (square / (float) (n));

        // Calculate Root.
        root = (double) Math.sqrt(mean);
        return root;
    }

}
//    public double MyED(double[] x, double[] y ){
//        double[] angle_atan = new double[x.length];
//        double[] angle_pos = new double[x.length];
//
//        double[] thetax = new double[x.length];     //임의
//        double[] thetay = new double[x.length];     //
//        double[] theta = new double[x.length];      //
//        double[] thetareal = new double[x.length];
//        double[] ed = new double[x.length];
//        double d_theta = 0.0;
//        double xy = 0.0;
//        double sum = 0;
//        //double basex = 0.0;
//        //double basey = 0.0;
//        int count = 0;
//
//
//        //get the angle of spiral
//        for (int i = 0; i < x.length; i++) {
//            angle_atan[i] = Math.atan2(y[i], x[i]);
//            //Log.d("결과~angle", String.valueOf(angle_atan[i]));
//            thetax[i] = x[i] - 569; //확인용
//            thetay[i] = y[i] - 920;
//            if(i==0){
////                theta[i] = findtheta(0, 0, theta[i], thetay[i]);
//                theta[i] = Math.atan2(thetay[i], thetax[i]);
//                d_theta = theta[i];
//            }else{
//                theta[i] = Math.atan2(thetay[i], thetax[i]);
//                if((theta[i]-theta[i-1] < -1.5*Math.PI))
//                    count++;    //너무 떨어서 왔다갔다하는 경우에는..?
//
//                thetareal[i] = theta[i]+count*(2*Math.PI);
//                d_theta = theta[i]-theta[i-1];
//                // theta[i] = findtheta(thetax[i-1], thetay[i], thetax[i], thetay[i]);
//            }
//
//            //Math.atan2(thetay[i], thetax[i]);  //아니 왜 모든 사분면에서 (+)가 나오냐고
//            xy = (Math.sqrt(Math.pow(thetax[i], 2) + Math.pow(thetay[i], 2)))/75*Math.PI;
//            //basex = atan
//            //ed = Math.sqrt(Math.pow(thetax[i]-))
//            Log.v("LineTaskAnalyze", "(x) " + thetax[i] + " (y) " + thetay[i] + " (theta) "+ theta[i] + " (count) " + count + " (realtheta) " + thetareal[i]);
//            Log.v("LineTaskAnalyze", "(r)" + xy);
//            ed[i] = (Math.abs(xy - thetareal[i]))/6.3*1.2;
//            sum += ed[i];
//        }
//        return sum/x.length;
//    }

