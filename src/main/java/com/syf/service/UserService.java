package com.syf.service;


import com.syf.model.UserRequest;
import com.syf.model.UserResponse;
import com.syf.model.authorize.AuthResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest user);

    List<UserResponse> getAllUsers();

    String deleteByUserId(Long userId);

    AuthResponse authorizeUser(String username, String password);
}
