package com.example.download.download_file;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@SpringBootApplication
@RestController
public class DownloadFileApplication {

    public static void main(String[] args) {
        SpringApplication.run(DownloadFileApplication.class, args);
    }


    @GetMapping("/download")
    public ResponseEntity downloadFileFromLocal() {
        String fileName = "C:\\Software\\tst\\test.csv";
        Path path = Paths.get(fileName);
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        // String contentType=;
        return ResponseEntity.ok()
                //.contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/downloadZipFile")
    public void downloadZipFile(HttpServletResponse response) {
        List<String> listOfFileNames = getListOfFileNames();
        downloadZipFile(response, listOfFileNames);
    }

    private List<String> getListOfFileNames() {
        List<String> listOfFileNames = new ArrayList<>();
        listOfFileNames.add("C:/Software/tst/test.csv");
        listOfFileNames.add("C:/Software/tst/test1.csv");
        return listOfFileNames;
    }


    public void downloadZipFile(HttpServletResponse response, List<String> listOfFileNames) {
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=DataExtraction.zip");
        try(ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream())) {
            for(String fileName : listOfFileNames) {
                FileSystemResource fileSystemResource = new FileSystemResource(fileName);
                ZipEntry zipEntry = new ZipEntry(fileSystemResource.getFilename());
                zipEntry.setSize(fileSystemResource.contentLength());
                zipEntry.setTime(System.currentTimeMillis());

                zipOutputStream.putNextEntry(zipEntry);

                StreamUtils.copy(fileSystemResource.getInputStream(), zipOutputStream);
                zipOutputStream.closeEntry();
            }
            zipOutputStream.finish();
        } catch (IOException e) {
           // logger.error(e.getMessage(), e);
        }
    }
}
