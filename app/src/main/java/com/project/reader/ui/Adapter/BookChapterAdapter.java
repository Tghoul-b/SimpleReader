package com.project.reader.ui.Adapter;

import android.content.Context;
import android.view.View;

import com.example.reader.R;
import com.project.reader.entity.BookChapterBean;

import java.util.Collections;

public class BookChapterAdapter extends CommonAdapter<BookChapterBean,BookChapterAdapter.ViewHolder> {

    public BookChapterAdapter(Context context, int layoutId) {
        super(context, layoutId);
    }

    @Override
    public void onBind(ViewHolder holder, BookChapterBean data) {
        holder.setText(R.id.chapterAdapterName,data.getChapterName());
        holder.setText(R.id.chapterAdapterStatus,"未缓存");
    }
    public void reverseAll(){
        Collections.reverse(mDatas);
        notifyDataSetChanged();
    }
    public static  class  ViewHolder extends CommonAdapter.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }

    }
}

