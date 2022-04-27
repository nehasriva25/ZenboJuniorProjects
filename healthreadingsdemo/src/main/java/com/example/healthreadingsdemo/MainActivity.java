/*
    Vitals and Health Readings Demo
    -----------------------------------------------------------------------------------------
    Allows user to record their vitals data (temperature, blood pressure, blood sugar, etc) by
    showing the medical device to the robot. The user can also sign into their profile by using
    their health card.

    MainActivity displays the possible vitals data the user can record and shows the results with
    the date and time they were taken

 */

package com.example.healthreadingsdemo;

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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.robotframework.API.VisionConfig;
import com.asus.robotframework.API.results.DetectPersonResult;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RVAdapter.TaskClickInterface {

    public static RobotAPI robotAPI;
    public static RobotCallback robotCallback = new RobotCallback() {
        @Override
        public void onResult(int cmd, int serial, RobotErrorCode err_code, Bundle result) {
            super.onResult(cmd, serial, err_code, result);
        }

        @Override
        public void onStateChange(int cmd, int serial, RobotErrorCode err_code, RobotCmdState state) {
            super.onStateChange(cmd, serial, err_code, state);
        }


        @Override
        public void onDetectPersonResult(List<DetectPersonResult> resultList) {
            robotAPI.utility.lookAtUser(0.5f-(resultList.get(0).getBodyLocCam().x));

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

        }

        @Override
        public void onRetry(JSONObject jsonObject) {

        }
    };


    private String[] measurementType = new String[]{"Temperature", "Blood Pressure", "Blood Sugar"};
    private List<MeasurementTask> measurementTasks = new ArrayList<MeasurementTask>();

    private RecyclerView recyclerView;

    private RVAdapter rvAdapter;
    private StaggeredGridLayoutManager layoutManager;
    Button loginViewProfile, updateProfile;
    CardView profile;
    TextView name, healthCard;

    public final int CAMERA_REQ = 44;

    private Boolean userLoggedIn = false, profileShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        robotAPI = new RobotAPI(getApplicationContext(), robotCallback);
        recyclerView = findViewById(R.id.starting_view);
        loginViewProfile = findViewById(R.id.login_profile);
        profile = findViewById(R.id.profile_card);
        profile.setVisibility(View.GONE);
        name = findViewById(R.id.name);
        healthCard = findViewById(R.id.healthCard);
        updateProfile = findViewById(R.id.updateProfile);


        loginViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userLoggedIn){
                    Intent i = new Intent(MainActivity.this, CameraActivity.class);
                    i.putExtra("taskName", "Profile");
                    startActivityForResult(i, 44);
                } else {
                    profileShown = !profileShown;
                    if (profileShown){
                        profile.setVisibility(View.VISIBLE);
                    } else {
                        profile.setVisibility(View.GONE);
                    }

                }
            }
        });

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CameraActivity.class);
                i.putExtra("taskName", "Profile");
                startActivityForResult(i, 44);

            }
        });


        robotAPI.robot.speak("It's time to take your measurements for health monitoring");

        loadTasks();
    }

    @Override
    protected void onResume(){
        super.onResume();
        generateTasks();
        if (userLoggedIn){
            loginViewProfile.setText("Profile");
        }


    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    private void loadTasks(){

        measurementTasks.add(new MeasurementTask("Temperature"));
        measurementTasks.add(new MeasurementTask("Blood Pressure"));
        measurementTasks.add(new MeasurementTask("Blood Sugar"));
        measurementTasks.add(new MeasurementTask("Testing"));


    }

    private void generateTasks(){

        layoutManager = new StaggeredGridLayoutManager(2, VERTICAL);
        layoutManager.setGapStrategy(GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        rvAdapter = new RVAdapter(measurementTasks, this, this::onTaskClick);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(rvAdapter);

    }

    public void clearResultsData(){
        for (int i = 0 ; i < measurementTasks.size(); i ++){
            measurementTasks.get(i).getResults().clear();
        }
    }

    @Override
    public void onTaskClick(int position) {

        Toast.makeText(MainActivity.this,
                        measurementTasks.get(position).getTaskName(),
                        Toast.LENGTH_SHORT).show();
        /* When a task is clicked, it's name (Temperature, Blood Sugar)
            is sent to the next activity */
        Intent i = new Intent(MainActivity.this,
                                CameraActivity.class);
        i.putExtra("taskName", measurementTasks.get(position).getTaskName());
        // Starting Camera Activity
        startActivityForResult(i, CAMERA_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
            If the result is successfully captured and processed, it is added
            to its corresponding measurement task once the user returns to
            the main page
         */
        if (requestCode == CAMERA_REQ && resultCode == Activity.RESULT_OK){
            // Receiving results from camera which was recieved from results display
            String result = data.getStringExtra("result");
            String taskName = data.getStringExtra("taskName");
            // taskname!=null was needed for every if statement to fix a bug
            if (taskName.equals("Temperature") && taskName != null){
                measurementTasks.get(0).getResults().add(result);
            } else if (taskName.equals("Blood Pressure")){
                measurementTasks.get(1).getResults().add(result);
            } else if (taskName.equals("Blood Sugar") && taskName != null){
                measurementTasks.get(2).getResults().add(result);
            } else if (taskName.equals("Testing") && taskName != null){
                measurementTasks.get(3).getResults().add(result);
            } else if (taskName.equals("Profile") && taskName != null){
                profile.setVisibility(View.VISIBLE);
                userLoggedIn = true;
                loginViewProfile.setText("View Profile");
                String[] healthCardData = result.split("\n");
                name.setText(healthCardData[0]);
                healthCard.setText(healthCardData[1]);
                //add to profile
            }
        }


    }
}

/*

 */