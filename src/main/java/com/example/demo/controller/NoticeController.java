package com.example.demo.controller;

import com.example.demo.entity.BaseTimeEntity;
import com.example.demo.entity.Notice;
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

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/aDFvMXMxZTFv")
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

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
    public String noticeWritePro(Notice notice, Model model, Principal principal){
        if(isAuthenticated()) {
            notice.setModifydate(null);
            notice.setContent(Jsoup.clean(notice.getOrigcontent(), Whitelist.none()));
            notice.setAuthor(principal.getName());
            notice.setRegdate(BaseTimeEntity.getCurrentDateTime());
            noticeService.write(notice);

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
    public String noticeUpdate(@PathVariable("id") Integer id, Notice notice, Model model){
        if(isAuthenticated()) {
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

    //글삭제
    @GetMapping("/notice/delete")
    public String noticeDelete(Integer id, Model model){
        if(isAuthenticated()) {
            if(noticeService.existsPost(id)) {
                noticeService.noticeDelete(id);
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
