package com.bcilab.tremorapp.Function;

import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import smile.math.Math;


/*
 * Calculate how closely the given line is drawn to match the given line.
 * input :  X, Y data of drawn spiral
 * output : total distance/ mean / standard deviation
 * */

public class fitting {

	public LineTaskAnalyze lineTaskAnalyze=new LineTaskAnalyze();
	//갤럭시 탭이 1cm당 가지는 pixel 갯수
	public final static double DPI =88.18;

	//직선 그리기 할 때, base line의 시작점과 끝점
	public static int startX = 480;
	public static int startY = 100;   //finalY -> 1848 ,startY -> 100
	public static double[] distance;
	public static int finalY=1848;   //this.resources.displayMetrics.heightPixels
	public static int count = 0;
	static String Clinic_ID;


	public static  baseline bring(double[] x, double[] y, double[] t) {
		return new baseline(x , y, t);}

	//obj = base, org = subject
	public double[] fitting(double[] orgX, double[] orgY, double[] time, int m, boolean SorL, String data_path, String id, String task, String both) throws FileNotFoundException {
		int n = m;
		double[] objX = new double[n] ;      double[] objY = new double[n];       double[] t = new double[n]  ;
		baseline base = bring(objX, objY, t);
		Clinic_ID = id;

		Log.v("이거와", "이거오는데!");
		base.setting(n);
		objX = base.getArray1();
		objY = base.getArray2();
		ArrayList<Double> base_x = new ArrayList<Double>();
		ArrayList<Double> base_y = new ArrayList<Double>();
		ArrayList<Double> pos_x = new ArrayList<Double>();
		ArrayList<Double> pos_y = new ArrayList<Double>();
		ArrayList<Double> time_array = new ArrayList<Double>();

		//orgX,orgY가 직접 그린 라인의 좌표
		for(Double d : objX)
			base_x.add(d);
		for(Double d : orgX)
			pos_x.add(d);
		for(Double d : objY)
			base_y.add(d);
		for(Double d : orgY)
			pos_y.add(d);
		for(Double d : time)
			time_array.add(d);
		/* ******************************** make csv file *************************************/
		File mfolder = Environment.getExternalStoragePublicDirectory("/TremorApp/"+Clinic_ID+"/"+task+both);

		String dataname = data_path.replaceAll("/","_");
		String foldername = "";
		/* ******************************** 데이터 저장 경로 만들기 *************************************/

		/* ******************************** 데이터 저장 경로 설정 완료 *************************************/
//		File csvfile = new File(mfolder, dataname);
//		Log.v("이거와", "이거오는데3!"+data_path);
//		try{
//
//			FileWriter fw = new FileWriter(csvfile);
//			fw.append("positionx"+",");
//			for(double posx : orgX)
//			{
//				fw.append(""+posx);
//				fw.append(",");
//			}
//			fw.append("\n");
//			fw.append("positiony"+",");
//			for(double posy: orgY)
//			{
//				fw.append(""+posy);
//				fw.append(",");
//			}
//			fw.append("\n");
//			fw.append("basex"+",");
//			for(double basex : objX)
//			{
//				fw.append(""+basex);
//				fw.append(",");
//			}
//			fw.append("\n");
//			fw.append("basey"+",");
//			for(double basey : objY)
//			{
//				fw.append(""+basey);
//				fw.append(",");
//			}
//			fw.append("\n");
//			fw.append("time"+",");
//			for(double tm : time_array)
//			{
//				fw.append(""+tm);
//				fw.append(",");
//			}
//			fw.close();
//
//		} catch (IOException e){
//			e.printStackTrace();
//		}
		/* ******************************** Upload csv file *************************************/



//		StorageReference spiral_reference = storage.getInstance().getReference();
//		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//		String uid = user.getUid();
//		Uri datafile = Uri.fromFile(csvfile);
//		final StorageReference filepath = spiral_reference.child(data_path.replace(dataname, "") + datafile.getLastPathSegment());
//		firebase_path = firebaseDatabase.getReference("URL List").child(uid).child(Clinic_ID).child("Path");
//		firebase_path.push().setValue(filepath.getPath());
//		filepath.putFile(datafile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//			@Override
//			public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//			}
//
//		})
//				.addOnFailureListener(new OnFailureListener() {
//					@Override
//					public void onFailure(@NonNull Exception e) {
//
//					}
//				});

		double[] re_bpf_x = new double[orgX.length];
		double[] re_bpf_y = new double[orgY.length];
		double[] re_pca = new double[orgX.length];
		double[] re_lpf_x = new double[orgX.length];
		double[] re_lpf_y = new double[orgY.length];
		double[] re_hil = new double[orgX.length];
		double[] re_fft = new double[orgX.length];
		double[] baseline_x = new double[orgX.length];
		double re_drawing_length = 0;

		double ErrorDistance = 0;
		double Velocity=0;
		double TremorMagnitude=0;
		double Finish_time = 0;
		double TremorFrequency=0;
		/* ******************************** Analysis *************************************/
		//line과 Spiral의 공통 부분

		//BPF
		re_bpf_x=lineTaskAnalyze.myBPF(orgX,SorL);
		re_bpf_y= lineTaskAnalyze.myBPF(orgY,SorL);

		//PCA
		re_pca = lineTaskAnalyze.myPCA(re_bpf_x,re_bpf_y);

		//LPF
		re_lpf_x=lineTaskAnalyze.myLPF(orgX,SorL);
		re_lpf_y= lineTaskAnalyze.myLPF(orgY,SorL);

		//Hilbert
		re_hil = lineTaskAnalyze.MyHilbert(re_pca);

		//Time , Frequency는 밑에 바로 결과 나오게 함
		Finish_time = time[orgX.length-1]/1000;

		//그린 그림의 길이
		re_drawing_length = lineTaskAnalyze.myDrawingLength(re_lpf_x);
		TremorMagnitude = Math.mean(re_hil)/DPI;
		Velocity = (re_drawing_length/DPI)/Finish_time;
		Log.v("속도 판정1", String.valueOf(re_drawing_length/DPI));
		Log.v("속도 판정2", String.valueOf(Finish_time/1000));
		Log.v("속도 판정3", String.valueOf(Velocity));
		TremorFrequency = lineTaskAnalyze.myFFT(re_pca);

		if(TremorMagnitude < 0.1 ){
			TremorFrequency = -1;
		}

		if (SorL) // Sprial
		{
			ErrorDistance = lineTaskAnalyze.MyED(re_lpf_x,re_lpf_y)/DPI;
		}
		else //Line
		{

			//매틀랩에서는 480을 빼줘야하지만 java의  LPF 에선 offset을 0으로 만들어줌
			for(int i=0;i<orgX.length;i++){
				baseline_x[i] = 0;
			}


			ErrorDistance = lineTaskAnalyze.myEUD(re_lpf_x,baseline_x)/DPI;



		}
		//[0]: TM , [1]: TF , [2]:time , [3]: ED , [4]:velocity
		double[] result = {TremorMagnitude,TremorFrequency,Finish_time,ErrorDistance,Velocity};
		return result;



//      for(int i = 0;i<orgX.length;i++){
//         Log.d("test1_bpfx","x :" +i+" "+ re_bpf_x[i] );
//         Log.d("test1_bpfy","y :" +i+" "+ re_bpf_y[i] );
//         Log.d("test1_pca","y :" +i+" "+ re_pca[i] );
//      }

		//firebase에 값넣기 - 실험용
//      ArrayList<Double> bpf_x = new ArrayList<Double>();
//      ArrayList<Double> bpf_y = new ArrayList<Double>();
//      ArrayList<Double> pca_re = new ArrayList<Double>();
//      ArrayList<Double> lpf_x = new ArrayList<Double>();
//      ArrayList<Double> lpf_y = new ArrayList<Double>();
//      ArrayList<Double> hil_re = new ArrayList<Double>();
//      ArrayList<Double> fft_re = new ArrayList<Double>();
//
//      bpf_x=lineTaskAnalyze.ArrayToArraylist(re_bpf_x);
//      bpf_y=lineTaskAnalyze.ArrayToArraylist(re_bpf_y);
//      pca_re=lineTaskAnalyze.ArrayToArraylist(re_pca);
//      lpf_x=lineTaskAnalyze.ArrayToArraylist(re_lpf_x);
//      lpf_y=lineTaskAnalyze.ArrayToArraylist(re_lpf_y);
//      hil_re=lineTaskAnalyze.ArrayToArraylist(re_hil);
//      fft_re=lineTaskAnalyze.ArrayToArraylist(re_fft);
//
//      firebaseLine.child("Task No ").child("BPF_x").setValue(bpf_x);
//      firebaseLine.child("Task No ").child("BPF_y").setValue(bpf_y);

//      firebaseLine.child("Task No ").child("Velocity").setValue(Velocity);
//      firebaseLine.child("Task No ").child("Finish_time").setValue(Finish_time);
//      firebaseLine.child("Task No ").child("ErrorDistance").setValue(ErrorDistance);
//      firebaseLine.child("Task No ").child("TremorMagnitude").setValue(TremorMagnitude);
//      firebaseLine.child("Task No ").child("TremorFrequency").setValue(TremorFrequency);



	}
	public static int getMaxValue(double[] numbers){
		double maxValue = numbers[0];
		int i;
		int cnt=0;
		for( i=1;i < numbers.length;i++){
			if(numbers[i] > maxValue){
				maxValue = numbers[i];
				cnt = i;
			}
		}
		return cnt;

	}


	static class baseline {
		private double[] baseX;
		private double[] baseY;
		private double[] t;
		private float min = 0;//baseline scope
		private double max = 4 * Math.PI;

		public baseline() {
		}

		public baseline(double[] array1, double[] array2, double[] time) {
			this.baseX = array1;
			this.baseY = array2;
			this.t = time;
		}

		public double[] getArray1() {
			return baseX;
		}

		public double[] getArray2() {
			return baseY;
		}

		public double[] getArray3() {
			return t;
		}

		public baseline setting(int length) {
			this.t = new double[length];
			for (int i = 0; i < length; i++) {
				this.t[i] = min + i * (max - min) / (length - 1);
				this.baseX[i] = fitting.startX;
				this.baseY[i] = finalY - (fitting.startY+i);
			}

			return new baseline(baseX, baseY, t);
		}

	}
}