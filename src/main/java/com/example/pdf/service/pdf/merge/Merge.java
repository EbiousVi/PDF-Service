package com.example.pdf.service.pdf.merge;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Merge {
    private String path;

/*    public Path merge(MultipartFile[] files) throws IOException {

        for (MultipartFile uploadFile : files) {
            List<Path> files = Files.list(Paths.get(path)).collect(Collectors.toList());
            PDFMergerUtility PDFmerger = new PDFMergerUtility();
            PDFmerger.setDestinationFileName("destinationFilePath");

            for (Path path : files) {
                File file1 = new File(path.toString());
                PDFmerger.addSource(file1);
            }
            PDFmerger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
        }
        return null;
    }*/

}
