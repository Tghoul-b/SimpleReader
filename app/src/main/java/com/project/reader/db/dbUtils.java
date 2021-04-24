package com.project.reader.db;

import com.project.reader.entity.BookChapterBean;
import com.project.reader.entity.BookChapterDB;
import com.project.reader.ui.util.tools.App;

import org.greenrobot.greendao.query.QueryBuilder;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class dbUtils {
    public static void saveAll(List<BookChapterDB> list){
        final List<BookChapterDB>  oldList= LitePal.findAll(BookChapterDB.class);
        System.out.println(oldList.size());
        if(oldList.size()==list.size())  return ;
        int i=oldList.size();
        for(;i<list.size();i++){
            list.get(i).save();
        }
    }

}
