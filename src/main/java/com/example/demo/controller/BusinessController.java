package com.example.demo.controller;

import com.example.demo.entity.BaseTimeEntity;
import com.example.demo.entity.Business;
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

import java.security.Principal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Controller
@RequestMapping("/aDFvMXMxZTFv")
public class BusinessController {
    @Autowired
    private BusinessService businessService;

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
    public String businessWritePro(Business business, Model model, Principal principal){
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
            businessService.write(business);

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
    public String businessUpdate(@PathVariable("id") Integer id, Business business, Model model){
        if(isAuthenticated()) {
            //기존에있던 글이 담겨져 나온다.
            Business businessTemp = businessService.businessView(id);

            //기존에있던 내용을 새로운 내용으로 덮어씌운다.
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

    //글삭제
    @GetMapping("/business/delete")
    public String businessDelete(Integer id, Model model){
        if(isAuthenticated()) {
            if(businessService.existsPost(id)) {
                businessService.businessDelete(id);
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
                model.addAttribute("username", userDetails.getUsername());
                model.addAttribute("eList", eList);
                model.addAttribute("maxPage", 10);
            }
            else {
                switch (type) {
                    case "s_title":
                        Page<Business> list = businessService.businessSearchList(searchKeyword, pageable); //검색리스트반환
                        model.addAttribute("username", userDetails.getUsername());
                        model.addAttribute("eList" , list);
                        model.addAttribute("maxPage", 10);
                        break;
                    case "s_content" :
                        Page<Business> list2 = businessService.businessSearchListByContent(searchKeyword, pageable); //검색리스트반환
                        model.addAttribute("username", userDetails.getUsername());
                        model.addAttribute("eList" , list2);
                        model.addAttribute("maxPage", 10);
                        break;
                    case "s_contitle" :
                        Page<Business> list3 = businessService.businessSearchListByTitleAndContent(searchKeyword, pageable); //검색리스트반환
                        model.addAttribute("username", userDetails.getUsername());
                        model.addAttribute("eList" , list3);
                        model.addAttribute("maxPage", 10);
                        break;
                    case "author" :
                        Page<Business> list4 = businessService.businessSearchListByAuthor(searchKeyword, pageable); //검색리스트반환
                        model.addAttribute("username", userDetails.getUsername());
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
