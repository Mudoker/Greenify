package com.example.greenify.model;

import java.util.ArrayList;
import java.util.UUID;

public class UserModel {

    private static UserModel UserSingleTon;

    private final UUID id;
    private String username;
    private String phone = "";
    private String email;

    private ArrayList<UUID> joinedEvents = new ArrayList<>();
    private ArrayList<UUID> hostedEvents = new ArrayList<>();

    private Double points = 0.0;

    private String deviceToken;

    public UserModel(UUID id, String username, String email, String deviceToken) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.deviceToken = deviceToken;
    }

    public UserModel(UUID id, String username, String phone, String email, ArrayList<UUID> joinedEvents, Double points, String deviceToken) {
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.joinedEvents = joinedEvents;
        this.points = points;
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

    public static UserModel getUserSingleTon() {
        if (UserSingleTon == null) {
            return new UserModel();
        }
        return UserSingleTon;
    }

    public static void setUserSingleTon(UserModel userModel) {
        UserSingleTon = userModel;
    }


    public ArrayList<UUID> getHostedEvents() {
        return hostedEvents;
    }

    public void setHostedEvents(ArrayList<UUID> hostedEvents) {
        this.hostedEvents = hostedEvents;
    }

    public void addHostedEvent(UUID hostedEvent) {
        this.hostedEvents.add(hostedEvent);
    }

    public void removeHostedEvent(UUID hostedEvent) {
        if (hostedEvents.contains(hostedEvent)) {
            hostedEvents.remove(hostedEvent);
        }
    }
}