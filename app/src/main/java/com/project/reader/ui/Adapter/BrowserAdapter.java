package com.project.reader.ui.Adapter;

import android.content.Context;
import android.widget.TextView;

import com.example.reader.R;
import com.project.reader.entity.BrowserBookBean;
import com.project.reader.ui.widget.View.CoverImageView;

import java.util.List;

public class BrowserAdapter extends CommonAdapter<BrowserBookBean,CommonAdapter.ViewHolder> {
    public BrowserAdapter(Context context, int layoutId) {
        super(context, layoutId);
    }

    public BrowserAdapter(Context context, List<BrowserBookBean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void onBind(ViewHolder holder, BrowserBookBean data) {
        TextView tv_bookName=holder.getView(R.id.browser_book_name);
        tv_bookName.setText(data.getBookName());
        TextView tv_author=holder.getView(R.id.browser_book_author);
        tv_author.setText(data.getBookType()+" | "+data.getAuthor());
        TextView tv_desc=holder.getView(R.id.browser_book_desc);
        tv_desc.setText(data.getDesc());
        CoverImageView imageView=holder.getView(R.id.brownser_item_img);
        imageView.load(data.getImgUrl(),data.getBookName(),data.getAuthor());
    }
}
