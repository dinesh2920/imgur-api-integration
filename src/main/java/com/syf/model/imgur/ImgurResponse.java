package com.syf.model.imgur;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImgurResponse {

    private String status_code;
    private boolean success;
    private ImgurData data;
}

