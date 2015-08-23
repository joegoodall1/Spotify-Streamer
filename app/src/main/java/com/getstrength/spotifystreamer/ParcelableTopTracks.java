package com.getstrength.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Joe on 27/07/15.
 */
public class ParcelableTopTracks implements Parcelable {

    public final String trackName;
    public final String albumName;
    public final String albumImage;

    public ParcelableTopTracks(String trackName, String albumName, String albumImage) {
        this.trackName = trackName;
        this.albumName = albumName;
        this.albumImage = albumImage;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.trackName);
        dest.writeString(this.albumName);
        dest.writeString(this.albumImage);
    }


    protected ParcelableTopTracks(ParcelableTopTracks in) {
        this.trackName = in.readString();
        this.albumName = in.readString();
        this.albumImage = in.readString();
    }

    public static final Creator<ParcelableTopTracks> CREATOR = new Creator<ParcelableTopTracks>() {
        public ParcelableTopTracks createFromParcel(Parcel source) {
            return new ParcelableTopTracks(source);
        }

        public ParcelableTopTracks[] newArray(int size) {
            return new ParcelableTopTracks[size];
        }
    };
}
