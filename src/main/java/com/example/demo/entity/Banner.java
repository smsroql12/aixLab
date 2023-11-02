package com.example.demo.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Banner {
    @Id // primary key를 의미
    @GeneratedValue(strategy = GenerationType.IDENTITY) // strategy를 GenerationType.IDENTITY로 해준다(MySQL, MariaDB / SEQUENCE는 오라클 용 / AUTO는 자동지정)
    private Integer id;
    private Integer seq;
    private String title;
    private String picture;
    private String link;
    private String author;

    public Banner() {

    }

    public Banner(Integer id, Integer seq, String title, String picture, String link, String author) {
        this.id = id;
        this.seq = seq;
        this.title = title;
        this.picture = picture;
        this.link = link;
        this.author = author;
    }

    public Integer getId() {
        return id;
    }

    public Integer getSeq() {
        return seq;
    }

    public String getTitle() {
        return title;
    }

    public String getPicture() {
        return picture;
    }

    public String getLink() {
        return link;
    }

    public String getAuthor() {
        return author;
    }
}
