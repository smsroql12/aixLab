package com.example.demo.repository;

import com.example.demo.entity.Business;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Integer> {
    Page<Business> findAllByOrderByIdDesc(Pageable pageable);
    Page<Business> findByTitleContainingOrderByIdDesc(String searchKeyword, Pageable pageable);
    Page<Business> findByContentContainingOrderByIdDesc(String searchKeyword, Pageable pageable);
    Page<Business> findByTitleContainingOrContentContainingOrderByIdDesc(String title, String content, Pageable pageable);

    Page<Business> findByAuthorContainingOrderByIdDesc(String searchKeyword, Pageable pageable);

    List<Business> findTop4ByOrderByIdDesc();

    boolean existsById(Integer id);

    Long countBy();
}
