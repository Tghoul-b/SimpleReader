package com.project.reader.entity;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BookCaseDB extends LitePalSupport {
    @Column(unique = true)
    private long bookId;//书名,作者,来源
    private String bookName;//书名
    private String author;//作者
    private String sourceClass;//来源的类
    private String url;//信息来源
    private String imgUrl;
    private Integer lastChapterNum;//上次阅读的最后章节
    private String lastDate;//上次阅读的最后时间
    private String lastChapter;//最新一章的名字
    private String updateTime;
    private boolean updateOrNot;
    private String sourceName;

    public BookCaseDB(){

    }

    public boolean isUpdateOrNot() {
        return updateOrNot;
    }

    public void setUpdateOrNot(boolean updateOrNot) {
        this.updateOrNot = updateOrNot;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public BookCaseDB(BookdetailBean bookdetailBean){
        bookId=bookdetailBean.hashCode();
        bookName=bookdetailBean.getBookName();
        author=bookdetailBean.getAuthor();
        sourceClass=bookdetailBean.getSourceClass();
        url=bookdetailBean.getInfoUrl();
        sourceName=bookdetailBean.getSourceName();
        lastChapterNum=bookdetailBean.getLastReadPosition();
        String s="yyyy-MM-dd HH:mm::ss";
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(s);
        Date date=new Date();
        String dateStr=simpleDateFormat.format(date);
        lastDate=dateStr;
        imgUrl=bookdetailBean.getImgUrl();
        updateTime=bookdetailBean.getUpdate_time();
        lastChapter=bookdetailBean.getLastChapter();
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
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

    public String getSourceClass() {
        return sourceClass;
    }

    public void setSourceClass(String sourceClass) {
        this.sourceClass = sourceClass;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getLastChapterNum() {
        return lastChapterNum;
    }

    public void setLastChapterNum(Integer lastChapterNum) {
        this.lastChapterNum = lastChapterNum;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    @Override
    public String toString() {
        return "BookCaseDB{" +
                "bookId=" + bookId +
                ", bookName='" + bookName + '\'' +
                ", author='" + author + '\'' +
                ", sourceClass='" + sourceClass + '\'' +
                ", url='" + url + '\'' +
                ", lastChapterNum=" + lastChapterNum +
                ", lastDate='" + lastDate + '\'' +
                '}';
    }
}
