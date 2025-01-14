package com.syf.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {

    // Method to hash a password using SHA-256
    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        // Get the MessageDigest instance for SHA-256
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        // Hash the password and get the byte array
        byte[] hashedBytes = messageDigest.digest(password.getBytes());

        // Convert the byte array into a hex string
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashedBytes) {
            // Convert each byte into a two-character hexadecimal representation
            hexString.append(String.format("%02x", b));
        }

        // Return the hashed password as a hex string
        return hexString.toString();
    }
}
