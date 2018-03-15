package com.example.android.musicplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class MusicListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);

        //we create a new arraylist from our new class Song
        ArrayList<Song> songs = new ArrayList<Song>();

        // now we need to pass the words to be translated. Remember the Song class expects 4 inputs.
        //this is the shortest way to create a new Word object and in the same time add it to the ArrayList songs
        songs.add(new Song (getResources().getDrawable(R.drawable.na),"Blackened","Metallica","And Justice for All", "Metal"));


    }
}
