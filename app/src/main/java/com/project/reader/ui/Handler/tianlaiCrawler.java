package com.project.reader.ui.Handler;

import com.project.reader.entity.SearchBookBean;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class tianlaiCrawler implements baseCrawler{
    @Override
    public List<SearchBookBean> getSearchResult(String url, String sourceClass, String searchRule) {
        try{
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

    @Override
    public List<SearchBookBean> deal_with_doc(Document document, String key, String sourceClass, String searchRule) {
        Elements item_details=document.getElementsByClass("result-game-item-detail");
        List<SearchBookBean>  ans=new ArrayList<>();
        for(int i=0;i<item_details.size();i++){
            SearchBookBean searchBookBean=new SearchBookBean();
            Element item_detail=item_details.get(i);
            String name=item_detail.getElementsByClass("result-item-title result-game-item-title")
                    .get(0).getElementsByTag("span").get(0).text();
            String url=item_detail.getElementsByTag("a").get(0).attr("href");

            url="https://www.233txt.com"+url;
            searchBookBean.setInfoUrl(url);
            searchBookBean.setName(name);
            String author=item_detail.getElementsByClass("result-game-item-info-tag").get(0)
                    .getElementsByTag("span").get(1).text();
            searchBookBean.setAuthor(author);
            searchBookBean.setSearchRule(searchRule);
            searchBookBean.setSourceClass(sourceClass);
            if(searchRule!=null&&searchRule.equals("bookname")){
                if(name.indexOf(key)==-1)  continue;
            }
            if(searchRule!=null&&searchRule.equals("author")){
                if(searchBookBean.getAuthor().indexOf(key)==-1)
                    continue;
            }
            String last_chapter=item_detail.getElementsByClass("result-game-item-info").get(0)
                    .getElementsByClass("result-game-item-info").get(0)
                    .getElementsByClass("result-game-item-info-tag-item")
                    .get(0).text();
            searchBookBean.setLastChapter(last_chapter);
            ans.add(searchBookBean);
        }
        return ans;
    }


    @Override
    public SearchBookBean getInfo(String url, SearchBookBean searchBookBean) {
        try {
            Connection conn = Jsoup.connect(url).timeout(5000);
            Document document = conn.get();
            Elements elements=document.getElementsByTag("meta");
            String reg = "<meta property=\"og:description\"([^\"]*)([^>]*)>";
            String html = elements.toString();
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
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
