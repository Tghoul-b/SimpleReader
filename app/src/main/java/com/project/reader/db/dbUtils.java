package com.project.reader.db;

import com.project.reader.entity.BookChapterBean;
import com.project.reader.entity.BookChapterBeanDao;
import com.project.reader.ui.util.tools.App;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class dbUtils {
    public static void insertAllBookChapter(List<BookChapterBean> beans){
        for(BookChapterBean bean:beans ){
            App.getDaoSession().insertOrReplace(bean);
        }
    }
    public  static List<BookChapterBean> queryAll(){
        return App.getDaoSession().loadAll(BookChapterBean.class);

    }
    public  static void deleteAll(){
        App.getDaoSession().deleteAll(BookChapterBean.class);
    }
    public static List<BookChapterBean>  queruAllChapters(String bookname,String author,String sourceName,int limit){
        List<BookChapterBean>  list=new ArrayList<>();
        BookChapterBeanDao bookChapterBeanDao=App.getDaoSession().getBookChapterBeanDao();
        QueryBuilder qb =bookChapterBeanDao.queryBuilder();
        qb.where(BookChapterBeanDao.Properties.BookName.eq(bookname),
                BookChapterBeanDao.Properties.Author.eq(author),
                BookChapterBeanDao.Properties.SourceName.eq(sourceName)).orderAsc(
                        BookChapterBeanDao.Properties.ChapterNum).limit(limit);
        list=qb.list();
        return list;
    }

}
