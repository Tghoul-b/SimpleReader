package com.project.reader.ui.Handler;

import com.project.reader.entity.BookSrcBean;

public class CrawlerHandler {
    public static baseCrawler getCrawler(String sourceClass) {
    try {
        Class clz=Class.forName(sourceClass);
        return (baseCrawler)clz.newInstance();
    }
    catch (Exception e){
        e.printStackTrace();
     }
    return null;
    }
}
