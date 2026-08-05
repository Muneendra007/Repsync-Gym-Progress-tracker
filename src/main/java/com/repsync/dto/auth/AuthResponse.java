package com.repsync.dto.auth;

import com.repsync.model.User;

/**
 * Response payload returned upon successful login or registration.
 * Includes the signed JWT token and the authenticated User profile.
 */
public class AuthResponse {
    private String token;
    private User user;

    public AuthResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
