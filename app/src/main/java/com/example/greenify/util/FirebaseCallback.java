package com.example.greenify.util;

public interface FirebaseCallback {
    void onSuccess(boolean success);

    void onFailure(Exception e);
}
