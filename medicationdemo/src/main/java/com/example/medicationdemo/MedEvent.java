/*
    A MedEvent is a record of a medication that the user has taken. It will contain information on
    the medication, dosage, and time the medication was taken
 */
package com.example.medicationdemo;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MedEvent {

    private String medicationName;
    private String dosage;
    private String timeTaken;

    public MedEvent(String medicationName, String dosage, String timeTaken) {
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.timeTaken = timeTaken;
    }
    // where date is DayUtil.currentDate.getTime()
    public MedEvent(String medicationName, String dosage, Date time) {
        this.medicationName = medicationName;
        this.dosage = dosage;
        SimpleDateFormat format = new SimpleDateFormat("HH:mm a");
        this.timeTaken = format.format(time);
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getTimeTaken() {
        return timeTaken;
    }

    public int getHourTaken(){
        return Integer.parseInt(timeTaken.split(":")[0]);
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }
}
