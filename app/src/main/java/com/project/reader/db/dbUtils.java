package com.project.reader.db;

import com.project.reader.entity.BookChapterBean;
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

}
