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

import java.util.ArrayList;
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

    private ArtistsAdapter mArtistsAdapter;
    private SpotifyService mSpotifyService;

    private ArtistsAdapter.ArtistsAdapterListener mArtistListener;

    public void setArtistListener(ArtistsAdapter.ArtistsAdapterListener artistListener) {
        mArtistListener = artistListener;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<ParcelableArtist> list = new ArrayList<>();
        for (int i = 0; i < mArtistsAdapter.getCount(); i++) {
            list.add(mArtistsAdapter.getItem(i));
        }


        outState.putParcelableArrayList("artists", list);
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

        mSpotifyService.searchArtists(searchQuery, new Callback<ArtistsPager>() {

            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                SpotifyFragment.this.onSpotifyResults(artistsPager);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("fail", error.toString());
            }
        });

        return true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mArtistsAdapter = new ArtistsAdapter(getActivity(), new ArrayList<ParcelableArtist>(), mArtistListener);
        if (savedInstanceState != null) {
            ArrayList<ParcelableArtist> artists = savedInstanceState.getParcelableArrayList("artists");
            if (artists != null)
                mArtistsAdapter.addAll(artists);
        }
        final ListView listView = (ListView) getView().findViewById(R.id.listView);
        listView.setAdapter(mArtistsAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    


    private void onSpotifyResults(ArtistsPager artistsPager) {

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
        final List<ParcelableArtist> list = new ArrayList<>();

        for (Artist artist : artists) {
            String url;
            if (artist.images.size() == 0) {
                url = null;
            } else {
                url = artist.images.get(0).url;
            }
            list.add(new ParcelableArtist(artist.name, url, artist.id));
        }


        // Get a reference to the ListView, and attach this adapter to it.

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mArtistsAdapter.clear();
                mArtistsAdapter.addAll(list);
            }
        });
    }
}


