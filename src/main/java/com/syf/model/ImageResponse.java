package com.syf.model;

import java.util.List;

public record ImageResponse(UserResponse userResponse, List<ImageDetails> imageDetails) {

    public record ImageDetails(Long imageId, String imageUrl) {
    }
}
