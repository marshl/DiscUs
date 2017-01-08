package com.marshl.discus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

public class MediaSearchResults extends AppCompatActivity {

    private List<Media> mediaList;

    MediaSearchResults(List<Media> mediaList)
    {
        super();
        this.mediaList = mediaList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_search_results);
    }
}
