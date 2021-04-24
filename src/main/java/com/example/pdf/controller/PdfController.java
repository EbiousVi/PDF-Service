package com.example.pdf.controller;

import com.example.pdf.exception.StorageException;
import com.example.pdf.service.pdf.PdfServiceImpl;
import com.example.pdf.service.storage.PathStorage;
import com.example.pdf.service.storage.StorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PdfController {
    private final StorageServiceImpl storageService;
    private final PdfServiceImpl pdfService;
    private final PathStorage pathStorage;

    @Autowired
    public PdfController(PdfServiceImpl pdfService, StorageServiceImpl storageService, PathStorage pathStorage) {
        this.pdfService = pdfService;
        this.storageService = storageService;
        this.pathStorage = pathStorage;
    }

    @PostMapping("/split")
    @ResponseBody
    public String split(@RequestBody Integer[] pages) throws StorageException {
        Path path = pdfService.splitByPages(pages, pathStorage.getUploadFilePath());
        return MvcUriComponentsBuilder.fromMethodName(PdfController.class, "serveFile",
                path.getFileName().toString()).build().toUri().toString();
    }

    @PostMapping("/upload-single")
    @ResponseBody
    public List<String> uploadFile(@RequestBody MultipartFile file) throws StorageException, UnsupportedEncodingException {
        Path uploadFilePath = storageService.saveUploadFile(file);
        return pdfService.renderImgForView(uploadFilePath)
                .stream()
                .map(path -> MvcUriComponentsBuilder.fromMethodName(PdfController.class, "serveFile",
                        path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList());
    }

    @GetMapping("/file/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws StorageException {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
