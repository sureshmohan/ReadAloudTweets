package com.hyprsoftware.readaloudtweets;

import android.app.IntentService;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twitter.sdk.android.core.models.Tweet;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by suresh on 10/4/2015.
 */
public class TTSService extends IntentService implements TextToSpeech.OnInitListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    public static final String ACTION_PLAY = "com.hyprsoftware.action.PLAY";
    MediaPlayer mMediaPlayer = null;
    private TextToSpeech tts;
    private boolean ready = false;
    private ArrayList<Tweet> tweets;
    private File destFile;
    private int currIndex = 0;

    public TTSService(){
        super(TTSService.class.getName());
    }

    public TTSService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public void onCreate(){
        super.onCreate();

        try {
            destFile = File.createTempFile("tts", ".wav", getFilesDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
        initMediaPlayer();
        tts = new TextToSpeech(this, this);
    }

    @Override
    public void onDestroy(){
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                ready = true;
                currIndex = 0;
                if(tweets != null && tweets.size() > 0){
                    Tweet twt = tweets.get(currIndex);
                    currIndex++;
                    String text = twt.text;
                    if (text!=null && text.length()>0) {
                        SpeakAsync(text);
                    }
                }
            }
        }
    }

    private void SpeakAsync(String text) {
        //trial synth to file here
        HashMap<String, String> myHashRender = new HashMap<String, String>();
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, text);

        try {
            //tts.synthesizeToFile(text,null, destFile, text);
            tts.speak(text, TextToSpeech.QUEUE_FLUSH,null,"Sfsdf");
        } catch (Exception e){
            e.printStackTrace();
        }

        //todo hand to media player for play pause etc
        //todo handle delete of the file at some stage - creation of activity?

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            public void onDone(String utteranceId) {
                // Speech file is created
                // Initializes Media Player
                //Log.d("File created ", "init mediaplayer then call playAudio()");
                initializeMediaPlayer();
            }

            @Override
            public void onError(String utteranceId) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStart(String utteranceId) {
                // TODO Auto-generated method stub
            }
        });
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
            String tweetstr =  intent.getStringExtra("data");
            final Type typeOf = new TypeToken<ArrayList<Tweet>>() {}.getType();
            tweets = new Gson().fromJson(tweetstr, typeOf);
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
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                //Log.d("MediaPlayer", " finished so release");
                mediaPlayer.reset();

                //Now move to next item
                if(currIndex <= tweets.size()) {
                    Tweet twt = tweets.get(currIndex);
                    currIndex++;
                    String text = twt.text;
                    if (text!=null && text.length()>0) {
                        SpeakAsync(text);
                    }
                }
            }
        });
    }

    private void initializeMediaPlayer(){
        //String fileName = getFilesDir() + "/tmp/tmp.wav";

        Uri uri  = Uri.parse("file://" + destFile.getAbsolutePath());

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mMediaPlayer.setDataSource(getApplicationContext(), uri);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
