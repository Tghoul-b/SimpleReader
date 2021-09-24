package com.project.reader.entity;

import org.litepal.crud.LitePalSupport;

public class MyPage extends LitePalSupport {


    private int id;
    private int bookId;
    private int chapter;
    private int startPosition;
    private int pageSize;

    private String FileName;

    public MyPage() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    @Override
    public String toString() {
        return "MyPage{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", chapter=" + chapter +
                ", startPosition=" + startPosition +
                ", pageSize=" + pageSize +
                ", FileName='" + FileName + '\'' +
                '}';
    }
}
