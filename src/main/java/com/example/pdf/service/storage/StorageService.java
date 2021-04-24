package com.example.pdf.service.storage;

import com.example.pdf.exception.StorageException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface StorageService {
    void init() throws StorageException;
    void deleteAll();
    Resource loadAsResource(String filename) throws StorageException;
    Path saveUploadFile(MultipartFile file) throws IOException, StorageException;
}
