package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.file.FileModel;
import com.example.demo.file.FileRepository;
import com.example.demo.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Controller
public class MainController {
    @Autowired
    private NoticeService noticeService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private BannerService bannerService;

    @Autowired
    private FileRepository fileRepository;

    @GetMapping(value = {"/", "/index"})
    public String index(Model model) {
        List<Notice> eList = noticeService.mainPage();
        model.addAttribute("eList", eList);

        List<Business> eList2 = businessService.mainPage();
        model.addAttribute("eList2", eList2);

        List<Banner> eList3 = bannerService.getAllBanner(1);
        double count = (double) bannerService.getCount();

        model.addAttribute("count", count);
        model.addAttribute("eList3", eList3);

        return "index";
    }

    @GetMapping("/info")
    public String info() {
        return "info";
    }

    @GetMapping("/director")
    public String director() {
        return "director";
    }

    @GetMapping("/vision")
    public String vision() {
        return "vision";
    }

    @GetMapping("/road")
    public String road() {
        return "road";
    }

    @GetMapping("/view") //example ->  localhost:8080/notice/view?id=1
    public String boardView(Model model, Integer id, String bType){
        String rtn = "";
        switch (bType) {
            case "notice":
                if (noticeService.existsPost(id)) {
                    List<FileModel> fileInfos = fileRepository.findByBno("1-" + id);

                    model.addAttribute("files", fileInfos);
                    model.addAttribute("fileCounts", fileInfos.size());
                    model.addAttribute("testboard", noticeService.noticeView(id));
                    rtn = "view_notice";

                } else {
                    model.addAttribute("message", "해당하는 글이 존재하지 않습니다.");
                    model.addAttribute("searchUrl", "/notice");
                    rtn = "message";
                }
                break;
            case "resource" :
                if (resourceService.existsPost(id)) {
                    List<FileModel> fileInfos = fileRepository.findByBno("2-" + id);

                    model.addAttribute("files", fileInfos);
                    model.addAttribute("fileCounts", fileInfos.size());
                    model.addAttribute("testboard", resourceService.resourceView(id));
                    rtn = "view_resource";

                } else {
                    model.addAttribute("message", "해당하는 글이 존재하지 않습니다.");
                    model.addAttribute("searchUrl", "/resource");
                    rtn = "message";
                }
                break;
            case "business":
                if (businessService.existsPost(id)) {
                    List<FileModel> fileInfos = fileRepository.findByBno("3-" + id);

                    model.addAttribute("files", fileInfos);
                    model.addAttribute("fileCounts", fileInfos.size());
                    model.addAttribute("testboard", businessService.businessView(id));
                    rtn = "view_business";

                } else {
                    model.addAttribute("message", "해당하는 글이 존재하지 않습니다.");
                    model.addAttribute("searchUrl", "/mainbusiness");
                    rtn = "message";
                }
                break;
            default:
                break;
        }
        return rtn;
    }

    @GetMapping("/notice")
    public String notice(@PageableDefault(page = 1, size = 10, direction = Sort.Direction.ASC) Pageable pageable,
                         Model model,
                         String searchKeyword,
                         String type) {

        if (searchKeyword == null || searchKeyword == "") {
            Page<Notice> eList = noticeService.getAllNotice(pageable);
            model.addAttribute("eList", eList);
            model.addAttribute("maxPage", 10);
            model.addAttribute("count", noticeService.getCount());
        }
        else {
            switch (type) {
                case "s_title":
                    Page<Notice> list = noticeService.noticeSearchList(searchKeyword, pageable); //검색리스트반환
                    model.addAttribute("eList" , list);
                    model.addAttribute("maxPage", 10);
                    model.addAttribute("count", list.getContent().size());
                    break;
                case "s_content" :
                    Page<Notice> list2 = noticeService.noticeSearchListByContent(searchKeyword, pageable); //검색리스트반환
                    model.addAttribute("eList" , list2);
                    model.addAttribute("maxPage", 10);
                    model.addAttribute("count", list2.getContent().size());
                    break;
                case "s_contitle" :
                    Page<Notice> list3 = noticeService.noticeSearchListByTitleAndContent(searchKeyword, pageable); //검색리스트반환
                    model.addAttribute("eList" , list3);
                    model.addAttribute("maxPage", 10);
                    model.addAttribute("count", list3.getContent().size());
                    break;
                case "author" :
                    Page<Notice> list4 = noticeService.noticeSearchListByAuthor(searchKeyword, pageable); //검색리스트반환
                    model.addAttribute("eList" , list4);
                    model.addAttribute("maxPage", 10);
                    model.addAttribute("count", list4.getContent().size());
                    break;
                default:
                    break;
            }
        }
        return "notice";
    }

    @GetMapping("/mainbusiness")
    public String mainbusiness(@PageableDefault(page = 1, size = 10, direction = Sort.Direction.ASC) Pageable pageable,
                               Model model,
                               String searchKeyword,
                               String type) {
        if (searchKeyword == null || searchKeyword == "") {
            Page<Business> eList = businessService.getAllBusiness(pageable);
            model.addAttribute("eList", eList);
            model.addAttribute("maxPage", 10);
            model.addAttribute("count", businessService.getCount());
        }
        else {
            switch (type) {
                case "s_title":
                    Page<Business> list = businessService.businessSearchList(searchKeyword, pageable); //검색리스트반환
                    model.addAttribute("eList" , list);
                    model.addAttribute("maxPage", 10);
                    model.addAttribute("count", list.getContent().size());
                    break;
                case "s_content" :
                    Page<Business> list2 = businessService.businessSearchListByContent(searchKeyword, pageable); //검색리스트반환
                    model.addAttribute("eList" , list2);
                    model.addAttribute("maxPage", 10);
                    model.addAttribute("count", list2.getContent().size());
                    break;
                case "s_contitle" :
                    Page<Business> list3 = businessService.businessSearchListByTitleAndContent(searchKeyword, pageable); //검색리스트반환
                    model.addAttribute("eList" , list3);
                    model.addAttribute("maxPage", 10);
                    model.addAttribute("count", list3.getContent().size());
                    break;
                case "author" :
                    Page<Business> list4 = businessService.businessSearchListByAuthor(searchKeyword, pageable); //검색리스트반환
                    model.addAttribute("eList" , list4);
                    model.addAttribute("maxPage", 10);
                    model.addAttribute("count", list4.getContent().size());
                    break;
                default:
                    break;
            }
        }
        return "mainbusiness";
    }

    @GetMapping("/resource")
    public String recourses(@PageableDefault(page = 1, size = 10, direction = Sort.Direction.ASC) Pageable pageable,
                            Model model,
                            String searchKeyword,
                            String type) {
        if (searchKeyword == null || searchKeyword == "") {
            Page<Resource> eList = resourceService.getAllResource(pageable);
            model.addAttribute("eList", eList);
            model.addAttribute("maxPage", 10);
            model.addAttribute("count", resourceService.getCount());
        }
        else {
            switch (type) {
                case "s_title":
                    Page<Resource> list = resourceService.resourceSearchList(searchKeyword, pageable); //검색리스트반환
                    model.addAttribute("eList" , list);
                    model.addAttribute("maxPage", 10);
                    model.addAttribute("count", list.getContent().size());
                    break;
                case "s_content" :
                    Page<Resource> list2 = resourceService.resourceSearchListByContent(searchKeyword, pageable); //검색리스트반환
                    model.addAttribute("eList" , list2);
                    model.addAttribute("maxPage", 10);
                    model.addAttribute("count", list2.getContent().size());
                    break;
                case "s_contitle" :
                    Page<Resource> list3 = resourceService.resourceSearchListByTitleAndContent(searchKeyword, pageable); //검색리스트반환
                    model.addAttribute("eList" , list3);
                    model.addAttribute("maxPage", 10);
                    model.addAttribute("count", list3.getContent().size());
                    break;
                case "author" :
                    Page<Resource> list4 = resourceService.resourceSearchListByAuthor(searchKeyword, pageable); //검색리스트반환
                    model.addAttribute("eList" , list4);
                    model.addAttribute("maxPage", 10);
                    model.addAttribute("count", list4.getContent().size());
                    break;
                default:
                    break;
            }
        }
        return "resource";
    }

    @GetMapping("/professor")
    public String professor(Model model) {
        List<Professor> eList = professorService.getAllProfessor(1);
        double count = (double) professorService.getCount() / 2;
        long r = Math.round(count);

        model.addAttribute("eList", eList);
        model.addAttribute("count", professorService.getCount());
        model.addAttribute("rNum", r);

        return "professor";
    }
}
