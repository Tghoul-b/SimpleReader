package com.project.reader.ui.Adapter;


import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.reader.R;
import com.project.reader.entity.CommentDetailBean;
import com.project.reader.ui.widget.CircleImageView;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class CommentExpandAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "CommentExpandAdapter";
    private List<CommentDetailBean> commentBeanList;
    private Context context;
    private CommentCallback commentCallback;
    private final  int max_show_comments=3;
    public void setCommentCallback(CommentCallback commentCallback) {
        this.commentCallback = commentCallback;
    }
    public CommentExpandAdapter(Context context, List<CommentDetailBean> commentBeanList)               {
        this.context = context;
        this.commentBeanList = commentBeanList;
    }
    public void add(CommentDetailBean bean){
        commentBeanList.add(bean);
        notifyDataSetChanged();
    }
    @Override
    public int getGroupCount() {
        return commentBeanList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        if(commentBeanList.get(i).getReplyList() == null){
            return 0;
        }else {
            return Math.min(commentBeanList.get(i).getReplyList().size()>0 ? commentBeanList.get(i).getReplyList().size():0,max_show_comments);//最多显示3条评论
        }

    }

    @Override
    public Object getGroup(int i) {
        return commentBeanList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return commentBeanList.get(i).getReplyList().get(i1);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return getCombinedChildId(groupPosition, childPosition);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
    boolean isLike = false;

    @Override
    public View getGroupView(final int groupPosition, boolean isExpand, View convertView, ViewGroup viewGroup) {
        final GroupHolder groupHolder;

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_item_layout, viewGroup, false);
            groupHolder = new GroupHolder(convertView);
            convertView.setTag(groupHolder);

        }else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        groupHolder.tv_name.setText(commentBeanList.get(groupPosition).getNickName());
        groupHolder.tv_time.setText(commentBeanList.get(groupPosition).getCreateDate());
        groupHolder.tv_content.setText(commentBeanList.get(groupPosition).getContent());
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean b, View convertView, ViewGroup viewGroup) {
        final ChildHolder childHolder;

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_reply_item_layout,viewGroup, false);
            childHolder = new ChildHolder(convertView);
            convertView.setTag(childHolder);
        }
        else {
            childHolder = (ChildHolder) convertView.getTag();
        }
        String replyUser = commentBeanList.get(groupPosition).getReplyList().get(childPosition).getNickName();
        if(!TextUtils.isEmpty(replyUser)){
            childHolder.tv_name.setText(replyUser + ":");
        }

        childHolder.tv_content.setText(commentBeanList.get(groupPosition).getReplyList().get(childPosition).getContent());
        int size=commentBeanList.get(groupPosition).getReplyList().size();
        int d=Math.min(size>0 ? size:0,max_show_comments);
        if(childPosition>=2&&childPosition==d-1) {
            childHolder.tv_loadMore.setVisibility(View.VISIBLE);
            String s=String.format("查看全部%d条回复",size);
            childHolder.tv_loadMore.setText(s);
            childHolder.tv_loadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commentCallback.OnItemClickListener(groupPosition);
                    commentCallback.OnSendMainReplyInfo(commentBeanList.get(groupPosition).getNickName(),
                            commentBeanList.get(groupPosition).getContent());
                }
            });
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private class GroupHolder{
        private CircleImageView logo;
        private TextView tv_name, tv_content, tv_time;
        public GroupHolder(View view) {
            logo =  view.findViewById(R.id.comment_item_logo);
            tv_content = view.findViewById(R.id.comment_item_content);
            tv_name = view.findViewById(R.id.comment_item_userName);
            tv_time = view.findViewById(R.id.comment_item_time);
        }
    }

    private class ChildHolder{
        private TextView tv_name, tv_content,tv_loadMore;
        public ChildHolder(View view) {
            tv_name = (TextView) view.findViewById(R.id.reply_item_user);
            tv_content = (TextView) view.findViewById(R.id.reply_item_content);
            tv_loadMore=(TextView)view.findViewById(R.id.tv_load_more);
        }
    }
    public interface  CommentCallback{
        public void OnItemClickListener(int groupId);
        public void OnSendMainReplyInfo(String name,String content);
    }
}
