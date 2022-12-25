package com.example.android.sunshineapp.sync;

import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.android.sunshineapp.data.WeatherContract;

public class SunshineSyncUtils {
    private static boolean sInilialized = false;
    private static final int JOB_ID = 111;

    public static void startImmediateSync(final Context context){
        Intent intent = new Intent(context,SunshineSyncIntentService.class);
        context.startService(intent);
    }

    static void scheduleJobService(final Context context){
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        ComponentName componentName = new ComponentName(context,SunshineJobSchedular.class);
        JobInfo jobInfo = new JobInfo.Builder(JOB_ID,componentName)
                .setPeriodic(1000*60*60*4)
                .build();
        jobScheduler.schedule(jobInfo);
    }

    synchronized public static void initialized(Context context){
        if(sInilialized){
            return;
        }
        sInilialized = true;
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                Uri contentUri = WeatherContract.WeatherEntry.CONTENT_URI;
                String[] projectionColumns = {WeatherContract.WeatherEntry._ID};
                String selectionStatement = WeatherContract.WeatherEntry
                        .getSqlSelectForTodayOnwards();


                Cursor cursor = context.getContentResolver().query(contentUri,projectionColumns,selectionStatement,null,null);
                if(cursor == null || cursor.getCount() == 0 ){
                    startImmediateSync(context);
                }
                assert cursor != null;
                cursor.close();
                return null;
            }
        }.execute();
        scheduleJobService(context);
    }
}
