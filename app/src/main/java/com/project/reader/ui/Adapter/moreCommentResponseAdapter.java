package com.project.reader.ui.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.reader.R;
import com.project.reader.entity.CommentDetailBean;
import com.project.reader.entity.ReplyDetailBean;
import com.project.reader.ui.Activity.CommentActivity;

import java.util.List;

public class moreCommentResponseAdapter extends CommonAdapter<ReplyDetailBean,moreCommentResponseAdapter.ViewHolder> {
    public moreCommentResponseAdapter(Context context, int layoutId) {
        super(context, layoutId);
    }

    public moreCommentResponseAdapter(Context context, List<ReplyDetailBean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void onBind(ViewHolder holder, ReplyDetailBean data) {
        TextView textViewName=holder.getView(R.id.more_comment_user_nickname);
        textViewName.setText(data.getNickName());
        TextView textViewTime=holder.getView(R.id.more_comment_response_time);
        textViewTime.setText(data.getCreateDate());
        TextView textViewContent=holder.getView(R.id.more_comment_response_content);
        textViewContent.setText("回复 "+data.getReplyPerson()+" :"+data.getContent());
        LinearLayout linearLayout=holder.getView(R.id.more_comment_reply_main_item);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mContext instanceof CommentActivity){
                    TextView textView=((CommentActivity)mContext).binding.moreCommentList.binding.moreCommentInputArea.binding.moreCommentResponseInfo;
                    textView.setText("回复 "+data.getNickName()+"："+data.getContent());
                }
            }
        });
    }
    public static  class  ViewHolder extends CommonAdapter.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }

    }
}
