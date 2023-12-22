package com.example.greenify.model;

import java.util.ArrayList;
import java.util.UUID;

public class EventOwnerModel extends UserModel {
    private ArrayList<UUID> hostedEvents;

    public EventOwnerModel(ArrayList<UUID> hostedEvents) {
        super();
        this.hostedEvents = hostedEvents;
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

    public EventOwnerModel parseUserModel(UserModel userModel) {
        // Extract relevant information from the UserModel
        String username = userModel.getUsername();
        String phone = userModel.getPhone();
        String email = userModel.getEmail();
        ArrayList<UUID> joinedEvents = userModel.getJoinedEvents();
        Double points = userModel.getPoints();
        String deviceToken = userModel.getDeviceToken();

        // Create a new EventOwnerModel using the extracted information
        EventOwnerModel eventOwnerModel = new EventOwnerModel(joinedEvents);
        eventOwnerModel.setUsername(username);
        eventOwnerModel.setPhone(phone);
        eventOwnerModel.setEmail(email);
        eventOwnerModel.setPoints(points);
        eventOwnerModel.setDeviceToken(deviceToken);

        return eventOwnerModel;
    }
}
