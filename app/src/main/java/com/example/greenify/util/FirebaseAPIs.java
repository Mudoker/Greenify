package com.example.greenify.util;

import android.util.Log;

import com.example.greenify.model.EventModel;
import com.example.greenify.model.SettingModel;
import com.example.greenify.model.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FirebaseAPIs {
    // Save user data
    public void storeUserData(UserModel userModel, FirebaseCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a Map from the User object
        Map<String, Object> userData = createUserDataMap(userModel);

        // Create a document reference in the "user" collection with the user's ID
        db.collection("users").document(userModel.getId().toString()).set(userData).addOnSuccessListener(aVoid -> {
            callback.onSuccess(true);
            Log.d("Firestore User", "DocumentSnapshot successfully written!");
        }).addOnFailureListener(e -> {
            callback.onFailure(e);
            Log.w("Firestore User Error", "Error writing document", e);
        });
    }

    public void updateUserData(UserModel userModel, FirebaseCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a Map from the User object
        Map<String, Object> userData = createUserDataMap(userModel);

        // Update the document in the "user" collection with the user's ID
        db.collection("users").document(userModel.getId().toString()).update(userData).addOnSuccessListener(aVoid -> {
            callback.onSuccess(true);
            Log.d("Firestore User", "DocumentSnapshot successfully updated!");
        }).addOnFailureListener(e -> {
            callback.onFailure(e);
            Log.w("Firestore User Error", "Error updating document", e);
        });
    }

    private Map<String, Object> createUserDataMap(UserModel userModel) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", userModel.getId());
        userData.put("username", userModel.getUsername());
        userData.put("phone", userModel.getPhone());
        userData.put("email", userModel.getEmail());
        userData.put("joinedEvents", userModel.getJoinedEvents());
        userData.put("points", userModel.getPoints());
        userData.put("deviceToken", userModel.getDeviceToken());
        return userData;
    }

    public void getUserData(String userEmail, OnSuccessListener<DocumentSnapshot> onSuccessListener, OnFailureListener onFailureListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query userQuery = db.collection("users").whereEqualTo("email", userEmail);

        // Execute the query
        userQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            // Check if there is a matching user document
            if (!queryDocumentSnapshots.isEmpty()) {
                // Get the first document (assuming email is unique, otherwise, handle multiple results)
                DocumentSnapshot userSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                UserModel currentUser = documentSnapshotToUserModel(userSnapshot);

                UserModel.setUserSingleTon(currentUser);

                Log.e("User data firestore", String.valueOf(userSnapshot));
                // Call the success listener with the user document
                onSuccessListener.onSuccess(userSnapshot);
            } else {
                // No matching user found, call the failure listener
                onFailureListener.onFailure(new Exception("User not found with email: " + userEmail));
            }
        }).addOnFailureListener(onFailureListener);
    }

    // Helper method to convert DocumentSnapshot to UserModel
    private UserModel documentSnapshotToUserModel(DocumentSnapshot documentSnapshot) {
        UUID id = UUID.fromString(documentSnapshot.getId());
        String username = documentSnapshot.getString("username");
        String phone = documentSnapshot.getString("phone");
        String email = documentSnapshot.getString("email");
        ArrayList<?> rawList = (ArrayList<?>) documentSnapshot.get("joinedEvents");
        ArrayList<UUID> joinedEvents = new ArrayList<>();

        // Iterate and cast each element to UUID
        if (rawList != null) {
            for (Object item : rawList) {
                if (item instanceof String) {
                    joinedEvents.add(UUID.fromString((String) item));
                }
            }
        }

        Double points = documentSnapshot.getDouble("points");
        String deviceToken = documentSnapshot.getString("deviceToken");

        return new UserModel(id, username, phone, email, joinedEvents, points, deviceToken);
    }


    // Function to store NotificationModel
    public void storeSettingModelData(SettingModel settingModel, FirebaseCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a Map from the NotificationModel object
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("id", settingModel.getId().toString());
        notificationData.put("notificationSound", settingModel.getNotificationSound());
        notificationData.put("pushNotification", settingModel.isPushNotification());
        notificationData.put("distanceUnit", settingModel.getDistanceUnit());
        notificationData.put("mapZoom", settingModel.getMapZoom());

        // Create a document reference in the "notifications" collection with the notification's ID
        db.collection("settings").document(settingModel.getId().toString()).set(notificationData).addOnSuccessListener(aVoid -> {
            callback.onSuccess(true);
            Log.d("Firestore Setting", "DocumentSnapshot successfully written!");
        }).addOnFailureListener(e -> {
            callback.onFailure(e);
            Log.w("Firestore Setting", "Error writing document", e);
        });
    }

    public void updateSettingModelData(SettingModel settingModel, FirebaseCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a Map from the NotificationModel object
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("id", settingModel.getId().toString());
        notificationData.put("notificationSound", settingModel.getNotificationSound());
        notificationData.put("pushNotification", settingModel.isPushNotification());
        notificationData.put("distanceUnit", settingModel.getDistanceUnit());
        notificationData.put("mapZoom", settingModel.getMapZoom());

        // Create a document reference in the "notifications" collection with the notification's ID
        db.collection("settings").document(settingModel.getId().toString()).update(notificationData).addOnSuccessListener(aVoid -> {
            callback.onSuccess(true);
            Log.d("Firestore Setting", "DocumentSnapshot successfully written!");
        }).addOnFailureListener(e -> {
            callback.onFailure(e);
            Log.w("Firestore Setting", "Error writing document", e);
        });
    }

    public void getSettingData(UUID id, OnSuccessListener<SettingModel> onSuccessListener, OnFailureListener onFailureListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference settingRef = db.collection("settings").document(id.toString());

        // Retrieve the document
        settingRef.get().addOnSuccessListener(documentSnapshot -> {
            // Check if the document exists
            if (documentSnapshot.exists()) {
                // Convert DocumentSnapshot to SettingModel
                SettingModel settingModel = documentSnapshot.toObject(SettingModel.class);
                if (settingModel != null) {
                    settingModel.setId(UUID.fromString(documentSnapshot.getId()));
                    onSuccessListener.onSuccess(settingModel);
                }
            } else {
                // Document doesn't exist
                onSuccessListener.onSuccess(null);
            }
        }).addOnFailureListener(onFailureListener);
    }


    // Event data
    public void storeEventData(EventModel eventModel, FirebaseCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a map with the fields you want to store
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("id", eventModel.getId().toString());
        eventMap.put("title", eventModel.getTitle());
        eventMap.put("ownerId", eventModel.getOwnerId());
        eventMap.put("description", eventModel.getDescription());
        eventMap.put("location", eventModel.getLocation());
        eventMap.put("participants", eventModel.getParticipants());
        eventMap.put("category", eventModel.getCategory());
        eventMap.put("point", eventModel.getPoint());
        eventMap.put("status", eventModel.getStatus());

        // Add the document to the "event" collection with the UUID as the document ID
        db.collection("events").document(eventModel.getId().toString()).set(eventMap).addOnSuccessListener(aVoid -> {
            Log.d("Firestore Event", "DocumentSnapshot successfully written!");
            callback.onSuccess(true);
        }).addOnFailureListener(e -> {
            Log.w("Firestore Event", "Error writing document", e);
            callback.onFailure(e);
        });

        UserModel.getUserSingleTon().addHostedEvent(eventModel.getId());

        // Update hosted event
        updateUserData(UserModel.getUserSingleTon(), callback);
    }

    public void updateEventData(EventModel eventModel, FirebaseCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a map with the fields you want to store
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("id", eventModel.getId().toString());
        eventMap.put("title", eventModel.getTitle());
        eventMap.put("ownerId", eventModel.getOwnerId());
        eventMap.put("description", eventModel.getDescription());
        eventMap.put("location", eventModel.getLocation());
        eventMap.put("participants", eventModel.getParticipants());
        eventMap.put("category", eventModel.getCategory());
        eventMap.put("point", eventModel.getPoint());
        eventMap.put("status", eventModel.getStatus());

        // Add the document to the "event" collection with the UUID as the document ID
        db.collection("events").document(eventModel.getId().toString()).update(eventMap).addOnSuccessListener(aVoid -> {
            callback.onSuccess(true);
            Log.d("Firestore Event", "DocumentSnapshot successfully written!");
        }).addOnFailureListener(e -> {
            callback.onFailure(e);
            Log.w("Firestore Event", "Error writing document", e);
        });

        UserModel.getUserSingleTon().addHostedEvent(eventModel.getId());

        // Update hosted event
        updateUserData(UserModel.getUserSingleTon(), callback);
    }

    public void deleteEventData(EventModel eventModel, FirebaseCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Delete the document in the "events" collection with the event's ID
        db.collection("events").document(eventModel.getId().toString()).delete().addOnSuccessListener(aVoid -> {
            callback.onSuccess(true);
            Log.d("Firestore Event", "DocumentSnapshot successfully deleted!");
        }).addOnFailureListener(e -> {
            callback.onFailure(e);
            Log.w("Firestore Event", "Error deleting document", e);
        });

        UserModel.getUserSingleTon().removeHostedEvent(eventModel.getId());

        // Update hosted event
        updateUserData(UserModel.getUserSingleTon(), callback);
    }


    public static void getEventsData(UUID ownerId, OnSuccessListener<List<EventModel>> onSuccessListener, OnFailureListener onFailureListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query = db.collection("events").whereEqualTo("ownerId", ownerId.toString());

        // Retrieve the document
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<EventModel> eventModels = new ArrayList<>();
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                // Convert each DocumentSnapshot to EventModel
                EventModel eventModel = documentSnapshot.toObject(EventModel.class);
                if (eventModel != null) {
                    eventModel.setId(UUID.fromString(documentSnapshot.getId()));
                    eventModels.add(eventModel);
                }
            }

            onSuccessListener.onSuccess(eventModels);
        }).addOnFailureListener(onFailureListener);
    }
}

