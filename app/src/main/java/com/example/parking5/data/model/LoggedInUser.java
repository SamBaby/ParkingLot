package com.example.parking5.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String displayName;

    private String permission;

    public LoggedInUser(String userId, String displayName, String permission) {
        this.userId = userId;
        this.displayName = displayName;
        this.permission = permission;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPermission() {
        return permission;
    }
}