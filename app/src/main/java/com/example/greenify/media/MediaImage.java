package com.example.greenify.media;

import android.graphics.Bitmap;

import java.util.UUID;

public class MediaImage {
    private final UUID id;
    private Bitmap image;

    private final UUID src;

    public MediaImage(Bitmap image, UUID ownerId) {
        this.id = UUID.randomUUID();
        this.image = image;
        this.src = ownerId;
    }

    public UUID getId() {
        return id;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public UUID getSrc() {
        return src;
    }
}
