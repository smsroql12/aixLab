package com.example.demo.controller;

import com.example.demo.entity.BaseTimeEntity;
import com.example.demo.entity.Business;
import com.example.demo.file.FileModel;
import com.example.demo.file.FileRepository;
import com.example.demo.service.BusinessService;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Controller
@RequestMapping("/aDFvMXMxZTFv")
public class BusinessController {
    @Autowired
    private BusinessService businessService;
    @Autowired
    FileRepository fileRepository;

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return authentication.isAuthenticated();
    }

    //글 작성 페이지
    @GetMapping("/business/write")
    public String businessWriteForm(Model model) {
        if(isAuthenticated()) {
            UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            model.addAttribute("username", userDetails.getUsername());
            return "admin/ad_business_write";
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
            return "message";
        }
    }

    //글 등록
    @PostMapping("/business/writepro")
    public String businessWritePro(Business business, Model model, Principal principal, @RequestParam(value = "files", required = false) MultipartFile[] files) {
        if(isAuthenticated()) {
            String tmpSrc = "";
            Pattern pattern = Pattern.compile("<img src=\"([^\"]+)");
            Matcher matcher = pattern.matcher(business.getOrigcontent());
            do  {
                if((matcher.find())) {
                    tmpSrc = matcher.group(1);
                }
            } while(false);

            business.setThumbnail(tmpSrc);
            business.setContent(Jsoup.clean(business.getOrigcontent(), Whitelist.none()));
            business.setModifydate(null);
            business.setAuthor(principal.getName());
            business.setRegdate(BaseTimeEntity.getCurrentDateTime());
            Integer bno = businessService.write(business);

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
                        fileModel = new FileModel(file.getOriginalFilename(), fakeNm, "3-"+bno, file.getContentType(), file.getBytes());
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
            model.addAttribute("searchUrl", "/aDFvMXMxZTFv/business/md");
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");

        }
        return "message";
    }


    //글 수정 페이지
    @GetMapping("/business/modify/{id}")
    public String businessModify(@PathVariable("id") Integer id, Model model){
        if(isAuthenticated()) {
            if(businessService.existsPost(id)) {
                UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                model.addAttribute("username", userDetails.getUsername());
                model.addAttribute("testboard", businessService.businessView(id));

                List<FileModel> fileInfos = fileRepository.findByBno("3-" + id);
                model.addAttribute("files", fileInfos);

                return "admin/ad_business_modify";
            }
            else {
                model.addAttribute("message", "잘못된 접근입니다.");
                model.addAttribute("searchUrl","/aDFvMXMxZTFv/business/md");
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
    @PostMapping("/business/update/{id}")
    public String businessUpdate(@PathVariable("id") Integer id, Business business, Model model, @RequestParam(value = "files", required = false) MultipartFile[] files, @RequestParam(value="delFileList", required = false)String delFileList) {
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
                        fileModel = new FileModel(file.getOriginalFilename(), fakeNm, "3-"+id, file.getContentType(), file.getBytes());
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
            Business businessTemp = businessService.businessView(id);
            String tmpSrc = "";
            Pattern pattern = Pattern.compile("<img src=\"([^\"]+)");
            Matcher matcher = pattern.matcher(business.getOrigcontent());
            do  {
                if((matcher.find())) {
                    tmpSrc = matcher.group(1);
                }
            } while(false);

            //기존에있던 내용을 새로운 내용으로 덮어씌운다.
            businessTemp.setThumbnail(tmpSrc);
            businessTemp.setTitle(business.getTitle());
            businessTemp.setOrigcontent(business.getOrigcontent());
            businessTemp.setContent(Jsoup.clean(business.getOrigcontent(), Whitelist.none()));
            businessTemp.setModifydate(BaseTimeEntity.getCurrentDateTime());

            businessService.write(businessTemp);

            model.addAttribute("message","수정이 완료되었습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/business/md");
            model.addAttribute("boardType", "business");
            model.addAttribute("postId", id);

        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
        }
        return "message";
    }

    //글 삭제
    @GetMapping("/business/delete")
    public String businessDelete(Integer id, Model model){
        if(isAuthenticated()) {
            if(businessService.existsPost(id)) {
                businessService.businessDelete(id);
                fileRepository.deleteByBno("3-" + id);
                model.addAttribute("message","삭제가 완료되었습니다.");
                model.addAttribute("searchUrl","/aDFvMXMxZTFv/business/md");
            }
            else {
                model.addAttribute("message", "잘못된 접근입니다.");
                model.addAttribute("searchUrl","/aDFvMXMxZTFv/business/md");
            }
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
        }
        return  "message";
    }

    //게시판 목록
    @GetMapping("/business/md")
    public String viewBusinessList(@PageableDefault(page = 1, size = 10, direction = Sort.Direction.ASC) Pageable pageable,
                                 Model model,
                                 String searchKeyword,
                                 String type) {
        UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(isAuthenticated()) {
            if (searchKeyword == null || searchKeyword == "") {
                Page<Business> eList = businessService.getAllBusiness(pageable);
                model.addAttribute("eList", eList);
                model.addAttribute("maxPage", 10);
            }
            else {
                switch (type) {
                    case "s_title":
                        Page<Business> list = businessService.businessSearchList(searchKeyword, pageable); //검색리스트반환
                        model.addAttribute("eList" , list);
                        model.addAttribute("maxPage", 10);
                        break;
                    case "s_content" :
                        Page<Business> list2 = businessService.businessSearchListByContent(searchKeyword, pageable); //검색리스트반환
                        model.addAttribute("eList" , list2);
                        model.addAttribute("maxPage", 10);
                        break;
                    case "s_contitle" :
                        Page<Business> list3 = businessService.businessSearchListByTitleAndContent(searchKeyword, pageable); //검색리스트반환
                        model.addAttribute("eList" , list3);
                        model.addAttribute("maxPage", 10);
                        break;
                    case "author" :
                        Page<Business> list4 = businessService.businessSearchListByAuthor(searchKeyword, pageable); //검색리스트반환
                        model.addAttribute("eList" , list4);
                        model.addAttribute("maxPage", 10);
                        break;
                    default:
                        break;
                }
            }
            return "admin/ad_business_md";
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
            return "message";
        }
    }
}
