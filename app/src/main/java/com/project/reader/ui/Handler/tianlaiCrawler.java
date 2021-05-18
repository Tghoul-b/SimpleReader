package com.project.reader.ui.Handler;

import android.content.Context;

import com.project.reader.entity.BookdetailBean;
import com.project.reader.entity.SearchBookBean;
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

public class tianlaiCrawler extends baseCrawler{
    @Override
    public List<BookdetailBean> getSearchResult(String url, String sourceClass, String searchRule) {
        return super.getSearchResult(url, sourceClass, searchRule);
    }

    @Override
    public List<BookdetailBean> deal_with_doc(Document document, String key, String sourceClass, String searchRule) {
        Elements item_details=document.getElementsByClass("result-game-item-detail");
        List<BookdetailBean>  ans=new ArrayList<>();
        for(int i=0;i<item_details.size();i++){
            BookdetailBean bookdetailBean=new BookdetailBean();
            Element item_detail=item_details.get(i);
            String name=item_detail.getElementsByClass("result-item-title result-game-item-title")
                    .get(0).getElementsByTag("span").get(0).text();
            String url=item_detail.getElementsByTag("a").get(0).attr("href");
            url="https://www.233txt.com"+url;
            bookdetailBean.setInfoUrl(url);

            String author=item_detail.getElementsByClass("result-game-item-info-tag").get(0)
                    .getElementsByTag("span").get(1).text();
            bookdetailBean.setAuthor(author);
            name= BaseApi.KeyMove(name,author,key);
            if(searchRule!=null&&searchRule.equals("bookname")){
                if(name==null||name.length()==0||name.indexOf(key)==-1)  continue;
            }
            if(searchRule!=null&&searchRule.equals("author")){
                if(bookdetailBean.getAuthor().indexOf(key)==-1)
                    continue;
            }
            bookdetailBean.setBookName(name);
            String last_chapter=item_detail.getElementsByClass("result-game-item-info").get(0)
                    .getElementsByClass("result-game-item-info").get(0)
                    .getElementsByClass("result-game-item-info-tag-item")
                    .get(0).text();
            bookdetailBean.setLastChapter(last_chapter);
            boolean flag=false;
            for(BookdetailBean bean:ans){
                if(bean.hashCode()==bookdetailBean.hashCode()){
                    flag=true;
                    break;
                }
            }
            if(!flag)
                ans.add(bookdetailBean);
        }
        return ans;
    }


    @Override
    public BookdetailBean getInfo(String url, BookdetailBean bookdetailBean) {
        try {
            Connection conn = Jsoup.connect(url).timeout(50000);
            Document document = conn.get();
            Elements elements=document.getElementsByTag("meta");
            String reg = "<meta property=\"og:description\"([^\"]*)([^>]*)>";
            String html = elements.toString();
            Matcher matcher = Pattern.compile(reg).matcher(html);
            String desc = "";
            if (matcher.find())
                desc = matcher.group(2);
            bookdetailBean.setDesc(desc);
            reg = "<meta property=\"og:image\"([^\"]*)\"([ ]*)([^\"]*)";
            matcher = Pattern.compile(reg).matcher(html);
            String imgUrl = "";
            while (matcher.find())
                imgUrl = matcher.group(3);
            bookdetailBean.setImgUrl(imgUrl);
            reg = "<meta property=\"og:novel:status\"([^\"]*)\"([ ]*)([^\"]*)";
            matcher = Pattern.compile(reg).matcher(html);
            String status = "";
            while (matcher.find())
                status = matcher.group(3);
            bookdetailBean.setStatus(status);
            reg = "<meta property=\"og:novel:category\"([^\"]*)\"([ ]*)([^\"]*)";
            matcher = Pattern.compile(reg).matcher(html);;
            while(matcher.find())
                bookdetailBean.setNovelType(matcher.group(3));
            reg="<meta property=\"og:novel:update_time\"([^\"]*)\"([ ]*)([^\"]*)";
            matcher=Pattern.compile(reg).matcher(html);
            while(matcher.find())
                bookdetailBean.setUpdate_time(matcher.group(3));
            return bookdetailBean;
        }catch (Exception e){
            e.printStackTrace();
            return bookdetailBean;
        }

    }
}
