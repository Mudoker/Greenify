package com.example.greenify.model;

import java.util.ArrayList;
import java.util.UUID;

public class EventModel {
    private UUID id;
    private String title;
    private String description;
    private String location;

    private final UUID ownerId;
    private ArrayList<UUID> participants;

    private String category;

    private Boolean status;

    public EventModel(String title, String description, String location, UUID ownerId, String category) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.location = location;
        this.ownerId = ownerId;
        this.category = category;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public UUID getOwnerId() {
        return ownerId;
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
        return 100.0;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
