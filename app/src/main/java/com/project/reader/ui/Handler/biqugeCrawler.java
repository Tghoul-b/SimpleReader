package com.project.reader.ui.Handler;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.project.reader.db.dbUtils;
import com.project.reader.entity.BookChapterBean;
import com.project.reader.entity.BookChapterDB;
import com.project.reader.entity.BookContentDB;
import com.project.reader.entity.BookdetailBean;
import com.project.reader.entity.SearchBookBean;
import com.project.reader.ui.util.tools.App;
import com.project.reader.ui.util.tools.BaseApi;
import com.project.reader.ui.widget.Page.ContentChapter;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.LitePal;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class biqugeCrawler extends baseCrawler{
    @Override
    public List<BookdetailBean> getSearchResult(String url, String sourceClass, String searchRule) {
        System.out.println(url);
        return super.getSearchResult(url, sourceClass, searchRule);
    }
    @Override
    public List<BookdetailBean> deal_with_doc(Document document,String key,String sourceClass,String searchRule)  {
        Elements divs = document.getElementsByTag("table");
        Element div = divs.get(0);
        List<BookdetailBean>  list=new ArrayList<>();
        Elements elementsByTag = div.getElementsByTag("tr");
        try {
            for (int i = 1; i < elementsByTag.size(); i++) {
                Element element = elementsByTag.get(i);
                Elements info = element.getElementsByTag("td");
                BookdetailBean bookdetailBean=new BookdetailBean();
                String name = info.get(0).text();

                String author=info.get(2).text();
                name=BaseApi.KeyMove(name,author,key);
                bookdetailBean.setBookName(name);
                bookdetailBean.setLastChapter(info.get(1).text());
                bookdetailBean.setAuthor(author);
                if(searchRule.equals("bookname")){
                    if(name==null||name.length()==0||name.indexOf(key)==-1)  continue;
                }
                if(searchRule.equals("author")){
                    if(bookdetailBean.getAuthor().indexOf(key)==-1)
                         continue;
                }
                String t_url = info.get(0).getElementsByTag("a").attr("href");
                t_url = "https://www.wqge.cc" + t_url;
                bookdetailBean.setInfoUrl(t_url);
                boolean flag=false;
                for(BookdetailBean bean:list){
                    if(bean.hashCode()==bookdetailBean.hashCode()) {
                        flag = true;
                        break;
                    }
                }
                if(!flag)  //相当于一个网站中通过书名过滤的书籍进行遴选，过滤之后重名的就不要了,减少App压力
                        list.add(bookdetailBean);
            }
            return list;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public BookdetailBean getInfo(String url, BookdetailBean Bean) {
        try {
            Connection conn = Jsoup.connect(url).timeout(50000);
            Document document = conn.get();
            Elements elements = document.getElementsByTag("meta");
            String reg = "<meta property=\"og:description\"([^\"]*)([^>]*)>";
            String html =elements.toString();
            Matcher matcher = Pattern.compile(reg).matcher(html);
            String desc = "";
            if (matcher.find())
                desc = matcher.group(2);
            Bean.setDesc(desc);
            reg = "<meta property=\"og:image\"([^\"]*)\"([ ]*)([^\"]*)";
            matcher = Pattern.compile(reg).matcher(html);
            String imgUrl = "";
            while (matcher.find())
                imgUrl = matcher.group(3);
            Bean.setImgUrl(imgUrl);
            reg = "<meta property=\"og:novel:status\"([^\"]*)\"([ ]*)([^\"]*)";
            matcher = Pattern.compile(reg).matcher(html);
            String status = "";
            while (matcher.find())
                status = matcher.group(3);
            Bean.setStatus(status);
            reg = "<meta property=\"og:novel:category\"([^\"]*)\"([ ]*)([^\"]*)";
            matcher = Pattern.compile(reg).matcher(html);;
            while(matcher.find())
                Bean.setNovelType(matcher.group(3));
            reg="<meta property=\"og:novel:update_time\"([^\"]*)\"([ ]*)([^\"]*)";
            matcher=Pattern.compile(reg).matcher(html);
            while(matcher.find())
                Bean.setUpdate_time(matcher.group(3));
            reg="<meta property=\"og:novel:latest_chapter_name\"([^\"]*)\"([ ]*)([^\"]*)";
            matcher=Pattern.compile(reg).matcher(html);
            while(matcher.find())
                Bean.setLastChapter(matcher.group(3));
            return Bean;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public List<BookChapterBean> getChapterList(BookdetailBean bean,onCallback callback){
        List<BookChapterBean> list = new ArrayList<>();
       try {
           String url = bean.getInfoUrl();
           Document document = BaseApi.getHtml(url);

           Element div = document.getElementById("list");
           Elements dtTag = document.getElementsByTag("dt");
           int index = dtTag.get(1).elementSiblingIndex();
           Elements ddTags = div.getElementsByIndexGreaterThan(index);
           for (int i = 0; i < ddTags.size(); i++) {
               Element ddTag = ddTags.get(i);
               String text = ddTag.getElementsByTag("a").get(0).text();
               int dotIndex = text.indexOf('.');
               if (dotIndex != -1)
                   text = text.substring(dotIndex + 1);
               String html = ddTag.getElementsByTag("a").get(0).attr("href");
               html = url + html;
               BookChapterBean bookChapterBean = new BookChapterBean();
               bookChapterBean.setOriginUrl(url);
               bookChapterBean.setChapterName(text);
               bookChapterBean.setUrl(html);
               bookChapterBean.setSourceClass(bean.getSourceClass());
               bookChapterBean.setChapterNum(i + 1);
               bookChapterBean.setBookName(bean.getBookName());
               list.add(bookChapterBean);


           }
           List<BookChapterDB>  listRes=new ArrayList<>();
           for(BookChapterBean chapterBean:list){
               listRes.add(new BookChapterDB(bean,chapterBean));
           }
           boolean flag= dbUtils.saveAll(listRes);//更新所有章节
           callback.UpdateOrNot(flag);
           return list;
       }catch (Exception e){
           long Id= Objects.hash(bean.getBookName(),bean.getAuthor(),bean.getSourceName());
            List<BookChapterDB>bookChapterDBList= LitePal.where("bookId = ? ",Long.toString(Id)).find(BookChapterDB.class);
            int i=0;
            for(BookChapterDB bookChapterDB:bookChapterDBList){
                BookChapterBean bookChapterBean=new BookChapterBean();
                String s=bookChapterDB.getUrl();
                while(s.charAt(s.length()-1)!='/')
                    s=s.substring(0,s.length()-1);
                s=s.substring(0,s.length()-1);
                bookChapterBean.setOriginUrl(s);
                bookChapterBean.setChapterName(bookChapterDB.getChapterName());
                bookChapterBean.setUrl(bookChapterDB.getUrl());
                bookChapterBean.setSourceClass(bean.getSourceClass());
                bookChapterBean.setChapterNum(i + 1);
                bookChapterBean.setBookName(bean.getBookName());
                i++;
                list.add(bookChapterBean);
            }
           return list;
       }
    }

    @Override
    public ContentChapter getContentFromChapter(BookChapterDB bookChapterDB) {
        String url=bookChapterDB.getUrl();
        try{
            Connection conn=Jsoup.connect(url).timeout(5000);
            Document document=conn.get();
            Element contentElement=document.getElementById("content");
            ContentChapter content=new ContentChapter();
            Elements pTags=contentElement.getElementsByTag("p");
            Elements elementsBookName=document.getElementsByClass("bookname");
            String title=elementsBookName.get(0).getElementsByTag("h1").get(0).text();
            content.setTitle(title);
            String c="  ";
            for(int i=0;i<pTags.size();i++){
                c+=pTags.get(i).text()+"splitRegex";
            }
            content.setContent(c);
            return content;
        }catch (Exception e ){
            e.printStackTrace();

        }
        return null;
    }

    @Override
    public BookdetailBean getChapterAndTime(String url, BookdetailBean Bean) {
        try {
            Connection conn = Jsoup.connect(url).timeout(50000);
            Document document = conn.get();
            Elements elements = document.getElementsByTag("meta");
            String html =elements.toString();
            Matcher matcher;
            String reg="<meta property=\"og:novel:update_time\"([^\"]*)\"([ ]*)([^\"]*)";
             matcher= Pattern.compile(reg).matcher(html);
            matcher=Pattern.compile(reg).matcher(html);
            while(matcher.find())
                Bean.setUpdate_time(matcher.group(3));
            reg="<meta property=\"og:novel:latest_chapter_name\"([^\"]*)\"([ ]*)([^\"]*)";
            matcher=Pattern.compile(reg).matcher(html);
            while(matcher.find())
                Bean.setLastChapter(matcher.group(3));
            return Bean;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
