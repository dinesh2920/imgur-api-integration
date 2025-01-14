# Imgur API Integration

## Overview
This Spring Boot application allows users to:
- Register with basic information (username and password).
- Upload, view, and delete images after authenticating with their username and password.
- Integrate with the [Imgur API](https://apidocs.imgur.com/) for uploading, viewing, and deleting images.
- Store user information (username, password, and image URLs) in an in-memory H2 database using JPA.

## Features
- **User Registration**: Users can register by providing a username and password.
- **User Authentication**: Users authenticate using their username and password.
- **Image Upload**: Users can upload images to Imgur.
- **View Images**: Users can view all images associated with their account.
- **Delete Images**: Users can delete images from their Imgur account.
- **User Profile**: Users can view their basic profile information and associated images.

## Technologies Used
- **Spring Boot 3.4.1**
- **JDK 17 or higher**
- **H2 In-Memory Database** for storing user data
- **JPA (Java Persistence API)** for database interactions
- **Imgur API** for image uploads, views, and deletions
- **JUnit** for unit testing
- **SLF4J with Logback** for logging

## Prerequisites
- JDK 17 or higher installed on your local machine.
- Imgur API credentials: You need to create an Imgur account and generate a client ID and secret to integrate with the Imgur API.
