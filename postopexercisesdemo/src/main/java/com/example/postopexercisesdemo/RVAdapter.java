/*
    Adapter displays the videos in a RecyclerView
 */
package com.example.postopexercisesdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.postopexercisesdemo.VideoItem;

import java.util.ArrayList;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    private ArrayList<VideoItem> videoItemArrayList;
    private Context context;
    private VideoClickInterface videoClickInterface;


    //
    public RVAdapter(ArrayList<VideoItem> videoItemArrayList, Context context, VideoClickInterface videoClickInterface) {
        this.videoItemArrayList = videoItemArrayList;
        this.context = context;
        this.videoClickInterface = videoClickInterface;

    }

    @NonNull
    @Override
    public RVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        // Set the data to textview and imageview.
        VideoItem videoItem = videoItemArrayList.get(position);
        holder.thumbnail.setImageBitmap(videoItem.getThumbnail());
        holder.videoTitleStr = videoItemArrayList.get(position).getVideoName();
        holder.completed = videoItemArrayList.get(position).isCompleted();
        holder.videoTitle.setText(holder.videoTitleStr);
        if (holder.completed) {
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);

            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            holder.thumbnail.setColorFilter(filter); // might need to use setImageBitmap
            holder.watchAgainMsg.setText("Watch Again?");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoItemArrayList.get(position).setCompleted(true);
                holder.completed = videoItemArrayList.get(position).isCompleted();
                videoClickInterface.onVideoClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        // this method returns the size of recyclerview
        return videoItemArrayList.size();
    }

    // View Holder Class to handle Recycler View.
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView videoTitle;
        private String videoTitleStr;
        private ImageView thumbnail;
        private TextView watchAgainMsg;
        private Boolean completed;
        //private View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.video_thumbnail);
            watchAgainMsg = itemView.findViewById(R.id.watch_again);

            videoTitle = itemView.findViewById(R.id.video_title);
            videoTitle.setText(videoTitleStr);
            //this.itemView = itemView;
//            courseIV = itemView.findViewById(R.id.idIVcourseIV);
        }
    }

    public interface VideoClickInterface {
        void onVideoClick(int position);
    }
}