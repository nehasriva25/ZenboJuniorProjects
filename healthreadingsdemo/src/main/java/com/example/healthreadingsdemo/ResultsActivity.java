/*
    ResultsActivity formats the results depending on what measurement task (temperature, blood suagr)
    the user has decided to do. Here, conversions, etc can also be applied
 */
package com.example.healthreadingsdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class ResultsActivity extends AppCompatActivity {
    Button retake, confirm;
    TextView resultDisplay, headingDisplay;
    String taskName, resultingText;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_show);
        retake = findViewById(R.id.retake);
        confirm = findViewById(R.id.confirm);
        resultDisplay = findViewById(R.id.info_text);
        headingDisplay = findViewById(R.id.confirmation_heading);

        // Getting the OCR and task name
        String result = getIntent().getStringExtra("result");
        taskName = getIntent().getStringExtra("taskName");
        // Processing the results (conversions and formatting)
        resultingText = processResult(result, taskName);

        if(taskName != null){
            headingDisplay.setText("Confirm your " + taskName);
        }

//        resultDisplay.setText(formattedResult);

        // Will return to Camera Activity with RESULT_CANCELLED to retake image
        retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Will return to Camera Activity and send formatted results with RESULT_OK
        // This will in turn be sent to Main Activity
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("taskName", taskName);
                returnIntent.putExtra("result", resultingText);

                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });




    }

    private String processResult(@NonNull String result, @NonNull String taskName){

        // Depending on the task name, the result will be formatted differently
        if (taskName != null){
            // Getting the current date and time to log results
            String currentDate = Calendar.getInstance().getTime().toString();
            if (taskName.equals("Temperature")){
                //String[] help = result.split("degrees");
                resultDisplay.setText(result + "°");
                return currentDate + " : " + result + "°";

            } else if (taskName.equals("Testing")){
                return result;
            }else if (taskName.equals("Blood Pressure")){
                return result;
            }
            else if (taskName.equals("Profile")){
                String[] healthCardLines = result.split("\n");

                //return with the first occurance of '-'
                resultDisplay.setText("Name: " + healthCardLines[2] + "\n Card: " + healthCardLines[3]);
                return healthCardLines[2] + "\n" + healthCardLines[3];

            } else if (taskName.equals("Blood Sugar")) {
                // Adding units to blood sugar
                resultDisplay.setText(result + " mmol/L");
                return currentDate + " : " + result + " mmol/L";
            }

        }



        return "Please retake your measurement";

    }
}
