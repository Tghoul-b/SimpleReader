package com.project.reader.entity;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.core.Observable;

public class BookSrcBean {
    private  String sourceNum;
    private  String sourceName;
    private  String sourceUrl;

    public BookSrcBean() {
    }

    public String getSourceNum() {
        return sourceNum;
    }

    public void setSourceNum(String sourceNum) {
        this.sourceNum = sourceNum;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
    public List<SearchBookBean>  getSearchResult(String url) throws  Exception{
        List<SearchBookBean>  list=new ArrayList<>();
        Connection conn = Jsoup.connect(url).timeout(5000);;
        conn.userAgent("Mozilla/5.0 (Linux; Android 7.1.1; MI 6 Build/NMF26X; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/6.2 TBS/043807 Mobile Safari/537.36 MicroMessenger/6.6.1.1220(0x26060135) NetType/WIFI Language/zh_CN");
        Document doc=conn.get();
        if(sourceName.equals("笔趣阁44")){
           list=deal_with_biquge_44(doc);
        }
        return list;
    }
    private List<SearchBookBean>  deal_with_biquge_44(Document doc) throws  Exception{
        Elements divs = doc.getElementsByTag("table");
        Element div = divs.get(0);
        List<SearchBookBean>  list=new ArrayList<>();
        Elements elementsByTag = div.getElementsByTag("tr");
        for (int i = 1; i < elementsByTag.size(); i++) {
            Element element = elementsByTag.get(i);
            Elements info = element.getElementsByTag("td");
            SearchBookBean searchBookBean=new SearchBookBean();
            searchBookBean.setName(info.get(0).text());
            searchBookBean.setLastChapter(info.get(1).text());
            searchBookBean.setAuthor(info.get(2).text());
            String t_url=info.get(0).getElementsByTag("a").attr("href");
            t_url="https://www.wqge.cc"+t_url;
            Connection conn=Jsoup.connect(t_url).timeout(5000);
            doc=conn.get();
            Elements elements=doc.getElementsByTag("meta");
            String reg="<meta property=\"og:description\"([^\"]*)([^>]*)>";
            String html=doc.html();
            Matcher matcher= Pattern.compile(reg).matcher(html);
            String desc="";
            if(matcher.find())
                desc=matcher.group(2);
            searchBookBean.setDesc(desc);
            reg="<meta property=\"og:image\"([^\"]*)\"([ ]*)([^\"]*)";
            matcher=Pattern.compile(reg).matcher(html);
            String imgUrl="";
            while(matcher.find())
                imgUrl=matcher.group(3);
            searchBookBean.setImgUrl(imgUrl);
            reg="<meta property=\"og:novel:status\"([^\"]*)\"([ ]*)([^\"]*)";
            matcher=Pattern.compile(reg).matcher(html);
            String status="";
            while(matcher.find())
                status=matcher.group(3);
            searchBookBean.setStatus(status);
            list.add(searchBookBean);

        }
        return list;
    }
}
