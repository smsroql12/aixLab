package com.example.demo.controller;

import com.example.demo.entity.BaseTimeEntity;
import com.example.demo.entity.Resource;
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

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/aDFvMXMxZTFv")
public class ResourceController {
    @Autowired
    private ResourceService resourceService;
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
    public String resourceWritePro(Resource resource, Model model, Principal principal){
        if(isAuthenticated()) {
            resource.setModifydate(null);
            resource.setContent(Jsoup.clean(resource.getOrigcontent(), Whitelist.none()));
            resource.setAuthor(principal.getName());
            resource.setRegdate(BaseTimeEntity.getCurrentDateTime());
            resourceService.write(resource);

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
    public String resourceUpdate(@PathVariable("id") Integer id, Resource resource, Model model){
        if(isAuthenticated()) {
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

    //글삭제
    @GetMapping("/resource/delete")
    public String resourceDelete(Integer id, Model model){
        if(isAuthenticated()) {
            if(resourceService.existsPost(id)) {
                resourceService.resourceDelete(id);
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
