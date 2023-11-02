package com.example.demo.repository;

import com.example.demo.entity.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Integer> {
    List<Banner> findAll();

    Page<Banner> findByTitleContaining(String titleKeyword, Pageable pageable);

    boolean existsById(Integer id);

    Long countBy();
}
