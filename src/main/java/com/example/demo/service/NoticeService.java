package com.example.demo.service;

import com.example.demo.entity.Notice;
import com.example.demo.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NoticeService {
    @Autowired
    private NoticeRepository noticeRepository;

    public void write(Notice notice){
        noticeRepository.save(notice);
    }

    //게시글이 있는지 확인
    public boolean existsPost(Integer id) {
        return noticeRepository.existsById(id);
    }
    //특정 게시글 불러오기
    public Notice noticeView(Integer id){
        return noticeRepository.findById(id).get();
    }

    public Long getCount() {
        return noticeRepository.countBy();
    }

    public List<Notice> mainPage() {
        return noticeRepository.findTop4ByOrderByIdDesc();
    }

    //특정게시글삭제
    public void noticeDelete(Integer id){
        noticeRepository.deleteById(id);
    }

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }


    //게시물 가져오기
    public Page<Notice> getAllNotice(Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, pageable.getPageSize());

        Page<Notice> res = noticeRepository.findAllByOrderByIdDesc(pageable);
        if (res == null) return null;

        return res;
    }

    //제목 검색된 결과 가져오기
    public Page<Notice> noticeSearchList(String searchKeyword, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, pageable.getPageSize());

        Page<Notice> res = noticeRepository.findByTitleContainingOrderByIdDesc(searchKeyword, pageable);
        if (res == null) return null;

        return res;
    }

    //내용 검색된 결과 가져오기
    public Page<Notice> noticeSearchListByContent(String searchKeyword, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, pageable.getPageSize());

        Page<Notice> res = noticeRepository.findByContentContainingOrderByIdDesc(searchKeyword, pageable);
        if (res == null) return null;

        return res;
    }

    //제목+내용 검색된 결과 가져오기
    public Page<Notice> noticeSearchListByTitleAndContent(String searchKeyword, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, pageable.getPageSize());

        Page<Notice> res = noticeRepository.findByTitleContainingOrContentContainingOrderByIdDesc(searchKeyword, searchKeyword, pageable);

        if (res == null) return null;

        return res;
    }

    //작성자 검색된 결과 가져오기
    public Page<Notice> noticeSearchListByAuthor(String searchKeyword, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, pageable.getPageSize());

        Page<Notice> res = noticeRepository.findByAuthorContainingOrderByIdDesc(searchKeyword, pageable);
        if (res == null) return null;

        return res;
    }
}
