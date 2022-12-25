package com.example.android.sunshineapp.sync;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;

public class SunshineJobSchedular extends JobService {
    AsyncTask<Void,Void,Void> asyncTask;
    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        asyncTask = new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                SunshineSyncTask.syncWeather(getApplicationContext());
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                jobFinished(jobParameters,true);
            }
        };
        asyncTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if(asyncTask != null){
            asyncTask.cancel(true);
        }
        return true;
    }
}
