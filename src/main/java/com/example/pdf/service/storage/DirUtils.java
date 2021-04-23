package com.example.pdf.service.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DirUtils {

    private static StorageServiceImpl storage;

    @Autowired
    public DirUtils(StorageServiceImpl storage) {
        DirUtils.storage = storage;
    }

    public static Path createDirInRootUploadDir(String dirName) {
        try {
            Path dirPath = storage.getUploadRootDir().resolve(dirName);
            if (!Files.exists(dirPath)) {
                Files.createDirectory(dirPath);
            }
            return dirPath;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AAAAAAAAAAAAAAAAAAAAAAAA");
        }
    }

    public static Path createRootUploadDir(Path dirName) {
        try {
            Path rootUploadDir = storage.getStorageRoot().resolve(dirName);
            Files.createDirectory(rootUploadDir);
            return rootUploadDir;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AAAAAAAAAAAAAAAAAAAAAAAA");
        }
    }
}
