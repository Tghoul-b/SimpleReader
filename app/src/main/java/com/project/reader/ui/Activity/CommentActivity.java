package com.project.reader.ui.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reader.R;
import com.example.reader.databinding.ActivityCommentBinding;
import com.project.reader.entity.BookdetailBean;
import com.project.reader.entity.CommentDetailBean;
import com.project.reader.entity.RemoteDBbean;
import com.project.reader.entity.ReplyDetailBean;
import com.project.reader.ui.Adapter.moreCommentResponseAdapter;
import com.project.reader.ui.util.DataHandler;
import com.project.reader.ui.util.cache.SpUtils;
import com.project.reader.ui.util.tools.App;
import com.project.reader.ui.util.tools.BaseApi;
import com.project.reader.ui.util.tools.SoftInputUtils;
import com.project.reader.ui.widget.Page.PageView;
import com.project.reader.ui.widget.View.CommentSetting;
import com.project.reader.ui.widget.View.moreCommentinputWidget;
import com.project.reader.ui.widget.utils.StatusBarUtil;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CommentActivity extends AppCompatActivity {


    public ActivityCommentBinding binding;
    private Animation mBottomInAnim,mBottomOutAnim;
    private onItemTouchListener listener;
    private List<CommentDetailBean> commentDetailBeanList;
    private Tencent mTencent;
    private UserInfo mInfo;
    private static final String APPID = "1105771437";
    public List<CommentDetailBean> getCommentDetailBeanList() {
        return commentDetailBeanList;
    }
    private BookdetailBean bookdetailBean;
    public void setListener(onItemTouchListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindView();
        initData();
        initClick();
    }
    private void bindView(){
        binding=ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            StatusBarUtil.setLightStatusBar(this,true,true);//状态栏设置成黑色
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    private void initClick(){
        binding.moreCommentSetting.setCallback(new CommentSetting.CommentSettingCallback() {
            @Override
            public void OnItemClickListener(int groupId) {
                ShowBottomList(groupId);
            }

            @Override
            public void SendMainReplyInfo(String name, String content) {
                binding.moreCommentList.binding.louzhuInfo.moreCommentUserNickname.setText(name);
                binding.moreCommentList.binding.louzhuInfo.moreCommentResponseContent.setText(content);
                String tmp_c=content.substring(0,Math.min(content.length(),10));
                binding.moreCommentList.binding.louzhuInfo.moreCommentReplyMainItem.setOnClickListener(new View.OnClickListener(){
                     @Override
                    public void onClick(View v) {
                        binding.moreCommentList.binding.moreCommentInputArea.binding.moreCommentResponseInfo.setText(
                                "回复 "+name+":"+tmp_c
                        );
                    }
                });
                binding.moreCommentList.binding.moreCommentInputArea.binding.moreCommentResponseInfo.setText(
                        "回复 "+name+":"+tmp_c
                );
            }
        });
        binding.moreCommentSetting.findViewById(R.id.detail_page_lv_comment).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(binding.moreCommentList.getVisibility()==View.VISIBLE){
                    binding.moreCommentList.setVisibility(View.GONE);
                    binding.moreCommentList.startAnimation(mBottomOutAnim);
                    listener.hideBottomLayout();
                }
                return false;
            }
        });
        SoftInputUtils softInputUtils=new SoftInputUtils();
//        EditText editText=binding.moreCommentList.binding.moreCommentInputArea.binding.moreCommentEditArea;
//        softInputUtils.attachSoftInput(editText, new SoftInputUtils.ISoftInputChanged() {
//            @Override
//            public void onChanged(boolean isSoftInputShow, int softInputHeight, int viewOffset) {
//                if (isSoftInputShow) {
//                    LinearLayout linearLayout=binding.moreCommentList.binding.moreCommentInputArea.binding.getRoot();
//                    RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)linearLayout.getLayoutParams();
//                    layoutParams.setMargins(0,0,0,softInputHeight);
//                    linearLayout.setLayoutParams(layoutParams);
//                } else {
//                    LinearLayout linearLayout=binding.moreCommentList.binding.moreCommentInputArea.binding.getRoot();
//                    RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)linearLayout.getLayoutParams();
//                    layoutParams.setMargins(0,0,0,0);
//                    linearLayout.setLayoutParams(layoutParams);
//                }
//            }
//        });
        EditText editText=binding.commentMainInputArea.binding.moreCommentEditArea;
        softInputUtils.attachSoftInput(editText, new SoftInputUtils.ISoftInputChanged() {
            @Override
            public void onChanged(boolean isSoftInputShow, int softInputHeight, int viewOffset) {
                if (isSoftInputShow) {
                    LinearLayout linearLayout=binding.commentMainInputArea.binding.getRoot();
                    RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)linearLayout.getLayoutParams();
                    layoutParams.setMargins(0,0,0,softInputHeight);
                    linearLayout.setLayoutParams(layoutParams);
                } else {
                    LinearLayout linearLayout=binding.commentMainInputArea.binding.getRoot();
                    RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)linearLayout.getLayoutParams();
                    layoutParams.setMargins(0,0,0,0);
                    linearLayout.setLayoutParams(layoutParams);
                }
            }
        });
        binding.commentMainInputArea.binding.moreCommentResponseInfo.setText("抢楼");
        binding.commentMainInputArea.binding.moreCommentInputIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=(String)binding.commentMainInputArea.binding.moreCommentEditArea.getText().toString();
                Date date=new Date();
                String strDateFormat = "yyyy-MM-dd HH:mm:ss";
                SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
                String dateStr=sdf.format(date);
                String nickName=SpUtils.getInstance(CommentActivity.this).getString("username",null);
                if(TextUtils.isEmpty(nickName))
                    nickName="匿名用户";
                CommentDetailBean commentDetailBean=new CommentDetailBean(nickName,s,dateStr);
                commentDetailBean.setReplyList(new ArrayList<>());
                RemoteDBbean remoteDBbean=new RemoteDBbean(bookdetailBean.hashCode(),nickName,s,dateStr,"root");
                DataHandler.InsertReplyInfo(remoteDBbean);
                binding.moreCommentSetting.adapter.add(commentDetailBean);
                binding.commentMainInputArea.binding.moreCommentEditArea.setText("");
            }
        });
    }

    private void initData(){
        mBottomInAnim= AnimationUtils.loadAnimation(this,R.anim.read_bottom_in);
        mBottomOutAnim=AnimationUtils.loadAnimation(this,R.anim.read_bottom_out);

        Intent intent=getIntent();
        bookdetailBean=(BookdetailBean)intent.getSerializableExtra("bookInfo");
        DataHandler.getCommentData(bookdetailBean.hashCode());
        commentDetailBeanList=new ArrayList<>();
    }
    private void ShowBottomList(int groupId){
        if(binding.moreCommentList.getVisibility()==View.GONE) {
            binding.moreCommentList.setVisibility(View.VISIBLE);
            binding.moreCommentList.startAnimation(mBottomInAnim);
            String s=String.format("%d楼的回复",groupId+1);
            TextView textViewTitle=binding.moreCommentList.findViewById(R.id.more_comment_title);
            textViewTitle.setText(s);
            s=String.format("%d条回复",7);
            TextView textViewSum=binding.moreCommentList.findViewById(R.id.sum_comment_list);
            textViewSum.setText(s);
           ListView listView= binding.moreCommentList.binding.moreCommentListview;

            moreCommentResponseAdapter mAdapter=new moreCommentResponseAdapter(this,R.layout.more_comment_reply_item);
            List<ReplyDetailBean> list_reply=commentDetailBeanList.get(groupId).getReplyList();
            listView.setAdapter(mAdapter);
            mAdapter.addAll(list_reply);
            binding.moreCommentList.binding.moreCommentInputArea.setClicker(new moreCommentinputWidget.OnInputClicker() {
                @Override
                public void SendComment(String s) {
                    String LoginName=SpUtils.getInstance(CommentActivity.this).getString("username",null);
                    if(TextUtils.isEmpty(LoginName))
                        LoginName="匿名用户";

                    String content=(String)binding.moreCommentList.binding.moreCommentInputArea.binding.moreCommentResponseInfo.getText();
                    int index=3;
                    while(content.charAt(index)!=':'&&content.charAt(index)!='：')
                        index++;
                    String replyPerson=content.substring(3,index-1);
                    Date date=new Date();
                    ReplyDetailBean replyDetailBean=new ReplyDetailBean(LoginName,s,replyPerson);
                    replyDetailBean.setCreateDate("刚刚");
                    RemoteDBbean bbean=new RemoteDBbean(bookdetailBean.hashCode(),LoginName,s,date.toString(),replyPerson);
                    mAdapter.add(replyDetailBean);
                    mAdapter.notifyDataSetChanged();
                    DataHandler.InsertReplyInfo(bbean);
                }
            });

        }
    }

    public BookdetailBean getBookdetailBean() {
        return bookdetailBean;
    }

    public interface onItemTouchListener{
        public void hideBottomLayout();
    }
}