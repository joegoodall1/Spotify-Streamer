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

import java.util.List;

/**
 * Created by Joe on 09/07/15.
 */
public class TopTracksAdapter extends ArrayAdapter<ParcelableTopTracks> {

    private static final String TRACK_NAME = "track_name";
    private static final String ALBUM_NAME = "album_name";

    public TopTracksAdapter(Context context, List<ParcelableTopTracks> parcelableTopTracks) {
        super(context, 0, parcelableTopTracks);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        final ParcelableTopTracks parcelableTopTracks = super.getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        // Lookup view for data population
        TextView mTextView = (TextView) convertView.findViewById(R.id.list_artists);
        TextView mTextView2 = (TextView) convertView.findViewById(R.id.album_name);
        ImageView mImageView = (ImageView) convertView.findViewById(R.id.artistView);


        // Populate the data into the template view using the data object
        mTextView.setText(parcelableTopTracks.trackName);
        mTextView2.setText(parcelableTopTracks.albumName);


        if (parcelableTopTracks.albumImage == null) {
            mImageView.setImageResource(R.mipmap.spotify1);
        } else {
            Picasso picasso = Picasso.with(super.getContext());
            RequestCreator requestCreator = picasso.load(parcelableTopTracks.albumImage);
            requestCreator.into(mImageView);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = TopTracksAdapter.this.getContext();
                Intent intent = new Intent(context, SpotifyPlayer.class);
                intent.putExtra(TopTracksAdapter.TRACK_NAME, parcelableTopTracks.trackName);
                intent.putExtra(TopTracksAdapter.ALBUM_NAME, parcelableTopTracks.albumName);
                context.startActivity(intent);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}