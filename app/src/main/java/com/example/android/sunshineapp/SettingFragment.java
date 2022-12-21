package com.example.android.sunshineapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.example.android.sunshineapp.data.SunshinePreferences;
import com.example.android.sunshineapp.data.WeatherContract;


public class SettingFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.setting_preference_screen);


        sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();

        int count = preferenceScreen.getPreferenceCount();



        for(int i = 0;i<count;i++){
            Preference p = preferenceScreen.getPreference(i);
            if(p instanceof ListPreference){
                String value = sharedPreferences.getString(p.getKey(), String.valueOf(R.string.celcius));
                setPreferenceSummary(p,value);
            }
            if(p instanceof EditTextPreference){
                String value = sharedPreferences.getString(p.getKey(),"Asha Model School");
                p.setSummary(value);
            }
        }
    }

    private void setPreferenceSummary(Preference p, String value) {
        if(p instanceof ListPreference){
            ListPreference listPreference = (ListPreference) p;
            int index = listPreference.findIndexOfValue(value);
            if(index>=0){
                listPreference.setSummary(listPreference.getEntries()[index]);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Activity activity = getActivity();

        if (key.equals(getString(R.string.pref_location_key))) {
            // we've changed the location
            // Wipe out any potential PlacePicker latlng values so that we can use this text entry.
            SunshinePreferences.resetLocationCoordinates(activity);
        } else if (key.equals(getString(R.string.pref_units_key))) {
            // units have changed. update lists of weather entries accordingly
            activity.getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null);
        }

        Preference preference = findPreference(key);
        if(preference instanceof ListPreference){
            String value = sharedPreferences.getString(preference.getKey(), String.valueOf(R.string.celcius));
            setPreferenceSummary(preference,value);
        }
        if(preference instanceof EditTextPreference){
            String value = sharedPreferences.getString(preference.getKey(),"Asha Model School");
            preference.setSummary(value);
        }
    }
}
