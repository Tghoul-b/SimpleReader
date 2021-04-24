package com.project.reader.entity;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Objects;

public class BookChapterDB extends LitePalSupport implements Serializable {
    @Column(unique = true)
    private long bookId;
    private String chapterName;
    private String url;

    public BookChapterDB() { ;
    }

    public  BookChapterDB(BookdetailBean bookdetailBean, BookChapterBean bookChapterBean){
        bookId=bookChapterBean.hashCode();
        chapterName=bookChapterBean.getChapterName();
        url=bookChapterBean.getUrl();
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
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
}
