package br.com.echitey.android.firebaseauthapp.utils;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Yawo on 06/03/18.
 */

public class MySharedPreferences {

    private AppCompatActivity activity;

    public MySharedPreferences(AppCompatActivity activity){
        this.activity = activity;
    }

    public String getString(String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return  preferences.getString(key,"");
    }

    public int getInt(String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return preferences.getInt(key, -1);
    }

    public void setString(String key, String value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);

        preferences.edit().putString(key, value).apply();
    }

    public void setInt(String key, int value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);

        preferences.edit().putInt(key, value).apply();
    }


}
