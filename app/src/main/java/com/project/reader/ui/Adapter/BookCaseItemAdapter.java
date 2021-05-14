package com.project.reader.ui.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.reader.R;
import com.project.reader.entity.BookCaseDB;
import com.project.reader.entity.BookdetailBean;
import com.project.reader.ui.Activity.ReadActivity;
import com.project.reader.ui.Handler.CrawlerHandler;
import com.project.reader.ui.Handler.baseCrawler;
import com.project.reader.ui.util.tools.App;
import com.project.reader.ui.util.tools.BaseApi;
import com.project.reader.ui.widget.View.CoverImageView;

import java.util.List;

public class BookCaseItemAdapter extends CommonAdapter<BookCaseDB,BookCaseItemAdapter.ViewHolder> {
    public BookCaseItemAdapter(Context context, int layoutId) {
        super(context, layoutId);
    }

    public BookCaseItemAdapter(Context context, List<BookCaseDB> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void onBind(ViewHolder holder, BookCaseDB data) {
        TextView textViewName=holder.getView(R.id.book_case_item_bookname);
        textViewName.setText(data.getBookName());
        CoverImageView imageView=holder.getView(R.id.book_case_item_img);
        imageView.load(data.getImgUrl(),data.getBookName(),"");
        TextView textViewLastChapter=holder.getView(R.id.book_case_item_lastchapter);
        TextView textViewTime=holder.getView(R.id.book_case_item_update_time);
        baseCrawler crawler= CrawlerHandler.getCrawler(data.getSourceClass());
        textViewLastChapter.setText("最新章节:"+data.getLastChapter());
        textViewTime.setText(data.getUpdateTime());
        LinearLayout linearLayout=holder.getView(R.id.book_case_item_linear);
    }

    public static  class  ViewHolder extends CommonAdapter.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }

    }
}
