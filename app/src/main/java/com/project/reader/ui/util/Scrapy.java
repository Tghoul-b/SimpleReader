package com.project.reader.ui.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.project.reader.ui.util.cache.ACache;

import org.jsoup.Jsoup;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Supplier;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class  Scrapy {
    private final CompositeDisposable disposables = new CompositeDisposable();
    String TAG="testRxjava";
   public  void initSuggestionBook(String url, Context context){
       disposables.add(sampleObservable(url)
               // Run on a background thread
               .subscribeOn(Schedulers.io())
               // Be notified on the main thread
               .observeOn(AndroidSchedulers.mainThread())
               .subscribeWith(new DisposableObserver<List<String>>() {
                   @Override public void onComplete() {

                   }

                   @Override public void onError(Throwable e) {
                       Toasty.error(context,"网络异常,部分功能可能无法实现",Toast.LENGTH_SHORT).show();
                   }

                   @Override public void onNext(List<String> list) {
                       ACache aCache=ACache.get(context);
                       String result = TextUtils.join(", ", list);
                       aCache.put("SuggestionList",result);
                   }
               }));
   }
    public Observable<List<String>> sampleObservable(String url) {
        return Observable.defer(new Supplier<ObservableSource<? extends List<String>>>() {
            @Override
            public ObservableSource<? extends List<String>> get() throws Throwable {
                try {
                    List<String> list = new ArrayList<>();
                    Connection conn = Jsoup.connect(url).timeout(5000);;
                    conn.userAgent("Mozilla/5.0 (Linux; Android 7.1.1; MI 6 Build/NMF26X; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/6.2 TBS/043807 Mobile Safari/537.36 MicroMessenger/6.6.1.1220(0x26060135) NetType/WIFI Language/zh_CN");
                    Document doc=conn.get();
                    Elements elements=doc.getElementsByClass("headerhui");
                    for(Element element:elements){
                        Element firstEle=element.getElementsByClass("n").get(0);
                        Element titleA=firstEle.getElementsByTag("a").get(1);
                        String title=titleA.attr("title");
                        list.add(title);
                    }
                    return Observable.just(list);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        });
    }
}
