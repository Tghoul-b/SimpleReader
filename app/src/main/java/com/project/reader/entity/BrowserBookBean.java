package com.project.reader.entity;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

public class BrowserBookBean extends LitePalSupport {
    private long bookId;
    private String bookName;
    private String author;
    private String desc;
    private String sourceName;
    private String bookType;
    private String InfoUrl;
    private String imgUrl;
    private String sourceClass;

    public BrowserBookBean() {
    }

    public BrowserBookBean(BookdetailBean bookdetailBean){
        this.bookName=bookdetailBean.getBookName();
        this.author=bookdetailBean.getAuthor();
        this.bookId=bookdetailBean.hashCode();
        this.sourceName=bookdetailBean.getSourceName();
        this.bookType=bookdetailBean.getNovelType();
        this.InfoUrl=bookdetailBean.getInfoUrl();
        this.imgUrl=bookdetailBean.getImgUrl();
        this.desc=bookdetailBean.getDesc();
        this.sourceClass=bookdetailBean.getSourceClass();
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public String getInfoUrl() {
        return InfoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        InfoUrl = infoUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSourceClass() {
        return sourceClass;
    }

    public void setSourceClass(String sourceClass) {
        this.sourceClass = sourceClass;
    }
}
