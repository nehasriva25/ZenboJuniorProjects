/*
    Adapter displays MedEvents in each hour. This can be implemented in each hour but hasn't been
    used yet
 */

package com.example.medicationdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class MedEventAdapter extends ArrayAdapter<MedEvent>
{
    public MedEventAdapter(@NonNull Context context, List<MedEvent> events)
    {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        MedEvent event = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.med_event_view, parent, false);

        TextView medEventText = convertView.findViewById(R.id.medEventText);

        String eventTitle = event.getMedicationName();
        medEventText.setText(eventTitle);
        return convertView;
    }
}