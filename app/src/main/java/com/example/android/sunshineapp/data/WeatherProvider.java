package com.example.android.sunshineapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android.sunshineapp.utilities.SunshineDateUtils;

public class WeatherProvider extends ContentProvider {
    private WeatherDbHelper weatherDbHelper;

    private static final int CODE_WEATHER = 100;
    private static final int CODE_WEATHER_WITH_DATE = 101;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{
        uriMatcher.addURI(WeatherContract.CONTENT_AUTHORITY,WeatherContract.PATH,CODE_WEATHER);
        uriMatcher.addURI(WeatherContract.CONTENT_AUTHORITY,WeatherContract.PATH+"/#",CODE_WEATHER_WITH_DATE);
    }

    @Override
    public boolean onCreate() {
        weatherDbHelper = new WeatherDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sort) {
        SQLiteDatabase database = weatherDbHelper.getReadableDatabase();

        Cursor cursor;
        int match = uriMatcher.match(uri);

        switch (match){
            case CODE_WEATHER:
                cursor = database.query(WeatherContract.WeatherEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sort);
                break;
            case CODE_WEATHER_WITH_DATE:
                String normalizedUtcDateString = uri.getLastPathSegment();
                selection = WeatherContract.WeatherEntry.COLUMN_DATE + " = ?";
                selectionArgs = new String[]{normalizedUtcDateString};
                cursor = database.query(WeatherContract.WeatherEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sort);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + match);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] cv) {
        SQLiteDatabase database = weatherDbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)){
            case CODE_WEATHER:
                database.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : cv) {
                        long weatherDate =
                                value.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE);
                        if (!SunshineDateUtils.isDateNormalized(weatherDate)) {
                            throw new IllegalArgumentException("Date must be normalized to insert");
                        }

                        long _id = database.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

//              COMPLETED (3) Return the number of rows inserted from our implementation of bulkInsert
                return rowsInserted;
            default:
                return super.bulkInsert(uri,cv);

        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("We are not implementing getType in Sunshine");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        throw new RuntimeException("We are not implementing insert in Sunshine, instead using bulkInsert");
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = weatherDbHelper.getWritableDatabase();

        int rowsDeleted = 0;
        switch (uriMatcher.match(uri)){
            case CODE_WEATHER:
                rowsDeleted = database.delete(WeatherContract.WeatherEntry.TABLE_NAME,selection,selectionArgs);
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Unexpected Uri"+uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new RuntimeException("We are not implementing update in Sunshine");
    }

    @Override
    public void shutdown() {
        super.shutdown();
        weatherDbHelper.close();
    }
}
