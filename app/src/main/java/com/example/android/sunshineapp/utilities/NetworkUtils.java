package com.example.android.sunshineapp.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.android.sunshineapp.data.SunshinePreferences;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the weather servers.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String DYNAMIC_WEATHER_URL =
            "https://andfun-weather.udacity.com/weather";

    private static final String STATIC_WEATHER_URL =
            "https://andfun-weather.udacity.com/staticweather";

    private static final String FORECAST_BASE_URL = STATIC_WEATHER_URL;

    /*
     * NOTE: These values only effect responses from OpenWeatherMap, NOT from the fake weather
     * server. They are simply here to allow us to teach you how to build a URL if you were to use
     * a real API.If you want to connect your app to OpenWeatherMap's API, feel free to! However,
     * we are not going to show you how to do so in this course.
     */

    /* The format we want our API to return */
    private static final String format = "json";
    /* The units we want our API to return */
    private static final String units = "metric";
    /* The number of days we want our API to return */
    private static final int numDays = 14;

    final static String QUERY_PARAM = "q";
    final static String LAT_PARAM = "lat";
    final static String LON_PARAM = "lon";
    final static String FORMAT_PARAM = "mode";
    final static String UNITS_PARAM = "units";
    final static String DAYS_PARAM = "cnt";

    public static URL getUrl(Context context) {
        if (SunshinePreferences.isLocationLatLonAvailable(context)) {
            double[] preferredCoordinates = SunshinePreferences.getLocationCoordinates(context);
            double latitude = preferredCoordinates[0];
            double longitude = preferredCoordinates[1];
            return buildUrlWithLatitudeLongitude(latitude, longitude);
        } else {
            String locationQuery = SunshinePreferences.getPreferredWeatherLocation(context);
            return buildUrlWithLocationQuery(locationQuery);
        }
    }

    //Builds the URL used to talk to the weather server using a location.

    private static URL buildUrlWithLocationQuery(String locationQuery) {
        Uri weatherQueryUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, locationQuery)
                .appendQueryParameter(FORMAT_PARAM, format)
                .appendQueryParameter(UNITS_PARAM, units)
                .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                .build();

        try {
            URL weatherQueryUrl = new URL(weatherQueryUri.toString());
            Log.v(TAG, "URL: " + weatherQueryUrl);
            return weatherQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }


     // Builds the URL used to talk to the weather server using latitude and longitude

    private static URL buildUrlWithLatitudeLongitude(Double latitude, Double longitude) {
        Uri weatherQueryUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(LAT_PARAM, String.valueOf(latitude))
                .appendQueryParameter(LON_PARAM, String.valueOf(longitude))
                .appendQueryParameter(FORMAT_PARAM, format)
                .appendQueryParameter(UNITS_PARAM, units)
                .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                .build();

        try {
            URL weatherQueryUrl = new URL(weatherQueryUri.toString());
            Log.v(TAG, "URL: " + weatherQueryUrl);
            return weatherQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //This method returns the entire result from the HTTP response.

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
