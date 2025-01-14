package com.syf.service;

import com.syf.model.ImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {

    ImageResponse.ImageDetails uploadAndSaveImage(MultipartFile file, String username);

    void deleteImage(String imageId);

    List<ImageResponse> getImagesByUsername(String username);
}
