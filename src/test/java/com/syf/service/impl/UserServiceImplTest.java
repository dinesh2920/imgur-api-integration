package com.syf.service.impl;

import com.syf.entity.Users;
import com.syf.exception.CustomException;
import com.syf.model.UserRequest;
import com.syf.model.UserResponse;
import com.syf.model.authorize.AuthResponse;
import com.syf.repository.UserRepository;
import com.syf.util.JwtUtil;
import com.syf.util.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest("testuser", "password", "test@example.com");
    }

    @Test
    void testCreateUser_ShouldReturnUserResponse_WhenUserIsCreatedSuccessfully() throws NoSuchAlgorithmException {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserResponse result = userService.createUser(userRequest);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.username());
        assertEquals("test@example.com", result.email());
        verify(userRepository, times(1)).save(any(Users.class));
    }

    @Test
    void testCreateUser_ShouldThrowException_WhenUsernameAlreadyExists() {
        // Arrange
        Users existingUser = new Users();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(existingUser));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> userService.createUser(userRequest));
        assertEquals("User already exists", exception.getMessage());
    }

    @Test
    void testGetAllUsers_ShouldReturnListOfUserResponse() {
        // Arrange
        Users user = new Users();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        when(userRepository.findAll()).thenReturn(List.of(user));

        // Act
        List<UserResponse> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).username());
    }

    @Test
    void testDeleteByUserId_ShouldReturnSuccessMessage_WhenUserIsDeletedSuccessfully() {
        // Arrange
        Users user = new Users();
        when(userRepository.findByUserId(1L)).thenReturn(Optional.of(user));

        // Act
        String result = userService.deleteByUserId(1L);

        // Assert
        assertEquals("User with Id: 1 deleted successfully!", result);
        verify(userRepository, times(1)).deleteByUserId(1L);
    }

    @Test
    void testDeleteByUserId_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> userService.deleteByUserId(1L));
        assertEquals("User doesn't exist", exception.getMessage());
    }

    @Test
    void testAuthorizeUser_ShouldReturnAuthResponse_WhenUserIsAuthorizedSuccessfully() throws NoSuchAlgorithmException {
        // Arrange
        Users user = new Users();
        user.setUsername("testuser");
        user.setPasswordHash(PasswordHasher.hashPassword("password"));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("testuser")).thenReturn("jwt-token");

        // Act
        AuthResponse result = userService.authorizeUser("testuser", "password");

        // Assert
        assertNotNull(result);
        assertEquals("jwt-token", result.jwt());
    }

    @Test
    void testAuthorizeUser_ShouldThrowException_WhenUsernameOrPasswordIsIncorrect() throws NoSuchAlgorithmException {
        // Arrange
        Users user = new Users();
        user.setUsername("testuser");
        user.setPasswordHash(PasswordHasher.hashPassword("password"));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> userService.authorizeUser("testuser", "wrongpassword"));
        assertEquals("Invalid username/password", exception.getMessage());
    }
}