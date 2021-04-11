package com.project.reader.ui.Adapter;



import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reader.R;
import com.project.reader.entity.SearchBookBean;
import com.project.reader.ui.Activity.AboutAppActivity;
import com.project.reader.ui.Activity.MainActivity;
import com.squareup.picasso.Picasso;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cn.itlowly.view.CoverImageView;

public abstract class CommonListAdapter<T> extends RecyclerView.Adapter<CommonListAdapter.ViewHolder> {
    private int mLayoutRes;
    protected List<T> mData;
    private ViewHolder viewHolder;
    //定义接口 OnItemClickListener
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public CommonListAdapter( int mLayoutRes) {
        this.mLayoutRes = mLayoutRes;
        this.mData=new ArrayList<>();
    }
    public T getItem(int position){
        return mData.get(position);
    }
    /**
     * inflate操作
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        viewHolder = ViewHolder.get(parent.getContext(), parent, mLayoutRes);
        return viewHolder;
    }


    /**
     * 暴露出来的接口
     *
     * @param holder
     * @param obj
     */
    public abstract void bindView(ViewHolder holder, T obj,int position);

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.getView(R.id.tv_book_img).setTag(R.id.btn_ok,position);
        bindView(holder, mData.get(position),position);
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });
        }
    }

    /**
     * 获取高度
     *
     * @return
     */
    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }
    public abstract void addAll(List<T> data,String Research);
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private SparseArray<View> mViews;
        private View mConvertView;
        private static View itemView;
        public ViewHolder(Context context, View itemView, ViewGroup parent) {
            super(itemView);
            mConvertView = itemView;
            mViews = new SparseArray<View>();
        }

        public static ViewHolder get(Context context, ViewGroup parent, int layoutid) {
            itemView = LayoutInflater.from(context).inflate(layoutid, parent, false);
            ViewHolder holder = new ViewHolder(context, itemView, parent);
            System.out.println(holder);
            return holder;
        }

        public <T extends View> T getView(int viewid) {
            View view = mViews.get(viewid);
            if (view == null) {
                view = mConvertView.findViewById(viewid);
                mViews.put(viewid, view);
            }
            return (T) view;
        }

        /**
         * 设置TextView文本
         */
        public ViewHolder setText(int viewId, CharSequence text) {
            View view = getView(viewId);
            if (view instanceof TextView) {
                ((TextView) view).setText(text);
            }
            return this;
        }

        /**
         * 设置View的Visibility
         */
        public ViewHolder setViewVisibility(int viewId, int visibility) {
            getView(viewId).setVisibility(visibility);
            return this;
        }

        /**
         * 设置ImageView的资源
         */
        public ViewHolder setImageResource(int viewId, int drawableRes) {
            View view = getView(viewId);
            if (view instanceof ImageView) {
                ((ImageView) view).setImageResource(drawableRes);
            } else {
                view.setBackgroundResource(drawableRes);
            }
            return this;
        }
    }

}