package com.example.rizzchat;

public class Contacts {
    public String name, status, image, timestamp; // Add timestamp field

    public Contacts() {}

    public Contacts(String name, String status, String image, String timestamp) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.timestamp = timestamp; // Initialize timestamp
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
