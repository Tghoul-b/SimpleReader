package com.project.reader.ui.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.reader.R;
import com.project.reader.entity.BookdetailBean;
import com.project.reader.entity.SearchBookBean;
import com.project.reader.ui.util.Engine.SearchEngine;
import com.project.reader.ui.util.tools.App;
import com.project.reader.ui.util.tools.BaseApi;
import com.project.reader.ui.widget.View.CoverImageView;;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResAdapter extends CommonRecycleAdapter<SearchBookBean>{
    private Context context;
    private String SearchKey;
    private List<String> tagList;
    private SearchEngine searchEngine;
    public SearchResAdapter(int ViewId, Context context, String searchKey, SearchEngine searchEngine){
        super(ViewId);
        this.context=context;
        this.SearchKey=searchKey;
        this.searchEngine=searchEngine;
        tagList=new ArrayList<>();
        Search2BookMap =new HashMap<>();
    }
    @Override
    public void addAll(Map<SearchBookBean,BookdetailBean>map, String Research) {
        List<SearchBookBean> copyDataS = mData;
        int originLen = copyDataS.size();//记录原本的记录哪些需要新加入书源
        int len = mData.size();
        List<Integer> list=new ArrayList<>();
        for(SearchBookBean searchBookBean:map.keySet()){
            boolean hasSame=false;
            for(int i=0;i<len;i++){
                SearchBookBean cp1=mData.get(i);
                //有相同作品名和作者的书的来源
                if(cp1.equals(searchBookBean)) {
                    hasSame=true;
                    boolean sameSrc=false;
                    for(BookdetailBean bookdetailBean:Search2BookMap.get(cp1)){
                        String src1=bookdetailBean.getSourceName();
                        String src2=map.get(searchBookBean).getSourceName();
                        //如果两者的书源来历相同
                        if(src2.equals(src1)) {
                            sameSrc=true;
                            break;
                        }
                    }
                    if(!sameSrc){
                        Search2BookMap.get(cp1).add(map.get(searchBookBean));
                        list.add(i);
                        final  int pos=i;
                        App.runOnUiThread(()->{
                            notifyItemChanged(pos);
                        });
                    }
                }
            }
            if(!hasSame){
                Search2BookMap.put(searchBookBean,new ArrayList<>());
                Search2BookMap.get(searchBookBean).add(map.get(searchBookBean));
                copyDataS.add(searchBookBean);
            }
        }
        synchronized (this) {
            App.runOnUiThread(() -> {
                mData = copyDataS;
                notifyItemRangeChanged(0,mData.size());
            });
        }
    }

    @Override
    public void bindView(ViewHolder holder,SearchBookBean bean, int position) {
        List<BookdetailBean> list=Search2BookMap.get(bean);
        BookdetailBean obj=list.get(list.size()-1);
        String rule=bean.getSearhRule();
        String name=bean.getName();
        if(App.ViewEmptyContent(holder.getView(R.id.tv_book_name))) {
            holder.setText(R.id.tv_book_name, name);
            if(rule.equals("bookname")){
                holder.setText(R.id.tv_book_name,getSpanString(name));
            }
            else{
                holder.setText(R.id.tv_book_author,getSpanString(obj.getAuthor()));
            }
        }
        if(App.ViewEmptyContent(holder.getView(R.id.tv_book_author)))
            holder.setText(R.id.tv_book_author,obj.getAuthor());
        if(App.ViewEmptyContent(holder.getView(R.id.tv_book_newest_chapter))) {
            TextView textView=holder.getView(R.id.tv_book_newest_chapter);
            holder.setText(R.id.tv_book_newest_chapter, "最新章节: " + list.get(0).getLastChapter());
        }
        String sourceClass=bean.getSourceClass();
        String s=String.format("书源:%s 共%d个源",obj.getSourceName(),list.size());
        holder.setText(R.id.tv_book_source,s);
        App.getHandler().postDelayed(()->{
            if(obj.NeedInfo()){
                searchEngine.initOtherinfo(bean.getSourceClass(),obj,success->{
                    if(success){
                        delayInit(holder,obj,position);
                    }
                });

            }
        },1000);
    }
    private void delayInit(ViewHolder holder, BookdetailBean obj,int position){
        CoverImageView imageView=holder.getView(R.id.tv_book_img);
        String url=obj.getImgUrl();
        if(App.ViewEmptyContent(holder.getView(R.id.tv_book_desc)))
            ReInitTag(holder,obj);
        if(App.ViewEmptyContent(holder.getView(R.id.tv_book_desc))) {
            holder.setText(R.id.tv_book_desc, "简介:" + obj.getDesc());
        }
        try {
            if(!App.isDestroy((Activity)context))//判断Activity不能消失
            {
                if(imageView.getTag()==null||(boolean)imageView.getTag()) {
                    Glide.with(context).load(url).placeholder(R.mipmap.ic_default)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    imageView.setTag(false);
                                    Drawable resource=context.getResources().getDrawable(R.mipmap.ic_default);
                                    Bitmap bitmap=BaseApi.drawableToBitamp(resource);
                                   // obj.setDrawable(BaseApi.bitmap2Bytes(bitmap));
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    imageView.setTag(false);
                                    Bitmap bitmap=BaseApi.drawableToBitamp(resource);
                                   // obj.setDrawable(BaseApi.bitmap2Bytes(bitmap));
                                    return false;
                                }
                            })
                            .into(imageView);
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void ReInitTag(ViewHolder holder,BookdetailBean obj){
        String status=obj.getStatus();
        status="status0:"+status;
        tagList.clear();
        if(status!=null&&status.length()>8)
            tagList.add(status);
        String type=obj.getNovelType();
            type="status1:"+type;
           if(type!=null&&type.length()>8)
               tagList.add(type);
        TagFlowLayout tagFlowLayout=holder.getView(R.id.tv_book_tag);
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
