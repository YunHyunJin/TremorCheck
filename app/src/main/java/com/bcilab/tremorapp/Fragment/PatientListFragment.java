package com.bcilab.tremorapp.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcilab.tremorapp.Adapter.ItemClickSupport;
import com.bcilab.tremorapp.Adapter.RecyclerViewAdapter;
import com.bcilab.tremorapp.Data.PatientItem;
import com.bcilab.tremorapp.PatientListActivity;
import com.bcilab.tremorapp.PersonalPatientActivity;
import com.bcilab.tremorapp.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class PatientListFragment extends Fragment {

    private ArrayList<PatientItem> patientList = new ArrayList<>();
    private ArrayList<PatientItem> selected_patientList = new ArrayList<>();
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private CheckBox all_checkBox;
    private boolean isMultiSelect = false;
    private boolean deleteMode = false;
    private View view ;
    private TextView patientTotal ;
    private TextView patientNum ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_patient_list, container, false);

        //int patineLength ;
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        recyclerView = view.findViewById(R.id.patientList);
        EditText searchPatient = (EditText) view.findViewById(R.id.searchPatient);
        RelativeLayout patientListL = (RelativeLayout)view.findViewById(R.id.patientListL);
        Button addPatient = (Button)view.findViewById(R.id.patientAdd);
        all_checkBox = (CheckBox) view.findViewById(R.id.all_checkBox);
        patientTotal = (TextView) view.findViewById(R.id.item_count) ;

        all_checkBox.setVisibility(View.GONE);

        searchPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.showSoftInput(searchPatient, 0);
            }
        });
        patientListL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(searchPatient.getWindowToken(), 0);
            }
        });
        searchPatient.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {
                query = query.toString().toLowerCase();
                final ArrayList<PatientItem> filteredList = new ArrayList<>();
                for (int i = 0; i < patientList.size(); i++) {
                    final String text = patientList.get(i).getClinicID();
                    final String text2 = patientList.get(i).getPatientName();
                    if (text.contains(query) || text2.contains(query)) {
                        filteredList.add(patientList.get(i));
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), filteredList, selected_patientList);
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerViewAdapter.notifyDataSetChanged();  // data set changed
            }
        });

        ((Button)view.findViewById(R.id.patientAdd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPatient();
            }
        });

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener(){

            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                if (isMultiSelect) multi_select(position);
                else {
                    Intent intent = new Intent(getActivity(), PersonalPatientActivity.class);
                    intent.putExtra("clinicID", patientList.get(position).getClinicID());//수정
                    intent.putExtra("patientName", patientList.get(position).getPatientName());
                    intent.putExtra("task", "UPDRS");
                    intent.putExtra("dateFirst",patientList.get(position).getDateFirst());
                    intent.putExtra("dateFinal",patientList.get(position).getDateFinal());
                    startActivity(intent);
                }
            }
        });

        ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                if (!isMultiSelect) {
                    delete_exit();
                }
                multi_select(position);
                return true;
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        patientTotal.setText(String.valueOf("Total "+PatientLoad()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), patientList, selected_patientList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();  // data set changed
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void addPatient(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.addpatient, null);
        builder.setView(view);
        final EditText clinicID = (EditText) view.findViewById(R.id.addClinicID);
        final EditText patientName = (EditText) view.findViewById(R.id.addPatientName);

        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (TextUtils.isEmpty(clinicID.getText().toString()) || TextUtils.isEmpty(patientName.getText().toString())) {
                    Toast.makeText(getActivity(), "빈칸을 모두 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else {
                    String clinic_id = clinicID.getText().toString();
                    String patient_name = patientName.getText().toString();

                    File path = Environment.getExternalStoragePublicDirectory(
                            "/TremorApp/"+clinic_id);
                    Log.v("PatintList", "환자 id"+clinic_id);
                    if (!path.mkdirs()) {

                        Toast.makeText(getActivity(), "동일한 Clinic ID가 존재합니다.\nid나 이름을 변경하여 등록하세요.", Toast.LENGTH_SHORT).show();

                    }
                    else{
                        Log.v("PatintList", "환자 추가"+clinic_id);
                        StringBuilder patient = new StringBuilder();
                        patient.append("Clinic_ID,Name,Count,FirstDate,FinalDate");
                        patient.append("\n"+clinic_id+","+patient_name+","+null+","+null+","+null);

                        File patientcsv = new File(path, "patient.csv") ;

                        try{
                            FileWriter write = new FileWriter(patientcsv, false);
                            PrintWriter csv = new PrintWriter(write);
                            csv.println(patient);
                            csv.close();
                            patientList.add(new PatientItem(clinic_id, patient_name, null, null, false));

                            Environment.getExternalStoragePublicDirectory(
                                    "/TremorApp/"+clinic_id+"/SpiralLeft").mkdir();
                            Environment.getExternalStoragePublicDirectory(
                                    "/TremorApp/"+clinic_id+"/SpiralRight").mkdir();
                            Environment.getExternalStoragePublicDirectory(
                                    "/TremorApp/"+clinic_id+"/LineLeft").mkdir();
                            Environment.getExternalStoragePublicDirectory(
                                    "/TremorApp/"+clinic_id+"/LineRight").mkdir();

                            recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), patientList, selected_patientList);
                            recyclerView.setAdapter(recyclerViewAdapter);

                            String total = patientTotal.getText().toString();
                            int totalNum = Integer.parseInt(total.substring(6, total.length()))+1 ;
                            patientTotal.setText("Total "+String.valueOf(totalNum)) ;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }


                    Toast.makeText(getActivity(), "환자 추가", Toast.LENGTH_SHORT).show();

                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean getdeleteMode(){
        return deleteMode;
    }
    public int PatientLoad() {
        patientList = new ArrayList<PatientItem>() ;
        File path = Environment.getExternalStoragePublicDirectory(
                "/TremorApp");

        File directory = new File(String.valueOf(path)) ;
        File[] foder = directory.listFiles() ;
        for (int i=0; i< foder.length; i++) {
            try {
                String patientPath = String.valueOf(path)+"/"+foder[i].getName() ;
                File patientCSV = new File(patientPath, "patient.csv");

                BufferedReader buffer = new BufferedReader(new FileReader(patientCSV));
                String str = buffer.readLine();
                while (str!=null) {
                    str = buffer.readLine();
                    String[] patientStr= str.split(",");
                    patientList.add(new PatientItem(patientStr[0], patientStr[1], patientStr[3].equals("null")? null:DateAdd(patientStr[3]), patientStr[3].equals("null")? null:DateAdd(patientStr[3]), false));
                    patientList.add(new PatientItem(patientStr[0], patientStr[1], patientStr[3].equals("null")? null:DateAdd(patientStr[3]), patientStr[3].equals("null")? null:DateAdd(patientStr[3]), false));
                    patientList.add(new PatientItem(patientStr[0], patientStr[1], patientStr[3].equals("null")? null:DateAdd(patientStr[3]), patientStr[3].equals("null")? null:DateAdd(patientStr[3]), false));
                }
                buffer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return foder.length ;
    }

    private String DateAdd(String date) {
        String[] array = date.split("/");
        return array[0].substring(2, 4) + "." + array[1] + "." + array[2];
    }

    public void multi_select(int position) {
        if (deleteMode == true) {
            if (selected_patientList.contains(patientList.get(position))) {
                selected_patientList.remove(patientList.get(position));
                patientList.get(position).setDeleteBox(false);

            } else {
                selected_patientList.add(patientList.get(position));
                patientList.get(position).setDeleteBox(true);
            }
            ((PatientListActivity)getActivity()).selectNum(selected_patientList.size());
            recyclerViewAdapter.refreshAdapter(patientList, selected_patientList);

        }
    }

    public void delete_exit() {
        if(isMultiSelect==true){
            deleteMode = false;
            isMultiSelect = false;
            recyclerViewAdapter.novisible();
            ((PatientListActivity)getActivity()).visibleBottom(View.GONE);
            all_checkBox.setVisibility(View.GONE);
            for (int i = 0; i < patientList.size(); i++) {
                patientList.get(i).setDeleteBox(false);
            }
            all_checkBox.setChecked(false);

            selected_patientList = new ArrayList<PatientItem>();
            recyclerViewAdapter.refreshAdapter(patientList, selected_patientList);
        }
        else{
            all_checkBox.setVisibility(View.VISIBLE);
            selected_patientList = new ArrayList<PatientItem>();
            isMultiSelect = true;
            ((PatientListActivity)getActivity()).visibleBottom(View.VISIBLE);
            recyclerViewAdapter.visible();
            deleteMode = true;
        }
    }

    public void removeList(String clinicID) {

    }
}
