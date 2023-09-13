package com.example.demo.repository;

import com.example.demo.entity.Professor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Integer> {
    List<Professor> findAll();

    Page<Professor> findByNameContaining(String searchKeyword, Pageable pageable);

    boolean existsById(Integer id);

    Long countBy();

}
