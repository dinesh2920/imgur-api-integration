package com.syf.service.impl;

import com.syf.entity.Users;
import com.syf.exception.CustomException;
import com.syf.model.UserRequest;
import com.syf.model.UserResponse;
import com.syf.model.authorize.AuthResponse;
import com.syf.repository.UserRepository;
import com.syf.service.UserService;
import com.syf.util.JwtUtil;
import com.syf.util.PasswordHasher;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        var username = userRequest.username();
        log.info("Checking whether username '{}' already exists", username);

        userRepository.findByUsername(username)
                .ifPresent(existingUser -> {
                    log.warn("User with username '{}' already exists", username);
                    throw new CustomException("User already exists", "USER_EXISTS");
                });

        // Create and save a new UserEntity from the UserRequest
        var userEntity = createUserEntity(userRequest);

        // Save and map to UserResponse
        userEntity = userRepository.save(userEntity);
        return mapToUserResponse(userEntity);
    }

    private Users createUserEntity(UserRequest userRequest) {
        try {
            return Users.builder()
                    .username(userRequest.username())
                    .passwordHash(PasswordHasher.hashPassword(userRequest.password()))
                    .email(userRequest.email())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        } catch (NoSuchAlgorithmException e) {
            log.error("Error in hashing the password", e);
            throw new CustomException("Error in registering user", "USER_REGISTRATION");
        }
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserServiceImpl::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String deleteByUserId(Long userId) {
        userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException("User doesn't exist", "USER_NOT_EXIST"));

        userRepository.deleteByUserId(userId);
        return "User with Id: " + userId + " deleted successfully!";
    }

    @Override
    public AuthResponse authorizeUser(String username, String password) {
        var userDetails = getUserByName(username);
        try {
            if (userDetails.username().equals(username) && PasswordHasher.hashPassword(password).equals(userDetails.password())) {
                var jwt = jwtUtil.generateToken(username);
                return new AuthResponse(jwt);
            } else {
                throw new CustomException("Invalid username/password", "BAD_CREDENTIALS");
            }
        } catch (NoSuchAlgorithmException e) {
            log.error("Error in hashing the password", e);
            throw new CustomException("Error in parsing password", "BAD_CREDENTIALS");
        }
    }

    private UserResponse getUserByName(String username) {
        var userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("User doesn't exist", "USER_NOT_EXIST"));

        return mapToUserResponse(userEntity);
    }

    private static UserResponse mapToUserResponse(Users userEntity) {
        return new UserResponse(
                userEntity.getUserId(),
                userEntity.getUsername(),
                userEntity.getPasswordHash(),
                userEntity.getEmail()
        );
    }
}
