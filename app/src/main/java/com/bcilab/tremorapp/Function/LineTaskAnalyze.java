package com.bcilab.tremorapp.Function;

import android.util.Log;

import com.bcilab.tremorapp.Data.Complex;

import java.util.ArrayList;
import java.util.List;

import edu.mines.jtk.dsp.BandPassFilter;
import marytts.util.math.ComplexArray;
import marytts.util.math.Hilbert;
import smile.math.Math;
import smile.math.matrix.DenseMatrix;
import smile.projection.PCA;

public class LineTaskAnalyze {
    private static fft ft;
    // sampling rate : 60
    private static final int srate = 60;
    //                                                              3/60(3Hz) ,15/60(15Hz), 다음 값들은 경험적.... 실험으로
    private static BandPassFilter BPF_line =new BandPassFilter(0.05,0.25,0.001,0.0005608);
    private static BandPassFilter LPF_line =new BandPassFilter(0.0012,0.05,0.00001,0.5605);
    private static BandPassFilter BPF_spiral =new BandPassFilter(0.05,0.25,0.1,0.0005608);
    private static BandPassFilter LPF_spiral =new BandPassFilter(0.0012,0.05,0.00001,0.5605);





    //어레이리스트에서 어레이로 바꿔주기 (밴드패스 필터링 라이브러리 때문에)
    public double[] ArratlistToArray(ArrayList<Double> arr) {
        double[] return_value=new double[arr.size()];

        for (int i =0;i<arr.size();i++){
            return_value[i]= arr.get(i);
        }

        return return_value;
    }

    public float[] DoubleToFloat(double[] arr) {
        float[] return_value=new float[arr.length];

        for (int i =0;i<arr.length;i++){
            return_value[i]= (float) arr[i];
        }

        return return_value;
    }

    public double[] FloatToDouble(float[] arr) {
        double[] return_value=new double[arr.length];

        for (int i =0;i<arr.length;i++){
            return_value[i]= (double) arr[i];
        }

        return return_value;
    }



    //어레이를 어레이리스트롤 바꿔주기
    public ArrayList<Double> ArrayToArraylist(double[] arr){
        ArrayList<Double> return_val= new ArrayList<Double>();

        for (double temp:arr){
            return_val.add(temp);
        }
        return return_val;
    }

    // *****************************BPF  *****************************
    public double[] myBPF(double[] data,boolean SpiralOrLine){
        for(double a : data) Log.v("LineTask", "이거 맞냐"+a);
        float [] data_float=new float[data.length];
        float[] temp_float = new float[data.length];
        double[] temp_double = new double[data.length];
        double[] BPF_result = new double[data.length];

        data_float=DoubleToFloat(data);

        //SpiralOrLine :  true일시 스파이럴 테스트 , false 일시 라인 테스트
        if(SpiralOrLine==true){
            BPF_spiral.setExtrapolation(BandPassFilter.Extrapolation.ZERO_SLOPE);
            //결과가 float라서 float형으로 써줌
            BPF_spiral.apply(data_float, temp_float);
            //firebase는 arraylist를 사용하는데 arraylist에서는 double형 밖에 안들어가는 걸로 기억.
            temp_double = FloatToDouble(temp_float);

            for(int i=0;i<temp_double.length;i++){
                BPF_result[i]= temp_double[i];
            }
        }else{
            BPF_line.setExtrapolation(BandPassFilter.Extrapolation.ZERO_SLOPE);
            BPF_line.apply(data_float,temp_float);

            temp_double=FloatToDouble(temp_float);

            for(int i=0;i<temp_double.length;i++){
                BPF_result[i]= temp_double[i];
            }
        }
        return BPF_result;
    }

    // ***************************** LPF *****************************
    public double[] myLPF(double[] data,boolean SpiralOrLine){
        float [] data_float=new float[data.length];
        float[] temp_float = new float[data.length];
        double[] temp_double = new double[data.length];
        double[] LPF_result = new double[data.length];

        data_float=DoubleToFloat(data);

        //SpiralOrLine true일시 스파이럴 테스트
        if(SpiralOrLine==true){
            LPF_spiral.setExtrapolation(BandPassFilter.Extrapolation.ZERO_SLOPE);
            LPF_spiral.apply(data_float, temp_float);

            temp_double = FloatToDouble(temp_float);

            for(int i=0;i<temp_double.length;i++){
                LPF_result[i]= temp_double[i];
            }
        }else{
            LPF_line.setExtrapolation(BandPassFilter.Extrapolation.ZERO_SLOPE);
            LPF_line.apply(data_float,temp_float);

            temp_double=FloatToDouble(temp_float);

            for(int i=0;i<temp_double.length;i++){
                LPF_result[i]= temp_double[i];
            }
        }
        Log.d("test1","잘돌고있음 LPF" );
        return LPF_result;
    }


    // ***************************** PCA *****************************

    public double[] myPCA(double[] x , double[] y ){
        DenseMatrix pca_prime;
        double[][] temp = new double[x.length][2];
        double[][] temp2 = new double[2][x.length];
        double[][] cong = new double[1][2];
        double[] re_pca = new double[x.length];
        double x_sum=0.0 ;

//        Csig = Filterdata' * Filterdata; % Covariance
//                [V,D] = eig(Csig); % eigenvector
//                W = V(:,end); % weight
//        new = W'*Filterdata';  % projection'
        //위 매틀랩 코드를 따라 만든 코드

        for(int i=0; i<x.length;i++){
            temp[i][0] = x[i];
            temp[i][1] = y[i];

            temp2[0][i] = x[i];
            temp2[1][i] = y[i];

        }

        PCA pca=new PCA(temp);
        pca_prime = pca.getProjection();

        for(int i = 0;i<x.length;i++){
            x_sum += x[i];
        }


        if(x_sum>0){
            cong[0][0]= - pca_prime.get(0,0);
            cong[0][1]= - pca_prime.get(1,0);

        }else {
            cong[0][0]= pca_prime.get(0,0);
            cong[0][1]= pca_prime.get(1,0);

        }


        Log.d("test1_pca",cong[0][0] + " "+ cong[0][1]);

        for(int i = 0;i<x.length;i++){
            re_pca[i]=cong[0][0] * temp2[0][i]+ cong[0][1]*temp2[1][i];
        }

        Log.d("test1","잘돌고있음 PCA" );
        return re_pca;
    }

    // ***************************** Hilbert *****************************

    public double[] MyHilbert(double[] data){
        ComplexArray complexArray = Hilbert.transform(data);
        //hilbert transform library 사용해서 real part . imagine part 나눠서 사용
        double[] hilbert_real = complexArray.real;
        double[] hilbert_imag = complexArray.imag;
        double[] temp = new double[data.length];
//        double envel = 0;
        double[] envel = new double[data.length];
        for(int i = 0; i<data.length; i++)
        {
            temp[i] = Math.sqrt(Math.pow(hilbert_real[i], 2) + Math.pow(hilbert_imag[i], 2));
        }


        for(int i = 0; i<data.length; i++)
        {
            envel[i] = Math.abs(temp[i]);
//            envel += Math.abs(temp[i]);
        }

//        envel = envel/data.length;
        Log.d("test1","잘돌고있음 Hilbert" );
        return envel;
    }

    // ***************************** DrawingLength *****************************

    public double myDrawingLength(double[] x){
        double result=0.0;
        // 실제로 그린 길이 만큼 나옴
        for (int i = 10; i < x.length-6; i++) {
            double d = x[i] - x[i-1];
            result += Math.sqrt(d*d);

        }
        return result;
    }

    // ***************************** EUD *****************************

    public double myEUD(double[] x , double[] y){
        double result=0.0;

        //유클리드 거리 측정.
        for (int i = 10; i < x.length-6; i++) {
            double d = x[i] - y[i];
            result += Math.sqrt(d * d);
        }

        return result/(x.length-15);
    }
    // ***************************** Spiral ED *****************************
    public double MyED(double[] x, double[] y ){

        double result = 0.0;
        int idx = 0;
        double[] angle_atan = new double[x.length];
        double[] angle_pos = new double[x.length];

        //get the angle of spiral
        for (int i = 0; i < x.length; i++)
        {
            angle_atan[i] = Math.atan2(y[i], x[i]);
        }

        for(int i = 0; i < x.length; i++)
        {
            if(i == 0)
                angle_pos[i] = unwrap(0, angle_atan[i]);
            else
                angle_pos[i] = unwrap(angle_atan[i-1], angle_atan[i]);
        }

        //calculate base point
        double v = 600;
        double w = 10*Math.PI;
        double t = angle_pos[0]/w;
        double r = v*t;
        double[] pair_pos_x = new double[x.length];
        double[] pair_pos_y = new double[x.length];

        for (int i = 0; i < x.length; i++)
        {

            if(i == 0)
            {
                pair_pos_x[i] = r*Math.cos(angle_pos[i]);
                pair_pos_y[i] = r*Math.sin(angle_pos[i]);

            }

            else
            {
                t = angle_pos[i]/w;
                r = v*t;
                pair_pos_x[i] = r*Math.cos(angle_pos[i]);
                pair_pos_y[i] = r*Math.sin(angle_pos[i]);


            }
            Log.v("LineTaskAnalyze", "linetaskanalyzzz position x "+x[i]+" y "+y[i]);
            Log.v("LineTaskAnalyze", "linetaskanalyzzz angleee angle_atan : "+angle_atan[i]+" angle_pos : "+angle_pos[i]);
            Log.v("LineTaskAnalyze", "linetaskanalyzzz pairpos x "+pair_pos_x[i]+" pairpos y "+pair_pos_y[i]);
        }
        //calculate error distance
        double sum = 0;
        for(int i = 0; i < x.length; i++)
        {
            sum += Math.sqrt(Math.pow((x[i]-pair_pos_x[i]), 2) + Math.pow((y[i]-pair_pos_y[i]), 2));
        }
        return sum/x.length;
    }
    //x축을 구하긴 하지만 사용하는거는 y축밖에 없음
    public double myFFT(double[] pca_data){


        int n = pca_data.length;
        Complex[] temp_com = new Complex[n];

        List<Integer> slice = new ArrayList<Integer>();

        Dataslice ds = new Dataslice();
        slice = ds.Dataslice(n);

        Log.d("sdsd1", "n is "  + n);
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
        double[] absfft_pca = new double[padlen/2];

        Complex inter;
        Complex nm = new Complex(padlen,0);

        inter = fft_pca[0].divides(nm);
        absfft_pca[0] = inter.abs();

        for(int i = 1; i < padlen/2; i++) {
            inter = fft_pca[i].divides(nm);
            absfft_pca[i] = 2*inter.abs();
            Log.d("test1_fft","fft  " +i+ " "+ absfft_pca[i]);
        }
        float[] index = new float[padlen/2];

        for(int j = 0; j < padlen/2 ; j++) {
            index[j] = (float) srate * (float) j / (float) padlen;
            Log.d("test1_fft","fft index  " +j+ " "+ index[j]);
        }

        result_fft = FFT_PeakFind(absfft_pca, index);

        return result_fft;
    }

    public double FFT_PeakFind(double[] result, float[] index) {
        double mean = 0; double std = 0;  double hz = 0; double max_amp = 0;
        double[] session = new double[result.length];
        double[] temp1 = new double[result.length]; //temp1 : 10~25Hz 사이의 값을 추출해서 저장하는 변수
        double[] temp2 = new double[result.length]; //temp2 : temp1에서 mean + 70*std 값보다 큰 값을 저장하는 변수

        // 10~25Hz 사이의 정보만 받기위해
        int a=0;
        for (int i = 0; i <result.length  ; i ++) {
            if (index[i] >= 10 && index[i] <= 25) {
                temp1[a++] = result[i];

            }
        }
        Log.d("test2","필터링된 주파수 갯수1:  " + a );

        // 10~25Hz 사이의 정보의 표준편차와 평균을 구함
        mean = Math.mean(temp1);
        std = Math.var(temp1);


        // 10~25Hz의 표준편차와 평균을 구해서  X = mean + std * A 의 식을 만듦
        // X의 값이 주파수 대역의 데이터가 하나의 peak점만 찾게하기 위한 A의 값을 정하면됨
        // 실험 해봤을땐 40이 적당했음
        int b=0;
        for (int i = 0; i <result.length  ; i ++) {
            if (result[i]>=(mean+40*std)) { //몇 배 할껀지 정하기
                temp2[b++] = result[i];

            }
        }

        Log.d("test2","필터링된 주파수 갯수2:  " + b );

        max_amp = Math.max(temp2);

        Log.d("test2","주파수 맥시멈:  " + max_amp );

        for (int i = 0; i <result.length  ; i ++) {
            if(max_amp ==result[i]){
                hz = index[i];
                break;
            }
        }
        //간혹 가다가 peak점은 찾았는데 떨림이 없는 데이터면 안되니까 3.2Hz이하면 안쳐줌
        if(hz < 3.2){
            hz = -1 ;
        }


        return hz;
    }

    public double rmsValue(double[] arr, int n)
    {
        double square = 0;
        double mean = 0;
        double root = 0;

        // Calculate square.
        for(int i = 0; i < n; i++)
        {
            square += Math.pow(arr[i], 2);
        }

        // Calculate Mean.
        mean = (square / (float) (n));

        // Calculate Root.
        root = (double) Math.sqrt(mean);
        return root;
    }

    public static double unwrap(double reference, double wrapped)
    {
        double po = 0.0;
        double dp = wrapped - reference;

        if (dp > Math.PI)
        {
            while(dp > Math.PI)
            {
                po -= 2*Math.PI;
                dp -= 2*Math.PI;
            }
        }

        if (dp < -Math.PI)
        {
            while(dp < -Math.PI)
            {
                po += 2*Math.PI;
                dp += 2*Math.PI;
            }
        }

        return wrapped + po;
    }

}
