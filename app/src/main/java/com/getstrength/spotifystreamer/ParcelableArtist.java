package com.getstrength.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Joe on 18/07/15.
 */
public class ParcelableArtist implements Parcelable {

    public final String name;
    public final String imageURL;
    public final String id;

    public ParcelableArtist(String name, String imageURL, String id) {
        this.name = name;
        this.imageURL = imageURL;
        this.id = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.imageURL);
        dest.writeString(this.id);
    }

    protected ParcelableArtist(Parcel in) {
        this.name = in.readString();
        this.imageURL = in.readString();
        this.id = in.readString();
    }

    public static final Creator<ParcelableArtist> CREATOR = new Creator<ParcelableArtist>() {
        public ParcelableArtist createFromParcel(Parcel source) {
            return new ParcelableArtist(source);
        }

        public ParcelableArtist[] newArray(int size) {
            return new ParcelableArtist[size];
        }
    };
}