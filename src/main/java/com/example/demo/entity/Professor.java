package com.example.demo.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.criteria.CriteriaBuilder;

@Entity
@Data
public class Professor {
    @Id // primary key를 의미
    @GeneratedValue(strategy = GenerationType.IDENTITY) // strategy를 GenerationType.IDENTITY로 해준다(MySQL, MariaDB / SEQUENCE는 오라클 용 / AUTO는 자동지정)
    private Integer id;
    private Integer seq;
    private String name;
    private String info;
    private String career;
    private String picture;

    public Professor() {

    }

    public Professor(Integer id, Integer seq, String name, String info, String career, String picture) {
        this.id = id;
        this.seq = seq;
        this.name = name;
        this.info = info;
        this.career = career;
        this.picture = picture;
    }

    public Integer getId() {
        return id;
    }

    public Integer getSeq() {
        return seq;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public String getCareer() {
        return career;
    }

    public String getPicture() {
        return picture;
    }
}
