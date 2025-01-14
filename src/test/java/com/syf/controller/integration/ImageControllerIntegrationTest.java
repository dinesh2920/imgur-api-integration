package com.syf.controller.integration;

import com.syf.entity.Image;
import com.syf.entity.Users;
import com.syf.repository.ImageRepository;
import com.syf.repository.UserRepository;
import com.syf.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.InputStream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ImageControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    private String validToken;

    @BeforeEach
    void setUp() {
        // Clear the database
        imageRepository.deleteAll();
        userRepository.deleteAll();

        // Add a test user
        Users user = new Users();
        user.setUsername("testuser");
        user.setPasswordHash("password");
        user.setEmail("test@example.com");
        user=userRepository.save(user);

        // Generate a valid token
        validToken = jwtUtil.generateToken("testuser");

        Image image = new Image();
        image.setImageUrl("http://img");
        image.setUser(user);
        image.setDeleteHash("deleteHash");
        imageRepository.save(image);
    }

    @Test
    void testUploadImageEndpoint() throws Exception {
        ClassPathResource imageResource = new ClassPathResource("test-car.jpg");
        InputStream inputStream = imageResource.getInputStream();
        MockMultipartFile mockImageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", inputStream);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/image")
                        .file(mockImageFile)
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void testViewImagesEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/image")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}