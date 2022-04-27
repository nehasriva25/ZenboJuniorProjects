/*
    Each Medication will include information on the name, route of administration, form, dosage,
    duration, and frequency
 */
package com.example.medicationdemo;


import java.io.Serializable;

public class Medication implements Serializable {

    private String medicationName;
    private String routeOfAdministration;
    private String dosage;
    private String form;
    private String frequency;
    private String duration;
    private final String NAME_UNKNOWN = "Could not find Medication Name: Review with Label, Pharmacy, or Doctor";
    private final String UNKNOWN_VALUE = "Review with Label, Pharmacy, or Doctor";
    private final String EVERY_DAY = "Every day";
    public Medication(){

    }

    public Medication(String medicationName, String roa, String dosage, String form, String frequency, String duration) {
        this.medicationName = medicationName;
        if (medicationName.equals(UNKNOWN_VALUE)){
            this.medicationName = NAME_UNKNOWN;
        }
        this.routeOfAdministration = roa;
        this.dosage = dosage;
        this.form = form;
        this.frequency = frequency;
        this.duration = duration;
    }


    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getRouteOfAdministration() {
        return routeOfAdministration;
    }

    public void setRouteOfAdministration(String routeOfAdministration) {
        this.routeOfAdministration = routeOfAdministration;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }


    // Formats how robot will convey the dosage information about the specific medication
    public String sayDosage(){
        String medName = this.medicationName;

        if (medName.equals(NAME_UNKNOWN)){
            medName = "An unknown medication";
        } else {
            medName = "your " + this.medicationName;
        }

        if(this.dosage.equals(UNKNOWN_VALUE)){
            return "Please " + UNKNOWN_VALUE + " for the dosage of " + medName + ". ";

        } else {
            return "Take " + this.dosage + " of " + medName + ". ";
        }

    }

    public String sayDuration(){
        String medName = this.medicationName;

        if (medName.equals(NAME_UNKNOWN)){
            medName = "An unknown medication";
        } else {
            medName = "your " + this.medicationName;
        }

        if(this.duration.equals(UNKNOWN_VALUE)){
            return "Please " + UNKNOWN_VALUE + " for the duration of " + medName + ". ";

        } else if (this.duration.equals(EVERY_DAY) || this.duration.equals("as needed")){
            return "Take " + medName + " " + this.duration + ". ";

        }else  {
            return "Take " + medName + " for " + this.duration + ". ";
        }

    }

    public String sayFrequency(){
        String medName = this.medicationName;

        if (medName.equals(NAME_UNKNOWN)){
            medName = "An unknown medication";
        } else {
            medName = "your " + this.medicationName;
        }

        if(this.frequency.equals(UNKNOWN_VALUE)){
            return "Please " + UNKNOWN_VALUE + " for how often you should take " + medName + ". ";

        } else {
            return "Take " + medName + " for " + this.frequency + ". ";
        }

    }
}
