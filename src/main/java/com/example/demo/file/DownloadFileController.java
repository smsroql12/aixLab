package com.example.demo.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DownloadFileController {
    @Autowired
    FileRepository fileRepository;

    /*
     * Retrieve Files' Information
     */
    @GetMapping("/files")
    public String getListFiles(Model model) {
        List<FileInfo> fileInfos = fileRepository.findAll().stream().map(
                        fileModel -> {
                            String filename = fileModel.getName();
                            String fakeFilename = fileModel.getFakename();
                            String url = MvcUriComponentsBuilder.fromMethodName(DownloadFileController.class, "downloadFile", fileModel.getFakename()).build().toString();
                            return new FileInfo(filename, fakeFilename, url);
                        }
                )
                .collect(Collectors.toList());

        model.addAttribute("files", fileInfos);
        return "listfiles";
    }

    /*
     * Download Files
     */
    @GetMapping("/files/{filename}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String filename){
        //FileModel files = fileRepository.findByName(filename);
        FileModel files = fileRepository.findByFakename(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + files.getFakename() + "\"")
                .body(files.getPic());
    }

}
