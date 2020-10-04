package com.example.pdf.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class StorageServiceImpl implements StorageService {
    private final Path rootLocation;
    private Path currentFilePath;

    public Path getCurrentFilePath() {
        return currentFilePath;
    }

    public void setCurrentFilePath(Path currentFilePath) {
        this.currentFilePath = currentFilePath;
    }

    @Autowired
    public StorageServiceImpl(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Can't create rootLocation", e);
        }
    }

    @Override
    public Resource loadAsResource(String filename) {
        try (Stream<Path> walk = Files.walk(rootLocation)) {
            List<Path> result = walk.filter(Files::isRegularFile).filter(x -> x.getFileName().toString().equals(filename))
                    .map(Path::toAbsolutePath).collect(Collectors.toList());
            Resource resource = new UrlResource(result.get(0).toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + filename);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }

    @Override
    public Path saveUploadFile(MultipartFile file) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        String filename = Objects.requireNonNull(file.getOriginalFilename()).replaceAll("\\s+", "_");
        Path rootUploadDir = rootLocation.resolve(filename.replaceAll("\\.[a-zA-Z].+", "") + "_" + dtf.format(now));
        try {
            Files.createDirectory(rootUploadDir);
        } catch (Exception e) {
            throw new RuntimeException("saveUploadFile method thrown Exception when Create Directory", e);
        }
        Path uploadFilePath = rootUploadDir.resolve("upload_" + UUID.randomUUID().toString() + ".pdf");
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, uploadFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException("Can't store upload file", e);
        }
        return uploadFilePath;
    }
}
