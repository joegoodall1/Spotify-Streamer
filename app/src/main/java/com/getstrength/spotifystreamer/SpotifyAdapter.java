package com.getstrength.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;


/**
 * Created by Joe on 09/07/15.
 */
public class SpotifyAdapter extends ArrayAdapter<Artist> {

    public SpotifyAdapter(Context context, List<Artist> artists) {
        super(context, 0, artists);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Artist artist = super.getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        // Lookup view for data population
        TextView mTextView = (TextView) convertView.findViewById(R.id.list_artists);
        ImageView mImageView = (ImageView) convertView.findViewById(R.id.artistView);
        // Populate the data into the template view using the data object
        mTextView.setText(artist.name);

        List<Image> images = artist.images;

        if (images.size() == 0) {
            mImageView.setImageResource(R.mipmap.spotify1);
        }
        else {
            Picasso picasso = Picasso.with(super.getContext());
            RequestCreator requestCreator = picasso.load(images.get(0).url);
            requestCreator.into(mImageView);
        }



        // Return the completed view to render on screen
        return convertView;
    }
}