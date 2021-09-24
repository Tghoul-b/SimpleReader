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
    public static boolean saveAll(List<BookChapterDB> list){
        final List<BookChapterDB>  oldList= LitePal.where("bookId = ? ",Long.toString(list.get(0).getBookId())).find(BookChapterDB.class);
        if(oldList.size()==list.size())  return false;
        int i=oldList.size();
        for(;i<list.size();i++){
            list.get(i).save();
        }
        return true;
    }
    public static void deleteAll(){
        LitePal.deleteAll(BookChapterDB.class);
    }

}
