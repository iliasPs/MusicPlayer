package com.example.android.musicplayer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 1;
    ImageButton play;
    ImageButton next;
    ImageButton previous;
    ImageButton repeat;
    TextView songTitleTV;

    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    ArrayList<Song> songs = new ArrayList<>();


    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if ((focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK)) {
                        //in both cases (loss transient or loss transient can duck we need to pause the player
                        //Pause playback
                        mMediaPlayer.pause();
                        //Start the audio track from the start since our files are to small in duration
                        mMediaPlayer.seekTo(0);
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        //this means we gained focus so we start(); the media player
                        mMediaPlayer.start();
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        //we lost focus so we stop playback and clear resources through our custom method.
                        releaseMediaPlayer();
                    }
                }
            };
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {//we added the completion listener implementation to a global variable
            // Now that the sound file has finished playing, release the media player resources.
            releaseMediaPlayer();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        songs = getIntent().getParcelableArrayListExtra("songs");
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        play = findViewById(R.id.play_pause);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.prev);
        songTitleTV = findViewById(R.id.textView);
        final int position=getIntent().getIntExtra("songtoplay", 0);
        final Song song = songs.get(position);
        songTitleTV.setText(song.getSongTitle());
        if (ContextCompat.checkSelfPermission(PlayerActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(PlayerActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(PlayerActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(PlayerActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        } else {
            player();
        }
    }

    public void player() {
//        Intent i = getIntent();
//        final String songToplay = i.getStringExtra("song");
//        Uri uri = Uri.parse(songToplay);
//        Log.v("playeractivity", "song to play is " + songToplay);
//        Log.v("playeractivity", "uri to play is " + uri);

        final int position=getIntent().getIntExtra("songtoplay", 0);
        final Song song = songs.get(position);
        if (songs.get(position) !=null){

            int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener //we need a focusListener
                    , AudioManager.STREAM_MUSIC, //use the music stream
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT); // and how long - in this case is temporary
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) { //we are checking to see if we got the focus

                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Uri fileUri = Uri.parse(songToplay);


                        mMediaPlayer = MediaPlayer.create(PlayerActivity.this, Uri.parse(song.getSongData()));
                        mMediaPlayer.start();
                    }
                });
                // we are releasing the memory usage at the start and in the end of the media played.
                //also check the mCompletionListener
//                mMediaPlayer.setOnCompletionListener(mCompletionListener);
            }

        }}


    /**
     * Clean up the media player by releasing its resources.
     */
    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mMediaPlayer.release();
            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mMediaPlayer = null;
            //abandon audio focus when playback is complete.
            //unregisters the AudioFocusChangeListener so we dont get anymore callbacks
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSION_REQUEST: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (ContextCompat.checkSelfPermission(MusicListActivity.this,
//                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMI SSION_GRANTED) {
//                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
//                        popList();
//                    } else {
//                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//                    return;
//                }
//            }
//        }
//    }
}
