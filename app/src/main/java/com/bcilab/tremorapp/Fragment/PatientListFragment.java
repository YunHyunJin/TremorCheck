package com.bcilab.tremorapp.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.bcilab.tremorapp.Adapter.RecyclerItemClickListener;
import com.bcilab.tremorapp.Adapter.RecyclerViewAdapter;
import com.bcilab.tremorapp.Data.PatientItem;
import com.bcilab.tremorapp.PersonalPatientActivity;
import com.bcilab.tremorapp.R;

import java.util.ArrayList;

public class PatientListFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_list, container, false);

        final ArrayList<PatientItem> patientList = new ArrayList<>();
        ArrayList<PatientItem> selected_patientList = new ArrayList<>();
        final RecyclerViewAdapter recyclerViewAdapter;
        final RecyclerView recyclerView;
        final EditText searchPatient;
        RelativeLayout patientListL;
        Button addPatient ;
        final InputMethodManager[] imm = new InputMethodManager[1];
        recyclerView = view.findViewById(R.id.patientList);
        searchPatient = (EditText) view.findViewById(R.id.searchPatient);
        patientListL = (RelativeLayout)view.findViewById(R.id.patientListL);
        addPatient = (Button)view.findViewById(R.id.patientAdd);


        patientListL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm[0] = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                imm[0].hideSoftInputFromWindow(searchPatient.getWindowToken(), 0);
            }
        });

        searchPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm[0].showSoftInput(searchPatient, 0);
            }
        });

        addPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPatient();
            }
        });
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), PersonalPatientActivity.class);
                intent.putExtra("clinicID", patientList.get(position).getClinicID());//수정
                intent.putExtra("patientName", patientList.get(position).getPatientName());
                intent.putExtra("task", "UPDRS");
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        patientList.add(new PatientItem("752", "하하하재경", "20.07.02", "20.08.02",  true));
        patientList.add(new PatientItem("21600752752", "하하하하하재경", "20.07.02", "20.08.02",  true));
        patientList.add(new PatientItem("21600752752", "하재경", "20.07.02", "20.08.02",  true));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), patientList, selected_patientList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();  // data set changed
        return view;
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

}
