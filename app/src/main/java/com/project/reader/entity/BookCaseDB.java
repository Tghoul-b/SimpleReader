package com.project.reader.entity;

import org.litepal.crud.LitePalSupport;

public class BookCaseDB extends LitePalSupport {
    private String bookName;
    private String author;
    private Integer lastPosition;//上次阅读的最后的位置
    private String lastDate;//上次阅读的最后时间
}
