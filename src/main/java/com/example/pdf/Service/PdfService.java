package com.example.pdf.Service;

import java.nio.file.Path;
import java.util.List;

public interface PdfService {
    Path extractRequiredPages(String[] checkboxValue, Path uploadFilePath);
    List<Path> renderImgForView(Path uploadFilePath);
}
