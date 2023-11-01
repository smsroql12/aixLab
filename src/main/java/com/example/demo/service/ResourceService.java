package com.example.demo.service;

import com.example.demo.entity.Resource;
import com.example.demo.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ResourceService {
    @Autowired
    private ResourceRepository resourceRepository;

    public Integer write(Resource resource){
        resourceRepository.save(resource);
        return resource.getId();
    }

    //게시글이 있는지 확인
    public boolean existsPost(Integer id) {
        return resourceRepository.existsById(id);
    }
    //특정 게시글 불러오기
    public Resource resourceView(Integer id){
        return resourceRepository.findById(id).get();
    }

    public Long getCount() {
        return resourceRepository.countBy();
    }

    //특정게시글삭제
    public void resourceDelete(Integer id){
        resourceRepository.deleteById(id);
    }

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }


    //게시물 가져오기
    public Page<Resource> getAllResource(Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, pageable.getPageSize());

        Page<Resource> res = resourceRepository.findAllByOrderByIdDesc(pageable);
        if (res == null) return null;

        return res;
    }

    //제목 검색된 결과 가져오기
    public Page<Resource> resourceSearchList(String searchKeyword, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, pageable.getPageSize());

        Page<Resource> res = resourceRepository.findByTitleContainingOrderByIdDesc(searchKeyword, pageable);
        if (res == null) return null;

        return res;
    }

    //내용 검색된 결과 가져오기
    public Page<Resource> resourceSearchListByContent(String searchKeyword, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, pageable.getPageSize());

        Page<Resource> res = resourceRepository.findByContentContainingOrderByIdDesc(searchKeyword, pageable);
        if (res == null) return null;

        return res;
    }

    //제목+내용 검색된 결과 가져오기
    public Page<Resource> resourceSearchListByTitleAndContent(String searchKeyword, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, pageable.getPageSize());

        Page<Resource> res = resourceRepository.findByTitleContainingOrContentContainingOrderByIdDesc(searchKeyword, searchKeyword, pageable);

        if (res == null) return null;

        return res;
    }

    //작성자 검색된 결과 가져오기
    public Page<Resource> resourceSearchListByAuthor(String searchKeyword, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, pageable.getPageSize());

        Page<Resource> res = resourceRepository.findByAuthorContainingOrderByIdDesc(searchKeyword, pageable);
        if (res == null) return null;

        return res;
    }
}
