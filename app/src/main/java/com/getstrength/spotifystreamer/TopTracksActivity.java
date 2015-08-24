package com.getstrength.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
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
public class TopTracksActivity extends AppCompatActivity {

    private ListView mListView;
    private TopTracksAdapter mTracksAdapter;

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

        mListView = (ListView) super.findViewById(R.id.listView);

        if (savedInstanceState != null) {
            ArrayList<ParcelableTopTrack> tracks = savedInstanceState.getParcelableArrayList("tracks");
            if (tracks != null) {
                mTracksAdapter = new TopTracksAdapter(this, tracks);
                mTracksAdapter.addAll(tracks);
                mListView.setAdapter(mTracksAdapter);
            }
        } else {
            initializeAdapter();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<ParcelableTopTrack> list = new ArrayList<>();
        for (int i = 0; i < mTracksAdapter.getCount(); i++) {
            list.add(mTracksAdapter.getItem(i));
        }

        outState.putParcelableArrayList("tracks", list);
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

        final ArrayList<ParcelableTopTrack> list = new ArrayList<>();

        for (Track track : tracks.tracks) {
            String url;
            if (track.album.images.size() == 0) {
                url = null;
            } else {
                url = track.album.images.get(0).url;
            }

            String artistName = "Unknown";
            if (track.artists.size() > 0) {
                artistName = track.artists.get(0).name;
            }

            list.add(new ParcelableTopTrack(track.name, track.album.name, url, track.preview_url, artistName));
        }

        mTracksAdapter = new TopTracksAdapter(this, list);

        super.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListView.setAdapter(mTracksAdapter);
            }
        });
    }
}
