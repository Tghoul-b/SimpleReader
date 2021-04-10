package com.project.reader.ui.util.Engine;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.project.reader.entity.BookSrcBean;
import com.project.reader.entity.SearchBookBean;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.dmoral.toasty.Toasty;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Supplier;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchEngine {
    private ExecutorService executorService;
    private Scheduler scheduler;
    private CompositeDisposable compositeDisposable;
    private String SrcConfigStr;
    private List<BookSrcBean>  SrcConfig;
    private Context context;
    private Integer threadsNumber;
    private Integer SearchSiteIndex;
    private OnSearchListener searchListener;
    private Integer SearchSuccessNum;//搜索成功的次数
    public SearchEngine(Context context){
        SrcConfigStr=loadConfig(context);
        this.context=context;
        SrcConfig=parseJson(SrcConfigStr);
        threadsNumber=4;//四个线程池
        InitSearchEngine();
    }
    public void setOnSearchListener(OnSearchListener searchListener) {
        this.searchListener = searchListener;
    }
    public void InitSearchEngine(){
        executorService= Executors.newFixedThreadPool(threadsNumber);
        scheduler= Schedulers.from(executorService);
        compositeDisposable=new CompositeDisposable();
    }
    public void Search(String Key){
        SearchSiteIndex=-1;
        SearchSuccessNum=0;
        for(int i=0;i<Math.min(SrcConfig.size(),threadsNumber);i++)
              SearchonEngine(Key);
    }
    public void stopSearch(){
        if(compositeDisposable!=null)  compositeDisposable.dispose();
        compositeDisposable=new CompositeDisposable();
    }
    public String loadConfig(Context context) {
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
    public List<BookSrcBean> parseJson(String str){
        List<BookSrcBean> list=new ArrayList<>();
        list= JSON.parseArray(str,BookSrcBean.class);
        return list;
    }
    private synchronized void  SearchonEngine(String Searchkey){
        SearchSiteIndex++;
        BookSrcBean srcBean=new BookSrcBean();
        if(SearchSiteIndex<SrcConfig.size()) {
            srcBean = SrcConfig.get(SearchSiteIndex);
            SearchObverable(Searchkey,srcBean)
                    .subscribeOn(scheduler)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<SearchBookBean>>(){
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onNext(@NonNull List<SearchBookBean> beans) {

                            if(beans!=null){
                                SearchSuccessNum++;
                                searchListener.loadMoreSearchBook(beans);
                            }
                            SearchonEngine(Searchkey);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }
    public static Observable<List<SearchBookBean>> SearchObverable(String key, final BookSrcBean bean) {
        return Observable.create(emitter -> {
            try {
                String url=bean.getSourceUrl();
                url=url.replace("{key}",key);
                emitter.onNext(bean.getSearchResult(url));
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
            emitter.onComplete();
        });
    }
    public interface OnSearchListener {

        void loadMoreFinish(Boolean isAll);
        void loadMoreSearchBook(List<SearchBookBean> beans);
        void searchBookError(Throwable throwable);
    }
}
