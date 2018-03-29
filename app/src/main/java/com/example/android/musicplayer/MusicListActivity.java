package com.example.android.musicplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MusicListActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 1;
    ArrayList<Song> songs = new ArrayList<>();
    AudioManager mAudioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        setContentView(R.layout.activity_music_list);

        if (ContextCompat.checkSelfPermission(MusicListActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MusicListActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MusicListActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(MusicListActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        } else {
            popList();
        }
    }

    public void popList() {
        ListView listView = findViewById(R.id.list);
        getMusic();
        SongAdapter songAdapter = new SongAdapter(this, songs);
        listView.setAdapter(songAdapter);
        //we create a new arraylist from our new class Song
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Song song = songs.get(position);
                Intent songsSend = new Intent(MusicListActivity.this, PlayerActivity.class);
                songsSend.putExtra("songs", songs);
                songsSend.putExtra("songtoplay", position);

                startActivity(songsSend);

            }
        });
    }


    public void getMusic() {
        String[] genres = {
                MediaStore.Audio.Genres.NAME,
                MediaStore.Audio.Genres._ID,
        };

        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//setting the uri for the song cursor
        Cursor genreCursor;

        String currentGenre;
        Bitmap currentImage = null;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);
        if (songCursor.moveToFirst()) {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songAlbum = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int songID = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int songDuration = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);


            do {
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentAlbum = songCursor.getString(songAlbum);
                String currentData = songCursor.getString(songData);
                int currentDuration = songDuration;


                int musicID = Integer.parseInt(songCursor.getString((songID)));// setting the track id to get the metadata later on
                Uri uri = MediaStore.Audio.Genres.getContentUriForAudioId("external", musicID); // setting the uri for the genre cursor
                genreCursor = getBaseContext().getContentResolver().query(uri, genres, null, null, null);

                Cursor imgCursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                        MediaStore.Audio.Albums._ID + "=?",
                        new String[]{String.valueOf(MediaStore.Audio.Albums._ID)},
                        null);
                int genreColumnIndex = genreCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);//getting the name of the genre through the index of the track
                if (genreCursor.moveToFirst() && genreCursor.getString(genreColumnIndex) != null) {//i need this && because i must check if the data is there in the first place (ie the genre tag might be missing)
                    do {
                        currentGenre = genreCursor.getString(genreColumnIndex);
                        if (imgCursor.moveToFirst()) {
                            do {

                                String imgPath = imgCursor.getString(imgCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                                Log.v("Musiclistactivity", "img path is" + imgPath);
                                currentImage = BitmapFactory.decodeFile(imgPath);


                            } while (imgCursor.moveToNext());

                        } else {
                            currentImage = BitmapFactory.decodeResource(getResources(), R.drawable.nocover);
                        }

                    } while (genreCursor.moveToNext());
                } else {
                    currentGenre = "N/A";
                }
                songs.add(new Song(currentImage, currentTitle, currentArtist, currentAlbum, currentGenre, currentData, currentDuration));//feeding the custom class

            } while (songCursor.moveToNext());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MusicListActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        popList();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    return;
                }
            }
        }
    }


}
