package com.project.reader.ui.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.example.reader.R;
import com.project.reader.entity.BookChapterBean;

import java.util.Collections;

public class BookChapterAdapter extends CommonAdapter<BookChapterBean,BookChapterAdapter.ViewHolder> {

    public BookChapterAdapter(Context context, int layoutId) {
        super(context, layoutId);
    }
    private OnItemClickListener clickListener;

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onBind(ViewHolder holder, BookChapterBean data) {
//        if(mContext instanceof ReadActivity) {
//            LinearLayout linearLayout = holder.getView(R.id.chapter_layout);
//            linearLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    clickListener.onItemClick(data);
//                }
//            });
//        }
        holder.setText(R.id.chapterAdapterName,data.getChapterName());

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

