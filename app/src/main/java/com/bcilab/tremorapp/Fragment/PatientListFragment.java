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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcilab.tremorapp.Adapter.ItemClickSupport;
import com.bcilab.tremorapp.Adapter.RecyclerViewAdapter;
import com.bcilab.tremorapp.Data.PatientItem;
import com.bcilab.tremorapp.PatientListActivity;
import com.bcilab.tremorapp.PersonalPatientActivity;
import com.bcilab.tremorapp.R;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class PatientListFragment extends Fragment {

    private ArrayList<PatientItem> patientList = new ArrayList<>();// * 사용자 담는 리스트
    private ArrayList<PatientItem> selected_patientList = new ArrayList<>();// * 삭제할 때나 검색할 때 사용되는 리스트
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private CheckBox all_checkBox;
    private boolean isMultiSelect = false;
    private boolean deleteMode = false;
    private View view ;
    private TextView patientTotal ;
    private TextView patientNum ;
    private int patient_num ;
    int year, month, year2, month2;
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

        Log.v("PatientList", "PatientListTTTTonCreatView");
        EditText searchPatient = (EditText) view.findViewById(R.id.searchPatient);
        RelativeLayout patientListL = (RelativeLayout)view.findViewById(R.id.patientListL);
        //Button addPatient = (Button)view.findViewById(R.id.patientAdd);
        all_checkBox = (CheckBox) view.findViewById(R.id.all_checkBox);
        //patientTotal = (TextView) view.findViewById(R.id.item_count) ;

        all_checkBox.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        all_checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (all_checkBox.isChecked()) {
                    for (int i = 0; i < patientList.size(); i++) {
                        if (!patientList.get(i).isDeleteBox()) multi_select(i);
                    }
                } else {
                    for (int i = 0; i < patientList.size(); i++) {
                        if (patientList.get(i).isDeleteBox()) multi_select(i);
                    }
                }
            }
        });
        ((PatientListActivity)getActivity()).patientNum(PatientLoad());
        searchPatient.setOnClickListener(new View.OnClickListener() {// * 화면에서 검색창(edittext) 때문에 바로 키패드 실행되는 것 방지
            @Override
            public void onClick(View v) {
                imm.showSoftInput(searchPatient, 0);
            }
        });
        patientListL.setOnClickListener(new View.OnClickListener() {// * 검색 창 제외하고 다른 화면 누를 시에 키패드 내려가기
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(searchPatient.getWindowToken(), 0);
            }
        });

        searchPatient.addTextChangedListener(new TextWatcher() {// * 검색 이벤트
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

        ((TextView)view.findViewById(R.id.sort_id)).setOnClickListener(new View.OnClickListener() {// * id로 정렬
            @Override
            public void onClick(View view) {
                final ArrayList<PatientItem> filteredList = new ArrayList<>();
                PopupMenu popupMenu = new PopupMenu(getActivity(), ((TextView)view.findViewById(R.id.sort_id)));
                popupMenu.getMenuInflater().inflate(R.menu.menu_clinidid, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.id_내림차순:
                                Collections.sort(patientList, new Comparator<PatientItem>() {
                                    @Override
                                    public int compare(PatientItem o1, PatientItem o2) {
                                        return o2.getClinicID().compareTo(o1.getClinicID());
                                    }
                                });
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), patientList, selected_patientList);
                                recyclerView.setAdapter(recyclerViewAdapter);
                                recyclerViewAdapter.notifyDataSetChanged();
                                break;

                            case R.id.id_오름차순:
                                Collections.sort(patientList, new Comparator<PatientItem>() {
                                    @Override
                                    public int compare(PatientItem o1, PatientItem o2) {
                                        return o1.getClinicID().compareTo(o2.getClinicID());
                                    }
                                });
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), patientList, selected_patientList);
                                recyclerView.setAdapter(recyclerViewAdapter);
                                recyclerViewAdapter.notifyDataSetChanged();
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        ((TextView)view.findViewById(R.id.sort_name)).setOnClickListener(new View.OnClickListener() {// * name으로 정렬
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), ((TextView)view.findViewById(R.id.sort_name)));
                popupMenu.getMenuInflater().inflate(R.menu.menu_clinidid, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.id_내림차순:
                                Collections.sort(patientList, new Comparator<PatientItem>() {
                                    @Override
                                    public int compare(PatientItem o1, PatientItem o2) {
                                        return o2.getPatientName().compareTo(o1.getPatientName());
                                    }
                                });
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), patientList, selected_patientList);
                                recyclerView.setAdapter(recyclerViewAdapter);
                                recyclerViewAdapter.notifyDataSetChanged();
                                break;

                            case R.id.id_오름차순:
                                Collections.sort(patientList, new Comparator<PatientItem>() {
                                    @Override
                                    public int compare(PatientItem o1, PatientItem o2) {
                                        return o1.getPatientName().compareTo(o2.getPatientName());
                                    }
                                });
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), patientList, selected_patientList);
                                recyclerView.setAdapter(recyclerViewAdapter);
                                recyclerViewAdapter.notifyDataSetChanged();
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        ((TextView)view.findViewById(R.id.sort_date)).setOnClickListener(new View.OnClickListener() {// * date로 정렬
            @Override
            public void onClick(View view) {
                final ArrayList<PatientItem> filteredList = new ArrayList<>();

                PopupMenu popupMenu = new PopupMenu(getActivity(), ((TextView)view.findViewById(R.id.sort_date)));
                popupMenu.getMenuInflater().inflate(R.menu.menu_date, popupMenu.getMenu());SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
                Date day = new Date();
                String today = date.format(day);
                final String[] array = today.split("/");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.id_전체:
                                for (int i = 0; i < patientList.size(); i++) {
                                    filteredList.add(patientList.get(i));
                                }
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), filteredList, selected_patientList);
                                recyclerView.setAdapter(recyclerViewAdapter);
                                recyclerViewAdapter.notifyDataSetChanged();
                                return true;

                            case R.id.id_1개월:
                                for (int i = 0; i < patientList.size(); i++) {
                                    if (patientList.get(i).getDateFinal()==null)  {
                                        filteredList.add(patientList.get(i));
                                    } else {
                                        String[] array2 = patientList.get(i).getDateFinal().split("[.]");
                                        if (array[1].equals("10") || array[1].equals("11") || array[1].equals("12")) {
                                            month = Integer.parseInt(array[1]);
                                        } else {
                                            month = Integer.parseInt(array[1].substring(1, 2));
                                        }
                                        if (array2[1].equals("10") || array2[1].equals("11") || array2[1].equals("12")) {
                                            month2 = Integer.parseInt(array2[1]);
                                        } else {
                                            month2 = Integer.parseInt(array2[1].substring(1, 2));
                                        }
                                        if (month == 1) {
                                            year = Integer.parseInt(array[0].substring(2, 4)) - 1;
                                            month = 12;
                                        } else {
                                            year = Integer.parseInt(array[0].substring(2, 4));
                                            month--;
                                        }
                                        year2 = Integer.parseInt(array2[0]);
                                        if (Integer.parseInt(array2[0]) > year) {
                                            filteredList.add(patientList.get(i));
                                        } else if (Integer.parseInt(array2[0]) == year) {
                                            if (month2 >= month) {
                                                filteredList.add(patientList.get(i));
                                            }
                                        }
                                    }
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), filteredList, selected_patientList);
                                    recyclerView.setAdapter(recyclerViewAdapter);
                                    recyclerViewAdapter.notifyDataSetChanged();
                                }
                                return true;
                            case R.id.id_3개월 :
                                for (int i = 0; i < patientList.size(); i++) {
                                    if (patientList.get(i).getDateFinal()==null) {
                                        filteredList.add(patientList.get(i));
                                    } else {
                                        String[] array2 = patientList.get(i).getDateFinal().split("[.]");
                                        if (array[1].equals("10") || array[1].equals("11") || array[1].equals("12")) {
                                            month = Integer.parseInt(array[1]);
                                        } else {
                                            month = Integer.parseInt(array[1].substring(1, 2));
                                        }
                                        if (array2[1].equals("10") || array2[1].equals("11") || array2[1].equals("12")) {
                                            month2 = Integer.parseInt(array2[1]);
                                        } else {
                                            month2 = Integer.parseInt(array2[1].substring(1, 2));
                                        }
                                        if (month <= 3) {
                                            year = Integer.parseInt(array[0].substring(2, 4)) - 1;
                                            month = 12 + (month - 3);
                                        } else {
                                            year = Integer.parseInt(array[0].substring(2, 4));
                                            month = month - 3;
                                        }
                                        year2 = Integer.parseInt(array2[0]);
                                        if (Integer.parseInt(array2[0]) > year) {
                                            filteredList.add(patientList.get(i));
                                        } else if (Integer.parseInt(array2[0]) == year) {
                                            if (month2 >= month) {
                                                filteredList.add(patientList.get(i));
                                            }
                                        }
                                    }
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), filteredList, selected_patientList);
                                    recyclerView.setAdapter(recyclerViewAdapter);
                                    recyclerViewAdapter.notifyDataSetChanged();

                                }
                                break;
                            case R.id.id_6개월 :
                                for (int i = 0; i < patientList.size(); i++) {
                                    if (patientList.get(i).getDateFinal()==null) {
                                        filteredList.add(patientList.get(i));
                                    } else {
                                        String[] array2 = patientList.get(i).getDateFinal().split("[.]");
                                        if (array[1].equals("10") || array[1].equals("11") || array[1].equals("12")) {
                                            month = Integer.parseInt(array[1]);
                                        } else {
                                            month = Integer.parseInt(array[1].substring(1, 2));
                                        }
                                        if (array2[1].equals("10") || array2[1].equals("11") || array2[1].equals("12")) {
                                            month2 = Integer.parseInt(array2[1]);
                                        } else {
                                            month2 = Integer.parseInt(array2[1].substring(1, 2));
                                        }
                                        if (month <= 6) {
                                            year = Integer.parseInt(array[0].substring(2, 4)) - 1;
                                            month = 12 + (month - 6);
                                        } else {
                                            year = Integer.parseInt(array[0].substring(2, 4));
                                            month = month - 6;
                                        }
                                        year2 = Integer.parseInt(array2[0]);
                                        if (Integer.parseInt(array2[0]) > year) {
                                            filteredList.add(patientList.get(i));
                                        } else if (Integer.parseInt(array2[0]) == year) {
                                            if (month2 >= month) {
                                                filteredList.add(patientList.get(i));
                                            }
                                        }
                                    }
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), filteredList, selected_patientList);
                                    recyclerView.setAdapter(recyclerViewAdapter);
                                    recyclerViewAdapter.notifyDataSetChanged();
                                }
                                break ;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener(){// * 리스트 클릭 event

            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                if (isMultiSelect) {
                    multi_select(position);
                    Log.v("PatientList", "Patient multiselect");
                }
                else {
                    Intent intent = new Intent(getActivity(), PersonalPatientActivity.class);
                    intent.putExtra("clinicID", recyclerViewAdapter.getPatientList().get(position).getClinicID());//수정
                    intent.putExtra("patientName", recyclerViewAdapter.getPatientList().get(position).getPatientName());
                    intent.putExtra("task", "Spiral");
                    startActivity(intent);
                    getActivity().finish();
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

        //patientTotal.setText(String.valueOf("Total "+PatientLoad()));


    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void addPatient(){// * 환자 추가
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

                    File path = Environment.getExternalStoragePublicDirectory(// * 입력한 id로 폴더 생성
                            "/TremorApp/"+clinic_id);
                    Log.v("PatintList", "환자 id"+clinic_id);
                    if (!path.mkdirs()) {// * 같은 폴더 있을 시에 다시 등록 요청.
                        Toast.makeText(getActivity(), "동일한 User ID가 존재합니다.\nid를 변경하여 등록하세요.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Log.v("PatintList", "환자 추가"+clinic_id);
                        StringBuilder patient = new StringBuilder();// * 파일 내용 저장하기 위한 변수
                        patient.append("Clinic_ID,Name,Count,FirstDate,FinalDate");// * 파일 내용 첫 줄
                        patient.append("\n"+clinic_id+","+patient_name+","+null+","+null+","+null);// * 두번 째 줄에 정보 들어가도록

                        File patientcsv = new File(path, "patient.csv") ;// * path에 csv 파일 생성

                        try{
                            FileWriter write = new FileWriter(patientcsv, false);// * 생성한 파일에 입력한 내용 입력되도록
                            PrintWriter csv = new PrintWriter(write);
                            csv.println(patient);
                            csv.close();
                            patientList.add(new PatientItem(clinic_id, patient_name, null, null, false));// * list에도 추가

                            Environment.getExternalStoragePublicDirectory(// * 생성한 사용자 id 폴더에 각각 test 폴더 추가
                                    "/TremorApp/"+clinic_id+"/SpiralLeft").mkdir();
                            Environment.getExternalStoragePublicDirectory(
                                    "/TremorApp/"+clinic_id+"/SpiralRight").mkdir();
                            Environment.getExternalStoragePublicDirectory(
                                    "/TremorApp/"+clinic_id+"/LineLeft").mkdir();
                            Environment.getExternalStoragePublicDirectory(
                                    "/TremorApp/"+clinic_id+"/LineRight").mkdir();

                            recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), patientList, selected_patientList);
                            recyclerView.setAdapter(recyclerViewAdapter);

                            patient_num++;
                            ((PatientListActivity)getActivity()).patientNum(patient_num);// * toolbar 에 사용자 수 적용
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getActivity(), "사용자 추가", Toast.LENGTH_SHORT).show();

                    }

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

    public boolean getdeleteMode(){// * 삭제 모드에 대한 값 return

        Log.v("PatientList", "Patient Delete Mode"+deleteMode);
        return deleteMode;
    }

    public int PatientLoad() {// * 폴더에 있는 사용자 가져오기
        patientList = new ArrayList<PatientItem>() ;
        File path = Environment.getExternalStoragePublicDirectory(
                "/TremorApp");

        File directory = new File(String.valueOf(path)) ;
        File[] foder = directory.listFiles() ;
        if (foder!=null) {
            if(foder.length>1) {
                for (int i = 0; i < foder.length; i++) {
                    try {
                        String patientPath = String.valueOf(path) + "/" + foder[i].getName();
                        File patientCSV = new File(patientPath, "patient.csv");// * 사용자 id 마다 path를 가지고, patient.csv 파일 읽어서 정보 가져오기

                        BufferedReader buffer = new BufferedReader(new FileReader(patientCSV));
                        String str = buffer.readLine();
                        while (str != null) {
                            str = buffer.readLine();
                            String[] patientStr = str.split(",");// * 정보 array에 입력
                            patientList.add(new PatientItem(patientStr[0].replaceAll("\\\"", ""), patientStr[1].replaceAll("\\\"", ""), (patientStr[3].equals("null") ? null : patientStr[3].replaceAll("\\\"", "")), (patientStr[4].equals("null") ? null : patientStr[4].replaceAll("\\\"", "")), false));
                        }
                        buffer.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), patientList, selected_patientList);
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerViewAdapter.notifyDataSetChanged();  // data set changed
                patient_num = foder.length - 1;
                return foder.length - 1;
            }
            else{
                return 0;
            }
        }
        else{
            patient_num = 0;
            return 0 ;
        }
    }

    public void multi_select(int position) {// * 삭제 사용자 선택했을 시
        Log.v("RecyclerView","RRRRRssmullti_select"+deleteMode);
        if (deleteMode == true) {
            if (selected_patientList.contains(patientList.get(position))) {
                selected_patientList.remove(patientList.get(position));
                patientList.get(position).setDeleteBox(false);

            } else {
                selected_patientList.add(patientList.get(position));
                patientList.get(position).setDeleteBox(true);
            }
            if (selected_patientList.size() > 0) {
                ((PatientListActivity)getActivity()).selectNum("총 " + selected_patientList.size() + " 명의 사용자 선택");
            }
            else
                ((PatientListActivity)getActivity()).selectNum("총 0 명의 사용자 선택");
            recyclerViewAdapter.refreshAdapter(patientList, selected_patientList);

            if(selected_patientList.size()==patientList.size()){
                all_checkBox.setChecked(true);
            }
            else all_checkBox.setChecked(false);
        }
    }
    public boolean getDeleteMode(){
        return deleteMode;
    }
    public void delete_exit() {// * 삭제 모드 시작 or 종료
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
    public void patient_delete(){// * 환자 삭제
        for (int i = 0 ; i<selected_patientList.size() ;i++) {
            File source = Environment.getExternalStoragePublicDirectory(
                    "/TremorApp/"+selected_patientList.get(i).getClinicID());
            File dest = Environment.getExternalStoragePublicDirectory(
                    "/TremorApp/RemovePatient/"+selected_patientList.get(i).getClinicID());
            try {// * 삭제한 환자는 폴더도 삭제 하나, RemovePatient에 해당 환자 폴더가 복사돼서 데이터는 남겨질 수 있도록.
                FileUtils.copyDirectory(source, dest);// * directory copy
                File[] deleteList = source.listFiles();
                for(File file : deleteList) {// * 폴더 완전히 삭제
                    if(file.listFiles()!=null && file.listFiles().length!=0) {
                        for (File inner_file : file.listFiles()) {
                            inner_file.delete();
                        }

                    }
                    file.delete();
                }
                source.delete();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        delete_exit();
        ((PatientListActivity)getActivity()).patientNum(PatientLoad());
    }
    public void removeList(String clinicID) {

    }
}
