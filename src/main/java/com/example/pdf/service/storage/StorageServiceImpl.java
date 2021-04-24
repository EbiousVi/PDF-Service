package com.example.pdf.service.storage;

import com.example.pdf.exception.StorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class StorageServiceImpl implements StorageService {

    private final PathStorage pathStorage;

    @Autowired
    public StorageServiceImpl(PathStorage pathStorage) {
        this.pathStorage = pathStorage;
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(pathStorage.getStorageRoot().toFile());
    }

    @Override
    public void init() throws StorageException {
        try {
            Files.createDirectories(pathStorage.getStorageRoot());
        } catch (IOException e) {
            throw new StorageException("Can't create rootDirectory");
        }
    }

    @Override
    public Resource loadAsResource(String filename) throws StorageException {
        try (Stream<Path> walk = Files.walk(pathStorage.getUploadRootDir())) {
            Optional<Path> path = walk
                    .filter(Files::isRegularFile)// not dir or link;
                    .filter(x -> x.getFileName().toString().equals(filename))
                    .map(Path::toAbsolutePath)
                    .findFirst();
            Resource resource = new UrlResource(path
                    .orElseThrow(() -> new StorageException("Resource Not Found"))
                    .toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new StorageException("Could not read uploadFile: " + filename);
            }
        } catch (IOException e) {
            throw new StorageException("Could not read uploadFile: " + filename, e);
        }
    }

    @Override
    public Path saveUploadFile(MultipartFile file) throws StorageException, UnsupportedEncodingException {
        if (file.isEmpty() | file.getOriginalFilename() == null) {
            throw new StorageException("File not present");
        }
        Path uploadFilePath = DirUtils.createUploadDirStructure("upload", file.getOriginalFilename());
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, uploadFilePath, StandardCopyOption.REPLACE_EXISTING);
            pathStorage.setUploadFilePath(uploadFilePath);
        } catch (IOException e) {
            throw new StorageException("Can't store uploadFile");
        }
        return uploadFilePath;
    }
}
