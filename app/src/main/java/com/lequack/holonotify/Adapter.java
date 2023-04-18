package com.lequack.holonotify;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lequack.holonotify.models.LiveStream;
import com.squareup.picasso.Picasso;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;


public class Adapter extends RecyclerView.Adapter<ViewHolder> {
    FloatingActionButton fab;
    AlarmManager alarmManager;
    private Context context;
    private List<LiveStream> streamList;
    private OnClickListener onClickListener;
    String status = "";
    public Adapter(Context context, List<LiveStream> streamList, OnClickListener onClickListener) {
        this.context = context;
        this.streamList = streamList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.stream_list, parent, false));
    }

    public String formatLocalDateTime(@NonNull long timestamp) {
        // Convert Unix timestamp to local date time
        LocalDateTime localTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
        // Define the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yy");
        // Format the local date time object
        String formattedDateTime = localTime.format(formatter);
        // Return the formatted string
        return formattedDateTime;
    }

    public void timeStamp(@NonNull ViewHolder holder, int position) {
        // Timestamps
        // Parse the timestamp string into Unix time
        long plannedTimestamp;
        long actualTimestamp = 0;
        try {
            plannedTimestamp = Instant.parse(streamList.get(position).getStart_scheduled()).getEpochSecond();
            if (!streamList.get(position).getStart_actual().equals("")) {
                actualTimestamp = Instant.parse(streamList.get(position).getStart_actual()).getEpochSecond();
            }
        } catch (DateTimeParseException e) {
            throw new RuntimeException(e);
        }
        //Get the current timestamp
        long currentTimestamp = System.currentTimeMillis() / 1000;
        //Set the start time text and color
        if (currentTimestamp < plannedTimestamp) {
            holder.startTime.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.startTime.setText("Starts at " + formatLocalDateTime(plannedTimestamp));
            status = "Stream is starting soon";
        } else if ((currentTimestamp >= plannedTimestamp) && actualTimestamp == 0) {
            holder.startTime.setText("Waiting for stream...");
            status = "Waiting for stream...";
        } else {
            //set text color to red
            holder.startTime.setTextColor(ContextCompat.getColor(context, R.color.red));
            holder.startTime.setText("Live now!");
            status = "Live now!";
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //set text fields
        holder.streamTitle.setText(streamList.get(position).getTitle());
        holder.channelTitle.setText(streamList.get(position).getChannel().getName());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onItemClick(streamList.get(position));

            }
        });
        //set time
        timeStamp(holder, position);

        //Set thumbnail
        //check empty
        if (streamList.get(position).getThumbnail_url() != null) {
            //set thumbnail
            Picasso.get().load(streamList.get(position).getThumbnail_url()).into(holder.streamThumbnail);
        } else {
            //set default thumbnail
            Picasso.get().load("https://demofree.sirv.com/nope-not-here.jpg");
        }

        //Set alarm button
        int tealColor = ContextCompat.getColor(context, R.color.teal_200);
        int greyColor = ContextCompat.getColor(context, R.color.gray);
        holder.fab.setImageResource(R.drawable.disablealarm);
        holder.fab.setBackgroundTintList(ColorStateList.valueOf(greyColor));
        holder.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.fab.setImageResource(R.drawable.alarm);
                holder.fab.setBackgroundTintList(ColorStateList.valueOf(tealColor));
                // Set alarm
                setAlarm(streamList.get(position).getStart_scheduled(), streamList.get(position).getTitle());
                // Revert back after 5 seconds
                holder.fab.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.fab.setImageResource(R.drawable.disablealarm);
                        holder.fab.setBackgroundTintList(ColorStateList.valueOf(greyColor));
                    }
                }, 5000);
            }
        });
    }

    @Override
    public int getItemCount() {
        return streamList.size();
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "HoloNotifyChannel";
            String description = "Channel for HoloNotify";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyHolo", name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build();
            channel.setSound(soundUri, attributes);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void setAlarm(String ISO8601, String title) {
        long unixSeconds = Instant.parse(ISO8601).getEpochSecond();

        createNotificationChannel();

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("streamTitle", title);
        intent.putExtra("is_live", status);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP,   unixSeconds*1000, pendingIntent);
    }
}
