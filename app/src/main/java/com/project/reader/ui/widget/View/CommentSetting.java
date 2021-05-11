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
import com.project.reader.ui.util.DataHandler;

import java.util.ArrayList;
import java.util.List;

public class CommentSetting extends FrameLayout {

   public CommentSettingBinding binding;
    public CommentExpandAdapter adapter;
    private Context mContext;
    private CommentSettingCallback callback;
    public List<CommentDetailBean> listComments;

    public void setListComments(List<CommentDetailBean> listComments) {
        this.listComments = listComments;
        initExpandableListView(listComments);
        System.out.println("get here");
    }

    public void setCallback(CommentSettingCallback callback) {
        this.callback = callback;
    }

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
        listComments=new ArrayList<>();
        initExpandableListView(listComments);
    }
    private void initExpandableListView(final List<CommentDetailBean> commentList){
        ExpandableListView expandableListView=binding.detailPageLvComment;
        adapter = new CommentExpandAdapter(mContext, commentList);
        adapter.setCommentCallback(new CommentExpandAdapter.CommentCallback() {
            @Override
            public void OnItemClickListener(int groupId) {
                callback.OnItemClickListener(groupId);
            }

            @Override
            public void OnSendMainReplyInfo(String name, String content) {
                callback.SendMainReplyInfo(name,content);
            }
        });
        expandableListView.setAdapter(adapter);
        expandableListView.setGroupIndicator(null);
        if(mContext instanceof CommentActivity){
            for(int i=0;i<commentList.size();i++)
              expandableListView.expandGroup(i);
        }
        if(mContext instanceof BookDetailedActivity) {
            expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                    boolean isExpanded = expandableListView.isGroupExpanded(groupPosition);
                    Log.e("测试", "onGroupClick: 当前的评论id>>>" + commentList.get(groupPosition).getId());
                    return true;
                }
            });

            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                    Toast.makeText(mContext, "点击了回复", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });

    }
    public interface CommentSettingCallback{
        public void OnItemClickListener(int groupId);
        public void SendMainReplyInfo(String name, String content);
    }
}
