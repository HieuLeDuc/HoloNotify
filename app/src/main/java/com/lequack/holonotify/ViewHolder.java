package com.lequack.holonotify;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ViewHolder extends RecyclerView.ViewHolder {

    TextView streamTitle, channelTitle, startTime;
    ImageView streamThumbnail;
    CardView cardView;
    FloatingActionButton fab;
    public ViewHolder(View view) {
        super(view);
        streamTitle = view.findViewById(R.id.textView_streamTitle);
        channelTitle = view.findViewById(R.id.textView_channelTitle);
        startTime = view.findViewById(R.id.textView_startTime);
        streamThumbnail = view.findViewById(R.id.imageView_streamThumbnail);
        cardView = view.findViewById(R.id.card_container);
        fab = view.findViewById(R.id.button_setAlarm);
}}