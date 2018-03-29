package com.example.android.musicplayer;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vega on 05-Mar-18.
 */

public class Song implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
    private Bitmap mSongImage;
    private String mSongID;
    private String mSongTitle; //private member variable of the class
    private String mSongArtist; //private member variable of the class
    private String mSongAlbum; //private member variable of the class
    private String mSongData; //private member variable of the class
    private String mSongGenre;
    private int mSongDuration;

    public Song(Bitmap songImage, String songTitle, String songArtist, String songAlbum, String songGenre, String songData, int songDuration) {

        mSongTitle = songTitle;
        mSongArtist = songArtist;
        mSongAlbum = songAlbum;
        mSongGenre = songGenre;
        mSongData = songData;
        mSongImage = songImage;
        mSongDuration = songDuration;
    }

    protected Song(Parcel in) {
        mSongImage = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
        mSongID = in.readString();
        mSongTitle = in.readString();
        mSongArtist = in.readString();
        mSongAlbum = in.readString();
        mSongData = in.readString();
        mSongGenre = in.readString();
        mSongDuration = in.readInt();
    }


    public Bitmap getmAlbumImg() {


        return mSongImage;
    }

    public String getSongTitle() {
        return mSongTitle;
    }

    public String getSongArtist() {
        return mSongArtist;
    }

    public String getSongAlbum() {
        return mSongAlbum;
    }

    public String getSongData() {
        return mSongData;
    }

    public String getSongGenre() {
        return mSongGenre;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mSongImage);
        dest.writeString(mSongID);
        dest.writeString(mSongTitle);
        dest.writeString(mSongArtist);
        dest.writeString(mSongAlbum);
        dest.writeString(mSongData);
        dest.writeString(mSongGenre);
        dest.writeInt(mSongDuration);
    }

}