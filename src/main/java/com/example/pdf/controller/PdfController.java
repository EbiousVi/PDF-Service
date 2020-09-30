package com.example.pdf.controller;

import com.example.pdf.Service.PdfServiceImpl;
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
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Controller
public class PdfController {
    private static Path splitPdfFolderPath;
    private static Path uploadMultipartFilePath;
    private final PdfServiceImpl pdfServiceImpl;

    @Autowired
    public PdfController(PdfServiceImpl pdfServiceImpl) {
        this.pdfServiceImpl = pdfServiceImpl;
    }

    @PostMapping("/merge")
    public String merge(@RequestParam("checkboxName") String[] checkboxValue, Model model) {
        Path path = pdfServiceImpl.mergeSplitFile(checkboxValue, splitPdfFolderPath);
        model.addAttribute("outputFile", MvcUriComponentsBuilder.fromMethodName(PdfController.class, "serveFile",
                path.getFileName().toString()).build().toUri().toString());
        return "pdf-service";
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, ModelMap model) {
        uploadMultipartFilePath = pdfServiceImpl.saveUploadFile(file);
        splitPdfFolderPath = pdfServiceImpl.splitUploadFile(uploadMultipartFilePath);
        model.addAttribute("renderImg", pdfServiceImpl.renderImage(uploadMultipartFilePath).stream().map(
                path -> MvcUriComponentsBuilder.fromMethodName(PdfController.class,
                        "serveFile", path.getFileName()
                                .toString()).build().toUri().toString())
                .collect(Collectors.toList()));
        return "pdf-service";
    }

    @GetMapping("/")
    public String home() {
        return "pdf-service";
    }

    @GetMapping("/file/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = pdfServiceImpl.loadAsResource(filename, uploadMultipartFilePath);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
