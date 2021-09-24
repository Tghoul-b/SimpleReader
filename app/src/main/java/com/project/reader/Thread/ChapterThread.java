package com.project.reader.Thread;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.reader.R;
import com.project.reader.db.dbUtils;
import com.project.reader.entity.BookChapterBean;
import com.project.reader.entity.BookChapterDB;
import com.project.reader.entity.BookContentDB;
import com.project.reader.entity.BookdetailBean;
import com.project.reader.ui.Adapter.BookChapterAdapter;
import com.project.reader.ui.Handler.CrawlerHandler;
import com.project.reader.ui.Handler.baseCrawler;
import com.project.reader.ui.util.tools.App;
import com.project.reader.ui.widget.Page.ContentChapter;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ChapterThread {
    private  List<BookChapterBean> listRes;
    private  BookChapterAdapter mAdapter;
    private  BookdetailBean bookdetailBean;
    private  ListView listView;
    private Context context;
    private BookChapterDB chapterDB;
    private baseCrawler crawler;
    private OnThreadFinish onThreadFinish;
    public ChapterThread(Context context, List<BookChapterBean> listRes, BookChapterAdapter mAdapter, BookdetailBean bookdetailBean, ListView listView) {
        this.listRes = listRes;
        this.mAdapter = mAdapter;
        this.bookdetailBean = bookdetailBean;
        this.listView = listView;
        this.context=context;
    }
    public ChapterThread(BookChapterDB bookChapterDB,baseCrawler crawler){
        this.chapterDB=bookChapterDB;
        this.crawler=crawler;
    }

    public void setOnThreadFinish(OnThreadFinish onThreadFinish) {
        this.onThreadFinish = onThreadFinish;
    }

    public  Thread chapterFromNextWork=new Thread(new Runnable() {  //这个是从网络加载
        @Override
        public void run() {
            try {
                baseCrawler crawler = CrawlerHandler.getCrawler(bookdetailBean.getSourceClass());
                listRes = crawler.getChapterList(bookdetailBean,updated -> { });
                mAdapter.addAll(listRes);
                App.runOnUiThread(() -> {
                    listView.setAdapter(mAdapter);
                });
                List<BookChapterDB> list=new ArrayList<>();
                for(BookChapterBean bean:listRes){
                    list.add(new BookChapterDB(bookdetailBean,bean));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    });
    public Thread getChapterContent=new Thread(new Runnable() {
        @Override
        public void run(){
            ContentChapter contentChapter =crawler.getContentFromChapter(chapterDB);
            onThreadFinish.loadChapterContent(contentChapter);
        }
    });
    public interface OnThreadFinish{
        public void loadChapterContent(ContentChapter contentChapter);
    }

}
