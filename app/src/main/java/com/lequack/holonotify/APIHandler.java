package com.lequack.holonotify;

import android.content.Context;
import android.widget.Toast;

import com.lequack.holonotify.models.LiveStream;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class APIHandler {
    Context context;
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://holodex.net/api/v2/")
            .addConverterFactory(GsonConverterFactory.create()).build();

    public APIHandler(Context context) {
        this.context = context;
    }

    public interface HolodexAPI_live {
        @GET("live")
        Call<List<LiveStream>> getLiveStreams(@Query("org") String org, @Query("sort") String sort,@Query("max_upcoming_hours") int max_upcoming_hours);
    }

    public interface HolodexAPI_channel {
        @GET("users/live")
        Call<List<LiveStream>> getLiveStreams(@Query("channels") String channels);
    }

    public void getLiveStreams(OnFetchDataListener listener, String query, String filter) {
        HolodexAPI_live holodexAPI_live = retrofit.create(HolodexAPI_live.class);
        Call<List<LiveStream>> call = holodexAPI_live.getLiveStreams(query,"start_scheduled", 72) ;
        try {
            call.enqueue(new retrofit2.Callback<List<LiveStream>>() {
                @Override
                public void onResponse(Call<List<LiveStream>> call, retrofit2.Response<List<LiveStream>> response) {
                    if (response.isSuccessful()) {
                        listener.onFetchData(response.body(), filter);
                        //Toast.makeText(context, "Success: ", Toast.LENGTH_SHORT).show();
                        }
                    else
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();

                }
                @Override
                public void onFailure(Call<List<LiveStream>> call, Throwable t) {
                    listener.onError("Request failed");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
