package com.project.reader.ui.util.tools;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.project.reader.entity.BookSrcBean;
import com.project.reader.entity.BookdetailBean;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.rxjava3.core.Observable;
import com.project.reader.ui.Handler.baseCrawler;
public class BaseApi {
    public static Observable<List<BookdetailBean>> SearchObverable(String key,BookSrcBean bean,baseCrawler baseCrawler,String searchRule) {
        return Observable.create(emitter -> {
            try {
                String url=bean.getSourceUrl();
                url=url.replace("{key}",key);
                List<BookdetailBean> c_res=baseCrawler.getSearchResult(url,bean.getSourceClass(),searchRule);
                emitter.onNext(c_res);
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
            emitter.onComplete();
        });
    }
    public static Observable<BookdetailBean>InitOtherinfo(BookdetailBean bookdetailBean, baseCrawler baseCrawler){
        return Observable.create(emitter -> {
            String url=bookdetailBean.getInfoUrl();
            emitter.onNext(baseCrawler.getInfo(url,bookdetailBean));
        });
    }
    public static String loadConfig(Context context) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getAssets().open("ReferencesSource.json"));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            String Result="";
            while((line = bufReader.readLine()) != null)
                Result += line;
            inputReader.close();
            bufReader.close();
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static List<BookSrcBean> parseJson(Context context){
        String str=loadConfig(context);
        List<BookSrcBean> list=new ArrayList<>();
        list= JSON.parseArray(str,BookSrcBean.class);
        return list;
    }
    public static String KeyMove(String s,String author,String Searchkey){
        String []keyWords={"笔趣阁","小说阅读","顶点","小说","最新章节阅读","最新章节读","最新章节","章节","列表","最新章","更新","最新","吧"};
        for(int i=0;i<keyWords.length;i++){
            String key=keyWords[i];
            while(key.indexOf(Searchkey)==-1&&s.indexOf(key)!=-1){  //这个查找的关键字包含这些过滤的词汇，则不过滤
                int t=s.indexOf(key);
                int len=key.length();
                s=s.substring(0,t)+s.substring(t+len);
            }
        }
        if(s.indexOf('(')!=-1){  //去除括号
            int st=s.indexOf('(');
            int end=st;
            for(;end<s.length();end++){
                if(s.charAt(end)==')')  {
                    end++;//过滤掉')'
                    break;
                }
            }
            s=s.substring(0,st)+s.substring(end);
        }
        while(s.indexOf(author)!=-1){
            int t=s.indexOf(author);
            int len=author.length();
            s=s.substring(0,t)+s.substring(t+len);
        }
       return s;
    }

}
