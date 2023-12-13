package com.example.greenify.util;

public class Environment {
    private static final String SUPPORT_EMAIL = "huuquoc7603@gmail.com";

    private static final int CORNER_RADIUS = 20;

    private static final String SHARED_PREFERENCES_NAME = "app_session";

    private static final String USER_SESSION_KEY = "user_session";


    public static String getSupportEmail() {
        return SUPPORT_EMAIL;
    }

    public static int getCornerRadius() {
        return CORNER_RADIUS;
    }

    public static String getSharedPreferencesName() {
        return SHARED_PREFERENCES_NAME;
    }

    public static String getUserSessionKey() {
        return USER_SESSION_KEY;
    }
}
