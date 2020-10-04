package com.example.pdf.Service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

    private final String location = "src\\main\\resources\\pdf-service";

    public String getLocation() {
        return location;
    }

}