package com.project.reader.ui.widget.Page;

import java.io.Serializable;

public class ContentChapter implements Serializable {
   private String title;
   private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ContentChapter{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
