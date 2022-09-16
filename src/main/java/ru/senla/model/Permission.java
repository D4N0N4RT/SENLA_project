package ru.senla.model;

public enum Permission {
    USERS_READ("users:read"),
    ADMIN_PERMISSION("admin:permission");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
