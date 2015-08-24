package com.getstrength.spotifystreamer;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.IOException;
import java.util.ArrayList;


public class SpotifyPlayer extends Activity implements OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private ToggleButton btnPlay;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private SeekBar songProgressBar;

    private TextView artistName;
    private TextView albumName;
    private ImageView albumArtwork;
    private TextView songTitleLabel;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    // Media Player
    private MediaPlayer mp;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private int currentSongIndex = 0;
    private ArrayList<ParcelableTopTrack> mParcelableTopTracks;
    private int mIndex;
    private TopTracksAdapter topTracksAdapter;
    private Utilities utils;

    private boolean paused = false;
    private int trackProgress = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_player);

        // All player buttons
        btnPlay = (ToggleButton) findViewById(R.id.imageButtonPlayPause);
        btnNext = (ImageButton) findViewById(R.id.imageButtonNext);
        btnPrevious = (ImageButton) findViewById(R.id.imageButtonPrevious);
        songProgressBar = (SeekBar) findViewById(R.id.seekBar);
        songTitleLabel = (TextView) findViewById(R.id.trackName);
        songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);

        artistName = (TextView) findViewById(R.id.artistName);
        albumName = (TextView) findViewById(R.id.albumName);
        albumArtwork = (ImageView) findViewById(R.id.albumArtwork);

        // Mediaplayer
        mp = new MediaPlayer();

        //songManager = new SongsManager();
        utils = new Utilities();

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paused = !paused;
                if (paused) {
                    mp.pause();
                    btnPlay.setBackgroundResource(R.drawable.ic_media_play);
                } else {
                    mp.start();
                    btnPlay.setBackgroundResource(R.drawable.ic_media_pause);
                }
            }
        });

        // Listeners
        songProgressBar.setOnSeekBarChangeListener(this); // Important
        mp.setOnCompletionListener(this); // Important

        /**
         * Next button click event
         * Plays next song by taking currentSongIndex + 1
         * */
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int nextIndex = 0;
                if (mIndex != mParcelableTopTracks.size()) {
                    nextIndex = ++mIndex;
                }
                playSong(nextIndex);
            }
        });

        /**
         * Back button click event
         * Plays previous song by currentSongIndex - 1
         * */
        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int nextIndex = 0;
                if (mIndex == 0) {
                    nextIndex = mParcelableTopTracks.size();
                }
                playSong(nextIndex);
            }
        });

        Intent intentTopTracksAdapter = getIntent();
        mParcelableTopTracks = intentTopTracksAdapter.getParcelableArrayListExtra("TopTracks");
        mIndex = intentTopTracksAdapter.getIntExtra("index", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        playSong(mIndex);
    }

    /**
     * Receiving song index from playlist view
     * and play the song
     */
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            currentSongIndex = data.getExtras().getInt("songIndex");
            // play selected song
            playSong(currentSongIndex);
        }

    }

    /**
     * Function to play a song
     *
     * @param songIndex - index of song
     */
    public void playSong(int songIndex) {
        // Play song
        try {
            if (mParcelableTopTracks == null) {
                return;
            } else if (mParcelableTopTracks.size() < songIndex) {
                return;
            }

            mIndex = songIndex;
            ParcelableTopTrack track = mParcelableTopTracks.get(songIndex);

            mp.reset();
            mp.setDataSource(track.trackUrl);
            mp.prepare();
            mp.seekTo(trackProgress);
            mp.start();
            // Displaying Song title

            artistName.setText(track.artistName);
            albumName.setText(track.albumName);
            songTitleLabel.setText(track.trackName);

            Picasso picasso = Picasso.with(this);
            RequestCreator requestCreator = picasso.load(track.albumImage);
            requestCreator.into(albumArtwork);

            // Changing Button Image to pause image
            btnPlay.setBackgroundResource(R.drawable.ic_media_pause);

            // set Progress bar values
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update timer on seekbar
     */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {

            if (mp == null) {
                return;
            }

            long totalDuration = mp.getDuration();
            trackProgress = mp.getCurrentPosition();

            // Displaying Total Duration time
            songTotalDurationLabel.setText("" + utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText("" + utils.milliSecondsToTimer(trackProgress));

            // Updating progress bar
            int progress = utils.getProgressPercentage(trackProgress, totalDuration);
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    /**
     *
     * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * When user starts moving the progress handler
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress handler
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
        mp = null;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }
}
