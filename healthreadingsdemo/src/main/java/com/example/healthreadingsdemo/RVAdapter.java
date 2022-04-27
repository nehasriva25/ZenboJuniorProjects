/*
    Adapter is used to display the different measurement tasks in a RecyclerView
 */
package com.example.healthreadingsdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    private List<MeasurementTask> taskArrayList;
    private Context context;
    private TaskClickInterface taskClickInterface;


    public RVAdapter(List<MeasurementTask> taskArrayList, Context context , TaskClickInterface taskClickInterface) {
        this.taskArrayList = taskArrayList;
        this.context = context;
        this.taskClickInterface = taskClickInterface;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        // Set the data to textview and imageview.
        MeasurementTask measurementTask = taskArrayList.get(position);
        holder.taskName = taskArrayList.get(position).getTaskName();
        holder.startTask.setText(holder.taskName);
        ResultsView listView = holder.itemView.findViewById(R.id.results_list);

        listView.setAdapter(new ArrayAdapter<String>(context.getApplicationContext(),
                R.layout.result_view_on_card, measurementTask.getResults()));

        holder.startTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskClickInterface.onTaskClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        // this method returns the size of recyclerview
        return taskArrayList.size();
    }

    // View Holder Class to handle Recycler View.
    public class ViewHolder extends RecyclerView.ViewHolder {

        private Button startTask;
        private String taskName;
        private ResultsView listView;
//        private TextView dateHeading, resultsHeading;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            startTask = itemView.findViewById(R.id.start_task);

            listView  = itemView.findViewById(R.id.results_list);

        }
    }

    public interface TaskClickInterface {
        void onTaskClick(int position);
    }
}