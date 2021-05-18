package com.project.reader.ui.Handler;

import android.content.Context;

import com.project.reader.entity.BookSrcBean;

import java.lang.reflect.Constructor;

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
