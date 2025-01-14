package com.syf.exception;

public class CustomException extends RuntimeException {

    public String getErrorCode() {
        return errorCode;
    }

    private String errorCode;

    public CustomException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}