package com.example.pdf.service.pdf.split;

import java.nio.file.Path;
import java.util.List;

public interface PdfService {
    Path extractRequiredPages(Integer[] checkboxValue, Path uploadFilePath);
    List<Path> renderImgForView(Path uploadFilePath);
}
