package com.syf.controller;

import com.syf.model.UserRequest;
import com.syf.model.UserResponse;
import com.syf.model.authorize.AuthRequest;
import com.syf.model.authorize.AuthResponse;
import com.syf.service.impl.UserServiceImpl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/user")
@Log4j2
@Validated
public class UserController {

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequest user) {
        UserResponse createdUser = userService.createUser(user);
        log.info("Successfully saved user: {}", createdUser.username());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authorizeUser(@Valid @RequestBody AuthRequest authRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Collect all validation errors
            String errorMessages = bindingResult.getAllErrors().stream()
                    .map(error -> ((FieldError) error).getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
        }

        AuthResponse accessToken = userService.authorizeUser(authRequest.username(), authRequest.password());
        return new ResponseEntity<>(accessToken, HttpStatus.OK);
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<?> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable("id") @Positive(message = "User ID must be positive") long userId) {
        String response = userService.deleteByUserId(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}