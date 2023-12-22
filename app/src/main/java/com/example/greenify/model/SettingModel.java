package com.example.greenify.model;

import java.util.UUID;

public class SettingModel {
    private UUID id;

    private int notificationSound;

    private boolean pushNotification;

    private String distanceUnit;

    private Double mapZoom;

    private static SettingModel Setting_Single_Ton;

    public SettingModel(UUID ownerId, int notificationSound, boolean pushNotification, String distanceUnit, Double mapZoom) {
        this.id = ownerId;
        this.notificationSound = notificationSound;
        this.pushNotification = pushNotification;
        this.distanceUnit = distanceUnit;
        this.mapZoom = mapZoom;
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

    public static SettingModel getSetting_Single_Ton() {
        return Setting_Single_Ton;
    }

    public static void setSetting_Single_Ton(SettingModel settingModel) {
        Setting_Single_Ton = settingModel;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
