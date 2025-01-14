package com.syf.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record UserResponse(
        Long userId,
        String username,
        @JsonIgnore String password,
        String email) {
}
