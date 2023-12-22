package com.example.greenify.model;

import java.util.ArrayList;
import java.util.UUID;

public class UserModel {
    private final UUID id;
    private String username;
    private String phone;
    private String email;

    private ArrayList<UUID> joinedEvents;

    private Double points;

    private String deviceToken;

    public UserModel(String email, String deviceToken) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.deviceToken = deviceToken;
    }

    public UserModel() {
        this.id = UUID.randomUUID();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<UUID> getJoinedEvents() {
        return joinedEvents;
    }

    public void setJoinedEvents(ArrayList<UUID> joinedEvents) {
        this.joinedEvents = joinedEvents;
    }

    public void addJoinedEvents(UUID joinedEvent) {
        this.joinedEvents.add(joinedEvent);
    }

    public Double getPoints() {
        return points;
    }

    public void setPoints(Double points) {
        this.points = points;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public UUID getId() {
        return id;
    }
}