package com.gmail.romkatsis.taskmanagementsystem.dtos.requests;

import com.gmail.romkatsis.taskmanagementsystem.enums.Role;
import jakarta.validation.constraints.*;

import java.util.Set;

public class RegistrationRequest {

    @NotBlank
    @Size(max = 128)
    @Email(regexp = "^[A-Za-z0-9]+(\\.[A-Za-z0-9])*@[A-Za-z0-9]{3,}\\.[A-Za-z]{2,}$")
    private String email;

    @NotBlank
    @Size(min = 8, max = 128)
    @Pattern(regexp = "^[A-Za-z0-9]*$")
    private String password;

    @NotBlank
    @Size(min = 3, max = 32)
    @Pattern(regexp = "^[A-Za-z0-9_.-]*$")
    private String username;

    @NotEmpty
    private Set<Role> roles;

    public RegistrationRequest() {
    }

    public RegistrationRequest(String email, String password, String username, Set<Role> roles) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.roles = roles;
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

    public @NotBlank @Size(min = 3, max = 32) @Pattern(regexp = "^[A-Za-z0-9_.-]*$") String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank @Size(min = 3, max = 32) @Pattern(regexp = "^[A-Za-z0-9_.-]*$") String username) {
        this.username = username;
    }

    public @NotEmpty Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(@NotEmpty Set<Role> roles) {
        this.roles = roles;
    }
}
