/*
    Each measurement task will have the name (temperature, etc), a list of the user's results,
     and a button used to start recording a new vitals result
 */
package com.example.healthreadingsdemo;

import android.widget.Button;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MeasurementTask {

    private String taskName;
    private List<String> results;
    private List<Integer> resultId;
    private Button startTask;

    public MeasurementTask(String taskName) {
        this.taskName = taskName;
        this.results = new ArrayList<String>();
        this.resultId = resultId;
        this.startTask = startTask;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }



    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }

    public List<Integer> getResultId() {
        return resultId;
    }

    public void setResultId(List<Integer> resultId) {
        this.resultId = resultId;
    }

    public Button getStartTask() {
        return startTask;
    }

    public void setStartTask(Button startTask) {
        this.startTask = startTask;
    }


}
