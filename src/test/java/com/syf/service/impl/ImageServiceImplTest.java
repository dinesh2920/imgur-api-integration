package com.syf.service.impl;

import com.syf.config.ImgurClient;
import com.syf.entity.Image;
import com.syf.entity.Users;
import com.syf.exception.CustomException;
import com.syf.model.ImageResponse;
import com.syf.model.imgur.ImgurData;
import com.syf.model.imgur.ImgurResponse;
import com.syf.repository.ImageRepository;
import com.syf.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ImgurClient imgurClient;

    @InjectMocks
    private ImageServiceImpl imageService;

    private Users user;
    private Image image;
    private MultipartFile mockImageFile;

    @BeforeEach
    void setUp() {
        user = Users.builder().userId(1L).username("testuser").passwordHash("password").email("abc@gmail.com").build();
        image = Image.builder().imageId(1L).deleteHash("2424223").imageUrl("http://imgur.com/image.jpg").user(user).build();
        mockImageFile = mock(MultipartFile.class);
        imageService = new ImageServiceImpl(userRepository, imageRepository, imgurClient);
    }

    @Test
    void testUploadAndSaveImage_ShouldReturnImageDetails_WhenUploadIsSuccessful() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        ImgurData imgurData = new ImgurData();
        imgurData.setLink("http://imgur.com/image.jpg");
        imgurData.setDeletehash("deleteHash");
        ImgurResponse imgurResponse = new ImgurResponse("200", true, imgurData);
        lenient().when(imgurClient.uploadImage(any(), any())).thenReturn(imgurResponse);
        when(imageRepository.save(any(Image.class))).thenReturn(image);

        // Act
        ImageResponse.ImageDetails result = imageService.uploadAndSaveImage(mockImageFile, "testuser");

        // Assert
        assertNotNull(result);
        assertEquals(image.getImageId(), result.imageId());
        assertEquals(image.getImageUrl(), result.imageUrl());
    }

    @Test
    void testUploadAndSaveImage_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> imageService.uploadAndSaveImage(mockImageFile, "testuser"));
        assertEquals("User not found for username: testuser", exception.getMessage());
    }

    @Test
    void testDeleteImage_ShouldDeleteImage_WhenImageExists() {
        // Arrange
        when(imageRepository.findById("1")).thenReturn(Optional.of(image));
        when(imgurClient.deleteImage(any(), any())).thenReturn(null);

        // Act
        imageService.deleteImage("1");

        // Assert
        verify(imageRepository, times(1)).delete(image);
    }

    @Test
    void testDeleteImage_ShouldThrowException_WhenImageNotFound() {
        // Arrange
        when(imageRepository.findById("1")).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> imageService.deleteImage("1"));
        assertEquals("Image not found with the Image Id: 1", exception.getMessage());
    }

    @Test
    void testGetImagesByUsername_ShouldReturnImages_WhenUserExists() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(imageRepository.findByUser(user)).thenReturn(List.of(image));

        // Act
        List<ImageResponse> result = imageService.getImagesByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testGetImagesByUsername_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> imageService.getImagesByUsername("testuser"));
        assertEquals("User not found for username: testuser", exception.getMessage());
    }
}