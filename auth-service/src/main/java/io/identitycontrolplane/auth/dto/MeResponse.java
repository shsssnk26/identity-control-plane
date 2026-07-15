package io.identitycontrolplane.auth.dto;

import java.util.List;

public class MeResponse {

    private String userId;
    private String email;
    private List<String> roles;

    public MeResponse(String userId, String email, List<String> roles) {
        this.userId = userId;
        this.email = email;
        this.roles = roles;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }
}