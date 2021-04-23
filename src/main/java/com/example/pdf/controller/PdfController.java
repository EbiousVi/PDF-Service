package com.example.pdf.controller;

import com.example.pdf.service.pdf.split.PdfServiceImpl;
import com.example.pdf.service.storage.StorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PdfController {
    private final StorageServiceImpl storageService;
    private final PdfServiceImpl pdfService;

    @Autowired
    public PdfController(PdfServiceImpl pdfService, StorageServiceImpl storageService) {
        this.pdfService = pdfService;
        this.storageService = storageService;
    }

    @PostMapping("/upload-multiple")
    @ResponseBody
    public List<Path> merge(@RequestBody MultipartFile[] files) {
        System.out.println(Arrays.toString(files));
        Arrays.stream(files).forEach(x -> System.out.println(x.getOriginalFilename()));
        return storageService.saveUploadFiles(files);
    }

    @PostMapping("/split")
    @ResponseBody
    public String split(@RequestBody Integer[] checkboxValue) {
        Path downloadPath = pdfService.extractRequiredPages(checkboxValue, storageService.getUploadFilePath());
        return MvcUriComponentsBuilder.fromMethodName(PdfController.class, "serveFile",
                downloadPath.getFileName().toString()).build().toUri().toString();
    }

    @PostMapping("/upload-single")
    @ResponseBody
    public List<String> uploadFile(@RequestBody MultipartFile file) throws IOException {
        System.out.println(file.getOriginalFilename());
        Path uploadFilePath = storageService.saveUploadFile(file);
        List<String> renderImg = pdfService.renderImgForView(uploadFilePath)
                .stream()
                .map(path -> MvcUriComponentsBuilder.fromMethodName(PdfController.class, "serveFile",
                        path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList());

        return renderImg;
    }

    @GetMapping("/")
    public String home() {
        return "pdf-service";
    }

    @GetMapping("/file/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
