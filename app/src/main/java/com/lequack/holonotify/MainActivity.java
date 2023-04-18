package com.lequack.holonotify;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lequack.holonotify.models.LiveStream;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    RecyclerView recyclerView;
    Adapter adapter;
    private final OnFetchDataListener<List<LiveStream>> listener = new OnFetchDataListener<List<LiveStream>>() {
        @Override
        public void onFetchData(List<LiveStream> data, String filter) {
            displayData(filterLiveStreams(data, filter));
        }

        @Override
        public void onError(String message) {

        }
    };
    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            apiHandler.getLiveStreams(listener, query, filter);
            //Toast.makeText(MainActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
            handler.postDelayed(this, 60000); // Call this runnable again after 5 seconds
        }
    };
    SearchView searchView;
    SwipeRefreshLayout swipeRefreshLayout;
    String query = "Hololive";
    String filter = "";
    APIHandler apiHandler = new APIHandler(this);
    BottomNavigationView bottomNavigationView;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Refresh every 60 seconds
        handler.postDelayed(refreshRunnable, 0);

        //SearchView
        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String filterQuery) {
                filter = filterQuery;

                apiHandler.getLiveStreams(listener, query, filter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String filterQuery) {
                filter = filterQuery;
                apiHandler.getLiveStreams(listener, query, filter);
                return false;
            }


        });
        //SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Perform refresh action here
                apiHandler.getLiveStreams(listener, query, filter);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        //BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item:
                        if (query.equals("Hololive")) {
                            query = "Nijisanji";
                            item.setIcon(R.drawable.niji); //change icon and color
                            //set bottom navigation bar color
                            bottomNavigationView.getBackground().setTint(getColor(R.color.holo));
                        } else{
                            query = "Hololive";
                            item.setIcon(R.drawable.holo);
                            bottomNavigationView.getBackground().setTint(getColor(R.color.niji));}
                        apiHandler.getLiveStreams(listener, query, filter);
                        return true;

                    default:
                        return true;
                }
            }
        });
    }

    private void displayData(List<LiveStream> data) {
        recyclerView = findViewById(R.id.recyclerView_main);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, data, this);
        recyclerView.setAdapter(adapter);
    }

    //Filtering if the title or channel name contains the filtered word
    public List<LiveStream> filterLiveStreams(List<LiveStream> liveStreams, String filter) {
        List<LiveStream> filteredList = new ArrayList<>();
        for (LiveStream liveStream : liveStreams) {
            if (liveStream.getTitle().toLowerCase().contains(filter.toLowerCase()) ||
                    liveStream.getChannel().getName().toLowerCase().contains(filter.toLowerCase())) {
                filteredList.add(liveStream);
            }
        }
        return filteredList;
    }

    //Redirecting to the YouTube video
    @Override
    public void onItemClick(LiveStream stream) {
        String URL = "https://www.youtube.com/watch?v=" + stream.getId();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
        startActivity(intent);
    }
}