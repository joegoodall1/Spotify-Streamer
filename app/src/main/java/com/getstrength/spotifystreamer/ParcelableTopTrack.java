package com.getstrength.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Joe on 27/07/15.
 */
public class ParcelableTopTrack implements Parcelable {

    public final String trackName;
    public final String albumName;
    public final String albumImage;
    public final String trackUrl;
    public final String artistName;

    public ParcelableTopTrack(String trackName, String albumName, String albumImage, String trackUrl, String artistName) {
        this.trackName = trackName;
        this.albumName = albumName;
        this.albumImage = albumImage;
        this.trackUrl = trackUrl;
        this.artistName = artistName;
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
        dest.writeString(this.trackUrl);
        dest.writeString(this.artistName);
    }


    protected ParcelableTopTrack(Parcel in) {
        this.trackName = in.readString();
        this.albumName = in.readString();
        this.albumImage = in.readString();
        this.trackUrl = in.readString();
        this.artistName = in.readString();
    }

    public static final Creator<ParcelableTopTrack> CREATOR = new Creator<ParcelableTopTrack>() {
        public ParcelableTopTrack createFromParcel(Parcel source) {
            return new ParcelableTopTrack(source);
        }

        public ParcelableTopTrack[] newArray(int size) {
            return new ParcelableTopTrack[size];
        }
    };
}
