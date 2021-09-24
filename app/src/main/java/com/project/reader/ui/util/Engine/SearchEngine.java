package com.project.reader.ui.util.Engine;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.project.reader.entity.BookSrcBean;
import com.project.reader.entity.BookdetailBean;
import com.project.reader.entity.SearchBookBean;
import com.project.reader.ui.Handler.CrawlerHandler;
import com.project.reader.ui.Handler.baseCrawler;
import com.project.reader.ui.util.tools.BaseApi;
import com.project.reader.ui.util.tools.HAUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Integer SearchFinishNum;
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
        SearchFinishNum=0;
        for(int i=0;i<Math.min(SrcConfig.size(),threadsNumber);i++)
              SearchonEngine(Key,searchRule);
    }
    public void stopSearch(){
        if(compositeDisposable!=null)  compositeDisposable.dispose();
        compositeDisposable=new CompositeDisposable();
    }

    /**
     *
     * @param Searchkey
     * @param searchRule
     * 根据Searchkey和searchRule来查找对应的书籍的结果，此函数用于接收SearchObverable的结果
     */
    private synchronized void  SearchonEngine(String Searchkey,String searchRule){
        SearchSiteIndex++;
        if(SearchSiteIndex<SrcConfig.size()) {
            BookSrcBean srcBean = SrcConfig.get(SearchSiteIndex);
            if(!srcBean.isEnable())//如果这个源不可用
            {
                SearchFinishNum++;
                return ;
            }
            baseCrawler crawler= CrawlerHandler.getCrawler(srcBean.getSourceClass());
            BaseApi.SearchObverable(Searchkey, srcBean,crawler,searchRule)
                    .subscribeOn(scheduler)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<BookdetailBean>>(){
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onNext(@NonNull List<BookdetailBean> beans) {
                            SearchFinishNum++;
                            if(beans!=null&&beans.size()!=0){
                                SearchSuccessNum++;
                                Map<SearchBookBean,BookdetailBean>  map=new HashMap<>();
                                for(BookdetailBean bean:beans) {
                                    SearchBookBean searchBookBean=new SearchBookBean(bean.getBookName()
                                    ,bean.getAuthor(),srcBean.getSourceClass(),searchRule);
                                    bean.setSourceName(srcBean.getSourceName());
                                    bean.setSourceClass(srcBean.getSourceClass());
                                    map.put(searchBookBean,bean);
                                }
                                searchListener.loadMoreSearchBook(map);
                            }
                            SearchonEngine(Searchkey,searchRule);
                        }
                        @Override
                        public void onError(@NonNull Throwable e) {
                            Toasty.error(context,"网络异常,搜索书籍失败",Toast.LENGTH_SHORT).show();
                            SearchonEngine(Searchkey,searchRule);
                            SearchFinishNum++;
                        }

                        @Override
                        public void onComplete() {
                            SearchFinishNum++;
                        }
                    });
        }
        else{
            if(SearchFinishNum>=SrcConfig.size()){
                if(SearchSuccessNum==0){
                    searchListener.loadMoreFinish(-1);//这个代表加载完毕,却没有一个成功结果
                }
                else{
                    searchListener.loadMoreFinish(1);//这个代表加载完毕
                }
            }
        }
    }
    public synchronized  void initOtherinfo(String sourceClass,BookdetailBean searchBookBean, GetInfoListener listener){
        baseCrawler crawler=CrawlerHandler.getCrawler(sourceClass);
        BaseApi.InitOtherinfo(searchBookBean,crawler)
                .subscribeOn(scheduler)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BookdetailBean>(){
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BookdetailBean res) {
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

        void loadMoreFinish(Integer isAll);
        void loadMoreSearchBook(Map<SearchBookBean,BookdetailBean> map);//在一个源里，同样的书，同样的作者应该只有唯一一本书
        void searchBookError(Throwable throwable);
    }
    public interface GetInfoListener{
        void loadFinish(boolean success);
    }
}
