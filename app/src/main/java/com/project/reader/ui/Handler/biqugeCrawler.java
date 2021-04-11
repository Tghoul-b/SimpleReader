package com.project.reader.ui.Handler;

import android.util.Log;

import com.project.reader.entity.SearchBookBean;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class biqugeCrawler implements baseCrawler{
    @Override
    public List<SearchBookBean> getSearchResult(String url,String sourceClass,String searchRule) {
        try {
            List<SearchBookBean>  list=new ArrayList<>();
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
        return null;
    }
    public List<SearchBookBean> deal_with_doc(Document document,String key,String sourceClass,String searchRule)  {
        Elements divs = document.getElementsByTag("table");
        Element div = divs.get(0);
        List<SearchBookBean>  list=new ArrayList<>();
        Elements elementsByTag = div.getElementsByTag("tr");
        try {
            for (int i = 1; i < elementsByTag.size(); i++) {
                Element element = elementsByTag.get(i);
                Elements info = element.getElementsByTag("td");
                SearchBookBean searchBookBean = new SearchBookBean();
                String name = info.get(0).text();
                if(searchRule.equals("bookname")){
                    if(name.indexOf(key)==-1)  continue;
                }

                searchBookBean.setName(name);
                searchBookBean.setLastChapter(info.get(1).text());
                searchBookBean.setAuthor(info.get(2).text());
                if(searchRule.equals("author")){
                    if(searchBookBean.getAuthor().indexOf(key)==-1)
                         continue;
                }
                searchBookBean.setSourceClass(sourceClass);
                String t_url = info.get(0).getElementsByTag("a").attr("href");
                t_url = "https://www.wqge.cc" + t_url;
                searchBookBean.setInfoUrl(t_url);
                searchBookBean.setSearchRule(searchRule);
                list.add(searchBookBean);
            }
            return list;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public SearchBookBean getInfo(String url, SearchBookBean searchBookBean) {
        try {
            Connection conn = Jsoup.connect(url).timeout(5000);
            Document document = conn.get();
            Elements elements = document.getElementsByTag("meta");
            String reg = "<meta property=\"og:description\"([^\"]*)([^>]*)>";
            String html = document.html();
            Matcher matcher = Pattern.compile(reg).matcher(html);
            String desc = "";
            if (matcher.find())
                desc = matcher.group(2);
            searchBookBean.setDesc(desc);
            reg = "<meta property=\"og:image\"([^\"]*)\"([ ]*)([^\"]*)";
            matcher = Pattern.compile(reg).matcher(html);
            String imgUrl = "";
            while (matcher.find())
                imgUrl = matcher.group(3);
            searchBookBean.setImgUrl(imgUrl);
            reg = "<meta property=\"og:novel:status\"([^\"]*)\"([ ]*)([^\"]*)";
            matcher = Pattern.compile(reg).matcher(html);
            String status = "";
            while (matcher.find())
                status = matcher.group(3);
            searchBookBean.setStatus(status);
            return searchBookBean;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

}
