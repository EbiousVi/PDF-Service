package com.example.pdf.service.storage;

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
import java.util.*;
import java.util.stream.Stream;

@Service
public class StorageServiceImpl implements StorageService {
/*    private final Path storageRoot = Paths.get("src", "main", "resources", "pdf-service");;
    private Path uploadRootDir;
    private Path uploadFilePath;
    private List<Path> uploadFilesPath = new ArrayList<>();

    public List<Path> getUploadFilesPath() {
        return uploadFilesPath;
    }

    public void setUploadFilesPath(List<Path> uploadFilesPath) {
        this.uploadFilesPath = uploadFilesPath;
    }

    public Path getUploadFilePath() {
        return uploadFilePath;
    }

    public void setUploadFilePath(Path uploadFilePath) {
        this.uploadFilePath = uploadFilePath;
    }

    public Path getUploadRootDir() {
        return uploadRootDir;
    }

    public void setUploadRootDir(Path uploadRootDir) {
        this.uploadRootDir = uploadRootDir;
    }

    public Path getStorageRoot() {
        return storageRoot;
    }*/

    private final PathsStorage pathsStorage;

    @Autowired
    public StorageServiceImpl(PathsStorage pathsStorage) {
        this.pathsStorage = pathsStorage;
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(storageRoot.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(storageRoot);
        } catch (IOException e) {
            throw new RuntimeException("Can't create rootLocation", e);
        }
    }

    @Override
    public Resource loadAsResource(String filename) {
        try (Stream<Path> walk = Files.walk(storageRoot)) {
            Optional<Path> result = walk.filter(Files::isRegularFile)
                    .filter(x -> x.getFileName().toString().equals(filename))
                    .map(Path::toAbsolutePath)
                    .findFirst();

            Resource resource = new UrlResource(result.get().toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read uploadFile: " + filename);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not read uploadFile: " + filename, e);
        }
    }

    @Override
    public Path saveUploadFile(MultipartFile file) throws IOException {
        Path rootUploadDir = DirUtils.createRootUploadDir(generateUploadRootDirName(file));
        setUploadRootDir(rootUploadDir);
        Path uploadDir = DirUtils.createDirInRootUploadDir("upload");
        String fileName = "upload_" + UUID.randomUUID().toString() + ".pdf";
        Path uploadFilePath = uploadDir.resolve(fileName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, uploadFilePath, StandardCopyOption.REPLACE_EXISTING);
            setUploadFilePath(uploadFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Can't store upload uploadFile", e);
        }
        return uploadFilePath;
    }

    public List<Path> saveUploadFiles(MultipartFile[] files) {
        Path rootUploadDir = DirUtils.createRootUploadDir(generateUploadRootDirName(files));
        setUploadRootDir(rootUploadDir);
        Path uploadDir = DirUtils.createDirInRootUploadDir("upload");
        Path uploadFilePath;

        for (int i = 0; i < files.length; i++) {
            try (InputStream inputStream = files[i].getInputStream()) {
                String fileName = i + "_" + files[i].getOriginalFilename();
                uploadFilePath = uploadDir.resolve(fileName);
                Files.copy(inputStream, uploadFilePath, StandardCopyOption.REPLACE_EXISTING);
                uploadFilesPath.add(uploadFilePath);
            } catch (Exception e) {
                throw new RuntimeException("Can't store upload uploadFile", e);
            }
        }
        return uploadFilesPath;
    }

    public Path generateUploadRootDirName(MultipartFile[] files) {
        String dateTime = createTimestamp();
        String filename = "size_" + files.length;
        return Paths.get(filename + dateTime);
    }

    private String createTimestamp() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        return "_" + dtf.format(now);
    }

    public Path generateUploadRootDirName(MultipartFile file) {
        String dateTime = createTimestamp();
        String filename = Objects.requireNonNull(file.getOriginalFilename()).replaceAll("\\s+", "_");
        String filenameWithoutSpace = filename.replaceAll("\\.[a-zA-Z].+", "");
        return Paths.get(filenameWithoutSpace + dateTime);
    }
}
