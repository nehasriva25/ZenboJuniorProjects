
package com.example.postopexercisesdemo;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Size;

import java.io.File;
import java.io.IOException;

public class VideoItem {
    private String videoName;
    private String videoPath;
    private Bitmap thumbnail;
    private boolean completed;
    private int id;


    public VideoItem(String videoName, String videoPath, boolean completed, int id) {
        this.videoName = videoName;
        this.videoPath = videoPath;
        this.thumbnail = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MICRO_KIND);
        this.completed = completed;
        this.id = id;


    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public Bitmap getThumbnail() {
        return this.thumbnail;
    }


    public void setThumbnail(Bitmap thumbnail) {
        if (isCompleted()){

        }

        this.thumbnail = thumbnail;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}



