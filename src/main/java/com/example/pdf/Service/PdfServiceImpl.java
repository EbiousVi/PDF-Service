package com.example.pdf.Service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PdfServiceImpl implements PdfService {

    @Override
    public Path extractRequiredPages(String[] checkboxValue, Path uploadFilePath) {
        Path splitFilePath = Path.of(uploadFilePath.getParent().toString() + "\\split_" + UUID.randomUUID() + ".pdf");
        File file = new File(uploadFilePath.toString());
        PDDocument uploadDocument = new PDDocument();
        PDDocument splitDocument = new PDDocument();
        try {
            uploadDocument = PDDocument.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String s : checkboxValue) {
            splitDocument.addPage(uploadDocument.getPage(Integer.parseInt(s)));
        }
        try {
            splitDocument.save(splitFilePath.toString());
            splitDocument.close();
            uploadDocument.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return splitFilePath;
    }

    @Override
    public List<Path> renderImgForView(Path uploadFilePath) {
        List<Path> listImgPath = new ArrayList<>();
        File file = new File(uploadFilePath.toString());
        PDDocument document = new PDDocument();
        try {
            document = PDDocument.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PDFRenderer pr = new PDFRenderer(document);
        try {
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                BufferedImage image = pr.renderImageWithDPI(i, 36);
                Path imgPath = Path.of(uploadFilePath.getParent().toString() + "\\img_" + UUID.randomUUID() + ".JPEG");
                listImgPath.add(imgPath);
                ImageIO.write(image, "JPEG", new File(imgPath.toString()));
            }
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listImgPath;
    }
}
