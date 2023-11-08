package com.example.demo.controller;

import com.example.demo.entity.BaseTimeEntity;
import com.example.demo.entity.Resource;
import com.example.demo.file.FileModel;
import com.example.demo.file.FileRepository;
import com.example.demo.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/aDFvMXMxZTFv")
public class ResourceController {
    @Autowired
    private ResourceService resourceService;
    @Autowired
    FileRepository fileRepository;

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return authentication.isAuthenticated();
    }

    @GetMapping("/resource/write")
    public String resourceWriteForm(Model model) {
        if(isAuthenticated()) {
            UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            model.addAttribute("username", userDetails.getUsername());
            return "admin/ad_resource_write";
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
            return "message";
        }
    }

    //글 등록
    @PostMapping("/resource/writepro")
    public String resourceWritePro(Resource resource, Model model, Principal principal, @RequestParam(value = "files", required = false) MultipartFile[] files){
        if(isAuthenticated()) {
            resource.setModifydate(null);
            resource.setContent(Jsoup.clean(resource.getOrigcontent(), Whitelist.none()));
            resource.setAuthor(principal.getName());
            resource.setRegdate(BaseTimeEntity.getCurrentDateTime());
            Integer bno = resourceService.write(resource);

            //file uploads
            List<String> fileNames = new ArrayList<String>();

            try {
                List<FileModel> storedFile = new ArrayList<FileModel>();

                for (MultipartFile file : files) {
                    FileModel fileModel = fileRepository.findByFakename(file.getOriginalFilename());
                    String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
                    String getExt = "." + ext;
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
                        fileModel = new FileModel(file.getOriginalFilename(), fakeNm, "2-"+bno, file.getContentType(), file.getBytes());
                    }

                    fileNames.add(file.getOriginalFilename());
                    storedFile.add(fileModel);
                }

                fileRepository.saveAll(storedFile);

            } catch (Exception e) {
                model.addAttribute("message", "파일 업로드 실패");
                model.addAttribute("searchUrl", fileNames);
            }
            //
            model.addAttribute("message", "글 작성이 완료되었습니다");
            model.addAttribute("searchUrl", "/aDFvMXMxZTFv/resource/md");
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");

        }
        return "message";
    }

    //글 수정 페이지
    @GetMapping("/resource/modify/{id}")
    public String resourceModify(@PathVariable("id") Integer id, Model model){
        if(isAuthenticated()) {
            if(resourceService.existsPost(id)) {
                UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                model.addAttribute("username", userDetails.getUsername());
                model.addAttribute("testboard", resourceService.resourceView(id));

                List<FileModel> fileInfos = fileRepository.findByBno("2-" + id);
                model.addAttribute("files", fileInfos);

                return "admin/ad_resource_modify";
            }
            else {
                model.addAttribute("message", "잘못된 접근입니다.");
                model.addAttribute("searchUrl","/aDFvMXMxZTFv/resource/md");
                return "message";
            }
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
            return "message";
        }
    }

    //글 수정
    @PostMapping("/resource/update/{id}")
    public String resourceUpdate(@PathVariable("id") Integer id, Resource resource, Model model, @RequestParam(value = "files", required = false) MultipartFile[] files, @RequestParam(value="delFileList", required = false)String delFileList){
        if(isAuthenticated()) {
            //기존 파일 삭제
            if(!delFileList.trim().isEmpty()) {
                String[] deleteArray = delFileList.split(",");
                try {
                    for (String m : deleteArray) {
                        fileRepository.deleteById(Long.parseLong(m));
                    }
                } catch (Exception e) {
                    model.addAttribute("message", "파일 삭제 실패 : " + e);
                    model.addAttribute("searchUrl", "/aDFvMXMxZTFv/md");
                }
            }

            //파일 업로드
            List<String> fileNames = new ArrayList<String>();

            try {
                List<FileModel> storedFile = new ArrayList<FileModel>();

                for (MultipartFile file : files) {
                    FileModel fileModel = fileRepository.findByFakename(file.getOriginalFilename());
                    String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
                    String getExt = "." + ext;
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
                        fileModel = new FileModel(file.getOriginalFilename(), fakeNm, "2-"+id, file.getContentType(), file.getBytes());
                    }

                    fileNames.add(file.getOriginalFilename());
                    storedFile.add(fileModel);
                }

                fileRepository.saveAll(storedFile);

            } catch (Exception e) {
                model.addAttribute("message", "파일 업로드 실패");
                model.addAttribute("searchUrl", fileNames);
            }

            //기존에있던 글이 담겨져 나온다.
            Resource resourceTemp = resourceService.resourceView(id);

            //기존에있던 내용을 새로운 내용으로 덮어씌운다.
            resourceTemp.setTitle(resource.getTitle());
            resourceTemp.setOrigcontent(resource.getOrigcontent());
            resourceTemp.setContent(Jsoup.clean(resource.getOrigcontent(), Whitelist.none()));
            resourceTemp.setModifydate(BaseTimeEntity.getCurrentDateTime());

            resourceService.write(resourceTemp);

            model.addAttribute("message","수정이 완료되었습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/resource/md");
            model.addAttribute("boardType", "resource");
            model.addAttribute("postId", id);
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
        }
        return "message";
    }

    //글 삭제
    @GetMapping("/resource/delete")
    public String resourceDelete(Integer id, Model model){
        if(isAuthenticated()) {
            if(resourceService.existsPost(id)) {
                resourceService.resourceDelete(id);
                fileRepository.deleteByBno("2-" + id);
                model.addAttribute("message","삭제가 완료되었습니다.");
                model.addAttribute("searchUrl","/aDFvMXMxZTFv/resource/md");
            }
            else {
                model.addAttribute("message", "잘못된 접근입니다.");
                model.addAttribute("searchUrl","/aDFvMXMxZTFv/resource/md");
            }
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
        }
        return  "message";
    }

    //게시판 목록
    @GetMapping("/resource/md")
    public String viewResourceList(@PageableDefault(page = 1, size = 10, direction = Sort.Direction.ASC) Pageable pageable,
                                Model model,
                                String searchKeyword,
                                String type) {
        UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(isAuthenticated()) {
            if (searchKeyword == null || searchKeyword == "") {
                Page<Resource> eList = resourceService.getAllResource(pageable);
                model.addAttribute("username", userDetails.getUsername());
                model.addAttribute("eList", eList);
                model.addAttribute("maxPage", 10);
            }
            else {
                switch (type) {
                    case "s_title":
                        Page<Resource> list = resourceService.resourceSearchList(searchKeyword, pageable); //검색리스트반환
                        model.addAttribute("username", userDetails.getUsername());
                        model.addAttribute("eList" , list);
                        model.addAttribute("maxPage", 10);
                        break;
                    case "s_content" :
                        Page<Resource> list2 = resourceService.resourceSearchListByContent(searchKeyword, pageable); //검색리스트반환
                        model.addAttribute("username", userDetails.getUsername());
                        model.addAttribute("eList" , list2);
                        model.addAttribute("maxPage", 10);
                        break;
                    case "s_contitle" :
                        Page<Resource> list3 = resourceService.resourceSearchListByTitleAndContent(searchKeyword, pageable); //검색리스트반환
                        model.addAttribute("username", userDetails.getUsername());
                        model.addAttribute("eList" , list3);
                        model.addAttribute("maxPage", 10);
                        break;
                    case "author" :
                        Page<Resource> list4 = resourceService.resourceSearchListByAuthor(searchKeyword, pageable); //검색리스트반환
                        model.addAttribute("username", userDetails.getUsername());
                        model.addAttribute("eList" , list4);
                        model.addAttribute("maxPage", 10);
                        break;
                    default:
                        break;
                }
            }
            return "admin/ad_resource_md";
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
            return "message";
        }
    }
}
