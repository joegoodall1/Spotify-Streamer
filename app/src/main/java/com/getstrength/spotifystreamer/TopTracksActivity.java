package com.getstrength.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by joe on 12/07/15.
 */
public class TopTracksActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        ActionBar actionBar = super.getSupportActionBar();
        actionBar.setCustomView(R.layout.top_ten_action_bar);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Top 10 Tracks");

        Bundle extras = super.getIntent().getExtras();
        String artistName = extras.getString("artist_name");
        TextView artistNameView = (TextView) actionBar.getCustomView().findViewById(R.id.action_bar_artist);
        artistNameView.setText(artistName);

        initializeAdapter();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                TopTracksActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeAdapter() {

        Bundle extras = super.getIntent().getExtras();
        String artistId = extras.getString("artist_id");
        if (artistId == null) {
            return;
        }

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotifyService = api.getService();

        Map<String, Object> options = new Hashtable<>();
        options.put("country", Locale.getDefault().getCountry());

        spotifyService.getArtistTopTrack(artistId, options, new Callback<Tracks>() {

            @Override
            public void success(Tracks tracks, Response response) {
                TopTracksActivity.this.onSpotifyResults(tracks, response);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("fail", error.toString());
            }
        });
    }

    private void onSpotifyResults(Tracks tracks, Response response) {

        List<Track> trackItems = tracks.tracks;

        if (trackItems.size() == 0) {
            super.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(TopTracksActivity.this, "No tracks found. Please refine search.", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        final TopTracksAdapter tracksAdapter = new TopTracksAdapter(this, trackItems);

        // Get a reference to the ListView, and attach this adapter to it.
        final ListView listView = (ListView) super.findViewById(R.id.listView);

        super.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(tracksAdapter);
            }
        });
    }
}
