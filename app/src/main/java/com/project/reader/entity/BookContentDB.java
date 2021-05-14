package com.project.reader.entity;

import org.litepal.LitePal;
import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class BookContentDB extends LitePalSupport {
    private long bookId;  //书名,章节,来源,作者的hashcode

    private String content;

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "BookContentDB{" +
                "id='" + bookId + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
