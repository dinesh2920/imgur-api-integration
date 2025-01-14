package com.syf.config;

import com.syf.model.imgur.ImgurResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@FeignClient(name = "imgurClient", url = "${imgur.url}")
public interface ImgurClient {

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ImgurResponse uploadImage(@RequestPart("image") MultipartFile file, @RequestParam("client_id") String clientId);

    @DeleteMapping("/{deleteHash}")
    ResponseEntity<Map> deleteImage(@RequestHeader("Authorization") String authorization, @PathVariable("deleteHash") String deleteHash);
}

