package com.example.pdf.service.pdf.split;

import com.example.pdf.service.storage.DirUtils;
import com.example.pdf.service.storage.StorageServiceImpl;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class PdfServiceImpl implements PdfService {

    private final StorageServiceImpl storage;

    @Autowired
    public PdfServiceImpl(StorageServiceImpl storage) {
        this.storage = storage;
    }

    @Override
    public Path extractRequiredPages(Integer[] checkboxValue, Path uploadFilePath) {
        System.out.println(Arrays.toString(checkboxValue));
        Arrays.sort(checkboxValue);
        System.out.println(Arrays.toString(checkboxValue));
        Path downloadDir = DirUtils.createDirInRootUploadDir("fileLink");
        File file = new File(uploadFilePath.toString());
        PDDocument uploadDocument;
        PDDocument splitDocument = new PDDocument();
        Path splitFilePath = null;
        try {
            uploadDocument = PDDocument.load(file);
            for (Integer value : checkboxValue) {
                splitDocument.addPage(uploadDocument.getPage(value));
            }
            String fileName = "split" + ".pdf";
            splitFilePath = downloadDir.resolve(fileName);
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
        PDDocument document;
        try {
            document = PDDocument.load(file);
            PDFRenderer render = new PDFRenderer(document);
            Path imgDirPath = DirUtils.createDirInRootUploadDir("renderImg");
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                BufferedImage image = render.renderImageWithDPI(i, 36);
                String imgName = "img_" + (i + 1) + ".JPEG";
                Path imgPath = Paths.get(imgDirPath.toString(), imgName);
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
