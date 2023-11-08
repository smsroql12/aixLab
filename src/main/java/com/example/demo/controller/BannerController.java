package com.example.demo.controller;

import com.example.demo.entity.Banner;
import com.example.demo.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/aDFvMXMxZTFv")
public class BannerController {
    @Autowired
    private BannerService bannerService;

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return authentication.isAuthenticated();
    }

    //배너 등록 페이지
    @GetMapping("/banner/write")
    public String bannerWriteForm(Model model) {
        if(isAuthenticated()) {
            UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            model.addAttribute("username", userDetails.getUsername());
            return "admin/ad_banner_write";
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
            return "message";
        }
    }

    //배너 등록
    @PostMapping("/banner/writepro")
    public String bannerWritePro(Banner banner, Model model){
        if(isAuthenticated()) {
            if(bannerService.getCount() < 1) {
                banner.setSeq(1);
            }
            else {
                List<Banner> banners = bannerService.getAllBanner(2);
                banner.setSeq(banners.get(0).getSeq() + 1);
            }
            UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            banner.setAuthor(userDetails.getUsername());
            bannerService.write(banner);

            model.addAttribute("message", "배너 등록이 완료되었습니다");
            model.addAttribute("searchUrl", "/aDFvMXMxZTFv/banner/md");
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");

        }
        return "message";
    }


    //배너 수정 페이지
    @GetMapping("/banner/modify/{id}")
    public String bannerModify(@PathVariable("id") Integer id, Model model){
        if(isAuthenticated()) {
            if(bannerService.existsPost(id)) {
                UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                model.addAttribute("username", userDetails.getUsername());
                model.addAttribute("testboard", bannerService.bannerView(id));
                return "admin/ad_banner_modify";
            }
            else {
                model.addAttribute("message", "잘못된 접근입니다.");
                model.addAttribute("searchUrl","/aDFvMXMxZTFv/banner/md");
                return "message";
            }
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
            return "message";
        }
    }

    //배너 시퀀스 순서 수정
    @PostMapping("/banner/updateseq")
    public String bannerSeqUpdate(Model model, String itemdict){
        if(isAuthenticated()) {
            //itemdict 변수  {"id" : "seq번호" ... } 로 들어옴.
            JSONObject jsonObject = new JSONObject(itemdict);
            jsonObject.keySet().forEach(keyStr ->
            {
                Object keyvalue = jsonObject.get(keyStr);
                Banner tmpBanner = bannerService.bannerView(Integer.parseInt(keyStr));
                tmpBanner.setSeq(Integer.parseInt((String)keyvalue));
                bannerService.write(tmpBanner);
            });
            model.addAttribute("message","수정이 완료되었습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/banner/md");
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
        }
        return "message";
    }

    @PostMapping("/banner/update/{id}")
    public String bannerUpdate(@PathVariable("id") Integer id, Banner banner, Model model){
        if(isAuthenticated()) {
            //기존에있던 내용이 담겨져 나온다.
            Banner bannerTemp = bannerService.bannerView(id);
            UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            //기존에있던 내용을 새로운 내용으로 덮어씌운다.
            bannerTemp.setTitle(banner.getTitle());
            bannerTemp.setAuthor(userDetails.getUsername());
            bannerTemp.setLink(banner.getLink());
            bannerTemp.setPicture(banner.getPicture());

            bannerService.write(bannerTemp);

            model.addAttribute("message","수정이 완료되었습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/banner/md");
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
        }
        return "message";
    }

    //배너 삭제
    @GetMapping("/banner/delete")
    public String bannerDelete(Integer id, Model model){
        if(isAuthenticated()) {
            if(bannerService.existsPost(id)) {
                bannerService.bannerDelete(id);
                model.addAttribute("message","삭제가 완료되었습니다.");
                model.addAttribute("searchUrl","/aDFvMXMxZTFv/banner/md");
            }
            else {
                model.addAttribute("message", "잘못된 접근입니다.");
                model.addAttribute("searchUrl","/aDFvMXMxZTFv/banner/md");
            }
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
        }
        return  "message";
    }

    //배너 목록
    @GetMapping("/banner/md")
    public String viewBannerList(Model model) {
        if(isAuthenticated()) {
            List<Banner> banners = bannerService.getAllBanner(1);
            UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            model.addAttribute("username", userDetails.getUsername());
            model.addAttribute("eList", banners);
            return "admin/ad_banner_md";
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
            return "message";
        }
    }
}
