package com.example.greenify.model;

import java.util.ArrayList;
import java.util.UUID;

public class UserModel {

    private static UserModel UserSingleTon;

    private final String id;
    private String username;
    private String phone = "";
    private String email;

    private ArrayList<String> joinedEvents = new ArrayList<>();
    private ArrayList<String> hostedEvents = new ArrayList<>();

    private Double points = 0.0;

    private String deviceToken;

    public UserModel(String id, String username, String email, String deviceToken) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.deviceToken = deviceToken;
    }

    public UserModel(String id, String username, String phone, String email, ArrayList<String> joinedEvents, ArrayList<String> hostedEvents, Double points, String deviceToken) {
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.joinedEvents = joinedEvents;
        this.hostedEvents = hostedEvents;
        this.points = points;
        this.deviceToken = deviceToken;
    }

    public UserModel() {
        this.id = UUID.randomUUID().toString();
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

    public ArrayList<String> getJoinedEvents() {
        return joinedEvents;
    }

    public void setJoinedEvents(ArrayList<String> joinedEvents) {
        this.joinedEvents = joinedEvents;
    }

    public void addJoinedEvents(String joinedEvent) {
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

    public String getId() {
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


    public ArrayList<String> getHostedEvents() {
        return hostedEvents;
    }

    public void setHostedEvents(ArrayList<String> hostedEvents) {
        this.hostedEvents = hostedEvents;
    }

    public void addHostedEvent(String hostedEvent) {
        this.hostedEvents.add(hostedEvent);
    }

    public void removeHostedEvent(String hostedEvent) {
        hostedEvents.remove(hostedEvent);
    }

    public void removeEnrolEvent(String joinedEvent) {
        joinedEvents.remove(joinedEvent);
    }
}