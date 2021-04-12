package com.project.reader.ui.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;
import com.example.reader.R;
import com.project.reader.entity.SearchBookBean;
import com.project.reader.ui.util.Engine.SearchEngine;
import com.project.reader.ui.util.tools.App;
import com.project.reader.ui.widget.CoverImageView;;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchResAdapter extends CommonListAdapter<SearchBookBean>{
    private Context context;
    private String SearchKey;
    private List<String> tagList;
    private TagFlowLayout tagFlowLayout;
    private SearchEngine searchEngine;
    public SearchResAdapter(int ViewId, Context context, String searchKey, SearchEngine searchEngine){
        super(ViewId);
        this.context=context;
        this.SearchKey=searchKey;
        this.searchEngine=searchEngine;
        tagList=new ArrayList<>();
    }
    @Override
    public void addAll(List<SearchBookBean> data, String Research) {
        List<SearchBookBean> copyDataS = mData;
        int len=mData.size();
        for(SearchBookBean tmp:data){
            boolean hasSame=false;
            for(SearchBookBean copy:copyDataS){
                if(copy.getName().indexOf(tmp.getName())!=-1&&
                        TextUtils.equals(copy.getAuthor(),tmp.getAuthor()))
                {
                    hasSame=true;
                    break;
                }
            }
            if(!hasSame)
                copyDataS.add(tmp);
        }
            synchronized (this) {
                App.runOnUiThread(() -> {
                    mData = copyDataS;
                    notifyItemRangeChanged(len,mData.size()-1);
                    //千万不能使用NotifyItemDataChanged()
                });
            }
    }

    @Override
    public void bindView(ViewHolder holder, SearchBookBean obj,int position) {
        TextView tvbookname=holder.getView(R.id.tv_book_name);
        tagFlowLayout=holder.getView(R.id.tv_book_tag);
        String rule=obj.getSearchRule();
        String name=obj.getName();
        holder.setText(R.id.tv_book_name,name);
        holder.setText(R.id.tv_book_author,obj.getAuthor());
        if(rule.equals("bookname")){
            holder.setText(R.id.tv_book_name,getSpanString(name));
        }
        else{
            holder.setText(R.id.tv_book_author,getSpanString(obj.getAuthor()));
        }
        holder.setText(R.id.tv_book_newest_chapter,"最新章节: "+obj.getLastChapter());
        String sourceClass=obj.getSourceClass();
        App.getHandler().postDelayed(()->{
            if(obj.needOtherInfo()){
                TextView tvBookName=holder.getView(R.id.tv_book_name);
                if (tvBookName.getTag() == null || !(Boolean) tvBookName.getTag()) {
                    tvBookName.setTag(true);
                } else {
                    delayInit(holder,obj,position);
                    return;
                }
                searchEngine.initOtherinfo(obj,success->{
                    if(success){
                        delayInit(holder,obj,position);
                    }
                    else{
                        tvBookName.setTag(false);
                    }
                });

            }
        },1000);
    }
    private void delayInit(ViewHolder holder, SearchBookBean obj,int position){
        CoverImageView imageView=holder.getView(R.id.tv_book_img);
        String url=obj.getImgUrl();
        ReInitTag(obj);
        holder.setText(R.id.tv_book_desc,"简介:"+obj.getDesc());
        try {
            if(!App.isDestroy((Activity)context))//判断Actiivity不能消失
            imageView.load(url,obj.getName(),obj.getAuthor());

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void ReInitTag(SearchBookBean obj){
        String status=obj.getStatus();
        status="status0:"+status;
        tagList.clear();
        tagList.add(status);
        tagFlowLayout.setAdapter(new BookTagAdapter(tagList,context,11));
    }
    private SpannableString getSpanString(String name){
        if(name==null)
            return new SpannableString("null");
        SpannableString spannableString = new SpannableString(name);
        int start=name.indexOf(SearchKey);
        if(start==-1){
            return spannableString;
        }
        spannableString.setSpan(new ForegroundColorSpan(Color.RED),
                start, start + SearchKey.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
