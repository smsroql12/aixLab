package com.example.demo.repository;

import com.example.demo.entity.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Integer> {
    Page<Resource> findAllByOrderByIdDesc(Pageable pageable);
    Page<Resource> findByTitleContainingOrderByIdDesc(String searchKeyword, Pageable pageable);
    Page<Resource> findByContentContainingOrderByIdDesc(String searchKeyword, Pageable pageable);
    Page<Resource> findByTitleContainingOrContentContainingOrderByIdDesc(String title, String content, Pageable pageable);

    Page<Resource> findByAuthorContainingOrderByIdDesc(String searchKeyword, Pageable pageable);

    boolean existsById(Integer id);

    Long countBy();
}
