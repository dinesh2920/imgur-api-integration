package com.syf.service.impl;

import com.syf.config.ImgurClient;
import com.syf.entity.Image;
import com.syf.entity.Users;
import com.syf.exception.CustomException;
import com.syf.model.UserResponse;
import com.syf.model.ImageResponse;
import com.syf.repository.ImageRepository;
import com.syf.repository.UserRepository;
import com.syf.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final ImgurClient imgurClient;

    @Value("${imgur.clientId}")
    private String clientId;

    @Autowired
    public ImageServiceImpl(UserRepository userRepository, ImageRepository imageRepository, ImgurClient imgurClient) {
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
        this.imgurClient = imgurClient;
    }

    @Override
    public ImageResponse.ImageDetails uploadAndSaveImage(MultipartFile file, String username) {
        var user = findUserByUsername(username);

        // Upload the image to Imgur API
        var res = imgurClient.uploadImage(file, clientId);

        // Obtain Imgur API URL
        var imgurUrl = res.getData().getLink();
        log.info("Image uploaded successfully, URL: {}", imgurUrl);

        // Save the image to Image Repository
        var image = Image.builder()
                .imageUrl(imgurUrl)
                .deleteHash(res.getData().getDeletehash())
                .user(user)
                .uploadedAt(LocalDateTime.now())
                .build();
        image = imageRepository.save(image);

        // Return the Image details
        return new ImageResponse.ImageDetails(image.getImageId(), image.getImageUrl());
    }

    @Override
    public void deleteImage(String imageId) {
        var image = imageRepository.findById(imageId)
                .orElseThrow(() -> new CustomException("Image not found with the Image Id: " + imageId, "IMG_NOT_FOUND"));

        // Delete the image from Imgur API
        log.info("Deleting image from Imgur API with delete hash: {}", image.getDeleteHash());
        imgurClient.deleteImage("Client-ID " + clientId, image.getDeleteHash());

        // Delete the image from the repository
        log.info("Deleting image from repo with image ID: {}", imageId);
        imageRepository.delete(image);
    }

    @Override
    public List<ImageResponse> getImagesByUsername(String username) {
        var user = findUserByUsername(username);

        // Retrieve images associated with the user
        var images = imageRepository.findByUser(user);
        var imageDetailsList = images.stream()
                .map(image -> new ImageResponse.ImageDetails(image.getImageId(), image.getImageUrl()))
                .collect(Collectors.toList());

        // Map and return the response
        return List.of(new ImageResponse(mapToUserResponse(user), imageDetailsList));
    }

    private Users findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("User not found for username: " + username, "USER_NOT_FOUND"));
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
