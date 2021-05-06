package com.project.reader.ui.widget.View;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.reader.databinding.CommentSettingBinding;
import com.project.reader.entity.CommentDetailBean;
import com.project.reader.entity.ReplyDetailBean;
import com.project.reader.ui.Activity.BookDetailedActivity;
import com.project.reader.ui.Activity.CommentActivity;
import com.project.reader.ui.Adapter.CommentExpandAdapter;

import java.util.ArrayList;
import java.util.List;

public class CommentSetting extends FrameLayout {

    private CommentSettingBinding binding;
    private CommentExpandAdapter adapter;
    private Context mContext;
    public CommentSetting(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CommentSetting(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CommentSetting(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CommentSetting(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    private void init(Context context){
        mContext=context;
        binding= CommentSettingBinding.inflate(LayoutInflater.from(context),this,true);
        initData();
    }
    private void initData(){
        List<CommentDetailBean> commentDetailBeanList=new ArrayList<>();
        CommentDetailBean commentDetailBean=new CommentDetailBean("呗","测试","2020-03-10");
        List<ReplyDetailBean> list=new ArrayList<>();
        for(int i=0;i<10;i++)
            list.add(new ReplyDetailBean("回复","回复"));
        commentDetailBean.setReplyList(list);
        commentDetailBeanList.add(commentDetailBean);
        for(int i=0;i<10;i++)
            commentDetailBeanList.add(new CommentDetailBean("回复2","回复2","回复2"));
        initExpandableListView(commentDetailBeanList);
    }
    private void initExpandableListView(final List<CommentDetailBean> commentList){
        ExpandableListView expandableListView=binding.detailPageLvComment;
        adapter = new CommentExpandAdapter(mContext, commentList);
        expandableListView.setAdapter(adapter);
        expandableListView.setGroupIndicator(null);
        if(mContext instanceof CommentActivity){
            for(int i=0;i<commentList.size();i++)
              expandableListView.expandGroup(i);
        }
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                boolean isExpanded = expandableListView.isGroupExpanded(groupPosition);
                Log.e("测试", "onGroupClick: 当前的评论id>>>"+commentList.get(groupPosition).getId());
                return true;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                Toast.makeText(mContext,"点击了回复", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });

    }
}
