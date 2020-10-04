package com.example.pdf.controller;

import com.example.pdf.Service.PdfServiceImpl;
import com.example.pdf.Service.StorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.nio.file.Path;
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

    @PostMapping("/split")
    public String split(@RequestParam("checkboxName") String[] checkboxValue, Model model) {
        Path downloadPath = pdfService.extractRequiredPages(checkboxValue, storageService.getCurrentFilePath());
        model.addAttribute("outputFile", MvcUriComponentsBuilder.fromMethodName(PdfController.class, "serveFile",
                downloadPath.getFileName().toString()).build().toUri().toString());
        return "pdf-service";
    }

    @PostMapping("/")
    public String uploadFile(@RequestParam("file") MultipartFile file, ModelMap model) {
        Path uploadFilePath = storageService.saveUploadFile(file);
        storageService.setCurrentFilePath(uploadFilePath);
        List<String> renderImg = pdfService.renderImgForView(uploadFilePath).stream()
                .map(path -> MvcUriComponentsBuilder.fromMethodName(PdfController.class,
                        "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList());
        model.addAttribute("renderImg", renderImg);
        return "pdf-service";
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
