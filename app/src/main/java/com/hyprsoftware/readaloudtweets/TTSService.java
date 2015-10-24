package com.hyprsoftware.readaloudtweets;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaPlayer;

import com.twitter.sdk.android.core.models.Tweet;

/**
 * Created by suresh on 10/4/2015.
 */
public class TTSService extends IntentService implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private static final String ACTION_PLAY = "com.example.action.PLAY";
    MediaPlayer mMediaPlayer = null;

    public TTSService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Tweet[] tweets = (Tweet[]) intent.getParcelableArrayExtra("data");
        initMediaPlayer();
    }

    public void initMediaPlayer() {
        // ...initialize the MediaPlayer here...
        mMediaPlayer = new MediaPlayer(); // initialize it here
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // ... react appropriately ...
        // The MediaPlayer has moved to the Error state, must be reset!
        return false;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(ACTION_PLAY)) {

            mMediaPlayer.prepareAsync(); // prepare async to not block main thread
        }
        return flags;
    }

    /** Called when MediaPlayer is ready */
    /**
     * Called when the media file is ready for playback.
     *
     * @param player the MediaPlayer that is ready for playback
     */
    @Override
    public void onPrepared(MediaPlayer player) {
        player.start();
    }

}
