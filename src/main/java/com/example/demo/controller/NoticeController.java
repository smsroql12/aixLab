package com.example.demo.controller;

import com.example.demo.entity.BaseTimeEntity;
import com.example.demo.entity.Notice;
import com.example.demo.file.DownloadFileController;
import com.example.demo.file.FileInfo;
import com.example.demo.file.FileModel;
import com.example.demo.file.FileRepository;
import com.example.demo.service.NoticeService;
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
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.File;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@RequestMapping("/aDFvMXMxZTFv")
public class NoticeController {
    @Autowired
    private NoticeService noticeService;
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
    @GetMapping("/notice/write")
    public String noticeWriteForm(Model model) {
        if(isAuthenticated()) {
            UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            model.addAttribute("username", userDetails.getUsername());
            return "admin/ad_notice_write";
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
            return "message";
        }
    }

    //글 등록
    @PostMapping("/notice/writepro")
    public String noticeWritePro(Notice notice, Model model, Principal principal, @RequestParam(value = "files", required = false) MultipartFile[] files) {
        if(isAuthenticated()) {
            notice.setModifydate(null);
            notice.setContent(Jsoup.clean(notice.getOrigcontent(), Whitelist.none()));
            notice.setAuthor(principal.getName());
            notice.setRegdate(BaseTimeEntity.getCurrentDateTime());
            Integer bno = noticeService.write(notice);

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
                        fileModel = new FileModel(file.getOriginalFilename(), fakeNm, "1-"+bno, file.getContentType(), file.getBytes());
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
            model.addAttribute("searchUrl", "/aDFvMXMxZTFv/notice/md");
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");

        }
        return "message";
    }


    //글 수정 페이지
    @GetMapping("/notice/modify/{id}")
    public String noticeModify(@PathVariable("id") Integer id, Model model){
        if(isAuthenticated()) {
            if(noticeService.existsPost(id)) {
                UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                model.addAttribute("username", userDetails.getUsername());
                model.addAttribute("testboard", noticeService.noticeView(id));

                List<FileModel> fileInfos = fileRepository.findByBno("1-" + id);
                model.addAttribute("files", fileInfos);

                return "admin/ad_notice_modify";
            }
            else {
                model.addAttribute("message", "잘못된 접근입니다.");
                model.addAttribute("searchUrl","/aDFvMXMxZTFv/notice/md");
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
    @PostMapping("/notice/update/{id}")
    public String noticeUpdate(@PathVariable("id") Integer id, Notice notice, Model model, @RequestParam(value = "files", required = false) MultipartFile[] files, @RequestParam(value="delFileList", required = false)String delFileList) {
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
                        fileModel = new FileModel(file.getOriginalFilename(), fakeNm, "1-"+id, file.getContentType(), file.getBytes());
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
            Notice noticeTemp = noticeService.noticeView(id);

            //기존에있던 내용을 새로운 내용으로 덮어씌운다.
            noticeTemp.setTitle(notice.getTitle());
            noticeTemp.setOrigcontent(notice.getOrigcontent());
            noticeTemp.setContent(Jsoup.clean(notice.getOrigcontent(), Whitelist.none()));
            noticeTemp.setModifydate(BaseTimeEntity.getCurrentDateTime());
            noticeService.write(noticeTemp);

            model.addAttribute("message","수정이 완료되었습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/notice/md");
            model.addAttribute("boardType", "notice");
            model.addAttribute("postId", id);
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
        }
        return "message";
    }

    //글 삭제
    @GetMapping("/notice/delete")
    public String noticeDelete(Integer id, Model model){
        if(isAuthenticated()) {
            if(noticeService.existsPost(id)) {
                noticeService.noticeDelete(id);
                fileRepository.deleteByBno("1-" + id);
                model.addAttribute("message","삭제가 완료되었습니다.");
                model.addAttribute("searchUrl","/aDFvMXMxZTFv/notice/md");
            }
            else {
                model.addAttribute("message", "잘못된 접근입니다.");
                model.addAttribute("searchUrl","/aDFvMXMxZTFv/notice/md");
            }
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
        }
        return  "message";
    }

    //게시판 목록
    @GetMapping("/notice/md")
    public String viewNoticeList(@PageableDefault(page = 1, size = 10, direction = Sort.Direction.ASC) Pageable pageable,
                                Model model,
                                String searchKeyword,
                                String type) {
        UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(isAuthenticated()) {
            if (searchKeyword == null || searchKeyword == "") {
                Page<Notice> eList = noticeService.getAllNotice(pageable);
                model.addAttribute("username", userDetails.getUsername());
                model.addAttribute("eList", eList);
                model.addAttribute("maxPage", 10);
            }
            else {
                switch (type) {
                    case "s_title":
                        Page<Notice> list = noticeService.noticeSearchList(searchKeyword, pageable); //검색리스트반환
                        model.addAttribute("username", userDetails.getUsername());
                        model.addAttribute("eList" , list);
                        model.addAttribute("maxPage", 10);
                        break;
                    case "s_content" :
                        Page<Notice> list2 = noticeService.noticeSearchListByContent(searchKeyword, pageable); //검색리스트반환
                        model.addAttribute("username", userDetails.getUsername());
                        model.addAttribute("eList" , list2);
                        model.addAttribute("maxPage", 10);
                        break;
                    case "s_contitle" :
                        Page<Notice> list3 = noticeService.noticeSearchListByTitleAndContent(searchKeyword, pageable); //검색리스트반환
                        model.addAttribute("username", userDetails.getUsername());
                        model.addAttribute("eList" , list3);
                        model.addAttribute("maxPage", 10);
                        break;
                    case "author" :
                        Page<Notice> list4 = noticeService.noticeSearchListByAuthor(searchKeyword, pageable); //검색리스트반환
                        model.addAttribute("username", userDetails.getUsername());
                        model.addAttribute("eList" , list4);
                        model.addAttribute("maxPage", 10);
                        break;
                    default:
                        break;
                }
            }
            return "admin/ad_notice_md";
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
            return "message";
        }
    }
}
