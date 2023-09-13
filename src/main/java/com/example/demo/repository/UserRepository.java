package com.example.demo.repository;

import com.example.demo.entity.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<SiteUser, Integer> {

    Page<SiteUser> findAllByOrderByIdDesc(Pageable pageable);
    Page<SiteUser> findByUsernameContainingOrderByIdDesc(String searchKeyword, Pageable pageable);
    Page<SiteUser> findByNameContainingOrderByIdDesc(String searchKeyword, Pageable pageable);

    Optional<SiteUser> findByUsername(String username);

    Optional<SiteUser> findById(Integer id);
}