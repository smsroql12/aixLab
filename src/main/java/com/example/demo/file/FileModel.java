package com.example.demo.file;

import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "file_model")
@Data
public class FileModel extends DateAudit {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "fakename")
    private String fakename;

    @Column(name = "mimetype")
    private String mimetype;

    @Column(name = "bno")
    private String bno;

    @Lob
    @Column(name = "pic")
    private byte[] pic;

    public FileModel() {
    }

    public FileModel(String name, String fakename, String bno, String mimetype, byte[] pic) {
        this.name = name;
        this.fakename = fakename;
        this.mimetype = mimetype;
        this.bno = bno;
        this.pic = pic;
    }
}