/*
     MedViewActivity displays all the medication that the user is currently on as well as each
     medication's information.

     Future implementations could include adding a feature where users can add their prescriptions
     in this activity
 */

package com.example.medicationdemo;

import static androidx.recyclerview.widget.StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS;
import static androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MedViewActivity extends AppCompatActivity implements RVAdapter.ClickInterface{

    private List<Medication> userMedication = new ArrayList<Medication>();
    public final int ADD_MEDICATION_REQ = 55;

    private RecyclerView recyclerView;

    private RVAdapter rvAdapter;
    private StaggeredGridLayoutManager layoutManager;
    Button backButton, medEventAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.med_view);
//        robotAPI = new RobotAPI(getApplicationContext(), robotCallback);
        recyclerView = findViewById(R.id.starting_view);
        backButton = findViewById(R.id.backToCalender);
        medEventAdd = findViewById(R.id.medEventViewButton);

        // Get user medication list from main activity
        userMedication = (List<Medication>) getIntent().getSerializableExtra("userMedication");
//        userMedication.add(new Medication("Tylenol", "by mouth", "1 tablet", "once a day", "for three days"));
//        userMedication.add(new Medication("Advil", "by mouth", "2 tablets", "once a day", "for three days"));



    }

    @Override
    protected void onResume(){
        super.onResume();

        layoutManager = new StaggeredGridLayoutManager(2, VERTICAL);
        layoutManager.setGapStrategy(GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        rvAdapter = new RVAdapter(userMedication, this, this::onTaskClick);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(rvAdapter);

    }

    public void endView(View view) {

        finish();
    }

    public void addMedication(View view){
        //go to camera activity under the task name add medication

//        Intent i = new Intent(MedViewActivity.this, CameraActivity.class);
//        i.putExtra("taskName", "Add Medication");
//        i.putExtra("userMedication", (Serializable) userMedication);
//        startActivityForResult(i, ADD_MEDICATION_REQ);

    }

    @Override
    public void onTaskClick(int position) {
        Toast.makeText(MedViewActivity.this, userMedication.get(position).getMedicationName(), Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_MEDICATION_REQ) {
            if (resultCode == Activity.RESULT_OK) {
                //imageAnalysis.clearAnalyzer();
                List<Medication> updatedUserMeds = (List<Medication>) data.getSerializableExtra("updatedUserMeds");
                String taskName = data.getStringExtra("taskName");
                Intent returnIntent = new Intent();
                returnIntent.putExtra("updatedUserMeds", (Serializable) updatedUserMeds);
                returnIntent.putExtra("taskName", taskName);
                setResult(Activity.RESULT_OK,returnIntent);
                //push and return intent
                finish();
            }
        }
    }




}
