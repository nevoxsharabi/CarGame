package com.example.cargame;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class MySharedPreferences {
    private static final String SP_FILE = "Subject_Mobile_Assignement";
    private SharedPreferences sharedPreferences;
    private static MySharedPreferences mySharedPreferences;

    private MySharedPreferences(Context context) {
        this.sharedPreferences = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
    }

    public static MySharedPreferences getInstance(Context context) {
        Log.i("info", "I'm here");
        if (mySharedPreferences == null) {
            mySharedPreferences = new MySharedPreferences(context);
        }
        return mySharedPreferences;
    }

    public String getStringSP(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    public void putIntSP(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void putStringSP(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

}

