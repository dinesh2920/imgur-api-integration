package com.syf.controller;

import com.syf.model.ImageResponse;
import com.syf.model.UserResponse;
import com.syf.service.impl.ImageServiceImpl;
import com.syf.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ImageControllerTest {

    @Mock
    private ImageServiceImpl imgService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private ImageController imageController;

    private String validToken;
    private String username;
    private MultipartFile mockImageFile;

    @BeforeEach
    void setUp() {
        validToken = "Bearer valid-token";
        username = "testuser";
        mockImageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", new byte[10]);
    }

    @Test
    void testUploadImage_ShouldReturnOk_WhenImageUploadedSuccessfully() {
        // Arrange
        String token = validToken;
        when(jwtUtil.getUsernameFromToken(validToken.substring(7))).thenReturn(username);
        when(imgService.uploadAndSaveImage(mockImageFile, username)).thenReturn(new ImageResponse.ImageDetails(1233476L, "/path/to/image"));

        // Act
        ResponseEntity<?> response = imageController.uploadImage(mockImageFile, token);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof ImageResponse.ImageDetails);
    }

    @Test
    void testViewImages_ShouldReturnOk_WhenImagesFound() {
        // Arrange
        String token = validToken;
        ImageResponse imageResponse = new ImageResponse(new UserResponse(1232L, "test","test", "test"), new ArrayList<>(Arrays.asList(new ImageResponse.ImageDetails(1234L, "/path/to/image"))));
        List<ImageResponse> images = Arrays.asList(imageResponse);
        when(jwtUtil.getUsernameFromToken(validToken.substring(7))).thenReturn(username);
        when(imgService.getImagesByUsername(username)).thenReturn(images);

        // Act
        ResponseEntity<?> response = imageController.viewImages(token);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof List);
    }

    @Test
    void testDeleteImage_ShouldReturnOk_WhenImageDeletedSuccessfully() {
        // Arrange
        String token = validToken;
        String imageId = "123";
        doNothing().when(imgService).deleteImage(imageId);
        when(jwtUtil.getUsernameFromToken(validToken.substring(7))).thenReturn(username);

        // Act
        ResponseEntity<?> response = imageController.deleteImageForAuthenticatedUser(imageId, token);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Image deleted successfully", response.getBody());
    }
}