package com.example.greenify.model;

import android.location.Location;

import java.util.ArrayList;
import java.util.UUID;

public class EventModel {
    private final UUID id;
    private String title;
    private String description;
    private Location location;

    private UUID ownerId;
    private ArrayList<UUID> participants;

    private String category;
    private Double point;

    private Boolean status;

    public EventModel(String title, String description, Location location, UUID ownerId, String category, Double point) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.location = location;
        this.ownerId = ownerId;
        this.category = category;
        this.point = point;
        status = true;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public ArrayList<UUID> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<UUID> participants) {
        this.participants = participants;
    }

    public void addParticipants(UUID participant) {
        this.participants.add(participant);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPoint() {
        return point;
    }

    public void setPoint(Double point) {
        this.point = point;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
