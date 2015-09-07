package com.getstrength.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements ArtistsAdapter.ArtistsAdapterListener {

    private TopTracksFragment mTracksFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTracksFragment = (TopTracksFragment) super.getSupportFragmentManager().findFragmentById(R.id.tracks_frag);

        SpotifyFragment artistFragment = (SpotifyFragment) super.getSupportFragmentManager().findFragmentById(R.id.artist_frag);
        artistFragment.setArtistListener(this);

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        String answer = null;
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                answer = "You are connected to a WiFi Network";
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                answer = "You are connected to a Mobile Network";
        } else
            answer = "No internet Connectivity";
        Toast.makeText(getApplicationContext(), answer, Toast.LENGTH_LONG).show();
    }

    public void onArtistsSelected(String artistId, String artistName) {
        if (mTracksFragment != null) {
            mTracksFragment.setArtist(artistId);
        } else {
            Intent intent = new Intent(this, TopTracksActivity.class);
            intent.putExtra(TopTracksActivity.ARTIST_NAME_KEY, artistName);
            intent.putExtra(TopTracksActivity.ARTIST_ID_KEY, artistId);
            super.startActivity(intent);
        }
    }
}


