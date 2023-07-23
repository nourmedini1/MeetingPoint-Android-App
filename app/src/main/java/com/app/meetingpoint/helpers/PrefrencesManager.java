package com.app.meetingpoint.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefrencesManager {
    private final SharedPreferences sharedPreferences ;
    public PrefrencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.PREFERENCE_NAME_KEY,Context.MODE_PRIVATE);
    }
    public void putBoolean(String key , Boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }
    public boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key,false);
    }
    public void putString(String key , String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }
    public String getString(String key){
        return sharedPreferences.getString(key,null);
    }
    public void clear(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }


}
