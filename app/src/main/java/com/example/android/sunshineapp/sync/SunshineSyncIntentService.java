package com.example.android.sunshineapp.sync;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

public class SunshineSyncIntentService extends IntentService {

    public SunshineSyncIntentService(){
        super(null);
    }

    public SunshineSyncIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SunshineSyncTask.syncWeather(this);
    }
}
