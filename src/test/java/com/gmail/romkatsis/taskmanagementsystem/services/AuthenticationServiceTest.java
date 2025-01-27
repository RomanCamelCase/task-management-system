package com.gmail.romkatsis.taskmanagementsystem.services;

import com.gmail.romkatsis.taskmanagementsystem.dtos.requests.LogInRequest;
import com.gmail.romkatsis.taskmanagementsystem.dtos.requests.RegistrationRequest;
import com.gmail.romkatsis.taskmanagementsystem.dtos.requests.TokenRequest;
import com.gmail.romkatsis.taskmanagementsystem.dtos.responses.TokensResponse;
import com.gmail.romkatsis.taskmanagementsystem.enums.Role;
import com.gmail.romkatsis.taskmanagementsystem.models.RefreshToken;
import com.gmail.romkatsis.taskmanagementsystem.models.User;
import com.gmail.romkatsis.taskmanagementsystem.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    private TokensResponse tokensResponse;

    @BeforeEach
    void setUp() {
        tokensResponse = new TokensResponse(
                "accessToken",
                "refreshToken");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refreshToken");
        when(jwtUtils.generateAccessToken(anyString(), anySet())).thenReturn("accessToken");
        when(refreshTokenService.createRefreshToken(any(User.class))).thenReturn(refreshToken);

    }

    @Test
    void registerUser_Successfully_ReturnsTokensResponse() {
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "test@example.com",
                "password",
                "username",
                Set.of(Role.ROLE_USER));

        User user = mock(User.class);
        user.setEmail(registrationRequest.getEmail());
        user.setUsername(registrationRequest.getUsername());
        user.setRoles(registrationRequest.getRoles());

        when(modelMapper.map(registrationRequest, User.class)).thenReturn(user);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        doAnswer(invocation -> {
            user.setId(1);
            return null;
        }).when(userService).saveUser(user);

        TokensResponse result = authenticationService.registerUser(registrationRequest);

        assertEquals(tokensResponse.getAccessToken(), result.getAccessToken());
        assertEquals(tokensResponse.getRefreshToken(), result.getRefreshToken());
        verify(userService, times(1)).saveUser(user);
        verify(jwtUtils, times(1)).generateAccessToken(anyString(), anySet());
        verify(refreshTokenService, times(1)).createRefreshToken(any(User.class));
    }

    @Test
    void authenticateUser_Successfully_ReturnsTokensResponse() {
        LogInRequest logInRequest = new LogInRequest(
                "test@example.com",
                "password");
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        User user = new User();
        user.setId(1);
        user.setEmail(logInRequest.getEmail());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("1");
        when(userService.findUserById(1)).thenReturn(user);

        TokensResponse result = authenticationService.authenticateUser(logInRequest);

        assertEquals(tokensResponse.getAccessToken(), result.getAccessToken());
        assertEquals(tokensResponse.getRefreshToken(), result.getRefreshToken());
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, times(1)).findUserById(1);
    }

    @Test
    void refreshUserTokens_Successfully_ReturnsTokensResponse() {
        TokenRequest tokenRequest = new TokenRequest("validRefreshToken");
        User user = new User();
        user.setId(1);

        when(refreshTokenService.verifyToken(tokenRequest.getRefreshToken())).thenReturn(user);

        TokensResponse result = authenticationService.refreshUserTokens(tokenRequest);

        assertEquals(tokensResponse.getAccessToken(), result.getAccessToken());
        assertEquals(tokensResponse.getRefreshToken(), result.getRefreshToken());
        verify(refreshTokenService, times(1)).verifyToken(tokenRequest.getRefreshToken());
        verify(jwtUtils, times(1)).generateAccessToken(anyString(), anySet());
        verify(refreshTokenService, times(1)).createRefreshToken(user);
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void logOut_FromAllDevices() {
        TokenRequest tokenRequest = new TokenRequest("validRefreshToken");
        User user = new User();
        user.setId(1);

        when(refreshTokenService.verifyToken(tokenRequest.getRefreshToken())).thenReturn(user);

        authenticationService.logOut(tokenRequest, true);

        verify(refreshTokenService, times(1)).verifyToken(tokenRequest.getRefreshToken());
        verify(refreshTokenService, times(1)).removeAllTokensByUser(user);
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void logOut_SingleDevice() {
        TokenRequest tokenRequest = new TokenRequest("validRefreshToken");
        User user = new User();
        user.setId(1);

        when(refreshTokenService.verifyToken(tokenRequest.getRefreshToken())).thenReturn(user);

        authenticationService.logOut(tokenRequest, false);

        verify(refreshTokenService, times(1)).verifyToken(tokenRequest.getRefreshToken());
        verify(refreshTokenService, times(0)).removeAllTokensByUser(user);
    }
}
