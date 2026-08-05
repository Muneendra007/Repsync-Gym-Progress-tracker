package com.repsync.model;

import com.repsync.model.enums.UserRole;

/**
 * Admin user - extends User with admin-specific capabilities.
 * 
 * Demonstrates: Inheritance (Admin IS-A User)
 * Admins can manage exercises (add, edit, delete).
 */
public class Admin extends User {

    public Admin() {
        super();
        setRole(UserRole.ADMIN);
    }

    public Admin(String username, String password, String email) {
        super(username, password, email);
        setRole(UserRole.ADMIN);
    }

    /**
     * Check if admin can manage exercises.
     * Admins always have this permission.
     */
    public boolean canManageExercises() {
        return true;
    }

    /**
     * Check if admin can manage users.
     */
    public boolean canManageUsers() {
        return true;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                '}';
    }
}
