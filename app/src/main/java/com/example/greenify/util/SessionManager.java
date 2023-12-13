package com.example.greenify.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private final SharedPreferences sharedPreferences;

    private final SharedPreferences.Editor editor;


    private final String USER_SESSION_KEY = Environment.getUserSessionKey();

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Environment.getSharedPreferencesName(), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveUserSession() {
        editor.putInt(USER_SESSION_KEY, 123);
        editor.commit();
    }

    public int getUserSession() {
        return sharedPreferences.getInt(Environment.getUserSessionKey(), -1);
    }

    public void removeUserSession() {
        editor.putInt(USER_SESSION_KEY, -1);
        editor.commit();
    }
}
