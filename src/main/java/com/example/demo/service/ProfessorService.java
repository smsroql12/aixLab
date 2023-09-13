package com.example.demo.service;

import com.example.demo.entity.Professor;
import com.example.demo.repository.ProfessorRepository;
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
public class ProfessorService {
    @Autowired
    private ProfessorRepository professorRepository;

    public void write(Professor professor){
        professorRepository.save(professor);
    }

    //해당 번호의 교수가 있는지 확인
    public boolean existsPost(Integer id) {
        return professorRepository.existsById(id);
    }

    public List<Professor> getAllProfessor(Integer sort) {
        if(sort == 1)
            return professorRepository.findAll(Sort.by(Sort.Direction.ASC, "seq"));
        else
            return professorRepository.findAll(Sort.by(Sort.Direction.DESC, "seq"));
    }

    //특정 게시글 불러오기
    public Professor professorView(Integer id){
        return professorRepository.findById(id).get();
    }

    public Long getCount() {
        return professorRepository.countBy();
    }

    //특정게시글삭제
    public void professorDelete(Integer id){
        professorRepository.deleteById(id);
    }

    public ProfessorService(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }


    //전체 교수 리스트 가져오기
    public List<Professor> getAllProfessor(int sort) {
        List<Professor> professorList;
        if(sort == 1)
            professorList = professorRepository.findAll(Sort.by(Sort.Direction.ASC, "seq"));
        else
            professorList = professorRepository.findAll(Sort.by(Sort.Direction.DESC, "seq"));

        return professorList;
    }

    //이름 검색된 결과 가져오기
    public Page<Professor> professorSearchList(String searchKeyword, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, pageable.getPageSize());

        Page<Professor> res = professorRepository.findByNameContaining(searchKeyword, pageable);
        if (res == null) return null;

        return res;
    }

}
