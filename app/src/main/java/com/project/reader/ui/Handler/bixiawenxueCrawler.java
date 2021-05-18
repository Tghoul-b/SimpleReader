package com.project.reader.ui.Handler;
/**
 * 弃用，这个小说源极其不稳定
 */

import android.content.Context;

import com.project.reader.entity.BookdetailBean;
import com.project.reader.ui.util.tools.BaseApi;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class bixiawenxueCrawler extends baseCrawler {
    @Override
    public List<BookdetailBean> deal_with_doc(Document document, String key, String sourceClass, String searchRule) {
        Element list_row=document.getElementsByClass("txt-list txt-list-row5").get(0);
        Elements li_list=list_row.getElementsByTag("li");
        List<BookdetailBean> list=new ArrayList<>();
        for(int i=1;i<li_list.size();i++){
            BookdetailBean bean=new BookdetailBean();
            Element title_href=li_list.get(i).getElementsByClass("s2").get(0).getElementsByTag("a")
                    .get(0);
            String name=title_href.text();
            String t_url=title_href.attr("href");
            String author=li_list.get(i).getElementsByClass("s4").get(0).text();
            String lastChapter=li_list.get(i).getElementsByClass("s3").get(0)
                                .getElementsByTag("a").get(0).text();
            t_url="https://www.bixia.org"+t_url;
            name= BaseApi.KeyMove(name,author,key);
            if(searchRule.equals("bookname")){
                if(name==null||name.length()==0||name.indexOf(key)==-1)  continue;
            }
            if(searchRule.equals("author")){
                if(author.indexOf(key)==-1)
                    continue;
            }

            bean.setBookName(name);
            bean.setAuthor(author);
            bean.setLastChapter(lastChapter);
            bean.setInfoUrl(t_url);
            boolean flag=false;
           for(BookdetailBean bookdetailBean:list){
               if(bookdetailBean.hashCode()==bean.hashCode()){
                   flag=true;
                   break;
               }
           }
           if(!flag)
            list.add(bean);
        }
        return list;
    }

    @Override
    public BookdetailBean getInfo(String url, BookdetailBean bean) {
        try{
            Connection connection= Jsoup.connect(url).timeout(50000);
            Document doc=connection.get();
            Elements elements=doc.getElementsByTag("meta");
            String reg = "<meta property=\"og:description\"([^\"]*)([^>]*)>";
            String html =elements.toString();
            Matcher matcher = Pattern.compile(reg).matcher(html);
            String desc = "";
            if (matcher.find())
                desc = matcher.group(2);
            bean.setDesc(desc);
            reg = "<meta property=\"og:image\"([^\"]*)\"([ ]*)([^\"]*)";
            matcher = Pattern.compile(reg).matcher(html);
            String imgUrl = "";
            while (matcher.find())
                imgUrl = matcher.group(3);
            bean.setImgUrl(imgUrl); reg = "<meta property=\"og:novel:status\"([^\"]*)\"([ ]*)([^\"]*)";
            matcher = Pattern.compile(reg).matcher(html);
            String status = "";
            while (matcher.find())
                status = matcher.group(3);
            bean.setStatus(status);
            return bean;


        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
