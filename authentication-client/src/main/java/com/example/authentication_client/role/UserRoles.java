package com.example.authentication_client.role;

public enum UserRoles {
    USER,
    ADMIN;

    public String toString() {
        return "Role_" + this.name();
    }
}
