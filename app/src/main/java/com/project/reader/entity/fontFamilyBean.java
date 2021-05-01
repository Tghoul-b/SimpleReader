package com.project.reader.entity;

public class fontFamilyBean {
    private String fontName;//字体名称
    private String fontSize;
    private int status;//0代表下载,1代表启用，2代表使用中

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
