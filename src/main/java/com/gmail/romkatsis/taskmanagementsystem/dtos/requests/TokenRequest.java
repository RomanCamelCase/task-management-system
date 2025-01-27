package com.gmail.romkatsis.taskmanagementsystem.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public class TokenRequest {

    @NotBlank
    private String refreshToken;

    public TokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public @NotBlank String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(@NotBlank String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
