package com.example.greenify.model;

import java.util.UUID;

public class NotificationModel {
    private final UUID id;
    private final UUID ownerId;

    private int notificationSound;

    private boolean pushNotification;

    private String distanceUnit;

    private Double mapZoom;

    public NotificationModel(UUID ownerId, int notificationSound, boolean pushNotification, String distanceUnit, Double mapZoom) {
        this.id = UUID.randomUUID();
        this.ownerId = ownerId;
        this.notificationSound = notificationSound;
        this.pushNotification = pushNotification;
        this.distanceUnit = distanceUnit;
        this.mapZoom = mapZoom;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public int getNotificationSound() {
        return notificationSound;
    }

    public void setNotificationSound(int notificationSound) {
        this.notificationSound = notificationSound;
    }

    public boolean isPushNotification() {
        return pushNotification;
    }

    public void setPushNotification(boolean pushNotification) {
        this.pushNotification = pushNotification;
    }

    public String getDistanceUnit() {
        return distanceUnit;
    }

    public void setDistanceUnit(String distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    public Double getMapZoom() {
        return mapZoom;
    }

    public void setMapZoom(Double mapZoom) {
        this.mapZoom = mapZoom;
    }

    public UUID getId() {
        return id;
    }
}
