package com.project.reader.ui.util.network;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.RequiresApi;

import com.project.reader.ui.util.cache.ACache;

import org.jsoup.Jsoup;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
   public  void initSuggestionBook(String url,Context context){
       disposables.add(sampleObservable(url)
               // Run on a background thread
               .subscribeOn(Schedulers.io())
               // Be notified on the main thread
               .observeOn(AndroidSchedulers.mainThread())
               .subscribeWith(new DisposableObserver<List<String>>() {
                   @Override public void onComplete() {

                   }

                   @Override public void onError(Throwable e) {
                   }

                   @Override public void onNext(List<String> list) {
                       ACache aCache=ACache.get(context);
                       String res=TextUtils.join(",",list);
                       aCache.put("SuggestionList",res);
                   }
               }));
   }
    public Observable<List<String>> sampleObservable(String url) {
        return Observable.defer(new Supplier<ObservableSource<? extends List<String>>>() {
            @Override
            public ObservableSource<? extends List<String>> get() throws Throwable {
                try {
                    List<String> list = new ArrayList<>();
                    Connection conn = Jsoup.connect(url).timeout(50000);;
                    conn.userAgent("Mozilla/5.0 (Linux; Android 7.1.1; MI 6 Build/NMF26X; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/6.2 TBS/043807 Mobile Safari/537.36 MicroMessenger/6.6.1.1220(0x26060135) NetType/WIFI Language/zh_CN");
                    Document doc=conn.get();
                    String html=doc.html();
                    String reg="<td><a class=\"name\"[^>]*>([^<]*)";
                    Matcher matcher = Pattern.compile(reg).matcher(html);
                    while(matcher.find()){
                        list.add(matcher.group(1));
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
    public interface loadMoreBook{
       public void LoadBooks(List<String> list);
    }
}
