package com.example.pdf.Service;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PdfServiceImpl implements PdfService {
    private final Path rootLocation;

    @Autowired
    public PdfServiceImpl(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }


    @Override
    public Path mergeSplitFile(String[] checkboxValue, Path splitFolder) {
        PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
        try {
            Files.createDirectory(Path.of(splitFolder.getParent().toString() + "\\download"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Path destinationDownloadFile = Path.of(splitFolder.getParent().toString() + "\\download\\Pages_" +
                Arrays.toString(checkboxValue).replaceAll("[\\[\\]']+", "").replaceAll(", ", "-") + ".pdf");
        pdfMergerUtility.setDestinationFileName(destinationDownloadFile.toString());

        try {
            for (String s : checkboxValue) {
                pdfMergerUtility.addSource(splitFolder + "\\" + s + ".pdf");
            }
            pdfMergerUtility.mergeDocuments();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return destinationDownloadFile;
    }

    @Override
    public Resource loadAsResource(String filename, Path uploadFilePath) {
        try (Stream<Path> walk = Files.walk(uploadFilePath.getParent().getParent())) {
            List<Path> result = walk.filter(Files::isRegularFile).filter(x -> x.getFileName().toString().equals(filename))
                    .map(Path::toAbsolutePath).collect(Collectors.toList());
            Resource resource = new UrlResource(result.get(0).toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + filename);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }

    @Override
    public Path saveUploadFile(MultipartFile file) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        UUID uuid = UUID.randomUUID();
        String filename = Objects.requireNonNull(file.getOriginalFilename()).replaceAll("\\s+", "_");
        String rootUploadDir = rootLocation + "\\" + filename.replaceAll("\\.[a-zA-Z].+", "") + "_" + dtf.format(now) + "_" + uuid.toString() + "\\upload";
        try {
            Files.createDirectories(Path.of(rootUploadDir));
        } catch (Exception e) {
            throw new RuntimeException("saveUploadFile method thrown Exception when Create Directory", e);
        }
        Path uploadMultipartFilePath = Path.of(rootUploadDir + "\\" + filename);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, uploadMultipartFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException("Can't store upload file", e);
        }
        return uploadMultipartFilePath;
    }

    @Override
    public Path splitUploadFile(Path uploadFilePath) {
        File file = new File(uploadFilePath.toString());
        PDDocument document = new PDDocument();
        try {
            document = PDDocument.load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Splitter splitter = new Splitter();
        List<PDDocument> Pages = new ArrayList<>();
        try {
            Pages = splitter.split(document);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Iterator<PDDocument> iterator = Objects.requireNonNull(Pages).listIterator();
        Path splitDir = Path.of(uploadFilePath.getParent().getParent().toString() + "\\splitPDF");
        try {
            Files.createDirectory(splitDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int pageNumber = 1;
        while (iterator.hasNext()) {
            PDDocument pd = iterator.next();
            String onePageDocumentPath = splitDir.toString() + "\\" + pageNumber++ + ".pdf";
            try {
                pd.save(onePageDocumentPath);
                pd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return splitDir;
    }

    @Override
    public List<Path> renderImage(Path uploadFilePath) {
        List<Path> listImgPath = new ArrayList<>();
        File file = new File(uploadFilePath.toString());
        PDDocument document = new PDDocument();
        try {
            document = PDDocument.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Path renderDir = Path.of(uploadFilePath.getParent().getParent().toString() + "\\renderIMG");
        try {
            Files.createDirectory(renderDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PDFRenderer pr = new PDFRenderer(document);
        int pageNumber = 1;
        try {
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                BufferedImage image = pr.renderImageWithDPI(i, 36);
                Path imgPath = Path.of(renderDir.toString() + "\\" + pageNumber++ + ".JPEG");
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
