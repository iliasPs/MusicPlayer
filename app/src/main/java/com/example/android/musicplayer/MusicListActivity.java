package com.example.android.musicplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
       private MediaPlayer mMediaPlayer;
       private AudioManager mAudioManager;
//
//    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
//            new AudioManager.OnAudioFocusChangeListener() {
//                @Override
//                public void onAudioFocusChange(int focusChange) {
//                    if ((focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK)) {
//                        //in both cases (loss transient or loss transient can duck we need to pause the player
//                        //Pause playback
//                        mMediaPlayer.pause();
//                        //Start the audio track from the start since our files are to small in duration
//                        mMediaPlayer.seekTo(0);
//                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
//                        //this means we gained focus so we start(); the media player
//                        mMediaPlayer.start();
//                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
//                        //we lost focus so we stop playback and clear resources through our custom method.
//                        releaseMediaPlayer();
//                    }
//                }
//            };
//
//    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
//        @Override
//        public void onCompletion(MediaPlayer mediaPlayer) {//we added the completion listener implementation to a global variable
//            // Now that the sound file has finished playing, release the media player resources.
//            releaseMediaPlayer();
//        }
//    };

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
//
//                releaseMediaPlayer();// we are releasing the memory usage at the start and in the end of the media played.
//                //since we initialized the AudioManager instance we can now requestFocus on it - which returns an int and it is a constant value - and we do it after the release
//                int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener //we need a focusListener
//                        , AudioManager.STREAM_MUSIC, //use the music stream
//                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT); // and how long - in this case is temporary
//                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) { //we are checking to see if we got the focus
//
//                    mMediaPlayer = MediaPlayer.create(MusicListActivity.this, Uri.parse(song.getSongData()));
//                    mMediaPlayer.start();

                    //Intent goToPlayer = new Intent (Intent.ACTION_VIEW);//this is used to send media uri
                Intent songsSend = new Intent();
                songsSend.setClass(MusicListActivity.this, PlayerActivity.class);
                songsSend.putExtra("songs", songs);
                Intent songtoplay = new Intent(MusicListActivity.this,PlayerActivity.class);
                songtoplay.putExtra("songtoplay", position);

//                    goToPlayer.setClass(MusicListActivity.this, PlayerActivity.class);
//                    String songtosend = Uri.parse(song.getSongData()).toString();
//                    goToPlayer.putExtra("song", songtosend);
                   startActivity(songtoplay);

                    // we are releasing the memory usage at the start and in the end of the media played.
                    //also check the mCompletionListener
//                    mMediaPlayer.setOnCompletionListener(mCompletionListener);
                }});}


    public void getMusic() {
        String[] genres = {
                MediaStore.Audio.Genres.NAME,
                MediaStore.Audio.Genres._ID
        };
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//setting the uri for the song cursor
        Cursor genreCursor;
        String currentGenre;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);
        if (songCursor.moveToFirst()) {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songAlbum = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int songID = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);


            do {
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentAlbum = songCursor.getString(songAlbum);
                String currentData = songCursor.getString(songData);


                int musicID = Integer.parseInt(songCursor.getString((songID)));// setting the track id to get the metadata later on
                Uri uri = MediaStore.Audio.Genres.getContentUriForAudioId("external", musicID); // setting the uri for the genre cursor
                genreCursor = getBaseContext().getContentResolver().query(uri, genres, null, null, null);
                int genreColumnIndex = genreCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);//getting the name of the genre through the index of the track
                if (genreCursor.moveToFirst() && genreCursor.getString(genreColumnIndex) != null) {//i need this && because i must check if the data is there in the first place (ie the genre tag might be missing)
                    do {                                                                           // maybe i should do a try command? - i ll check that later
                        currentGenre = genreCursor.getString(genreColumnIndex);

                    } while (genreCursor.moveToNext());
                } else {
                    currentGenre = "N/A";
                }
                songs.add(new Song(currentTitle, currentArtist, currentAlbum, currentGenre, currentData));//feeding the custom class

            } while (songCursor.moveToNext());



        }
        int i;
        for (i=0; i<songs.size(); i++){
            Log.v("musiclistactivity", "the data on position are " + songs.get(i).getSongAlbum() + songs.get(i).getSongArtist() + songs.get(i).getSongTitle() + songs.get(i).getSongData()) ;}
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

//    /**
//     * Clean up the media player by releasing its resources.
//     */
//    private void releaseMediaPlayer() {
//        // If the media player is not null, then it may be currently playing a sound.
//        if (mMediaPlayer != null) {
//            // Regardless of the current state of the media player, release its resources
//            // because we no longer need it.
//            mMediaPlayer.release();
//            // Set the media player back to null. For our code, we've decided that
//            // setting the media player to null is an easy way to tell that the media player
//            // is not configured to play an audio file at the moment.
//            mMediaPlayer = null;
//            //abandon audio focus when playback is complete.
//            //unregisters the AudioFocusChangeListener so we dont get anymore callbacks
//            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        // When the activity is stopped, release the media player resources because we won't
//        // be playing any more sounds.
//        releaseMediaPlayer();
//    }
}
