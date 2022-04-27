/*
    VideoPlayActivity will play the exercise video that the user has clicked on
 */
package com.example.postopexercisesdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;
import android.os.Bundle;


import org.json.JSONObject;


public class VideoPlayActivity extends AppCompatActivity {


    private MediaController mediaController;
    private VideoView videoView;
    private RelativeLayout videoLayout;
    private String videoName, videoPath;
    private Boolean isCompleted;
    private int id;
    private  VideoItem videoItem;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_playing);



        videoView = findViewById(R.id.video_view);

        videoName = getIntent().getStringExtra("videoName");
        videoPath = getIntent().getStringExtra("videoPath");
        isCompleted = getIntent().getBooleanExtra("isCompleted", false);
        id = getIntent().getIntExtra("id", -1);

        videoLayout = findViewById(R.id.video_layout);


        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.start();
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {


            }
        });





    }

    @Override
    protected void onResume(){
        super.onResume();


    }


    @Override
    protected void onPause(){
        super.onPause();
        videoView.pause();

    }

    @Override
    protected void onStop(){
        super.onStop();
        videoView.pause();

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        videoView.pause();
    }



}