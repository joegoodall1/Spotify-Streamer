package com.getstrength.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by Joe on 01/09/15.
 */
public class TopTracksFragment extends Fragment {

    private ListView mListView;
    private TopTracksAdapter mTracksAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) super.getActivity()).getSupportActionBar();
        actionBar.setCustomView(R.layout.top_ten_action_bar);
        actionBar.setDisplayHomeAsUpEnabled(true);


        Bundle extras = super.getActivity().getIntent().getExtras();
        if (extras != null) {
            String artistName = extras.getString("artist_name");
            TextView artistNameView = (TextView) actionBar.getCustomView().findViewById(R.id.action_bar_artist);
            artistNameView.setText(artistName);
        }

        mListView = (ListView) view.findViewById(R.id.listView);

        if (savedInstanceState != null) {
            ArrayList<ParcelableTopTrack> tracks = savedInstanceState.getParcelableArrayList("tracks");
            if (tracks != null) {
                mTracksAdapter = new TopTracksAdapter(super.getActivity(), tracks);
                mTracksAdapter.addAll(tracks);
                mListView.setAdapter(mTracksAdapter);
            }
        } else {
            if (extras != null) {
                String artistId = extras.getString("artist_id");
                if (artistId != null) {
                    setArtist(artistId);
                }
            }
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
                TopTracksFragment.this.getActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setArtist(String artistId) {

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotifyService = api.getService();

        Map<String, Object> options = new Hashtable<>();
        options.put("country", Locale.getDefault().getCountry());

        spotifyService.getArtistTopTrack(artistId, options, new Callback<Tracks>() {

            @Override
            public void success(Tracks tracks, Response response) {
                TopTracksFragment.this.onSpotifyResults(tracks, response);
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
            super.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(TopTracksFragment.super.getActivity(), "No tracks found. Please refine search.", Toast.LENGTH_SHORT).show();
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

        mTracksAdapter = new TopTracksAdapter(super.getActivity(), list);

        super.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListView.setAdapter(mTracksAdapter);
            }
        });
    }
}
