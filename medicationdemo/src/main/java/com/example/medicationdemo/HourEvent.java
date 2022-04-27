/*
    An hour in the calender will have a list of MedEvents (a list of all the records of medication
    that the user has taken)
 */
package com.example.medicationdemo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

class HourEvent
{
    String time;
    ArrayList<MedEvent> medEvents;

    public HourEvent(String time, ArrayList<MedEvent> medEvents)
    {
        this.time = time;
        this.medEvents = medEvents;
    }

    public String getTime()
    {
        return time;
    }

    public String getTimeString(){
        return new SimpleDateFormat("h:mm a").format(this.time);
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public ArrayList<MedEvent> getEvents()
    {
        return medEvents;
    }

    public void addMedEvent(MedEvent medEvent){
        this.medEvents.add(medEvent);
    }

    public void setEvents(ArrayList<MedEvent> events)
    {
        this.medEvents = events;
    }
}