package com.bcilab.tremorapp.Data;

public class TaskItem {

    String taskDate;
    String taskNum;
    String taskImage;
    String taskHandside;

    public TaskItem(String taskDate, String taskNum, String taskImage, String taskHandside) {
        this.taskDate = taskDate;
        this.taskNum = taskNum;
        this.taskImage = taskImage;
        this.taskHandside = taskHandside ;
    }

    public String getTaskDate() {
        return taskDate;
    }

    public String getTaskNum() {
        return taskNum;
    }

    public String getTaskImage() { return taskImage ; }

    public String getTaskHandside() { return taskHandside; }
}
