package com.example.reader;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import com.project.reader.entity.BookChapterBean;
import com.project.reader.entity.BookChapterDB;
import com.project.reader.entity.BookdetailBean;
import com.project.reader.entity.DaoMaster;
import com.project.reader.entity.DaoSession;
import com.project.reader.entity.ProblemBean;
import com.project.reader.entity.SearchBookBean;
import com.project.reader.ui.Handler.biqugeCrawler;
import com.project.reader.ui.Handler.bixiawenxueCrawler;
import com.project.reader.ui.Handler.tianlaiCrawler;
import com.project.reader.ui.util.network.Scrapy;
import com.project.reader.ui.util.tools.App;
import com.project.reader.ui.util.tools.BaseApi;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;


import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.Gson;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest  {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    public static String toHexEncoding(int color) {
        String R, G, B;
        StringBuffer sb = new StringBuffer();
        R = Integer.toHexString(Color.red(color));
        G = Integer.toHexString(Color.green(color));
        B = Integer.toHexString(Color.blue(color));
        //判断获取到的R,G,B值的长度 如果长度等于1 给R,G,B值的前边添0
        R = R.length() == 1 ? "0" + R : R;
        G = G.length() == 1 ? "0" + G : G;
        B = B.length() == 1 ? "0" + B : B;
        sb.append("0x");
        sb.append(R);
        sb.append(G);
        sb.append(B);
        return sb.toString();
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
    }
    @Test
    public void TestBiXia(){
        bixiawenxueCrawler crawler=new bixiawenxueCrawler();
        String url="https://www.bixia.org/ar.php?keyWord=你好";
        System.out.println(crawler.getSearchResult(url,"bookname","bookname"));
    }
    @Test
    public void TestBiquGe(){
        String url="https://www.wqge.cc/50_50245/";
        biqugeCrawler  crawler=new biqugeCrawler();

        System.out.println(crawler.getInfo(url,new BookdetailBean()));

    }
    @Test
    public void Guolv(){
        String s="辰东圣墟最新章节阅读";
        s= BaseApi.KeyMove(s,"辰东","最新");
        System.out.println(s);
    }
    @Test
    public void GetChapterBiqu(){
        BookdetailBean bean=new BookdetailBean();
        bean.setInfoUrl("https://www.wqge.cc/8_8187/");
        biqugeCrawler crawler=new biqugeCrawler();
        //List<BookChapterBean> list=crawler.getChapterList(bean);
    }
    @Test
    public  void TestCreateTable(){
        BookChapterDB bookChapterDB=new BookChapterDB();
        bookChapterDB.setUrl("https://www.wqge.cc/8_8187/178024995.html");
        biqugeCrawler crawler=new biqugeCrawler();
        String c=crawler.getContentFromChapter(bookChapterDB).getContent();
        System.out.println(c);
    }
    @Test
    public void ParseColor(){
        int color=-3226980;
        System.out.println(toHexEncoding(color));
    }

    @Test
    public void ParseLeetcode() {
        String[] records = new String [20000];
        String s = "https://leetcode.com/api/problems/all/";
        try{
            FileOutputStream fos = new FileOutputStream("link.txt");
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            Document document = BaseApi.getHtml(s);
            String content = document.getElementsByTag("body").toString();
            int bIndex = content.indexOf("{");
            int eIndex = content.indexOf("</body>");
            content = content.substring(bIndex, eIndex);
            Gson gson = new Gson();
            int maxv = 0;
            ProblemBean instance = gson.fromJson(content, ProblemBean.class);
            for(ProblemBean.StatStatusPairsBean statStatusPairsBean: instance.getStat_status_pairs()){
                int id = statStatusPairsBean.getStat().getFrontend_question_id();
                String qs = statStatusPairsBean.getStat().getQuestion__title_slug();
                maxv = Math.max(maxv, id);
                records[id] = qs;
            }
            for(int i = 1;i<=2000;i++){
                String t;
                if(records[i] == null)
                    t = "can not access";
                else
                    t = "https://leetcode-cn.com/problems/" + records[i];
                t += "\n";
                bos.write(t.getBytes());
            }
            bos.close();
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
}