package com.project.reader.ui.Handler;

import android.content.Context;

import com.project.reader.entity.BookChapterBean;
import com.project.reader.entity.BookChapterDB;
import com.project.reader.entity.BookSrcBean;
import com.project.reader.entity.BookdetailBean;
import com.project.reader.entity.SearchBookBean;
import com.project.reader.ui.util.tools.BaseApi;
import com.project.reader.ui.widget.Page.ContentChapter;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

public class baseCrawler {
    public List<BookdetailBean> getSearchResult(String url,String sourceClass,String searchRule){
        List<BookdetailBean>  list=new ArrayList<>();
        try {
            int len=url.length()-1;
            while(url.charAt(len)!='=') len--;
            String key=url.substring(len+1);
            Document doc= BaseApi.getHtml(url);
            list=deal_with_doc(doc,key,sourceClass,searchRule);
            return list;
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }
    public List<BookdetailBean>  deal_with_doc(Document document,String key,String sourceClass,String searchRule){
        return null;//子类来重写
    }
    public BookdetailBean getInfo(String url, BookdetailBean bean){
        return null;//子类来重写
    }
    public List<BookChapterBean>  getChapterList(BookdetailBean bean,onCallback callback){
        return  null;
    }
    public ContentChapter getContentFromChapter(BookChapterDB bookChapterDB){return null;}
    public BookdetailBean getChapterAndTime(String url,BookdetailBean bookdetailBean){return null;}
    public interface onCallback{
        public void UpdateOrNot(boolean updated);
    }
}
