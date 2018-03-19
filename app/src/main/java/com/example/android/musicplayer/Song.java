package com.example.android.musicplayer;

/**
 * Created by vega on 05-Mar-18.
 */

public class Song {

    private int mAlbumImg;
    private String mSongID;
    private String mSongTitle; //private member variable of the class
    private String mSongArtist; //private member variable of the class
    private String mSongAlbum; //private member variable of the class
    private String mSongData; //private member variable of the class
    private String mSongGenre;




    public Song(/*int albumImg,*/ String songTitle, String songArtist, String songAlbum, String songData) {
//        mAlbumImg = albumImg;
        mSongTitle = songTitle;
        mSongArtist = songArtist;
        mSongAlbum = songAlbum;
        mSongData = songData;



    }

    public Song(String songID, String songTitle, String songArtist, String songAlbum, String songGenre) {
        mSongID = songID;
        mSongTitle = songTitle;
        mSongArtist = songArtist;
        mSongAlbum = songAlbum;
        mSongGenre = songGenre;

    }


//    public int getAlbumImg() {
//        return mAlbumImg;
//    }

    public String getSongID(){
        return mSongID;
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
    public String getSongGenre(){
        return mSongGenre;
    }

}



