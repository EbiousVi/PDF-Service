package com.example.pdf.service.pdf;

import com.example.pdf.exception.StorageException;

import java.nio.file.Path;
import java.util.List;

public interface PdfService {
    Path splitByPages(Integer[] checkboxValue, Path uploadFilePath) throws StorageException;
    List<Path> renderImgForView(Path uploadFilePath) throws StorageException;
}
