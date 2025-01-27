package com.gmail.romkatsis.taskmanagementsystem.controllers;


import com.gmail.romkatsis.taskmanagementsystem.dtos.requests.LogInRequest;
import com.gmail.romkatsis.taskmanagementsystem.dtos.requests.RegistrationRequest;
import com.gmail.romkatsis.taskmanagementsystem.dtos.requests.TokenRequest;
import com.gmail.romkatsis.taskmanagementsystem.dtos.responses.TokensResponse;
import com.gmail.romkatsis.taskmanagementsystem.services.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public TokensResponse registerUser(@RequestBody @Valid RegistrationRequest registrationRequest) {
        return authenticationService.registerUser(registrationRequest);
    }

    @PostMapping("/log-in")
    @ResponseStatus(HttpStatus.OK)
    public TokensResponse authenticateUser(@RequestBody @Valid LogInRequest logInRequest) {
        return authenticationService.authenticateUser(logInRequest);
    }

    @PostMapping("/refresh-tokens")
    @ResponseStatus(HttpStatus.OK)
    public TokensResponse refreshTokens(@RequestBody TokenRequest request) {
        return authenticationService.refreshUserTokens(request);
    }

    @PostMapping("/log-out")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody @Valid TokenRequest refreshTokenRequest,
                       @RequestParam(defaultValue = "false") boolean fromAllDevices) {
        authenticationService.logOut(refreshTokenRequest, fromAllDevices);
    }
}
