package com.project.reader.ui.Handler;

import com.project.reader.entity.BookSrcBean;
import com.project.reader.entity.BookdetailBean;
import com.project.reader.entity.SearchBookBean;

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
            Connection conn = Jsoup.connect(url).timeout(5000);;
            conn.userAgent("Mozilla/5.0 (Linux; Android 7.1.1; MI 6 Build/NMF26X; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/6.2 TBS/043807 Mobile Safari/537.36 MicroMessenger/6.6.1.1220(0x26060135) NetType/WIFI Language/zh_CN");
            Document doc=conn.get();
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

}
