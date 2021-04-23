package com.project.reader.entity;

import java.io.Serializable;

/**
 * 这一页是用来填充目录详情页的
 */
public class BookChapterBean implements Serializable {
    private String chapterName;  //
    private String url;

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
