package com.example.demo.service;

import com.example.demo.entity.Business;
import com.example.demo.repository.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BusinessService {
    @Autowired
    private BusinessRepository businessRepository;

    public void write(Business business){
        businessRepository.save(business);
    }

    //게시글이 있는지 확인
    public boolean existsPost(Integer id) {
        return businessRepository.existsById(id);
    }
    //특정 게시글 불러오기
    public Business businessView(Integer id){
        return businessRepository.findById(id).get();
    }

    public List<Business> mainPage() {
        return businessRepository.findTop4ByOrderByIdDesc();
    }

    public Long getCount() {
        return businessRepository.countBy();
    }

    //특정게시글삭제
    public void businessDelete(Integer id){
        businessRepository.deleteById(id);
    }

    public BusinessService(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }


    //게시물 가져오기
    public Page<Business> getAllBusiness(Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, pageable.getPageSize());

        Page<Business> res = businessRepository.findAllByOrderByIdDesc(pageable);
        if (res == null) return null;

        return res;
    }

    //제목 검색된 결과 가져오기
    public Page<Business> businessSearchList(String searchKeyword, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, pageable.getPageSize());

        Page<Business> res = businessRepository.findByTitleContainingOrderByIdDesc(searchKeyword, pageable);
        if (res == null) return null;

        return res;
    }

    //내용 검색된 결과 가져오기
    public Page<Business> businessSearchListByContent(String searchKeyword, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, pageable.getPageSize());

        Page<Business> res = businessRepository.findByContentContainingOrderByIdDesc(searchKeyword, pageable);
        if (res == null) return null;

        return res;
    }

    //제목+내용 검색된 결과 가져오기
    public Page<Business> businessSearchListByTitleAndContent(String searchKeyword, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, pageable.getPageSize());

        Page<Business> res = businessRepository.findByTitleContainingOrContentContainingOrderByIdDesc(searchKeyword, searchKeyword, pageable);

        if (res == null) return null;

        return res;
    }

    //작성자 검색된 결과 가져오기
    public Page<Business> businessSearchListByAuthor(String searchKeyword, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, pageable.getPageSize());

        Page<Business> res = businessRepository.findByAuthorContainingOrderByIdDesc(searchKeyword, pageable);
        if (res == null) return null;

        return res;
    }
}
