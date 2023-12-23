package com.example.greenify.util;

import android.net.Uri;

public interface FirebaseCallback {
    void onSuccess(boolean success);

    void onSuccess(Uri uri);

    void onFailure(Exception e);
}
