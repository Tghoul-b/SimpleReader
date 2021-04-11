package com.project.reader.ui.util.Engine;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.project.reader.entity.BookSrcBean;
import com.project.reader.entity.SearchBookBean;
import com.project.reader.ui.Handler.CrawlerHandler;
import com.project.reader.ui.Handler.baseCrawler;
import com.project.reader.ui.util.tools.BaseApi;
import com.project.reader.ui.util.tools.HAUtils;

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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.dmoral.toasty.Toasty;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
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
    private static  HAUtils haUtils;
    public SearchEngine(Context context){
        SrcConfig=BaseApi.parseJson(context);
        threadsNumber=8;//四个线程
        haUtils=new HAUtils();
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
    public void Search(String Key,String searchRule){
        SearchSiteIndex=-1;
        SearchSuccessNum=0;
        for(int i=0;i<Math.min(SrcConfig.size(),threadsNumber);i++)
              SearchonEngine(Key,searchRule);
    }
    public void stopSearch(){
        if(compositeDisposable!=null)  compositeDisposable.dispose();
        compositeDisposable=new CompositeDisposable();
    }
    private synchronized void  SearchonEngine(String Searchkey,String searchRule){
        SearchSiteIndex++;
        BookSrcBean srcBean=new BookSrcBean();
        if(SearchSiteIndex<SrcConfig.size()) {
            srcBean = SrcConfig.get(SearchSiteIndex);
            baseCrawler crawler= CrawlerHandler.getCrawler(srcBean.getSourceClass());
            BaseApi.SearchObverable(Searchkey, srcBean,crawler,searchRule)
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
                            SearchonEngine(Searchkey,searchRule);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Toasty.error(context,"网络异常,搜索书籍失败",Toast.LENGTH_SHORT).show();
                            SearchonEngine(Searchkey,searchRule);
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        }
        else{
            if(SearchSiteIndex>=SrcConfig.size()){
                if(SearchSuccessNum==0){
                    Toasty.warning(context,"未搜索到相关书籍",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public synchronized  void initOtherinfo(SearchBookBean searchBookBean,GetInfoListener listener){
        String sourceClass=searchBookBean.getSourceClass();
        baseCrawler crawler=CrawlerHandler.getCrawler(sourceClass);
        BaseApi.InitOtherinfo(searchBookBean,crawler)
                .subscribeOn(scheduler)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SearchBookBean>(){
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull SearchBookBean res) {
                        listener.loadFinish(true);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        listener.loadFinish(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    public interface OnSearchListener {

        void loadMoreFinish(Boolean isAll);
        void loadMoreSearchBook(List<SearchBookBean> beans);
        void searchBookError(Throwable throwable);
    }
    public interface GetInfoListener{
        void loadFinish(boolean success);
    }
}
