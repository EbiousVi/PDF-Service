package com.example.pdf.service.pdf;

import com.example.pdf.exception.StorageException;
import com.example.pdf.service.storage.DirUtils;
import com.example.pdf.service.storage.PathStorage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PdfServiceImpl implements PdfService {

    private final PathStorage pathStorage;

    @Autowired
    public PdfServiceImpl(PathStorage pathStorage) {
        this.pathStorage = pathStorage;
    }


    @Override
    public Path splitByPages(Integer[] pages, Path uploadFilePath) throws StorageException {
        if (pages.length == 0) throw new StorageException("Missing pages number from frontend");
        Arrays.sort(pages);
        Path downloadDir = DirUtils.createDir("download");
        File file = new File(uploadFilePath.toString());
        PDDocument uploadDocument;
        PDDocument splitDocument = new PDDocument();
        Path splitFilePath;
        try {
            uploadDocument = PDDocument.load(file);
            StringBuilder pageNumbers = new StringBuilder();
            for (Integer page : pages) {
                splitDocument.addPage(uploadDocument.getPage(page));
                pageNumbers.append(page + 1).append(",");
            }
            pageNumbers.deleteCharAt(pageNumbers.lastIndexOf(","));
            String fileName = "split_pages_" + pageNumbers + ".pdf";
            splitFilePath = downloadDir.resolve(fileName);
            splitDocument.save(splitFilePath.toString());
            splitDocument.close();
            uploadDocument.close();
        } catch (Exception e) {
            throw new StorageException("Can't Split " + uploadFilePath, e);
        }
        return splitFilePath;
    }

    @Override
    public List<Path> renderImgForView(Path uploadFilePath) throws StorageException {
        List<Path> listImgPath = new ArrayList<>();
        File file = new File(uploadFilePath.toString());
        PDDocument document;
        try {
            document = PDDocument.load(file);
            PDFRenderer render = new PDFRenderer(document);
            Path imgDirPath = DirUtils.createDir("renderImg");
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                BufferedImage image = render.renderImageWithDPI(i, 36);
                String imgName = "img_" + (i + 1) + ".JPEG";
                Path imgPath = Paths.get(imgDirPath.toString(), imgName);
                listImgPath.add(imgPath);
                ImageIO.write(image, "JPEG", new File(imgPath.toString()));
            }
            document.close();
        } catch (Exception e) {
            throw new StorageException("Render img trouble ", e);
        }
        return listImgPath;
    }
}
