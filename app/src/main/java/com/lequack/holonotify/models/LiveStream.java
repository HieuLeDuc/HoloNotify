package com.lequack.holonotify.models;

public class LiveStream {
    String id = "";
    String title = "";
    String type = "";
    String topic_id = "";
    String published_at = "";
    String available_at = "";
    String duration = "";
    String status = "";
    String start_scheduled = "";
    String start_actual = "";
    String end_actual = "";
    String live_viewers = "";
    String thumbnail_url = "";
    Channel channel = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(String topic_id) {
        this.topic_id = topic_id;
    }

    public String getPublished_at() {
        return published_at;
    }

    public void setPublished_at(String published_at) {
        this.published_at = published_at;
    }

    public String getAvailable_at() {
        return available_at;
    }

    public void setAvailable_at(String available_at) {
        this.available_at = available_at;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStart_scheduled() {
        return start_scheduled;
    }

    public void setStart_scheduled(String start_scheduled) {
        this.start_scheduled = start_scheduled;
    }

    public String getStart_actual() {
        return start_actual;
    }

    public void setStart_actual(String start_actual) {
        this.start_actual = start_actual;
    }

    public String getEnd_actual() {
        return end_actual;
    }

    public void setEnd_actual(String end_actual) {
        this.end_actual = end_actual;
    }

    public String getLive_viewers() {
        return live_viewers;
    }

    public void setLive_viewers(String live_viewers) {
        this.live_viewers = live_viewers;
    }

    public String getThumbnail_url() {
        this.thumbnail_url = "https://i3.ytimg.com/vi/" + this.id + "/maxresdefault.jpg";
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void updateUrl() {
        this.thumbnail_url = "https://i3.ytimg.com/vi/" + this.id + "/maxresdefault.jpg";
    }
}
