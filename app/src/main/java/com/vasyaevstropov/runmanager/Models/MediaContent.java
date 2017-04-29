package com.vasyaevstropov.runmanager.Models;

import android.os.Parcel;
import android.os.Parcelable;


public class MediaContent implements Parcelable{
    private String title;
    private String artist;
    private String Uri;

    public static final String currentSong = "CURRENT_SONG";

    protected MediaContent(Parcel in) {
        title = in.readString();
        artist = in.readString();
        Uri = in.readString();
    }

    public MediaContent(String title, String artist, String uri) {
        this.title = title;
        this.artist = artist;
        Uri = uri;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(Uri);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MediaContent> CREATOR = new Creator<MediaContent>() {
        @Override
        public MediaContent createFromParcel(Parcel in) {
            return new MediaContent(in);
        }

        @Override
        public MediaContent[] newArray(int size) {
            return new MediaContent[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getUri() {
        return Uri;
    }

    public static Creator<MediaContent> getCREATOR() {
        return CREATOR;
    }

}
