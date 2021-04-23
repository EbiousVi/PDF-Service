package com.example.pdf.service.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface StorageService {
    void init();
    void deleteAll();
    Resource loadAsResource(String filename);
    Path saveUploadFile(MultipartFile file) throws IOException;
}
