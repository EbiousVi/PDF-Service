package com.example.pdf.Service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface StorageService {
    void init();
    void deleteAll();
    Resource loadAsResource(String filename);
    Path saveUploadFile(MultipartFile file);
}
