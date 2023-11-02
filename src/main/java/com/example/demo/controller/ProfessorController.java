package com.example.demo.controller;

import com.example.demo.entity.BaseTimeEntity;
import com.example.demo.entity.Notice;
import com.example.demo.entity.Professor;
import com.example.demo.service.ProfessorService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/aDFvMXMxZTFv")
public class ProfessorController {
    @Autowired
    private ProfessorService professorService;

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return authentication.isAuthenticated();
    }

    //글 작성 페이지
    @GetMapping("/professor/write")
    public String professorWriteForm(Model model) {
        if(isAuthenticated()) {
            UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            model.addAttribute("username", userDetails.getUsername());
            return "admin/ad_professor_write";
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
            return "message";
        }
    }

    //글 등록
    @PostMapping("/professor/writepro")
    public String professorWritePro(Professor professor, Model model){
        if(isAuthenticated()) {
            if(professorService.getCount() < 1) {
                professor.setSeq(1);
            }
            else {
                List<Professor> professors = professorService.getAllProfessor(2);
                professor.setSeq(professors.get(0).getSeq() + 1);
            }
            professorService.write(professor);

            model.addAttribute("message", "글작성이 완료되었습니다");
            model.addAttribute("searchUrl", "/aDFvMXMxZTFv/professor/md");
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");

        }
        return "message";
    }


    //글 수정 페이지
    @GetMapping("/professor/modify/{id}")
    public String professorModify(@PathVariable("id") Integer id, Model model){
        if(isAuthenticated()) {
            if(professorService.existsPost(id)) {
                UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                model.addAttribute("username", userDetails.getUsername());
                model.addAttribute("testboard", professorService.professorView(id));
                return "admin/ad_professor_modify";
            }
            else {
                model.addAttribute("message", "잘못된 접근입니다.");
                model.addAttribute("searchUrl","/aDFvMXMxZTFv/professor/md");
                return "message";
            }
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
            return "message";
        }
    }

    //교수 시퀀스 순서 수정
    @PostMapping("/professor/updateseq")
    public String professorSeqUpdate(Model model, String itemdict){
        if(isAuthenticated()) {
            //itemdict 변수  {"id" : "seq번호" ... } 로 들어옴.
            JSONObject jsonObject = new JSONObject(itemdict);
            jsonObject.keySet().forEach(keyStr ->
            {
                Object keyvalue = jsonObject.get(keyStr);
                Professor tmpProfessor = professorService.professorView(Integer.parseInt(keyStr));
                tmpProfessor.setSeq(Integer.parseInt((String)keyvalue));
                professorService.write(tmpProfessor);
            });
            model.addAttribute("message","수정이 완료되었습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/professor/md");
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
        }
        return "message";
    }

    @PostMapping("/professor/update/{id}")
    public String professorUpdate(@PathVariable("id") Integer id, Professor professor, Model model){
        if(isAuthenticated()) {
            //기존에있던 내용이 담겨져 나온다.
            Professor professorTemp = professorService.professorView(id);

            //기존에있던 내용을 새로운 내용으로 덮어씌운다.
            professorTemp.setName(professor.getName());
            professorTemp.setInfo(professor.getInfo());
            professorTemp.setCareer(professor.getCareer());
            professorTemp.setPicture(professor.getPicture());

            professorService.write(professorTemp);

            model.addAttribute("message","수정이 완료되었습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/professor/md");
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
        }
        return "message";
    }

    //교수 삭제
    @GetMapping("/professor/delete")
    public String professorDelete(Integer id, Model model){
        if(isAuthenticated()) {
            if(professorService.existsPost(id)) {
                professorService.professorDelete(id);
                model.addAttribute("message","삭제가 완료되었습니다.");
                model.addAttribute("searchUrl","/aDFvMXMxZTFv/professor/md");
            }
            else {
                model.addAttribute("message", "잘못된 접근입니다.");
                model.addAttribute("searchUrl","/aDFvMXMxZTFv/professor/md");
            }
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
        }
        return  "message";
    }

    //게시판 목록
    @GetMapping("/professor/md")
    public String viewProfessorList(Model model) {
        if(isAuthenticated()) {
            List<Professor> professors = professorService.getAllProfessor(1);
            UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            model.addAttribute("username", userDetails.getUsername());
            model.addAttribute("eList", professors);
            return "admin/ad_professor_md";
        }
        else {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("searchUrl","/aDFvMXMxZTFv/login");
            return "message";
        }
    }
}

