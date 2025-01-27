package com.gmail.romkatsis.taskmanagementsystem.services;

import com.gmail.romkatsis.taskmanagementsystem.dtos.requests.LogInRequest;
import com.gmail.romkatsis.taskmanagementsystem.dtos.requests.RegistrationRequest;
import com.gmail.romkatsis.taskmanagementsystem.dtos.requests.TokenRequest;
import com.gmail.romkatsis.taskmanagementsystem.dtos.responses.TokensResponse;
import com.gmail.romkatsis.taskmanagementsystem.models.RefreshToken;
import com.gmail.romkatsis.taskmanagementsystem.models.User;
import com.gmail.romkatsis.taskmanagementsystem.utils.JwtUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final RefreshTokenService refreshTokenService;

    private final UserService userService;

    private final JwtUtils jwtUtils;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService, UserService userService, JwtUtils jwtUtils, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public TokensResponse registerUser(RegistrationRequest registrationRequest) {
        User user = modelMapper.map(registrationRequest, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userService.saveUser(user);
        return generateTokensByUser(user);
    }

    @Transactional
    public TokensResponse authenticateUser(LogInRequest logInRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                logInRequest.getEmail(),
                logInRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Integer userId = Integer.valueOf(userDetails.getUsername());
        User user = userService.findUserById(userId);

        return generateTokensByUser(user);
    }

    @Transactional
    public TokensResponse refreshUserTokens(TokenRequest refreshToken) {
        User user = refreshTokenService.verifyToken(refreshToken.getRefreshToken());
        return generateTokensByUser(user);
    }

    @Transactional
    public void logOut(TokenRequest refreshToken, boolean fromAllDevices) {
        User user = refreshTokenService.verifyToken(refreshToken.getRefreshToken());

        if (fromAllDevices) {
            refreshTokenService.removeAllTokensByUser(user);
        }
    }

    private TokensResponse generateTokensByUser(User user) {
        String accessToken = jwtUtils.generateAccessToken(user.getId().toString(), user.getRoles());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return new TokensResponse(accessToken, refreshToken.getToken());
    }
}
