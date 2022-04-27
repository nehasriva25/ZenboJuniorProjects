/*
    AddMedicationActivity is used to add a record of taking a medication
 */
package com.example.medicationdemo;

import static androidx.recyclerview.widget.StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS;
import static androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.robotframework.API.RobotUtil;
import com.asus.robotframework.API.SpeakConfig;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddMedicationActivity extends AppCompatActivity implements AddMedAdapter.ClickInterface{

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

            String intentionID = RobotUtil.queryListenResultJson(jsonObject, "IntentionId");

//            final String NO_MED_NAME = "noGivenMedName";


            if (intentionID.equals("confirmAddMed")){
                clbkRobotAPI.robot.speak("All Right, I've added your medication");
               confirmButton.callOnClick();

            }

        }

        @Override
        public void onRetry(JSONObject jsonObject) {

        }
    };
    private static List<Medication> userMedication = new ArrayList<Medication>();
    private final String NO_MED_NAME = "noGivenMedName";
    public final int ADD_MEDICATION_REQ = 55;

    private static RecyclerView recyclerView;

    private AddMedAdapter addMedAdapter;
    private StaggeredGridLayoutManager layoutManager;
    static Button backButton, confirmButton;
    TextView medNameShow, dosageShow, timeShow;

    private String getAddedMedicationName;

    public final static String TAG = "MedicationDemo";
    public final static String DOMAIN = "CC78BD2081914E17BAD293B2D0974CB9";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        robotAPI = new RobotAPI(getApplicationContext(), robotCallback);
        // Getting medication array and the medication name
        // (if there is one)
        userMedication = (List<Medication>)
                getIntent().getSerializableExtra("userMedication");
        getAddedMedicationName = getIntent().getStringExtra("addedMedication");
        // If user is manually adding a record
        if (getAddedMedicationName.equals(NO_MED_NAME)){
            //start Initial set up
            setContentView(R.layout.add_med_view);
            startInitialSetUp();
        // Else if the medication name can be found in the array
        } else if (foundMedication(getAddedMedicationName) > -1){
            //start results setup and go to the confirmation layout
            setContentView(R.layout.result_show);
            startResultsSetUp(userMedication.get(foundMedication(getAddedMedicationName))
                    .getMedicationName(),
                    userMedication.get(foundMedication(getAddedMedicationName)).getDosage());
            robotAPI.robot.speakAndListen("Can you confirm that you took the correct " +
                    "dosage of " + getAddedMedicationName + " at this time? ",
                    new SpeakConfig().timeout(20));
            robotAPI.robot.jumpToPlan(DOMAIN, "getMedName");


        }



    }

    private void startInitialSetUp(){
        recyclerView = findViewById(R.id.starting_view);
        backButton = findViewById(R.id.backToCalender);

        layoutManager = new StaggeredGridLayoutManager(1, VERTICAL);
        layoutManager.setGapStrategy(GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        addMedAdapter = new AddMedAdapter(userMedication, this, this::onTaskClick);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(addMedAdapter);

    }

    private void startResultsSetUp(String medName, String dosage){
        backButton = findViewById(R.id.backButton);
        confirmButton = findViewById(R.id.confirm);


        // Allows user to choose a different medication to record
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.add_med_view);
                startInitialSetUp();

            }
        });
        // Will return to Main Activity with the added medication information
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("addedMedName", medNameShow.getText());
                returnIntent.putExtra("addedDosage", dosageShow.getText());
                returnIntent.putExtra("addedTime", timeShow.getText());
                setResult(Activity.RESULT_OK,returnIntent);
                //push and return intent
                finish();
            }
        });
        medNameShow = findViewById(R.id.medNameShow);
        dosageShow = findViewById(R.id.dosageShow);
        timeShow = findViewById(R.id.timeShow);

        medNameShow.setText(medName);
        dosageShow.setText(dosage);

        timeShow.setText(DayUtil.getHourMinute(DayUtil.currentDate));

    }

    @Override
    protected void onResume(){
        super.onResume();
        robotAPI.robot.registerListenCallback(robotListenCallback); //???

        clbkRobotAPI = robotAPI;
    }

    public void endView(View view) {
        finish();
    }

    public void confirmAddedMed (View view){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("addedMedName", medNameShow.getText());
        returnIntent.putExtra("addedDosage", dosageShow.getText());
        returnIntent.putExtra("addedTime", timeShow.getText());
        setResult(Activity.RESULT_OK,returnIntent);
        //push and return intent
        finish();

    }

    // When user clicks the medication they took
    @Override
    public void onTaskClick(int position) {
        setContentView(R.layout.result_show);
        startResultsSetUp(userMedication.get(position).getMedicationName(),
                            userMedication.get(position).getDosage());

    }

    private static int foundMedication(String MedicationName) {
        for (int i = 0; i < userMedication.size(); i++) {
            if (userMedication.get(i).getMedicationName().equalsIgnoreCase(MedicationName)) {
                return i;
            }
        }
        return -1;
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






}
