package com.example.demo.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface FileRepository extends JpaRepository<FileModel, Long> {
    FileModel findByName(String name);
    FileModel findByFakename(String name);
    List<FileModel> findByBno(String bno);

    void deleteById(Long id);

    void deleteByBno(String bno);
}