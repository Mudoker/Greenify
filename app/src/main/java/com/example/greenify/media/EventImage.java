package com.example.greenify.media;

import java.util.ArrayList;
import java.util.UUID;

public class EventImage {
    private final UUID id;
    private Byte[] titleImage;
    private ArrayList<Byte[]> contentImages;

    private UUID ownerId;

    public EventImage(Byte[] titleImage, UUID ownerId) {
        this.id = UUID.randomUUID();
        this.titleImage = titleImage;
        this.ownerId = ownerId;
    }

    public UUID getId() {
        return id;
    }

    public Byte[] getTitleImage() {
        return titleImage;
    }

    public void setTitleImage(Byte[] titleImage) {
        this.titleImage = titleImage;
    }

    public ArrayList<Byte[]> getContentImages() {
        return contentImages;
    }

    public void setContentImages(ArrayList<Byte[]> contentImages) {
        this.contentImages = contentImages;
    }

    public void addContentImages(Byte[] contentImage) {
        this.contentImages.add(contentImage);
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }
}
