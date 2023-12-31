package com.example.demo.repository;

import com.example.demo.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer>  {
    Page<Notice> findAllByOrderByIdDesc(Pageable pageable);
    Page<Notice> findByTitleContainingOrderByIdDesc(String searchKeyword, Pageable pageable);
    Page<Notice> findByContentContainingOrderByIdDesc(String searchKeyword, Pageable pageable);
    Page<Notice> findByTitleContainingOrContentContainingOrderByIdDesc(String title, String content, Pageable pageable);

    Page<Notice> findByAuthorContainingOrderByIdDesc(String searchKeyword, Pageable pageable);

    List<Notice> findTop4ByOrderByIdDesc();

    boolean existsById(Integer id);

    Long countBy();
}
