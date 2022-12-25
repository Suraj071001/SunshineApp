package com.example.android.sunshineapp.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.example.android.sunshineapp.data.WeatherContract;
import com.example.android.sunshineapp.utilities.NetworkUtils;
import com.example.android.sunshineapp.utilities.OpenWeatherJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class SunshineSyncTask {

    synchronized public static void syncWeather(Context context){
        
        try{
            URL url = NetworkUtils.getUrl(context);
            String jsonResult = NetworkUtils.getResponseFromHttpUrl(url);

            ContentValues [] contentValues = OpenWeatherJsonUtils.getWeatherContentValuesFromJson(context,jsonResult);
            if (contentValues != null && contentValues.length != 0) {
                /* Get a handle on the ContentResolver to delete and insert data */
                ContentResolver sunshineContentResolver = context.getContentResolver();

//              COMPLETED (4) If we have valid results, delete the old data and insert the new
                /* Delete old weather data because we don't need to keep multiple days' data */
                sunshineContentResolver.delete(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        null,
                        null);

                /* Insert our new weather data into Sunshine's ContentProvider */
                sunshineContentResolver.bulkInsert(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        contentValues);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

    }
}
