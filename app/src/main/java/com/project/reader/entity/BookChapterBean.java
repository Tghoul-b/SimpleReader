package com.project.reader.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Keep;

import java.util.Objects;

@Entity
public class BookChapterBean {
    @Id
    private long id;
    private String bookName;
    private String chapterName;
    private String chapterUrl;
    private String chapterContent;
    private String sourceName;
    private String author;
    private Integer chapterNum;

    @Generated(hash = 1780890838)
    public BookChapterBean(long id, String bookName, String chapterName, String chapterUrl, String chapterContent, String sourceName, String author, Integer chapterNum) {
        this.id = id;
        this.bookName = bookName;
        this.chapterName = chapterName;
        this.chapterUrl = chapterUrl;
        this.chapterContent = chapterContent;
        this.sourceName = sourceName;
        this.author = author;
        this.chapterNum = chapterNum;
    }

    public Integer getChapterNum() {
        return chapterNum;
    }

    public void setChapterNum(Integer chapterNum) {
        this.chapterNum = chapterNum;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    @Generated(hash = 853839616)
    public BookChapterBean() {
    }

    public String getChapterContent() {
        return chapterContent;
    }

    public void setChapterContent(String chapterContent) {
        this.chapterContent = chapterContent;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getChapterUrl() {
        return chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookChapterBean)) return false;
        BookChapterBean that = (BookChapterBean) o;
        return id == that.id &&
                Objects.equals(bookName, that.bookName) &&
                Objects.equals(chapterName, that.chapterName) &&
                Objects.equals(chapterUrl, that.chapterUrl) &&
                Objects.equals(chapterContent, that.chapterContent) &&
                Objects.equals(sourceName, that.sourceName) &&
                Objects.equals(author, that.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookName, chapterName, chapterUrl, chapterContent, sourceName, author, chapterNum);
    }

    @Override
    public String toString() {
        return "BookChapterBean{" +
                "id=" + id +
                ", bookName='" + bookName + '\'' +
                ", chapterName='" + chapterName + '\'' +
                ", chapterUrl='" + chapterUrl + '\'' +
                ", chapterContent='" + chapterContent + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", author='" + author + '\'' +
                ", chapterNum=" + chapterNum +
                '}';
    }
}
