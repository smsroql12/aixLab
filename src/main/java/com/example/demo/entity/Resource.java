package com.example.demo.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Resource {
    @Id // primary key를 의미
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String content;

    private String origcontent;

    private String regdate;

    private String modifydate;

    private String author;

    public Resource() {

    }

    public Resource(Integer id, String title, String content, String origcontent, String regdate, String modifydate, String author) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.origcontent = origcontent;
        this.regdate = regdate;
        this.author = author;
        this.modifydate = modifydate;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getOrigcontent() { return origcontent; }

    public String getRegdate() { return regdate; }

    public String getAuthor() { return author; }

    public String getModifydate() {return modifydate; }
}
