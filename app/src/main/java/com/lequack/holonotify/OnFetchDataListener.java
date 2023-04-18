package com.lequack.holonotify;

import com.lequack.holonotify.models.LiveStream;

import java.util.List;

public interface OnFetchDataListener<T> {
    void onFetchData(List<LiveStream> liveStreams, String filter);
    void onError(String error);
}
