package com.example.greenify.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class EventModel implements Serializable {
    private String id;
    private String title;
    private String description;
    private String location;

    private String ownerId;
    private ArrayList<String> participants = new ArrayList<>();

    private String category;

    private Boolean status;

    private final Date createdDate;

    public EventModel() {
        status = true;
        createdDate = new Date();
    }

    public EventModel(String title, String description, String location, String ownerId, String category) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.location = location;
        this.ownerId = ownerId;
        this.category = category;
        status = true;
        createdDate = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getOwnerId() {
        return ownerId;
    }

    public ArrayList<String> getParticipants() {
        // Ensure participants list is initialized before returning
        if (participants == null) {
            participants = new ArrayList<>();
        }
        return participants;
    }

    public void setParticipants(ArrayList<String> participants) {
        this.participants = participants;
    }

    public void addParticipants(String participant) {
        // Ensure participants list is initialized before adding
        if (participants == null) {
            participants = new ArrayList<>();
        }
        this.participants.add(participant);
    }

    public void removeParticipants(String participant) {
        if (participants != null) {
            this.participants.remove(participant);
        }
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
