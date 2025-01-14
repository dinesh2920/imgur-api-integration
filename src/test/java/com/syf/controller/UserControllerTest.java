package com.syf.controller;

import com.syf.model.UserRequest;
import com.syf.model.UserResponse;
import com.syf.model.authorize.AuthRequest;
import com.syf.model.authorize.AuthResponse;
import com.syf.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private UserController userController;

    private UserRequest userRequest;
    private UserResponse userResponse;
    private AuthRequest authRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest("testuser", "password", "test@example.com");
        userResponse = new UserResponse(1L, "testuser", "test","test@example.com");
        authRequest = new AuthRequest("testuser", "password");
        authResponse = new AuthResponse("accessToken");
    }

    @Test
    void testCreateUser_ShouldReturnCreated_WhenUserIsCreated() {
        // Arrange
        when(userService.createUser(userRequest)).thenReturn(userResponse);

        // Act
        ResponseEntity<?> response = userController.createUser(userRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userResponse, response.getBody());
    }

    @Test
    void testAuthorizeUser_ShouldReturnOk_WhenCredentialsAreValid() {
        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.authorizeUser(authRequest.username(), authRequest.password())).thenReturn(authResponse);

        // Act
        ResponseEntity<?> response = userController.authorizeUser(authRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(authResponse, response.getBody());
    }

    @Test
    void testAuthorizeUser_ShouldReturnBadRequest_WhenValidationFails() {
        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(new FieldError("authRequest", "username", "Username is required")));

        // Act
        ResponseEntity<?> response = userController.authorizeUser(authRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("username: Username is required"));
    }

    @Test
    void testGetAllUsers_ShouldReturnOk_WhenUsersAreFound() {
        // Arrange
        List<UserResponse> userList = Arrays.asList(userResponse);
        when(userService.getAllUsers()).thenReturn(userList);

        // Act
        ResponseEntity<?> response = userController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(((List<?>) response.getBody()).size() > 0);
    }

    @Test
    void testDeleteUserById_ShouldReturnOk_WhenUserIsDeleted() {
        // Arrange
        long userId = 1L;
        when(userService.deleteByUserId(userId)).thenReturn("User deleted successfully");

        // Act
        ResponseEntity<?> response = userController.deleteUserById(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User deleted successfully", response.getBody());
    }
}