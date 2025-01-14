package com.syf.controller;

import com.syf.model.ImageResponse;
import com.syf.service.impl.ImageServiceImpl;
import com.syf.util.JwtUtil;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/images")
public class ImageController {

    private final ImageServiceImpl imgService;
    private final JwtUtil jwtUtil;

    @Autowired
    public ImageController(ImageServiceImpl imgService, JwtUtil jwtUtil) {
        this.imgService = imgService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") @NotNull MultipartFile file,
                                         @RequestHeader("Authorization") @NotNull String accessToken) {
        try {
            String username = validateToken(accessToken);
            log.info("Username extracted from token: {}", username);

            // Upload & save the image associated with the user.
            ImageResponse.ImageDetails imageDetails = imgService.uploadAndSaveImage(file, username);
            log.info("Image uploaded successfully for user: {}", username);

            return ResponseEntity.ok(imageDetails);
        } catch (Exception exception) {
            String errorMsg = "Error while uploading the image";
            log.error(errorMsg, exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMsg);
        }
    }

    @GetMapping("/view")
    public ResponseEntity<?> viewImages(@RequestHeader("Authorization") @NotNull String accessToken) {
        try {
            String username = validateToken(accessToken);
            log.info("Username extracted from token: {}", username);

            // Retrieve the images by username
            List<ImageResponse> userImages = imgService.getImagesByUsername(username);
            log.info("Images retrieved successfully for user: {}", username);

            return ResponseEntity.ok(userImages);
        } catch (Exception exception) {
            String errorMsg = "Error while viewing images";
            log.error(errorMsg, exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMsg);
        }
    }

    @DeleteMapping("/delete/{imageId}")
    public ResponseEntity<?> deleteImageForAuthenticatedUser(@PathVariable @NotNull String imageId,
                                                             @RequestHeader("Authorization") @NotNull String accessToken) {
        try {
            validateToken(accessToken);
            log.info("Validating token");

            // Deleting the image with ImageId
            imgService.deleteImage(imageId);
            log.info("Image deleted successfully for imageId: {}", imageId);

            return ResponseEntity.ok("Image deleted successfully");
        } catch (Exception exception) {
            String errorMsg = "Error while deleting the image";
            log.error(errorMsg, exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMsg);
        }
    }

    private String validateToken(String accessToken) {
        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }
        return jwtUtil.getUsernameFromToken(accessToken);
    }
}