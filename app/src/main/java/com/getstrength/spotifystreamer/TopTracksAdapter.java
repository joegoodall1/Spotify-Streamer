package com.getstrength.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;

/**
 * Created by Joe on 09/07/15.
 */
public class TopTracksAdapter extends ArrayAdapter<ParcelableTopTrack> {

    private static final String TRACK_NAME = "track_name";
    private static final String ALBUM_NAME = "album_name";

    private ArrayList<ParcelableTopTrack> mParcelableTopTracks;

    public TopTracksAdapter(Context context, ArrayList<ParcelableTopTrack> parcelableTopTracks) {
        super(context, 0, parcelableTopTracks);
        mParcelableTopTracks = parcelableTopTracks;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        final ParcelableTopTrack parcelableTopTrack = super.getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        // Lookup view for data population
        TextView mTextView = (TextView) convertView.findViewById(R.id.list_artists);
        TextView mTextView2 = (TextView) convertView.findViewById(R.id.album_name);
        ImageView mImageView = (ImageView) convertView.findViewById(R.id.artistView);

        // Populate the data into the template view using the data object
        mTextView.setText(parcelableTopTrack.trackName);
        mTextView2.setText(parcelableTopTrack.albumName);

        if (parcelableTopTrack.albumImage == null) {
            mImageView.setImageResource(R.mipmap.spotify1);
        } else {
            Picasso picasso = Picasso.with(super.getContext());
            RequestCreator requestCreator = picasso.load(parcelableTopTrack.albumImage);
            requestCreator.into(mImageView);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = TopTracksAdapter.this.getContext();
                Intent intent = new Intent(context, SpotifyPlayer.class);
                intent.putParcelableArrayListExtra("TopTracks", mParcelableTopTracks);
                intent.putExtra("index", position);
                context.startActivity(intent);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}