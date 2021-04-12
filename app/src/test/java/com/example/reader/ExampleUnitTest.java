package com.example.reader;

import com.project.reader.entity.SearchBookBean;
import com.project.reader.ui.Handler.tianlaiCrawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void test_Rex() throws  Exception{
        String t_url="https://www.wqge.cc"+"/8_8187/";
        Connection conn= Jsoup.connect(t_url).timeout(5000);
        Document doc=conn.get();
        String reg="<meta property=\"og:description\"([^\"]*)([^>]*)>";
        String html=doc.html();
        Matcher matcher= Pattern.compile(reg).matcher(html);
        while(matcher.find()){
            System.out.println(matcher.group(2));
        }
        reg="<meta property=\"og:image\"([^\"]*)\"([ ]*)([^\"]*)";
        matcher=Pattern.compile(reg).matcher(html);
        while(matcher.find())
            System.out.println(matcher.group(3));
        reg="<meta property=\"og:novel:status\"([^\"]*)\"([ ]*)([^\"]*)";
        matcher=Pattern.compile(reg).matcher(html);
        while(matcher.find())
            System.out.println(matcher.group(3));
    }
    @Test
    public void TestQiQi(){
        String url="https://www.qq717.com/search.php?keyword=%E4%BD%A0%E5%A5%BD";
        Connection connection=Jsoup.connect(url).timeout(5000);
    }
    @Test
    public void TestTianLai(){
        tianlaiCrawler  crawler=new tianlaiCrawler();
        String url="https://www.233txt.com/files/article/html/69/69664/";
        System.out.println(crawler.getInfo(url,new SearchBookBean()));
    }
}