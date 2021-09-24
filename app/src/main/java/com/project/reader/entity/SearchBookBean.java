package com.project.reader.entity;
import java.util.Objects;

public class SearchBookBean {
    private String name;
    private String author;
    private String SourceClass;//这个是通过反射找到处理的类对象
    private String SearchRule;

    public SearchBookBean() {
    }

    public SearchBookBean(String name, String author, String sourceClass, String searchRule) {
        this.name = name;
        this.author = author;
        SourceClass = sourceClass;
        SearchRule = searchRule;
    }

    public String getSearhRule() {
        return SearchRule;
    }

    public void setSearhRule(String searhRule) {
        SearchRule = searhRule;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSourceClass() {
        return SourceClass;
    }

    public void setSourceClass(String sourceClass) {
        SourceClass = sourceClass;
    }

    public boolean equals(SearchBookBean obj) {
        boolean flag1 = (this.name.indexOf(obj.getName()) != -1) || (obj.getName().indexOf(this.name) != -1);
        boolean flag2 = author.equals(obj.getAuthor());
        return flag1 && flag2;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name,author);
    }

    @Override
    public String toString() {
        return "SearchBookBean{" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", SourceClass='" + SourceClass + '\'' +
                ", SearchRule='" + SearchRule + '\'' +
                '}';
    }
}

