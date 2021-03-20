package com.bcilab.tremorapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bcilab.tremorapp.Data.PatientItem;
import com.bcilab.tremorapp.Fragment.PatientListFragment;
import com.bcilab.tremorapp.R;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private Activity activity;
    private ArrayList<PatientItem> patientList = new ArrayList<>();
    private ArrayList<PatientItem> selected_patientList = new ArrayList<>();
    private Context mContext;
    private boolean checkboxIsVisible = false ;

    public RecyclerViewAdapter(Context context, ArrayList<PatientItem> patientList, ArrayList<PatientItem> selected_patientList) {
        this.mContext = context;
        this.patientList = patientList;
        this.selected_patientList = selected_patientList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView clinicID;
        TextView patientName;
        TextView dateFirst;
        TextView dateFinal;
        CheckBox checkBox ;
        ConstraintLayout cl_listitem;

        public MyViewHolder(View itemView) {
            super(itemView);
            clinicID = (TextView) itemView.findViewById(R.id.clinicIDItem);
            patientName = (TextView) itemView.findViewById(R.id.patientNameItem);
            dateFirst = (TextView) itemView.findViewById(R.id.dateFirstItem);
            dateFinal = (TextView) itemView.findViewById(R.id.dateFinalItem);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox) ;
            cl_listitem = (ConstraintLayout) itemView.findViewById(R.id.cl_listitem);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_list_item, parent, false);

        final RecyclerViewAdapter.MyViewHolder vHolder = new RecyclerViewAdapter.MyViewHolder(itemView) ;
        return vHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PatientItem data = patientList.get(position);
        holder.clinicID.setText(data.getClinicID());
        holder.patientName.setText(data.getPatientName());
        holder.dateFirst.setText(data.getDateFirst());
        holder.dateFinal.setText(data.getDateFinal());
        holder.checkBox.setChecked(data.isDeleteBox());
        holder.checkBox.setVisibility(checkboxIsVisible? View.VISIBLE: View.GONE);

        PatientListFragment patientListFragment = new PatientListFragment();

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //set your object's last status
                Log.v("RecyclerView","RRRRRss");
                if(patientListFragment.getDeleteMode()==true){
                    Log.v("RecyclerView","RRRRR");
                    patientListFragment.multi_select(position);
                }
                else{
                    Log.v("RecyclerView","RRRRRs"+patientListFragment.getDeleteMode());
                    patientListFragment.multi_select(position);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public void clear() {
        int size = patientList.size() ;
        patientList.clear() ;
        notifyItemRangeRemoved(0, size);
    }


    //어댑터 정비
    public void refreshAdapter(ArrayList<PatientItem> patientList, ArrayList<PatientItem> selected_patientList) {
        this.selected_patientList = selected_patientList;
        this.patientList = patientList;
        this.notifyDataSetChanged();
    }
    public void visible(){
        checkboxIsVisible = true ;
    }

    public void novisible(){
        checkboxIsVisible = false ;
    }
    public ArrayList<PatientItem> getPatientList() {
        return patientList;
    }
}