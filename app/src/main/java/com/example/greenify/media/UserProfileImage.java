package com.example.greenify.media;

import java.util.Base64;
import java.util.UUID;

public class UserProfileImage {
    private final UUID id;
    private Base64 src;

    private UUID ownerId;

    public UserProfileImage(Base64 src, UUID ownerId) {
        this.id = UUID.randomUUID();
        this.src = src;
        this.ownerId = ownerId;
    }

    public UUID getId() {
        return id;
    }

    public Base64 getSrc() {
        return src;
    }

    public void setSrc(Base64 src) {
        this.src = src;
    }

    public UUID getOwnerId() {
        return ownerId;
    }
}
