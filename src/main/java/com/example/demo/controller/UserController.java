package com.example.demo.controller;

import com.example.demo.entity.Business;
import com.example.demo.entity.SiteUser;
import com.example.demo.entity.UserCreateForm;
import com.example.demo.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/aDFvMXMxZTFv")
public class UserController {

    private final UserService userService;
    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return authentication.isAuthenticated();
    }

    @GetMapping(value = {"/", "/index", "/main", "/home"})
    public String home(Model model) {
        if(isAuthenticated()) {
            UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            model.addAttribute("username", userDetails.getUsername());
            return "admin/ad_main";
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
            return "message";
        }
    }

    @GetMapping("/account/withdrawal")
    public String viewAccountList(@PageableDefault(page = 1, size = 10, direction = Sort.Direction.ASC) Pageable pageable,
                                  Model model,
                                  String searchKeyword,
                                  String type) {

        UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(isAuthenticated()) {
            if (searchKeyword == null || searchKeyword == "") {
                Page<SiteUser> eList = userService.getAllUserInfo(pageable);
                model.addAttribute("username", userDetails.getUsername());
                model.addAttribute("eList", eList);
                model.addAttribute("maxPage", 10);
            }
            else {
                switch (type) {
                    case "s_username":
                        Page<SiteUser> list = userService.userNameSearchList(searchKeyword, pageable); //검색리스트반환
                        model.addAttribute("username", userDetails.getUsername());
                        model.addAttribute("eList" , list);
                        model.addAttribute("maxPage", 10);
                        break;
                    case "s_name":
                        Page<SiteUser> list2 = userService.nameSearchList(searchKeyword, pageable); //검색리스트반환
                        model.addAttribute("username", userDetails.getUsername());
                        model.addAttribute("eList" , list2);
                        model.addAttribute("maxPage", 10);
                        break;
                    default:
                        break;
                }
            }
            return "admin/ad_user_md";
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
            return "message";
        }
    }

    @GetMapping("/account/signup")
    public String signup(Model model) {
        if(isAuthenticated()) {
            UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            model.addAttribute("username", userDetails.getUsername());
            return "admin/ad_user_write";
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
            return "message";
        }

    }

    @PostMapping("/account/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult, Model model) {
        if(isAuthenticated()) {
            if (bindingResult.hasErrors()) {
                model.addAttribute("message", "계정 생성 에러입니다.");
                model.addAttribute("searchUrl","/aDFvMXMxZTFv/account/signup");
                return "message";
            }

            if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
                model.addAttribute("message", "비밀번호가 일치하지 않습니다.");
                model.addAttribute("searchUrl","/aDFvMXMxZTFv/account/signup");
                return "message";
            }

            try {
                userService.create(userCreateForm.getUsername(),
                        userCreateForm.getName(), userCreateForm.getPassword1());
            }catch(DataIntegrityViolationException e) {
                e.printStackTrace();
                model.addAttribute("message", "이미 등록된 사용자입니다.");
                model.addAttribute("searchUrl","/aDFvMXMxZTFv/account/signup");
                return "message";
            }catch(Exception e) {
                e.printStackTrace();
                model.addAttribute("message", "계정 생성 에러입니다.");
                model.addAttribute("searchUrl","/aDFvMXMxZTFv/account/signup");
                return "message";
            }

            model.addAttribute("message", "계정 생성 완료");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/account/signup");
            return "message";
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
            return "message";
        }
    }

    @GetMapping("/account/delete")
    public String accountDelete(Integer id, Model model){
        if(isAuthenticated()) {
            if(userService.existsPost(id)) {
                Optional<SiteUser> userInfoTmp = userService.getUserInfo(id);
                UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

                if(userInfoTmp.get().getUsername().equals(userDetails.getUsername())) { // 삭제하려는 계정이 현재 로그인 되어있는 계정이라면
                    model.addAttribute("message","현재 로그인 되어있는 계정은 삭제 할 수 없습니다.");
                    model.addAttribute("searchUrl","/aDFvMXMxZTFv/account/withdrawal");
                }
                else {
                    userService.accountDelete(id);
                    model.addAttribute("message","삭제가 완료되었습니다.");
                    model.addAttribute("searchUrl","/aDFvMXMxZTFv/account/withdrawal");
                }
            }
            else {
                model.addAttribute("message", "잘못된 접근입니다.");
                model.addAttribute("searchUrl","/aDFvMXMxZTFv/account/withdrawal");
            }
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
        }
        return  "message";
    }

    @GetMapping("/login")
    public String login(Model model) {
        if(isAuthenticated()) {
            UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            model.addAttribute("username", userDetails.getUsername());
            return "admin/ad_main";
        }
        else
            return "admin/ad_login_form";
    }

}