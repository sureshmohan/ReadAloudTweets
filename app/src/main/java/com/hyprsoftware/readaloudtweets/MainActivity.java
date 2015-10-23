package com.hyprsoftware.readaloudtweets;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity {

    private TwitterLoginButton loginButton;
    private ListView twtResults;
    private List<Tweet> crtTweets = new ArrayList<>();
    private Speaker speaker;
    private final int CHECK_CODE = 0x1;
    //private final int LONG_DURATION = 5000;
    //private final int SHORT_DURATION = 1200;

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "8pNZfitPTXkDo2xX8TVnfQrYX";
    private static final String TWITTER_SECRET = "oWh8d0u2NKKPFyrJA4g91Nqw4qzoOlX0mx8kwOATmjJu4SCI29";

    private void checkTTS(){
        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(check, CHECK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CHECK_CODE){
            if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
                speaker = new Speaker(this);
            }else {
                Intent install = new Intent();
                install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(install);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);
        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        twtResults = (ListView) findViewById(R.id.listView);

        twtResults.setVisibility(View.INVISIBLE);

        checkTTS();

        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls

                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
                // Can also use Twitter directly: Twitter.getApiClient()
                StatusesService statusesService = twitterApiClient.getStatusesService();

                statusesService.homeTimeline(20, null, null, null, null, null, null, new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> result) {

                        crtTweets.addAll(result.data);

                        //Hide the login button and the text on main
                        loginButton.setVisibility(View.INVISIBLE);

                        //Make the Listview visible
                        twtResults.setVisibility(View.VISIBLE);

/*                        for(int i=0; i<= result.data.size();i++){
                            Long id = result.data.get(i).getId();
                        }*/


                        final TweetArrayAdapter adptr = new TweetArrayAdapter(MainActivity.this, R.layout.tweet_card, crtTweets);
                        twtResults.setAdapter(adptr);
                    }

                    @Override
                    public void failure(TwitterException e) {

                    }
                });


//                statusesService.show(524971209851543553L, null, null, null, new Callback<Tweet>() {
//
//                    @Override
//                    public void success(Result<Tweet> result) {
//                        //Do something with result, which provides a Tweet inside of result.data
//                    }
//
//                    public void failure(TwitterException exception) {
//                        //Do something on failure
//                    }
//                });
//
//
//           TwitterApiClient  apiClient = new TwitterApiClient((TwitterSession)result.data);


            }

            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void onPlayButtonPressed(MenuItem item) {
        if(speaker != null){

            speaker.allow(true);


            for (Tweet tweet:crtTweets) {
                speaker.speak(tweet.text);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speaker.destroy();
    }
}
