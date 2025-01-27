package com.gmail.romkatsis.taskmanagementsystem.services;

import com.gmail.romkatsis.taskmanagementsystem.enums.Role;
import com.gmail.romkatsis.taskmanagementsystem.exceptions.EmailAlreadyRegisteredException;
import com.gmail.romkatsis.taskmanagementsystem.exceptions.ResourceNotFoundException;
import com.gmail.romkatsis.taskmanagementsystem.models.User;
import com.gmail.romkatsis.taskmanagementsystem.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {


    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User(
                1,
                "test@example.com",
                "password",
                "username",
                new HashSet<>(List.of(Role.ROLE_USER)));
    }

    @Test
    void findUserById_UserExists_ReturnsUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));

        User result = userService.findUserById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void findUserById_UserDoesNotExist_ThrowsException() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(ResourceNotFoundException.class, () -> userService.findUserById(1));

        assertEquals("User by id 1 not found", exception.getMessage());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void saveUser_ValidUser_SavesSuccessfully() {
        when(userRepository.save(mockUser)).thenReturn(mockUser);

        assertDoesNotThrow(() -> userService.saveUser(mockUser));

        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void saveUser_EmailAlreadyRegistered_ThrowsException() {
        when(userRepository.save(mockUser)).thenThrow(new DataIntegrityViolationException("Duplicate email"));

        EmailAlreadyRegisteredException exception =
                assertThrows(EmailAlreadyRegisteredException.class, () -> userService.saveUser(mockUser));

        assertEquals("Email test@example.com already registered", exception.getMessage());
        verify(userRepository, times(1)).save(mockUser);
    }
}