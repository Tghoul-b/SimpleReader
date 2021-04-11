package com.project.reader.ui.util.tools;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.project.reader.entity.BookSrcBean;
import com.project.reader.entity.SearchBookBean;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;

import com.project.reader.ui.Handler.baseCrawler;
import com.project.reader.ui.util.tools.HAUtils;
public class BaseApi {
    public static Observable<List<SearchBookBean>> SearchObverable(String key,BookSrcBean bean,baseCrawler baseCrawler,String searchRule) {
        return Observable.create(emitter -> {
            try {
                String url=bean.getSourceUrl();
                url=url.replace("{key}",key);
                List<SearchBookBean> c_res=baseCrawler.getSearchResult(url,bean.getSourceClass(),searchRule);
                emitter.onNext(c_res);
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
            emitter.onComplete();
        });
    }
    public static Observable<SearchBookBean>InitOtherinfo(SearchBookBean searchBookBean,baseCrawler baseCrawler){
        return Observable.create(emitter -> {
            String url=searchBookBean.getInfoUrl();
            emitter.onNext(baseCrawler.getInfo(url,searchBookBean));
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

}
