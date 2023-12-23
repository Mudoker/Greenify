package com.example.greenify.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class EventModel implements Serializable {
    private UUID id;
    private String title;
    private String description;
    private String location;

    private UUID ownerId;
    private ArrayList<UUID> participants;

    private String category;

    private Boolean status;

    private final Date createdDate;

    public EventModel() {
        status = true;
        createdDate = new Date();
    }

    public EventModel(String title, String description, String location, UUID ownerId, String category) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.location = location;
        this.ownerId = ownerId;
        this.category = category;
        status = true;
        createdDate = new Date();
    }

    public EventModel(UUID id, String title, String description, String location, UUID ownerId, ArrayList<UUID> participants, String category, Boolean status, Date createdDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.ownerId = ownerId;
        this.participants = participants;
        this.category = category;
        this.status = status;
        this.createdDate = createdDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(String id) {
        this.id = UUID.fromString(id);
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = UUID.fromString(ownerId);
    }

//    @PropertyName("id")
//    public String getIdString() {
//        return id != null ? id.toString() : null;
//    }
//
//    @PropertyName("id")
//    public void setIdString(String idString) {
//        if (idString != null) {
//            this.id = UUID.fromString(idString);
//        }
//    }
}
