package com.bcilab.tremorapp.Adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcilab.tremorapp.Data.TaskItem;
import com.bcilab.tremorapp.R;
import com.bcilab.tremorapp.ResultActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class TaskListViewAdapter extends RecyclerView.Adapter<TaskListViewAdapter.MyViewHolder>{
    public ArrayList<TaskItem> taskList = new ArrayList<>();
    public ArrayList<TaskItem> selected_taskList = new ArrayList<>() ;
    Menu context_menu;
    Context mContext;
    RequestManager mGlideRequestManager;
    public TaskListViewAdapter(Context context, ArrayList<TaskItem> taskList, ArrayList<TaskItem> selected_taskList){
        this.mContext = context;
        this.taskList = taskList;
        this.selected_taskList = selected_taskList ;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView taskNum;
        TextView taskDate;
        ImageView imageView;
        TextView taskHandside;
        LinearLayout ta_listitem;

        public MyViewHolder(View itemView) {
            super(itemView);
            taskNum = (TextView) itemView.findViewById(R.id.taskNum);
            taskDate = (TextView) itemView.findViewById(R.id.taskDate);
            taskHandside = (TextView) itemView.findViewById(R.id.taskHandside);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            ta_listitem = (LinearLayout) itemView.findViewById(R.id.ta_listitem2);
        }
    }

    @Override
    public TaskListViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View itemView = LayoutInflater.from(mContext).inflate(R.layout.task_list, parent, false);
        final MyViewHolder vHolder = new MyViewHolder(itemView) ;
        return vHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        TaskItem data = taskList.get(position);
        holder.taskNum.setText(data.getTaskNum());
        holder.taskDate.setText(data.getTaskDate());
        holder.taskHandside.setText(data.getTaskHandside());
        Glide.with(mContext).load(data.getTaskImage()).into(holder.imageView);
        if (selected_taskList.contains(taskList.get(position)))
            holder.ta_listitem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_selected_state));
        else
            holder.ta_listitem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_normal_state));

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void clear() {
        int size = taskList.size() ;
        taskList.clear() ;
        notifyItemRangeRemoved(0, size);
    }


    //어댑터 정비
    public void refreshAdapter() {
        this.selected_taskList = selected_taskList;
        this.taskList = taskList;
        this.notifyDataSetChanged();
    }


    public void removeList(int position) {

    }

    public void TaskNo(final int position) {

    }
}
