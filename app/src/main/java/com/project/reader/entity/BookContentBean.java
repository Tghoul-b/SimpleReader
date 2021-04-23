package com.project.reader.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Keep;

import java.util.Objects;

/**
 * 这个是用来存储章节内容 id=Objects.hash(bookname,author,sourceName,chapterNum);  content等于该章的内容
 */
@Entity
public class BookContentBean {
    private long id;
    private String content;

    @Generated(hash = 570097970)
    public BookContentBean(long id, String content) {
        this.id = id;
        this.content = content;
    }

    @Generated(hash = 225088435)
    public BookContentBean() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
