package com.gmail.romkatsis.taskmanagementsystem.services;

import com.gmail.romkatsis.taskmanagementsystem.exceptions.InvalidTokenException;
import com.gmail.romkatsis.taskmanagementsystem.models.RefreshToken;
import com.gmail.romkatsis.taskmanagementsystem.models.User;
import com.gmail.romkatsis.taskmanagementsystem.repositories.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshTokenService refreshTokenService;

    private final int refreshTokenDuration = 600000;

    @BeforeEach
    void setUp() {
        refreshTokenService = new RefreshTokenService(refreshTokenRepository, refreshTokenDuration);
    }

    @Test
    void createRefreshToken_Successfully_ReturnsRefreshToken() {
        User user = new User();
        user.setId(1);

        RefreshToken result = refreshTokenService.createRefreshToken(user);

        verify(refreshTokenRepository).save(result);
        assertNotNull(result.getToken());
        assertEquals(user, result.getUser());
        assertTrue(result.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    @Test
    void verifyToken_ValidToken_ReturnsUser() {
        User user = new User();
        user.setId(1);
        RefreshToken refreshToken = new RefreshToken(
                UUID.randomUUID().toString(),
                LocalDateTime.now().plus(refreshTokenDuration, ChronoUnit.MILLIS),
                user
        );
        when(refreshTokenRepository.findByToken(refreshToken.getToken()))
                .thenReturn(Optional.of(refreshToken));

        User result = refreshTokenService.verifyToken(refreshToken.getToken());

        assertEquals(user, result);
        verify(refreshTokenRepository, times(1)).delete(refreshToken);
    }

    @Test
    void testVerifyToken_TokenNotFound() {
        String invalidToken = "invalid-token";
        when(refreshTokenRepository.findByToken(invalidToken)).thenReturn(Optional.empty());

        InvalidTokenException exception = assertThrows(InvalidTokenException.class, () ->
                refreshTokenService.verifyToken(invalidToken)
        );

        assertEquals("Unable to validate refresh token", exception.getMessage());
    }

    @Test
    void verifyToken_ExpiredToken_ThrowsInvalidTokenException() {
        RefreshToken refreshToken = new RefreshToken(
                UUID.randomUUID().toString(),
                LocalDateTime.now().minus(1, ChronoUnit.MILLIS),
                new User()
        );
        when(refreshTokenRepository.findByToken(refreshToken.getToken())).thenReturn(Optional.of(refreshToken));

        InvalidTokenException exception = assertThrows(InvalidTokenException.class, () ->
                refreshTokenService.verifyToken(refreshToken.getToken())
        );

        assertEquals("The refresh token is no longer valid", exception.getMessage());
        verify(refreshTokenRepository, times(1)).delete(refreshToken);
        verify(refreshTokenRepository, times(1)).flush();
    }

    @Test
    void removeAllTokensByUser_Successfully() {
        User user = new User();

        refreshTokenService.removeAllTokensByUser(user);

        verify(refreshTokenRepository, times(1)).deleteAllByUser(user);
    }
}