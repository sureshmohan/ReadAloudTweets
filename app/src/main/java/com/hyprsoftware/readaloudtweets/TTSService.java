package com.hyprsoftware.readaloudtweets;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by suresh on 10/4/2015.
 */
public class TTSService extends IntentService {

    public TTSService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        intent.getParcelableArrayListExtra("data");
    }
}
