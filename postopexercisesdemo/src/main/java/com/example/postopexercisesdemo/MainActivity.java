/*
    Post Op Exercises Demo
    -----------------------------------------------------------------------------------------
    Meant to remind patients about exercises they should to post surgery. Tracks which exercises
    they have done and guide patients through their exercises through videos.

    Videos used
    https://www.youtube.com/watch?v=Er-Fl_poWDk
    https://www.youtube.com/watch?v=XJ8rQrZ_z7E
    https://www.youtube.com/watch?v=FLofcotLfw0
    https://www.youtube.com/watch?v=iLRKbSesWA8

    MainActivity displays the videos, allowing the user to click on which exercise they would like
    to go through. The robot also reminds them how many videos they have left to watch.

 */


package com.example.postopexercisesdemo;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.util.Size;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import android.os.Bundle;

import android.app.Activity;


import com.asus.robotframework.API.MotionControl;
import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.robotframework.API.RobotFace;
import com.asus.robotframework.API.WheelLights;

import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements RVAdapter.VideoClickInterface {

    public RobotAPI robotAPI;
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

        }

        @Override
        public void onRetry(JSONObject jsonObject) {

        }
    };

    public int videosLeft;


    private RecyclerView recyclerView;
    public  ArrayList<VideoItem> videoList = new ArrayList<VideoItem>();
    private ArrayList<VideoItem> watchedVideos = new ArrayList<VideoItem>();

    private RVAdapter rvAdapter;
    private GridLayoutManager layoutManager;

    private TextView textView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        robotAPI = new RobotAPI(getApplicationContext(), robotCallback);

        robotAPI.robot.speak("Welcome to your post op exercises");
        recyclerView = findViewById(R.id.all_video_view);


        //create an array list of videos
        loadVideos();

    }

    @Override
    protected void onResume() {
        super.onResume();

        generateList();

        if (videosLeft > 1) {
             robotAPI.robot.speak("You still have " + videosLeft + " exercises to go through");
        } else if (videosLeft == 1) {
             robotAPI.robot.speak("You still have one exercise to go through");
        } else {
            robotAPI.robot.speak("You have no more exercises to go through");
        }

        if (videosLeft >= 1) {
            robotAPI.robot.speak("Please click on any remaining videos to go through your exercises");
        } else {
            robotAPI.robot.speak("Please check back tomorrow to go through your exercises");
        }

    }

    private void loadVideos() {

        // Video data would come from a database where you would check if the video was already played

        videoList.add(new VideoItem("Leg Balance",
                "android.resource://" + getPackageName() + "/" + R.raw.video, false, videoList.size()));
        videoList.add(new VideoItem("Patella Mobilisation",
                "android.resource://" + getPackageName() + "/" + R.raw.video2, false, videoList.size()));
        videoList.add(new VideoItem("Heel Slide",
                "android.resource://" + getPackageName() + "/" + R.raw.video3, false, videoList.size()));
        videoList.add(new VideoItem("Knee Extension",
                "android.resource://" + getPackageName() + "/" + R.raw.video4, false, videoList.size()));




    }

    private void generateList() {

        ContentResolver contentResolver = getContentResolver();
        String videoPath;

        // Video data would come from a database where you would check if the video was already played

        videosLeft = videoList.size();

        // Checking if the video is already watched
        for (int i = 0; i < videoList.size(); i++){
            if (videoList.get(i).isCompleted()){
                videosLeft --;
            }

                // in reality, we would get videos from a database so this code would be different
                Uri uri = Uri.parse(videoList.get(i).getVideoPath());
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(getApplicationContext(), uri);
                videoList.get(i).setThumbnail(retriever.getFrameAtTime(10, MediaMetadataRetriever.OPTION_PREVIOUS_SYNC));
            //}
        }



        layoutManager = new GridLayoutManager(this, 2);
        rvAdapter = new RVAdapter(videoList, this, this::onVideoClick);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(rvAdapter);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onVideoClick(int position) {
        Intent i = new Intent(MainActivity.this, VideoPlayActivity.class);


        i.putExtra("videoName", videoList.get(position).getVideoName());
        i.putExtra("videoPath", videoList.get(position).getVideoPath());
        i.putExtra("isCompleted", videoList.get(position).isCompleted());
        i.putExtra("id", videoList.get(position).getId());

        // Launching Video Play Activity
        startActivity(i);
    }


}