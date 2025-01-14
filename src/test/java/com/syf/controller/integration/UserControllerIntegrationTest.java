package com.syf.controller.integration;

import com.syf.model.UserRequest;
import com.syf.repository.UserRepository;
import com.syf.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    private String validToken;

    @BeforeEach
    void setUp() throws Exception {
        // Clear the database
        userRepository.deleteAll();

        // Add a test user
        UserRequest userRequest = new UserRequest("testuser", "password", "test@example.com");
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"password\",\"email\":\"test@example.com\"}"))
                .andExpect(status().isCreated());

        // Generate a valid token
        validToken = jwtUtil.generateToken("testuser");
    }

    @Test
    void testCreateUserEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"newuser\",\"password\":\"newpassword\",\"email\":\"new@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void testAuthorizeUserEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetAllUsersEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/user/getAllUsers")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}