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

/**
 * Created by Joe on 09/07/15.
 */
public class ArtistsAdapter extends ArrayAdapter<ParcelableArtist> {

    public interface ArtistsAdapterListener {
        void onArtistsSelected(String artistId, String artistName);
    }

    private ArtistsAdapterListener mListener;

    public ArtistsAdapter(Context context, List<ParcelableArtist> parcelableArtists, ArtistsAdapterListener listener) {
        super(context, 0, parcelableArtists);
        mListener = listener;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        final ParcelableArtist parcelableArtist = super.getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }


        // Lookup view for data population
        TextView mTextView = (TextView) convertView.findViewById(R.id.list_artists);
        ImageView mImageView = (ImageView) convertView.findViewById(R.id.artistView);

        // Populate the data into the template view using the data object
        mTextView.setText(parcelableArtist.name);

        if (parcelableArtist.imageURL == null) {
            mImageView.setImageResource(R.mipmap.spotify1);
        } else {
            Picasso picasso = Picasso.with(super.getContext());
            RequestCreator requestCreator = picasso.load(parcelableArtist.imageURL);
            requestCreator.into(mImageView);
        }


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onArtistsSelected(parcelableArtist.id, parcelableArtist.name);
                }
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}