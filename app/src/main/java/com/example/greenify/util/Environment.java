package com.example.greenify.util;

public class Environment {
    private static final String SUPPORT_EMAIL = "huuquoc7603@gmail.com";
    private static final SystemResponse SYSTEM_RESPONSE = new SystemResponse();

    private static final int CORNER_RADIUS = 20;

    private static final String SHARED_PREFERENCES_NAME = "app_session";

    private static final String USER_SESSION_KEY = "user_session";

    private static final String EMAIL_REGEX = "^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$";


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

    public static SystemResponse getSystemResponse() {
        return SYSTEM_RESPONSE;
    }

    public static String getEmailRegex() {
        return EMAIL_REGEX;
    }
}


