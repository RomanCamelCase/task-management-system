package com.gmail.romkatsis.taskmanagementsystem.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class LogInRequest {

    @NotBlank
    @Size(max = 128)
    @Email(regexp = "^[A-Za-z0-9]+(\\.[A-Za-z0-9])*@[A-Za-z0-9]{3,}\\.[A-Za-z]{2,}$")
    private String email;

    @NotBlank
    @Size(min = 8, max = 128)
    @Pattern(regexp = "^[A-Za-z0-9]*$")
    private String password;

    public LogInRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public @NotBlank @Size(max = 128) @Email(regexp = "^[A-Za-z0-9]+(\\.[A-Za-z0-9])*@[A-Za-z0-9]{3,}\\.[A-Za-z]{2,}$") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank @Size(max = 128) @Email(regexp = "^[A-Za-z0-9]+(\\.[A-Za-z0-9])*@[A-Za-z0-9]{3,}\\.[A-Za-z]{2,}$") String email) {
        this.email = email;
    }

    public @NotBlank @Size(min = 8, max = 128) @Pattern(regexp = "^[A-Za-z0-9]*$") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank @Size(min = 8, max = 128) @Pattern(regexp = "^[A-Za-z0-9]*$") String password) {
        this.password = password;
    }
}
