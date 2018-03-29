package com.example.android.musicplayer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MY_PERMISSION_REQUEST = 1;


    Handler handler;
    Runnable runnable;
    ImageButton play_pause;
    ImageButton next;
    ImageButton previous;
    ImageButton repeat;
    ImageView cover;
    Button backLib;
    TextView songTitleTV;
    TextView startDuration;
    TextView endDuration;
    SeekBar seekBar;
    Song song;
    int position;
    ArrayList<Song> songsList = new ArrayList<>();
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
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
        handler = new Handler();
        setContentView(R.layout.activity_player);
        songsList = getIntent().getParcelableArrayListExtra("songs");
        cover = findViewById(R.id.cover);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        play_pause = findViewById(R.id.play_pause);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.prev);
        repeat = findViewById(R.id.repeat);
        play_pause.setOnClickListener(this);
        next.setOnClickListener(this);
        previous.setOnClickListener(this);
        repeat.setOnClickListener(this);
        songTitleTV = findViewById(R.id.textView);
        startDuration = findViewById(R.id.startDuration);
        endDuration = findViewById(R.id.endDuration);
        seekBar = findViewById(R.id.seekBar);
        backLib = findViewById(R.id.lib);
        backLib.setOnClickListener(this);
        String songStart = String.format("%02d:%02d", 0, 0);
        String songEnd = String.format("%02d:%02d", 0, 0);
        startDuration.setText(songStart);
        endDuration.setText(songEnd);
        position = getIntent().getIntExtra("songtoplay", 0);
        song = songsList.get(position);
        songTitleTV.setText(song.getSongTitle());
        Bitmap songCover = songsList.get(position).getmAlbumImg();
        cover.setImageBitmap(songCover);

        if (ContextCompat.checkSelfPermission(PlayerActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(PlayerActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(PlayerActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(PlayerActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play_pause: {
                play_pause.setImageResource(R.drawable.pause);
                play(song.getSongData());
                break;
            }
            case R.id.next: {
                position++;
                if (position >= songsList.size()) {//setting this so we dont get out of bounds

                    Toast.makeText(PlayerActivity.this, getResources().getString(R.string.nomore), Toast.LENGTH_SHORT).show();
                }

                position = songsList.size() - 1;
                Song nextSong = songsList.get(position);
                String songTitle = songsList.get(position).getSongTitle();
                Bitmap songCover = songsList.get(position).getmAlbumImg();
                cover.setImageBitmap(songCover);
                songTitleTV.setText(songTitle);
                String nxt = nextSong.getSongData();
                play(nxt);
            }
            break;

            case R.id.prev: {
                position--;
                if (position < 0) {//setting this so we dont get out of bounds
                    Toast.makeText(PlayerActivity.this, getResources().getString(R.string.nomore), Toast.LENGTH_SHORT).show();
                }

                position = 0;

                Song prevSong = songsList.get(position);
                String songTitle = songsList.get(position).getSongTitle();
                Bitmap songCover = songsList.get(position).getmAlbumImg();
                cover.setImageBitmap(songCover);
                songTitleTV.setText(songTitle);
                String prv = prevSong.getSongData();
                play(prv);
            }
            break;

            case R.id.lib:{
                Intent i = new Intent();
                i.setClass(PlayerActivity.this, MusicListActivity.class);
                startActivity(i);
            }

        }
    }

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

    private void duration(int dur) {
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000 / 1000;
        String songTime = String.format("%02d:%02d", mns, scs);
        endDuration.setText(songTime);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            play_pause.setImageResource(R.drawable.play);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMediaPlayer != null) {
            mMediaPlayer.start();

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
    }


    private boolean requestFocus() {
        //we are checking to see if we got the focus
        int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener //we need a focusListener
                , AudioManager.STREAM_MUSIC, //use the music stream
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT); // and how long - in this case is temporary
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return true;
        } else {
            // we are releasing the memory usage at the start and in the end of the media played.
            //also check the mCompletionListener
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            return false;
        }
    }


    private void play(String data) {

        if (requestFocus()) {
            mMediaPlayer = MediaPlayer.create(PlayerActivity.this, Uri.parse(data));
            mMediaPlayer.start();
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            final int dur = mMediaPlayer.getDuration();
            duration(dur);
            setSeekBar();
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mMediaPlayer != null) {
                        seekBar.setProgress(mMediaPlayer.getCurrentPosition());
                        seekBar.setMax(mMediaPlayer.getDuration());
                    }
                    handler.postDelayed(this, 25);
                }
            });
            if (mMediaPlayer.isPlaying()) {
                setSeekBar();
                play_pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onPause();
                        play_pause.setImageResource(R.drawable.play);
                    }
                });
            } else {
                onResume();
                setSeekBar();
                play_pause.setImageResource(R.drawable.pause);
                duration(mMediaPlayer.getDuration());

                mMediaPlayer.start();
            }

        }
    }


    private void setSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mMediaPlayer != null && fromUser) {
                    mMediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


}
