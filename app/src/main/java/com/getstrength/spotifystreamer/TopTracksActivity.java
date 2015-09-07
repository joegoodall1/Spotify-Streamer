package com.getstrength.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by joe on 12/07/15.
 */
public class TopTracksActivity extends AppCompatActivity {

    public static final String ARTIST_NAME_KEY = "artist_name";
    public static final String ARTIST_ID_KEY = "artist_id";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.top_tracks_activity);

    }
}
