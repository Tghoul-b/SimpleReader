package com.project.reader.ui.widget.Page;

import java.util.List;

public class ContentPage{
    int position;
    String title;
    int titleLines; //标题占多少行
    List<String> lines;  //主要是有多少行

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTitleLines() {
        return titleLines;
    }

    public void setTitleLines(int titleLines) {
        this.titleLines = titleLines;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    @Override
    public String toString() {
        return "ContentPage{" +
                "position=" + position +
                ", title='" + title + '\'' +
                ", titleLines=" + titleLines +
                ", lines=" + lines +
                '}';
    }
}
