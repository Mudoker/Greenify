package com.example.greenify.util;

public class SystemResponse {
    private static SystemResponse INSTANCE;

    public static SystemResponse getINSTANCE() {
        return INSTANCE;
    }

    public String getINVALID_CREDENTIALS() {
        return "Invalid Credentials";
    }

    public String getUSER_NOT_FOUND() {
        return "User not found";
    }

    public String getBAD_REQUEST() {
        return "Bad Request";
    }

    public String getSuccess() {
        return "Success";
    }

    public String getSendToMailBox() {
        return "Please check your mailbox";
    }

    public String mapLoadFail() {
        return "Failed to load map";
    }

}
