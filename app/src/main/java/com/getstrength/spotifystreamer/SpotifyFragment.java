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

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class SpotifyFragment extends Fragment {

    private SpotifyAdapter mSpotifyAdapter;


    public SpotifyFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
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
        SpotifyApi api = new SpotifyApi();

        SpotifyService spotify = api.getService();

        spotify.searchArtists(searchQuery, new Callback<ArtistsPager>() {

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


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        return rootView;
    }

    private void onSpotifyResults(ArtistsPager artistsPager, Response response) {

      


        Activity activity = super.getActivity();

        mSpotifyAdapter = new SpotifyAdapter(activity, artistsPager.artists.items);


        // Get a reference to the ListView, and attach this adapter to it.
        final ListView listView = (ListView) getView().findViewById(R.id.listView);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(mSpotifyAdapter);
            }
        });
    }
}


