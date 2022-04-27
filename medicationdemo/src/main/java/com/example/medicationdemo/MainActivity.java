/*
    Medication Demo
    -----------------------------------------------------------------------------------------
    Allows users to view all of their medications (not care pathway specific) and manually add
    a record of when they have taken their medications

    The robot can answer user questions such as "How often can I use my inhaler?", "How many pills do
    I take?", "How long do I need to take this medication", etc. The robot can also assist in adding
    a record of when the patient has taken their medication. If a calendar was implemented based off
    the medications' frequencies, the robot can also be used to remind users when to take their
    medications.

 */
package com.example.medicationdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.asus.robotframework.API.DialogSystem;
import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.robotframework.API.RobotUtil;
import com.asus.robotframework.API.SpeakConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// chang this to extends Activity
public class MainActivity extends Activity {

    public RobotAPI robotAPI;
    public static RobotAPI clbkRobotAPI;
    public static RobotCallback robotCallback = new RobotCallback() {
        @Override
        public void onResult(int cmd, int serial, RobotErrorCode err_code, Bundle result) {
            super.onResult(cmd, serial, err_code, result);
        }

        @Override
        public void onStateChange(int cmd, int serial, RobotErrorCode err_code, RobotCmdState state) {
            super.onStateChange(cmd, serial, err_code, state);
        }

    };

    public static RobotCallback.Listen robotListenCallback = new RobotCallback.Listen() {

        @Override
        public void onFinishRegister() {

        }
        @Override
        public void onVoiceDetect(JSONObject jsonObject) {

        }
        @Override
        public void onSpeakComplete(String s, String s1) {

        }
        @Override
        public void onEventUserUtterance(JSONObject jsonObject) {


        }

        @Override
        public void onResult(JSONObject jsonObject) {

            String inputContext = RobotUtil.queryListenResultJson(jsonObject,
                                                                "input_context");
            String intentionID = RobotUtil.queryListenResultJson(jsonObject,
                                                                "IntentionId");
            final String NO_MED_NAME = "noGivenMedName";

            if (intentionID.equals("forgotDuration")) { //How long do I take advil for?
                String userMedName = RobotUtil.queryListenResultJson(jsonObject, "MedicationName");

                if (userMedName.equals(NO_MED_NAME)){ // User didn't provide a medication name

                    /* If there is more than one medication in the array
                        Prompt user to trigger medNameOrAllPlan's intent */
                    if (userMedication.size() > 1){
                        questionType = userQuestionType.DURATION;
                        clbkRobotAPI.robot.speakAndListen("Can you specify which medication you're talking about or would you like me to provide your duration information for all of them", new SpeakConfig().timeout(20)) ;
                        // else if there is only one medication in the list
                    } else if (userMedication.size() > 0)  {
                        // Say the duration information of that medication
                        Medication specifiedMedication = userMedication.get(0);
                        clbkRobotAPI.robot.speak(specifiedMedication.sayDuration());
                    }
                    // else if the given name is a valid medication in the array
                } else if (!userMedName.equals(NO_MED_NAME) && (foundMedication(userMedName)) > -1){
                    // Find the med name in the list and say its duration information
                    Medication specifiedMedication = userMedication.get(foundMedication((userMedName)));
                    clbkRobotAPI.robot.speak(specifiedMedication.sayDuration());

                } else {
                    clbkRobotAPI.robot.speak("You don't seem to have any added medication");

                }
            } else if (intentionID.equals("forgotDosage")) { //How many pills do I take?
                String userMedName = RobotUtil.queryListenResultJson(jsonObject, "MedicationName");

                if (userMedName.equals(NO_MED_NAME)){

                    if (userMedication.size() > 1){
                        questionType = userQuestionType.DOSAGE;
                        clbkRobotAPI.robot.speakAndListen("Can you specify which medication you're talking about or would you like me to provide your dosage information for all of them", new SpeakConfig().timeout(20)) ;
                    } else if (userMedication.size() > 0)  {
                        Medication specifiedMedication = userMedication.get(0);
                        clbkRobotAPI.robot.speak(specifiedMedication.sayDosage());
                    }

                } else if (!userMedName.equals(NO_MED_NAME) && (foundMedication(userMedName) > -1)){
                    //find the med name in the list
                    Medication specifiedMedication = userMedication.get(foundMedication((userMedName)));
                    clbkRobotAPI.robot.speak(specifiedMedication.sayDosage());

                } else {
                    clbkRobotAPI.robot.speak("You don't seem to have any added medication");

                }
            } else if (intentionID.equals("forgotFreq")) { //How Often do I take my pills?
                String userMedName = RobotUtil.queryListenResultJson(jsonObject,
                                                        "MedicationName");

                if (userMedName.equals(NO_MED_NAME)){ // User didn't specify name
                    /* If there is more than one medication in the array
                        Prompt user to trigger medNameOrAllPlan's intent */
                    if (userMedication.size() > 1){
                        questionType = userQuestionType.FREQUENCY;
                        clbkRobotAPI.robot.speakAndListen("Can you specify which " +
                                "medication you're talking about or would you like me to " +
                                "provide your frequency information for all of them",
                                new SpeakConfig().timeout(20)) ;
                    // else if there is only one medication
                    } else if (userMedication.size() > 0)  {
                        Medication specifiedMedication = userMedication.get(0);
                        // Say the frequency information for that medication
                        clbkRobotAPI.robot.speak(specifiedMedication.sayFrequency());
                    }

                  // else if the given name is a valid medication in the array
                } else if (!userMedName.equals(NO_MED_NAME) && (foundMedication(userMedName)) > -1){
                    // Find the med name in the list and say its frequency information
                    Medication specifiedMedication = userMedication
                                                    .get(foundMedication((userMedName)));
                    clbkRobotAPI.robot.speak(specifiedMedication.sayFrequency());

                } else {
                    clbkRobotAPI.robot.speak("You don't seem to have any added medication");

                }
            } else if (intentionID.equals("medNameOrAllPlan")){
                /* For this plan, userMedName will either be a medication name or "all"
                    if the user says "all", this will default to NO_MED_NAME */
                String userMedName = RobotUtil.queryListenResultJson(jsonObject, "userMedName");
                /* user wants information on all the medications
                    or robot couldn't find medication based on given medication name  */
                if (userMedName.equals(NO_MED_NAME) || (foundMedication(userMedName)) < 0){
                    switch (questionType){
                        case DURATION:
                            for (int i = 0; i < userMedication.size(); i++) {
                                if (i == userMedication.size() - 1){
                                    clbkRobotAPI.robot.speak("and ");
                                }
                                clbkRobotAPI.robot.speak(userMedication.get(i).sayDuration());
                            }
                            break;
                        case DOSAGE:
                            for (int i = 0; i < userMedication.size(); i++) {
                                if (i == userMedication.size() - 1){
                                    clbkRobotAPI.robot.speak("and ");
                                }
                                clbkRobotAPI.robot.speak(userMedication.get(i).sayDosage());
                            }
                            break;
                        case FREQUENCY:
                            for (int i = 0; i < userMedication.size(); i++) {
                                if (i == userMedication.size() - 1){
                                    clbkRobotAPI.robot.speak("and ");
                                }
                                clbkRobotAPI.robot.speak(userMedication.get(i).sayDosage());
                            }
                        default:
                            clbkRobotAPI.robot.speakAndListen("What question about your medication do you have?", new SpeakConfig().timeout(20));
                            clbkRobotAPI.robot.jumpToPlan(DOMAIN, "launchAppPlan");

                    }

                } else {
                    Medication specifiedMedication = userMedication.get(foundMedication((userMedName)));
                    switch (questionType){
                        case DURATION:
                            clbkRobotAPI.robot.speak(specifiedMedication.sayDuration());
                            break;
                        case DOSAGE:
                            clbkRobotAPI.robot.speak(specifiedMedication.sayDosage());
                            break;
                        case FREQUENCY:
                            clbkRobotAPI.robot.speak(specifiedMedication.sayFrequency());
                        default:
                            clbkRobotAPI.robot.speakAndListen("What question about your medication do you have?", new SpeakConfig().timeout(20));
                            clbkRobotAPI.robot.jumpToPlan(DOMAIN, "launchAppPlan");

                    }

                }

            // If user intends to add record of taking medication
            } else if (intentionID.equals("addMedication")){
                String userMedName = RobotUtil.queryListenResultJson(jsonObject,
                        "MedicationName");
                // If no medication name is specified, prompt to trigger getMedName plan's intent
                if (userMedName.equals(NO_MED_NAME)){
                    clbkRobotAPI.robot.speakAndListen("That's Great! " +
                            "What medication did you take?", new SpeakConfig().timeout(20));
                    //Else if medication name is found in the array
                } else if (foundMedication(userMedName) > -1 ){

                    addedMedicationName = userMedication.get(foundMedication(userMedName))
                                                        .getMedicationName();
                    /* Going to the Add Medication Record Activity
                      by calling the Add Record button's click */
                    medEventAdd.callOnClick();

                } else {
                    clbkRobotAPI.robot.speak("I can't seem to find a listed medication " +
                            "under that name. Please try again or press the view tab to see your " +
                            "listed medications");
                }

            }
            // Getting the name of medication that the user took
            else if (intentionID.equals("getMedName")){
                String userMedName = RobotUtil.queryListenResultJson(jsonObject, "userMedName");
                // If that name exists in the array
                if (foundMedication(userMedName) > -1){
                    // Going to addMedicationActivity
                    clbkRobotAPI.robot.speak("okay!!");
                    addedMedicationName = userMedication.get(foundMedication(userMedName)).getMedicationName();
                    medEventAdd.callOnClick();
                }

            }



        }

        @Override
        public void onRetry(JSONObject jsonObject) {

        }
    };

    public final static String TAG = "MedicationDemo";
    public final static String DOMAIN = "CC78BD2081914E17BAD293B2D0974CB9";
    public final int VIEW_MED_REQ = 22;
    public static final int ADD_MED_REQ = 42;
    ArrayList<HourEvent> hourList = new ArrayList<>();
    private static List<Medication> userMedication = new ArrayList<Medication>();
    public enum userQuestionType {DURATION, DOSAGE, FREQUENCY, UNKNOWN};
    private static userQuestionType questionType = userQuestionType.UNKNOWN;



    static ImageButton  medEventAdd, questionAsk;
    Button medViewButton;
    private TextView monthText;
    private TextView dayOfWeek;
    private TextView date;
    private ListView hourListView;
    private static String addedMedicationName = "noGivenMedName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        robotAPI = new RobotAPI(getApplicationContext(), robotCallback);

        medEventAdd = findViewById(R.id.EventAddButton);
        questionAsk = findViewById(R.id.userQuestionButton);
        medViewButton = findViewById(R.id.medEventViewButton);
        monthText = findViewById(R.id.monthText);
        dayOfWeek = findViewById(R.id.dayOfWeek);
        date = findViewById(R.id.date);
        hourListView = findViewById(R.id.hourListView);

        // Adding Button Feature
        questionAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robotAPI.robot.speakAndListen("How can I help you.",
                                            new SpeakConfig().timeout(20));
                robotAPI.robot.jumpToPlan(DOMAIN, "launchAppPlan");

            }
        });


        // Adding a test Medication
        userMedication.add(new Medication("Advil", "by mouth", "One Tablet", "tablet", "once a day", "three days"));
        userMedication.add(new Medication("GENERIC RX", "Review with Label, Pharmacy, or Doctor", "500MG ONE TABLET", "TABLET", "ONCE DAILY", "Review with Label, Pharmacy, or Doctor"));
        userMedication.add(new Medication("Advair", "by mouth", "one inhale", "disc", "every 12 hours", "Every day"));  // daily puffer
        userMedication.add(new Medication("Ventolin", "by mouth", "two puffs", "puff", "every four to six hours as needed, up to 3 times a day", "as needed")); // flare up med/rescue inhaler
        userMedication.add(new Medication("Amoxicillin", "by mouth", "two 500MG tablets", "TABLET", "three times a day", "five days when needed")); // antibiotic
        userMedication.add(new Medication("Prednisone", "by mouth", "one 50MG pill", "TABLET", "Once daily", "ten days when needed")); // prednesone for short of breath
        userMedication.add(new Medication("Lipitor", "by mouth", "one 20MG pill", "TABLET", "Once daily", "Every day")); // prednesone for short of breath




        addHours();

        // Adding a test Medication record -> advil was taken at 9:am
        hourList.get(9).addMedEvent(new MedEvent("Advil", "2 tablets", "9:00 AM"));

        robotAPI.robot.speak("It's time to take your medication! Let me know when you're done so I can record it");
        robotAPI.robot.speak("If you need to update your medication information, go to the View tab");
        robotAPI.robot.speak("Let me know if you have any questions.");
        robotAPI.robot.jumpToPlan(DOMAIN, "launchAppPlan");
    }

    @Override
    protected void onResume() {
        super.onResume();
        DayUtil.currentDate = Calendar.getInstance();
        monthText.setText(DayUtil.getMonthAndYear(DayUtil.currentDate));
        dayOfWeek.setText(DayUtil.getWeekDay(DayUtil.currentDate));
        date.setText(DayUtil.getDate(DayUtil.currentDate));
        // update HourList
        HourAdapter hourAdapter = new HourAdapter(getApplicationContext(), hourList);
        hourListView.setAdapter(hourAdapter);


        /*
            Importing the patient's medication to the dialog server at run time. This way, the robot
            can recognize the patient's specific medication.

            The medication is currently being imported from a hard coded array of Medication objects
            (userMedication) which have information like name, duration, frequency, etc,
            but can be taken from a database where prescriptions can be stored
         */

        // Creating a JSON array of the medication names
        JSONArray medicationNameUpdate = new JSONArray();
        for (int i = 0 ; i < userMedication.size(); i ++){
            medicationNameUpdate.put(userMedication.get(i).getMedicationName());
        }

        /* Sending the array of names to the dialog server
            userMedicationName is the entity group defined in the DDE project  */
        robotAPI.robot.dynamicEditInstance(DOMAIN, DialogSystem.DynamicEditAction.updateNewInstance,
                                        "userMedicationName",medicationNameUpdate);



        robotAPI.robot.registerListenCallback(robotListenCallback);

        clbkRobotAPI = robotAPI;

    }

    // Initializes the array that holds the events for each hour
    private void addHours() {

        for (int hour = 0; hour < 24; hour++) {
            String time = DayUtil.getHourTime(hour);
            ArrayList<MedEvent> events = new ArrayList<>();
            HourEvent hourEvent = new HourEvent(time, events);
            hourList.add(hourEvent);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        robotAPI.robot.unregisterListenCallback();
        //stop listen user utterance
        robotAPI.robot.stopSpeakAndListen();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        robotAPI.release();
    }



    // Helper function to find medication based on medication name
    private static int foundMedication(String MedicationName) {
        for (int i = 0; i < userMedication.size(); i++) {
            if (userMedication.get(i).getMedicationName().equalsIgnoreCase(MedicationName)) {
                return i;
            }
        }
        return -1;
    }

    // When View button is pressed
    public void startView(View view) {
        Intent i = new Intent(MainActivity.this, MedViewActivity.class);
        /* Main Activity will pass over the userMedication array
             Ideally, we would just be able to take the medication from a database in
             the MedView Activity instead of having to pass it over */
        i.putExtra("userMedication", (Serializable) userMedication);
        // Starting MedView Activity
        startActivityForResult(i, VIEW_MED_REQ);
    }

    // When user wants to add medication record
    public void addMedicationPressed(View view) {
        addMedRecord(addedMedicationName);
        /* Here we can perform Med Checks to ensure
           user took the correct dosage, didn't take more than freq, etc */
    }

    private void addMedRecord(String addedMedicationName){
        Intent i = new Intent(MainActivity.this,
                                AddMedicationActivity.class);
        i.putExtra("userMedication", (Serializable) userMedication);
        /* addedMedicationName is automatically NO_MED_NAME
           unless user adds medication record via dialog
           (where they are prompted to specify the name) */
        i.putExtra("addedMedication", addedMedicationName);
        startActivityForResult(i, ADD_MED_REQ);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VIEW_MED_REQ) {
            if (resultCode == Activity.RESULT_OK) {
                /*
                 Ideally medication would be able to be added in MedView Activity.
                 the following lines update the user's medication as a result of this activity.
                 Later on it can be replaced by getting the medication from a database
                List<Medication> updatedUserMeds = (List<Medication>)
                                                    data.getSerializableExtra("updatedUserMeds");
                userMedication = updatedUserMeds;
                */
            }
        } else if (requestCode == ADD_MED_REQ) {
            if (resultCode == Activity.RESULT_OK) {

                // Update Schedule with the medication that was added
                MedEvent medicationRecord = new MedEvent(data.getStringExtra("addedMedName"),
                                                           data.getStringExtra("addedDosage"),
                                                           data.getStringExtra("addedTime"));
                hourList.get(medicationRecord.getHourTaken()).addMedEvent(medicationRecord);
                // Resets the name of the medication to add once it has been added
                addedMedicationName = "noGivenMedName";

            }
        }
    }
}

/*

 */