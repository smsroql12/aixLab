package com.example.demo.service;

import com.example.demo.entity.Banner;
import com.example.demo.repository.BannerRepository;
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
public class BannerService {
    @Autowired
    BannerRepository bannerRepository;

    public void write(Banner banner){
        bannerRepository.save(banner);
    }

    //해당 번호의 교수가 있는지 확인
    public boolean existsPost(Integer id) {
        return bannerRepository.existsById(id);
    }

    public List<Banner> getAllBanner(Integer sort) {
        if(sort == 1)
            return bannerRepository.findAll(Sort.by(Sort.Direction.ASC, "seq"));
        else
            return bannerRepository.findAll(Sort.by(Sort.Direction.DESC, "seq"));
    }

    //특정 게시글 불러오기
    public Banner bannerView(Integer id){
        return bannerRepository.findById(id).get();
    }

    public Long getCount() {
        return bannerRepository.countBy();
    }

    //특정게시글삭제
    public void bannerDelete(Integer id){
        bannerRepository.deleteById(id);
    }

    public BannerService(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }


    //전체 교수 리스트 가져오기
    public List<Banner> getAllBanner(int sort) {
        List<Banner> bannerList;
        if(sort == 1)
            bannerList = bannerRepository.findAll(Sort.by(Sort.Direction.ASC, "seq"));
        else
            bannerList = bannerRepository.findAll(Sort.by(Sort.Direction.DESC, "seq"));

        return bannerList;
    }

    //이름 검색된 결과 가져오기
    public Page<Banner> bannerSearchList(String searchKeyword, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, pageable.getPageSize());

        Page<Banner> res = bannerRepository.findByTitleContaining(searchKeyword, pageable);
        if (res == null) return null;

        return res;
    }
}
