package com.project.reader.entity;

import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class BookdetailBean implements Serializable {
    private String bookName;//书名
    private String author;//作者
    private String infoUrl;//得到具体信息的链接
    private String imgUrl;//封面的url
    private String SourceName;//来源的名称
    private String SourceClass;//来源的处理类
    private String Status;//更新或完本
    private String lastChapter;//最新章节
    private String wordCount;//字数
    private String Desc;//描述
    private byte[] drawable;//图片的drawble  byte数组
    private String NovelType;
    private  String update_time;
    private int lastReadPosition;//上次阅读到的章节index
    private String lastReadTime;//上次阅读时间
    public BookdetailBean(){

    }
    public BookdetailBean(BrowserBookBean browserBookBean){
        this.bookName=browserBookBean.getBookName();
        this.author=browserBookBean.getAuthor();
        this.infoUrl=browserBookBean.getInfoUrl();
        this.imgUrl=browserBookBean.getImgUrl();
        this.SourceClass=browserBookBean.getSourceClass();
        this.SourceName=browserBookBean.getSourceName();
        this.Desc=browserBookBean.getDesc();
        this.NovelType=browserBookBean.getBookType();
    }
    public BookdetailBean(BookCaseDB bookCaseDB){
        this.setBookName(bookCaseDB.getBookName());
        this.setAuthor(bookCaseDB.getAuthor());
        this.setLastReadPosition(bookCaseDB.getLastChapterNum());
        this.setInfoUrl(bookCaseDB.getUrl());
        this.setSourceClass(bookCaseDB.getSourceClass());
        this.setImgUrl(bookCaseDB.getImgUrl());
        this.setLastChapter(bookCaseDB.getLastChapter());
        this.setUpdate_time(bookCaseDB.getUpdateTime());
        this.setSourceName(bookCaseDB.getSourceName());
    }
    public int getLastReadPosition() {
        return lastReadPosition;
    }

    public void setLastReadPosition(int lastReadPosition) {
        this.lastReadPosition = lastReadPosition;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getNovelType() {
        return NovelType;
    }

    public void setNovelType(String novelType) {
        NovelType = novelType;
    }

    public String getSourceClass() {
        return SourceClass;
    }

    public byte[] getDrawable() {
        return drawable;
    }

    public void setDrawable(byte[] drawable) {
        this.drawable = drawable;
    }

    public void setSourceClass(String sourceClass) {
        SourceClass = sourceClass;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
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

    public String getInfoUrl() {
        return infoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSourceName() {
        return SourceName;
    }

    public void setSourceName(String sourceName) {
        SourceName = sourceName;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;
    }

    public String getWordCount() {
        return wordCount;
    }

    public void setWordCount(String wordCount) {
        this.wordCount = wordCount;
    }


    @Override
    public String toString() {
        return "BookdetailBean{" +
                "bookName='" + bookName + '\'' +
                ", author='" + author + '\'' +
                ", infoUrl='" + infoUrl + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", SourceName='" + SourceName + '\'' +
                ", SourceClass='" + SourceClass + '\'' +
                ", Status='" + Status + '\'' +
                ", lastChapter='" + lastChapter + '\'' +
                ", wordCount='" + wordCount + '\'' +
                ", Desc='" + Desc + '\'' +
                ", drawable=" + Arrays.toString(drawable) +
                ", NovelType='" + NovelType + '\'' +
                '}';
    }


    @Override
    public int hashCode() {
        return Objects.hash(bookName,author,SourceName);
    }

    public boolean NeedInfo(){
        return bookName==null||author==null||infoUrl==null||imgUrl==null||Status==null||
                update_time==null||lastChapter==null;
    }
}
