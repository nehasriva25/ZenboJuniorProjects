/*
    Mental Health and COPD Demo
    -----------------------------------------------------------------------------------------
    Originally was meant to showcase Zenbo's ability to be a companion robot but evolved to
    include an escalation determination feature for COPD patients using Canada's COPD Action
    Plan

    User's can report COPD symptoms and answer some follow up questions for the robot to
    determine what level they are at what actions they should take.

    User's can also say how they are feeling (happy, sad, confused, nervous) and the robot can
    help them

 */
package com.example.mentalhealthdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;


import com.asus.robotframework.API.ExpressionConfig;
import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.robotframework.API.RobotFace;
import com.asus.robotframework.API.RobotUtil;
import com.asus.robotframework.API.SpeakConfig;
import com.asus.robotframework.API.VisionConfig;
import com.asus.robotframework.API.results.DetectFaceResult;
import com.asus.robotframework.API.results.DetectPersonResult;

import org.json.JSONObject;

public class MainActivity extends Activity {
    public RobotAPI robotAPI;

    // Use when needing to use the robot in its callback functions
    public static RobotAPI clbkRobotAPI = null;

    // Callback functions can be found in Dev samples in the SDK folder
    // Most often used for face and person recognition
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
            clbkRobotAPI.utility.lookAtUser(0.5f-(resultList.get(0).getBodyLocCam().x));
            clbkRobotAPI.vision.requestDetectFace(faceConfig);

        }
        @Override
        public void onDetectFaceResult(List<DetectFaceResult> resultList) {
            clbkRobotAPI.utility.lookAtUser(0.5f-(resultList.get(0).getFaceLocCam().x));

        }

    };

    // Most often used for dialog recognition and logic
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

            faceConfig.enableDebugPreview = false;
            faceConfig.enableDetectHead = true;
            faceConfig.enableFacePosture = true;
            faceConfig.enableCandidateObj = false;
            faceConfig.enableHeadGazeClassifier = true;
            faceConfig.intervalInMS = 500;
            clbkRobotAPI.vision.requestDetectFace(faceConfig);

            // Getting the PlanID and input context as defined in the DDE
            String sIntentionID = RobotUtil.queryListenResultJson(jsonObject, "IntentionId");
            String inputContext = RobotUtil.queryListenResultJson(jsonObject, "input_context");

            // These are the values of variables when no user response is given to fill them
            // These values are defined in the DDE
            final String DEFAULT_USER_EMOTION = "noUserEmotion";
            final String DEFAULT_PAST_EMOTION = "noPastEmotion";
            final String DEFAULT_BODY_PAIN = "noBodyPain";
            final String NO_SYMPTOM = "noSymptom";
            final String NOT_BETTER = "notBetter";


            Log.d(TAG, "Intention Id = " + sIntentionID);

            if (sIntentionID.equals("userFeelingPlan")) {
              String pastEmotion = RobotUtil.queryListenResultJson(jsonObject, "pastEmotion");
                String userEmotion = RobotUtil.queryListenResultJson(jsonObject, "userEmotion");
                String painBodyPart = RobotUtil.queryListenResultJson(jsonObject, "userBodyPain");

                if (userEmotion != null) {


                    if (!pastEmotion.equals(DEFAULT_PAST_EMOTION) && ((pastEmotion.equals("pain") || pastEmotion.equals("sick")))) {
                        if (userEmotion.equals("fine") || userEmotion.equals("okay")) {

                            clbkRobotAPI.robot.setExpression(RobotFace.HAPPY);
                            clbkRobotAPI.robot.speakAndListen("I'm glad you're feeling better! I'll make a note of your improvement in your record", new SpeakConfig().timeout(1));

                        }
                    } else if (userEmotion.equals("pain")) {
                        if (painBodyPart.equals(DEFAULT_BODY_PAIN)) { // no body part specified
                            clbkRobotAPI.robot.speakAndListen("Where are you in pain?", new SpeakConfig().timeout(20));
                        } else if (painBodyPart.equals("chest")){
                            userMainSymptom = "Chest Pain";
                            clbkRobotAPI.robot.speak("Hmmm thanks for letting me know. " +
                                    "Let me check your care pathway to see what we should do about your "
                                    + painBodyPart + " hurting");
                            userLevel = escalationLevel.ESCALATE_TO_RED;
                            clbkRobotAPI.robot.jumpToPlan(DOMAIN, "COPDRedPlan");

                        }
                            else {
                                clbkRobotAPI.robot.speakAndListen("Hmmm thanks for letting me know. " +
                                        "Let me check your care pathway to see what we should do about your "
                                        + painBodyPart + " hurting", new SpeakConfig().timeout(1));
                        }



                    } else if (userEmotion.equals("nervous") || userEmotion.equals("stressed")) {
                        clbkRobotAPI.robot.setExpression(RobotFace.INNOCENT);

                        clbkRobotAPI.robot.speakAndListen("Oh no it's okay. What are you worried about", new SpeakConfig().timeout(20));

                    } else if (userEmotion.equals("sad")) {
                        //https://www.youtube.com/watch?v=GAgrENmhZy4

                        clbkRobotAPI.robot.setExpression(RobotFace.INNOCENT);
                        clbkRobotAPI.robot.speakAndListen("I'm sorry you're feeling down. Can I give you a hug?", new SpeakConfig().timeout(1));
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=GAgrENmhZy4"));
                        mContext.startActivity(browserIntent);

                    } else if (userEmotion.equals("happy")) {

                        clbkRobotAPI.robot.setExpression(RobotFace.SHY);

                        clbkRobotAPI.robot.speakAndListen("I'm happy you're happy!", new SpeakConfig().timeout(1));


                    } else if (userEmotion.equals("confused")) {

                        clbkRobotAPI.robot.setExpression(RobotFace.QUESTIONING);
                        clbkRobotAPI.robot.speakAndListen("Hmm are you confused about any health procedures or medication?", new SpeakConfig().timeout(20));
                    }

                }

            } else if (inputContext.equals("userInPain") && sIntentionID.equals("getBodyPart")) {
                String painBodyPart = RobotUtil.queryListenResultJson(jsonObject, "userBodyPain");

                if (painBodyPart.equals("chest")){
                    userMainSymptom = "Chest Pain";
                    clbkRobotAPI.robot.speak("Hmmm thanks for letting me know. " +
                            "Let me check your care pathway to see what we should do about your "
                            + painBodyPart + " hurting");
                    userLevel = escalationLevel.ESCALATE_TO_RED;
                    clbkRobotAPI.robot.speakAndListen("Just so I know, have you had any symptoms like being short of breath, drowsy, or suddenly confused or nervous?\"", new SpeakConfig().timeout(20));
                    clbkRobotAPI.robot.jumpToPlan(DOMAIN, "COPDRedPlan");

                } else {
                    clbkRobotAPI.robot.speakAndListen("Hmmm thanks for letting me know. " +
                            "Let me check your care pathway to see what we should do about your "
                            + painBodyPart + " hurting", new SpeakConfig().timeout(1));
                }



            } else if (inputContext.equals("userIsStressed") && sIntentionID.equals("explainWorry")) {

                clbkRobotAPI.robot.setExpression(RobotFace.EXPECTING);
                clbkRobotAPI.robot.speakAndListen(" I might not be able to help with everything, " +
                        "but I am here for you! " +
                        "Sometimes, when I am feeling nervous, " +
                        "I like to do some breathing exercises to help calm myself down. " +
                        "Would you like me to show you?", new SpeakConfig().timeout(20));

            } else if (inputContext.equals("breathingPlanStart") && sIntentionID.equals("breathingPlan")) {

                // breathing plan
                clbkRobotAPI.robot.speak("Great! Together, we are going to breathe in, hold, breathe out, and hold again. We'll do each step for 4 counts");
                clbkRobotAPI.robot.setExpression(RobotFace.TIRED);
                clbkRobotAPI.robot.speak("Let's start by closing our eyes");

                clbkRobotAPI.robot.speak("First inhale! 1...2...3...4", new SpeakConfig().speed(75));
                clbkRobotAPI.robot.speak("Hold! 1...2...3...4", new SpeakConfig().speed(75));
                clbkRobotAPI.robot.speak("Now exhale! 1...2...3...4", new SpeakConfig().speed(75));
                clbkRobotAPI.robot.speak("Hold! 1...2...3...4", new SpeakConfig().speed(75));

                clbkRobotAPI.robot.setExpression(RobotFace.SHY);
                clbkRobotAPI.robot.speakAndListen("I hope you feel better", new SpeakConfig().timeout(1));


            } else if (inputContext.equals("userConfused") && sIntentionID.equals("explainHealthConfusion")) {
                clbkRobotAPI.robot.speakAndListen("Okay! Let me go through some resources about your specific care pathway", new SpeakConfig().timeout(1));

            // When the current plan is COPDPlan
            } else if (sIntentionID.equals("COPDPlan")) {
                // Symptom is saved from the belief mainSymptom
                String mainSymptom = RobotUtil.queryListenResultJson(jsonObject, "mainSymptom");
                String betterIndication = RobotUtil.queryListenResultJson(jsonObject,
                                                                    "copdBetter");
                // If a symptom is specified (not the default value)
                if (!mainSymptom.equals(NO_SYMPTOM) && betterIndication.equals(NOT_BETTER)) {
                    userMainSymptom = mainSymptom;
                     /* Since the output context for COPDPlan is 48HoursCheck,
                           the robot will prompt the user to answer in a way that will
                           match moreThan48HoursPlan's intent or lessThan48HoursPlan's intent
                           (their input contexts are both 48HoursCheck)
                           (intents and output contexts are configured in DDE)
                         */
                    /* If the user is currently at green, we check if we need to escalate
                     to yellow by asking if the symptoms have been present for at least 48 hours */
                    if (userLevel == escalationLevel.GREEN) {
                        clbkRobotAPI.robot.speakAndListen("Let me check your care pathway to see what I can do. Have you had this symptom for at least two days?", new SpeakConfig().timeout(20));
                    /* If the user is at yellow, we check if we need to escalate to red by asking
                       if flare up meds have been taken for at least 48 hours */
                    } else if (userLevel == escalationLevel.YELLOW) {
                        clbkRobotAPI.robot.speakAndListen("Let me check your care pathway to see what I can do. You are currently at a yellow action level. Have you been taking your flare up medication for at least two days?", new SpeakConfig().timeout(20));

                    }
                } else if (betterIndication.equals("baseline")){
                    clbkRobotAPI.robot.setExpression(RobotFace.HAPPY);
                    clbkRobotAPI.robot.speak("That's amazing to hear! I'm so glad you're doing better.");
                    clbkRobotAPI.robot.speakAndListen("I'll make a note of your improvement in your record and bring you back to a Green Action Level", new SpeakConfig().timeout(1));
                    userLevel = escalationLevel.GREEN;
                    additionalSymptoms.clear();
                    userMainSymptom = "None";
                }

            } else if (sIntentionID.equals("COPDRedSymptoms")){
                clbkRobotAPI.robot.speakAndListen("Have you been taking flare up medication for more than two days?", new SpeakConfig().timeout(20));
                clbkRobotAPI.robot.jumpToPlan(DOMAIN, "COPDPlan");

            }
            // Robot immediately jumps to this plan when user is experiencing chest pain
            else if (sIntentionID.equals("COPDRedPlan")){
                clbkRobotAPI.robot.speakAndListen("Just so I know, have you had any symptoms like being short of breath, drowsy, or suddenly confused or nervous?", new SpeakConfig().timeout(20));

            }
            /* If the user answers "yes", DDE data will trigger the current plan
               to be moreThan48HoursPlan */
            else if (sIntentionID.equals("moreThan48HoursPlan")) {
                /* The user's level is green, they have indicated that they've been experiencing
                     symptoms for more than 2 days. Therefore they must be escalated to yellow */
                if (userLevel == escalationLevel.GREEN) {
                    userLevel = escalationLevel.ESCALATE_TO_YELLOW;
                    // Asking for any other symptoms
                    clbkRobotAPI.robot.speakAndListen("Can you tell me what other symptoms " +
                            "you have?", new SpeakConfig().timeout(25));

                } else if (userLevel == escalationLevel.ESCALATE_TO_RED){
                    robotCOPDDecision();

                /* The user's level is yellow, they have indicated that they've been taking
                   meds for more than 2 days. Therefore they must be escalated to red */
                } else if (userLevel == escalationLevel.YELLOW) {
                    userLevel = escalationLevel.ESCALATE_TO_RED;
                    // Since at red, we immediately jump to deciding what action to take
                    robotCOPDDecision();
                }

            /* If the user answer "no", DDE data will trigger the current plan
                to be lessThan48HoursPlan */
            } else if (sIntentionID.equals("lessThan48HoursPlan")) {
                /* If the user's level is green, they have indicated that their symptoms have
                    been present for less than 2 days. They stay at the green level but are asked
                    if they have other symptoms */
                if (userLevel == escalationLevel.GREEN) {
                    clbkRobotAPI.robot.speakAndListen("Can you tell me what other symptoms you have?", new SpeakConfig().timeout(25));
                } else if (userLevel == escalationLevel.ESCALATE_TO_RED){
                    robotCOPDDecision();
                /* If the user's level is yellow, they have indicated that they have been taking
                    their meds for less that 2 days. The stay at the yellow level but are asked
                    if they have other symptoms  */
                } else if (userLevel == escalationLevel.YELLOW) {
                    clbkRobotAPI.robot.speakAndListen("All right! Continue taking your flare up medication. Are you having any other symptoms?", new SpeakConfig().timeout(25));
                }

            } else if (sIntentionID.equals("getCOPDSymptoms")) {
                String moreSymptoms = RobotUtil.queryListenResultJson(jsonObject, "moreSymptoms");
                // The user has indicated additional symptoms
                if (!moreSymptoms.equals(NO_SYMPTOM)) {
                    /* Adding additional symptoms to user's record and asking if they have been
                         present for at least 2 days */
                    additionalSymptoms.add(moreSymptoms);
                    clbkRobotAPI.robot.speakAndListen("Have any of these symptoms lasted for at least two days?", new SpeakConfig().timeout(20));
                } else {
                // If there are no additional symptoms, robot jumps to deciding what action to take
                    robotCOPDDecision();
                }
            } else if (sIntentionID.equals("otherSymptomsMoreThan48HoursPlan")) {
                /* The user's level is green, they have indicated that they've been experiencing
                    symptoms for more than 2 days. Therefore they must be escalated to yellow */
                if (userLevel == escalationLevel.GREEN) {
                    userLevel = escalationLevel.ESCALATE_TO_YELLOW;
                }
                /* If user is at yellow but hasn't taken flare up meds for 2 days,
                   they should stay at yellow. If they have taken flare up meds for 2 days
                   they will have already been escalated to red in moreThan48HoursPlan.
                   Therefore, symptoms are recorded but there is no escalation if a user at
                   yellow level has additional symptoms that have lasted more than 48 hours
                */
                clbkRobotAPI.robot.speak("Thanks for letting me know! I've made a note about your symptoms");
                // Decide what action to take
                robotCOPDDecision();

            } else if (sIntentionID.equals("otherSymptomsLessThan48HoursPlan")) {
                // Decide on what action to take
                robotCOPDDecision();
            } else if (sIntentionID.equals("determine911")) {
                clbkRobotAPI.robot.speakAndListen("Are you able to go to an emergency department by yourself? ", new SpeakConfig().timeout(20));

            } else if (sIntentionID.equals("needToDial")) {
                clbkRobotAPI.robot.speak("That's ok! I will call 911 for you");

            } else if (sIntentionID.equals("dontNeedToDial")) {
                clbkRobotAPI.robot.speak("All right! Please go to the emergency department. Since I messaged your circle of care, check your Healix app for updates");
            } else if (!sIntentionID.equals("launchAppPlan")) {
                updateText();
                updateLevel();
                clbkRobotAPI.robot.setExpression(RobotFace.HIDEFACE);
            }


        }

        @Override
        public void onRetry(JSONObject jsonObject) {

        }
    };

    private static void robotCOPDDecision() {
        // Instructing the user what to do based on what their determined level is

        if (userLevel == escalationLevel.GREEN) {
            clbkRobotAPI.robot.speak("This doesn't seem to be a flare up so I've put you at a Green action level. For now, continue taking your daily puffers.");
            clbkRobotAPI.robot.speak("If you want, I can help you figure out other reasons why you may be having these symptoms");
        } else if (userLevel == escalationLevel.YELLOW) {
            clbkRobotAPI.robot.speak("Don't worry, you are still at a yellow action level. Continue taking your prescriptions for flare ups for the symptoms on the screen and your daily puffers");
            clbkRobotAPI.robot.speak("I'll check back with you tomorrow to see how you are feeling");
        } else if (userLevel == escalationLevel.RED) {
            clbkRobotAPI.robot.speak("Please dial 911 or go to the nearest emergency department.");
            clbkRobotAPI.robot.speak("I've sent a message to your circle of care.");
        } else if (userLevel == escalationLevel.ESCALATE_TO_YELLOW) {
            clbkRobotAPI.robot.speak("This seems to be a flare up so I've put you at a yellow action level. Please start taking your flare up medication for the symptoms on the screen. ");
            clbkRobotAPI.robot.speak("Since you need to take a rescue inhaler, start taking your Ventolin. This is your blue puffer. ");
           /* If the user has symptoms that involve sputum or shortness of breath,
               they require additional flare up medications */
            if (userMainSymptom.equals("sputum") || symptomCheck("sputum")){
                clbkRobotAPI.robot.speak("You should also take your Amoxicillin to help with your change in sputum. ");
            }
            if (userMainSymptom.equals("shortnessOfBreath") || symptomCheck("shortnessOfBreath")){
                clbkRobotAPI.robot.speak("Also take your Prednisone to help with your breathing. ");
            }
            clbkRobotAPI.robot.speak("Make sure you also continue to take Advair, your daily purple puffer.");
            clbkRobotAPI.robot.speak("I've already notified your circle of care. Don't worry you've got this!");
            userLevel = escalationLevel.YELLOW;
        } else if (userLevel == escalationLevel.ESCALATE_TO_RED) {
            clbkRobotAPI.robot.speak("According to your care pathway, this seems to be something that requires a medical professional. " +
                    "I've put you at a red action level and sent a message to your circle of care."
                    + " Please go to the nearest emergency department but don't panic, " +
                    "I've put you at this level to make sure you're getting the best care. " );
            userLevel = escalationLevel.RED;
            /* Jumping to DDE logic that will determine if robot
                needs to call 911 depending on user answer */
            clbkRobotAPI.robot.jumpToPlan(DOMAIN, "determine911");
        }
        // Updating level on screen
        updateText();
        updateLevel();
    }


    // Store the Domain UUID
    public final static String TAG = "MentalHealthDemo";
    public final static String DOMAIN = "1E17D89D65214A87936271F424CE8A10";
    public final static String APPid = "TWv0dWvJOp2UAKkf";
    public static TextView symptomsShow, greenLevel, yellowLevel, redLevel;
    public static Button startListening;

    // These are the possible states the escalation level can be in
    public enum escalationLevel {GREEN, YELLOW, ESCALATE_TO_YELLOW, ESCALATE_TO_RED, RED};
    // For demo purposes, the user starts as green
    public static escalationLevel userLevel = escalationLevel.GREEN;

    public static String userMainSymptom;
    public static ArrayList<String> additionalSymptoms = new ArrayList<String>();

    static VisionConfig.FaceDetectConfig faceConfig = new VisionConfig.FaceDetectConfig();

    private static Context mContext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the robotAPI instance
        robotAPI = new RobotAPI(getApplicationContext(), robotCallback);
        symptomsShow = findViewById(R.id.symptomsShow);
        greenLevel = findViewById(R.id.greenLevel);
        yellowLevel = findViewById(R.id.yellowLevel);
        redLevel = findViewById(R.id.redLevel);
        startListening = findViewById(R.id.listen_start);

        // On button press, The robot starts the speaking, listening, understanding user process
        startListening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Have the robot begin to listen for a user phrase/intent
                robotAPI.robot.speakAndListen("What can I help you with?",
                                                new SpeakConfig().timeout(20));

                /*
                   Connect the DDE Logic to the developed app by using the domain UUID and the
                   launching plan (launching plan is designed in the DDE app)
                */
                robotAPI.robot.jumpToPlan(DOMAIN, "launchAppPlan");

            }
        });
        /* Upon creation, robot will initiate conversation. If user doesn't answer
           they can always re-initiate conversation through the "Talk" button  */
        robotAPI.robot.setExpression(RobotFace.HIDEFACE);
        robotAPI.robot.speakAndListen("How are you feeling", new SpeakConfig().timeout(20));
        robotAPI.robot.jumpToPlan(DOMAIN, "launchAppPlan");


    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
            If using dialog, register the listen callback
            and set the static instance of robotAPI
         */
        robotAPI.robot.registerListenCallback(robotListenCallback);
        clbkRobotAPI = robotAPI;
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        updateText();
        updateLevel();

    }

    private static void updateLevel() {
        if (userLevel == escalationLevel.GREEN) {
            greenLevel.setVisibility(View.VISIBLE);
            yellowLevel.setVisibility(View.INVISIBLE);
            redLevel.setVisibility(View.INVISIBLE);
        } else if (userLevel == escalationLevel.YELLOW) {
            greenLevel.setVisibility(View.INVISIBLE);
            yellowLevel.setVisibility(View.VISIBLE);
            redLevel.setVisibility(View.INVISIBLE);
        } else {
            greenLevel.setVisibility(View.INVISIBLE);
            yellowLevel.setVisibility(View.INVISIBLE);
            redLevel.setVisibility(View.VISIBLE);
        }

    }



    private static void updateText() {
        String symptomText = "";

        if (userMainSymptom !=null) {
            symptomText = "Symptom of Concern: " + userMainSymptom;
        }
        if (additionalSymptoms.size() > 0) {
            symptomText += "\n Additional Symptoms: ";
            for (int i = 0; i < additionalSymptoms.size(); i++) {
                symptomText += additionalSymptoms.get(i) + ", ";
            }
        }

        if (symptomText!=null) {
            symptomsShow.setText(symptomText);
        }

    }

    //checking if user has a certain symptom
    public static Boolean symptomCheck(String symptom){
        if (additionalSymptoms.size() <= 0){
            return false;
        } else {
            for (int i = 0; i < additionalSymptoms.size(); i ++){
                if (additionalSymptoms.get(i).contains(symptom)){
                    return true;
                }
            }
        }
        return false;

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister listen callback
        robotAPI.robot.unregisterListenCallback();

        // If using facial recognition, stop the "Recognize Face" request
        robotAPI.vision.cancelDetectFace();

        // Stop listen user utterance
        robotAPI.robot.stopSpeakAndListen();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        robotAPI.release();
    }
}