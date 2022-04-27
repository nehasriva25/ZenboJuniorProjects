/*
    Adapter is used to display the medications in a RecyclerView
 */
package com.example.medicationdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    private List<Medication> medsArrayList;
    private Context context;
    private ClickInterface clickInterface;


    //
    public RVAdapter(List<Medication> medsArrayList, Context context , ClickInterface clickInterface) {
        this.medsArrayList = medsArrayList;
        this.context = context;
        this.clickInterface = clickInterface;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.med_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        // Set the data to textview and imageview.
        Medication medication = medsArrayList.get(position);
        holder.medNameHeading.setText(medication.getMedicationName());
        holder.roa.setText(medication.getRouteOfAdministration());
        holder.dosage.setText(medication.getDosage());
        holder.form.setText(medication.getForm());
        holder.frequency.setText(medication.getFrequency());
        holder.duration.setText(medication.getDuration());
        holder.editMedInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickInterface.onTaskClick(position);

            }
        });





    }

    @Override
    public int getItemCount() {
        // this method returns the size of recyclerview
        return medsArrayList.size();
    }

    // View Holder Class to handle Recycler View.
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView medNameHeading, dosage, form, roa, frequency, duration;
        private Button editMedInfo;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            medNameHeading = itemView.findViewById(R.id.med_nameHeading);
            dosage = itemView.findViewById(R.id.dosage);
            form = itemView.findViewById(R.id.form);
            roa = itemView.findViewById(R.id.roa);
            frequency = itemView.findViewById(R.id.frequency);
            duration = itemView.findViewById(R.id.duration);
            editMedInfo = itemView.findViewById(R.id.editMedInfo);
        }
    }

    public interface ClickInterface {
        void onTaskClick(int position);
    }
}