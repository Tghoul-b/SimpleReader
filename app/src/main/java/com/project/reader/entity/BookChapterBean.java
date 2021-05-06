package com.project.reader.entity;

import java.io.Serializable;

/**
 * 这一页是用来填充目录详情页的
 */
public class BookChapterBean implements Serializable {
    private long BookId;
    private String bookName;
    private String chapterName;  //
    private String originUrl;//原始的地址
    private String url;
    private String sourceClass;
    private int chapterNum;

    public long getBookId() {
        return BookId;
    }

    public void setBookId(long bookId) {
        BookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getChapterNum() {
        return chapterNum;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    public void setChapterNum(int chapterNum) {
        this.chapterNum = chapterNum;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSourceClass() {
        return sourceClass;
    }

    public void setSourceClass(String sourceClass) {
        this.sourceClass = sourceClass;
    }

    @Override
    public String toString() {
        return "BookChapterBean{" +
                "chapterName='" + chapterName + '\'' +
                ", url='" + url + '\'' +
                ", sourceClass='" + sourceClass + '\'' +
                ", chapterNum=" + chapterNum +
                '}';
    }
}
