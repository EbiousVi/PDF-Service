package com.example.pdf.service.storage;

import com.example.pdf.exception.StorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DirUtils {
    private static PathStorage pathStorage;

    @Autowired
    public DirUtils(PathStorage pathStorage) {
        DirUtils.pathStorage = pathStorage;
    }

    public static Path createDir(String dirName) throws StorageException {
        try {
            Path dirPath = pathStorage.getUploadRootDir().resolve(dirName);
            if (!Files.exists(dirPath)) {
                Files.createDirectory(dirPath);
            }
            return dirPath;
        } catch (IOException e) {
            throw new StorageException("Can't createDir", e);
        }
    }

    public static Path createUploadDirStructure(String uploadDirName, String uploadFile) throws StorageException {
        Path rootUploadDir = createRootUploadDir(generateUploadRootDirName(uploadFile));
        pathStorage.setUploadRootDir(rootUploadDir);
        Path uploadDir = createDir(uploadDirName);
        return uploadDir.resolve(uploadFile.replaceAll("\\s+", "_"));
    }

    private static Path createRootUploadDir(String dirName) throws StorageException {
        try {
            Path rootUploadDir = pathStorage.getStorageRoot().resolve(dirName);
            if (!Files.exists(rootUploadDir)) {
                Files.createDirectory(rootUploadDir);
            }
            return rootUploadDir;
        } catch (Exception e) {
            throw new StorageException("Can't createRootUploadDir", e);
        }
    }

    private static String generateUploadRootDirName(String fileName) {
        String timestamp = createTimestamp();
        String deleteFileExtension = fileName.replaceAll("\\.[a-zA-Z].+", "");
        return replaceWhiteSpace(deleteFileExtension + timestamp);
    }

    private static String createTimestamp() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    private static String replaceWhiteSpace(String fileName) {
        return fileName.replaceAll("\\s+", "_");
    }
}
