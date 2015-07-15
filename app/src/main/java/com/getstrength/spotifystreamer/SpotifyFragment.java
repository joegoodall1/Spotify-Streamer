package com.getstrength.spotifystreamer;

/**
 * Created by Joe on 16/06/15.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class SpotifyFragment extends Fragment {

    private static final String SAVE_QUERY_KEY = "query";

    private String mSearchQuery;
    private ArtistsAdapter mArtistsAdapter;
    private SpotifyService mSpotifyService;

    public SpotifyFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);

        SpotifyApi api = new SpotifyApi();
        mSpotifyService = api.getService();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                return performQuery(query);
            }

            @Override
            public boolean onQueryTextChange(String query) {

                return performQuery(query);
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }


    private boolean performQuery(String searchQuery) {

        mSearchQuery = searchQuery;

        mSpotifyService.searchArtists(searchQuery, new Callback<ArtistsPager>() {

            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                SpotifyFragment.this.onSpotifyResults(artistsPager, response);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("fail", error.toString());
            }
        });

        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            String query = savedInstanceState.getString(SpotifyFragment.SAVE_QUERY_KEY);
            if (query != null) {
                performQuery(query);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SpotifyFragment.SAVE_QUERY_KEY, mSearchQuery);
    }

    private void onSpotifyResults(ArtistsPager artistsPager, Response response) {

        final Activity activity = super.getActivity();

        List<Artist> artists = artistsPager.artists.items;
        if (artists.size() == 0) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "No artist found. Please refine search.", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        mArtistsAdapter = new ArtistsAdapter(activity, artists);

        // Get a reference to the ListView, and attach this adapter to it.
        final ListView listView = (ListView) getView().findViewById(R.id.listView);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(mArtistsAdapter);
            }
        });
    }
}


