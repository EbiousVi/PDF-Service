package com.example.pdf.Service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface PdfService {
    Path saveUploadFile(MultipartFile file);
    Path splitUploadFile(Path uploadFile);
    List<Path> renderImage(Path uploadFile);
    Path mergeSplitFile(String[] checkboxValue, Path path);
    Resource loadAsResource(String filename, Path path);
}
