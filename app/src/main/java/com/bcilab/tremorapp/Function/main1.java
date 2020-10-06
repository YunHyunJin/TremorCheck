package com.bcilab.tremorapp.Function;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


public class main1 {

	private static fft ft;
	private static LineTaskAnalyze lineTaskAnalyze;
	private static FillNull fn ;
	private static fitting fg;
	private static double[] Result;
	private static final int srate = 50;
	private static int count = 0;
	static String Clinic_ID;

	//private static DatabaseReference firebaseLine = firebaseDatabase.getReference("Line RowData");
	public static int line_count;
	private static String lline_count;
	static File file;
	static String m;
	static Context ctx;



	public static double[] main1(String args, Context context, String id, String data_path, String task, String both) throws IOException {


		ctx = context;
		Clinic_ID = id;
		double[][] fildata;// filled null point data

		/* read data - skip*/
		List<Double> orgX = new ArrayList<Double>();
		List<Double> orgY = new ArrayList<Double>();
		List<Double> time = new ArrayList<Double>();
		String csvFile = args;
		BufferedReader br = null;
		String line = "";
		file = new File(ctx.getFilesDir(), Clinic_ID + "LineRow_num.txt");

		//writeToFile("0", ctx);

		//firebase에 데이터 보내는 용인데 firebase 구조를 바꿔서 아마 이거는 안될듯?
		//테스트 용으로 자기 fireBase 저장소 만들어서 test옹으로 쓰면 될듯
		try {

			/*firebaseLine.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
					for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()){
						line_count = (int) dataSnapshot.getChildrenCount();
						if(line_count < 10){
							lline_count = "0" + line_count;
						}
						else{
							lline_count = String.valueOf(line_count);
						}
						writeToFile(String.valueOf(line_count), ctx);
					}
				}

				@Override
				public void onCancelled(@NonNull DatabaseError databaseError) {
					Log.v("알림", "Failed"); }
			});*/

			if(file.exists()){
				m = readFromFile(ctx);
				Log.v("SpiralRow", m);
			}

			br = new BufferedReader(new FileReader(csvFile));
			br.readLine();
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",+");
				orgX.add(Double.parseDouble(data[0]));
				if(lline_count == null)
					lline_count = "00";
				//firebaseLine.child("Task No "+lline_count).child("x_position").setValue(orgX);
				//firebaseLine.child("Task No "+lline_count).child("y_position").setValue(orgY);
				//firebaseLine.child("Task No "+lline_count).child("time").setValue(time);
				orgY.add(Double.parseDouble(data[1]));
				time.add(Double.parseDouble(data[2]));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		/* data setting - must do */
		int n = time.size();

		//pre-processing
		fn = new FillNull();
		fildata = new double[2][n];
		fildata[0] = fn.FillNull(orgX,n);
		fildata[1] = fn.FillNull(orgY,n);




		//separate data
		int start = 1;
		List<Integer> slice = new ArrayList<Integer>();
		Dataslice ds = new Dataslice();
		slice = ds.Dataslice(n);
		Log.d("sdsd1", "n is "  + n);
		int m = slice.size();

		fg = new fitting();

		//make position listarray to array
		double[] x_position = new double[n];
		double[] y_position = new double[n];
		double[] time_array = new double[n];
		int size = 0;


		for(double temp: orgX)
		{
			x_position[size++] = temp;
			if(temp == n)
				break;
		}
		size = 0;
		for(double temp: orgY)
		{
			y_position[size++] = temp;
			if(temp == n)
				break;
		}
		size = 0;
		for(double temp: time)
		{
			time_array[size++] = temp;
			Log.d("test1_time","time"+ temp);
			if(temp == n)
				break;
		}

		//5가지 결과값 저장용 - 라인 테스트 용
		Result=new double[5];
		Result = fg.fitting(x_position, y_position,time_array, n, false, data_path, Clinic_ID, task, both);

		for(int i = 0;i<5;i++){
			Result[i] = Math.round(Result[i]*1000)/1000.0;
			Log.d("test2","result:" +i+" "+ Result[i] );
		}


		//Result [0]: TM , [1]: TF , [2]:time , [3]: ED , [4]:velocity
		return Result;
	}

	// write File
	private static void writeToFile(String data, Context context) {
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(Clinic_ID + "LineRow_num.txt", Context.MODE_PRIVATE));
			outputStreamWriter.write(data);
			outputStreamWriter.close();
		}
		catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}



	// read File
	private static String readFromFile(Context context) {
		String ret = "";
		try {

			InputStream inputStream = context.openFileInput(Clinic_ID + "LineRow_num.txt");
			if ( inputStream != null ) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();

				while ( (receiveString = bufferedReader.readLine()) != null ) {
					stringBuilder.append(receiveString);
				}

				inputStream.close();
				ret = stringBuilder.toString();
			}
		}
		catch (FileNotFoundException e) {
			Log.e("login activity", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("login activity", "Can not read file: " + e.toString());
		}
		return ret;
	}
}