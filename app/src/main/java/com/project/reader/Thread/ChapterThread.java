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
import com.project.reader.entity.BookdetailBean;
import com.project.reader.ui.Adapter.BookChapterAdapter;
import com.project.reader.ui.Handler.CrawlerHandler;
import com.project.reader.ui.Handler.baseCrawler;
import com.project.reader.ui.util.tools.App;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class ChapterThread {
    private  List<BookChapterBean> listRes;
    private  BookChapterAdapter mAdapter;
    private  BookdetailBean bookdetailBean;
    private  ListView listView;
    private Context context;
    public ChapterThread(Context context, List<BookChapterBean> listRes, BookChapterAdapter mAdapter, BookdetailBean bookdetailBean, ListView listView) {
        this.listRes = listRes;
        this.mAdapter = mAdapter;
        this.bookdetailBean = bookdetailBean;
        this.listView = listView;
        this.context=context;
    }

    public  Thread chapterFromNextWork=new Thread(new Runnable() {  //这个是从网络加载
        @Override
        public void run() {
            try {
                baseCrawler crawler = CrawlerHandler.getCrawler(bookdetailBean.getSourceClass());
                listRes = crawler.getChapterList(bookdetailBean);
                mAdapter.addAll(listRes);
                int cnt = 0;
                App.runOnUiThread(() -> {
                    listView.setAdapter(mAdapter);
                });
            }catch (Exception e){
                Looper.prepare();
                Toasty.error(context, "网络异常,无法获取章节列表", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    });
}
