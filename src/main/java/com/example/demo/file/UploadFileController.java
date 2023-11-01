package com.example.demo.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
public class UploadFileController {
    @Autowired
    FileRepository fileRepository;

    @GetMapping("/uploads")
    public String index() {
        return "uploadform";
    }

    @PostMapping("/uploads")
    public String uploadMultipartFile(@RequestParam("files") MultipartFile[] files, Model model) {
        List<String> fileNames = new ArrayList<String>();

        try {
            List<FileModel> storedFile = new ArrayList<FileModel>();

            for (MultipartFile file : files) {
                FileModel fileModel = fileRepository.findByFakename(file.getOriginalFilename());
                //System.out.println(file.getContentType());
                //System.out.println(file.getOriginalFilename());
                String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
                String getExt = "." + ext;
                //System.out.println(getExt);
                if (fileModel != null) {
                    // update new contents
                    fileModel.setPic(file.getBytes());
                } else {
                    String filenm_first = "";
                    int filenm_second = 0;
                    String filenm_third = "";
                    int filenm_fourth = 0;
                    String filenm_fifth = "";

                    for(int i = 1; i <= 8; i++) {
                        char ch = (char) ((Math.random() * 26) + 65);
                        filenm_first = filenm_first.concat(Character.toString(ch));
                    }

                    filenm_second = (int)(Math.random() * 8999) + 1000;

                    for(int i = 1; i <= 4; i++) {
                        char ch = (char) ((Math.random() * 26) + 65);
                        filenm_third = filenm_third.concat(Character.toString(ch));
                    }

                    filenm_fourth = (int)(Math.random() * 8999) + 1000;

                    for(int i = 1; i <= 8; i++) {
                        char ch = (char) ((Math.random() * 26) + 65);
                        filenm_fifth = filenm_fifth.concat(Character.toString(ch));
                    }

                    String fakeNm = filenm_first + "_" + filenm_second + "_" + filenm_third + "_" + filenm_fourth + "_" + filenm_fifth + getExt;
                    fileModel = new FileModel(file.getOriginalFilename(), fakeNm, "", file.getContentType(), file.getBytes());
                }

                fileNames.add(file.getOriginalFilename());
                storedFile.add(fileModel);
            }

            // Save all Files to database
            fileRepository.saveAll(storedFile);

            model.addAttribute("message", "Files uploaded successfully!");
            model.addAttribute("files", fileNames);
        } catch (Exception e) {
            model.addAttribute("message", "Fail!");
            model.addAttribute("files", fileNames);
        }

        return "uploadform";
    }
}
