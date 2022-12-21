package com.example.android.sunshineapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class WeatherDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "weather.db";
    private static final int VERSION = 1;

    public WeatherDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_SQL_DATABASE = "CREATE TABLE " + WeatherContract.WeatherEntry.TABLE_NAME + "("
                + WeatherContract.WeatherEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + WeatherContract.WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL,"
                + WeatherContract.WeatherEntry.COLUMN_DATE       + " INTEGER NOT NULL,"
                + WeatherContract.WeatherEntry.COLUMN_DEGREES     + " REAL NOT NULL,"
                + WeatherContract.WeatherEntry.COLUMN_HUMIDITY   + " REAL NOT NULL,"
                + WeatherContract.WeatherEntry.COLUMN_PRESSURE   + " REAL NOT NULL,"
                + WeatherContract.WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL,"
                + WeatherContract.WeatherEntry.COLUMN_MAX_TEMP   + " REAL NOT NULL,"
                + WeatherContract.WeatherEntry.COLUMN_MIN_TEMP   + " REAL NOT NULL,"
                + " UNIQUE (" + WeatherContract.WeatherEntry.COLUMN_DATE + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(CREATE_SQL_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ WeatherContract.WeatherEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
