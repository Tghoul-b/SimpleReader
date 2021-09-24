package com.project.reader.ui.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import com.example.reader.R;
import com.example.reader.databinding.ActivityCommentBinding;
import com.project.reader.entity.BookdetailBean;
import com.project.reader.entity.CommentDetailBean;
import com.project.reader.entity.RemoteDBbean;
import com.project.reader.entity.ReplyDetailBean;
import com.project.reader.ui.Adapter.CommentExpandAdapter;
import com.project.reader.ui.Adapter.moreCommentResponseAdapter;
import com.project.reader.ui.util.DataHandler;
import com.project.reader.ui.util.cache.SpUtils;
import com.project.reader.ui.util.tools.App;
import com.project.reader.ui.util.tools.SoftInputUtils;
import com.project.reader.ui.widget.View.moreCommentinputWidget;
import com.project.reader.ui.widget.utils.StatusBarUtil;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.Tencent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class CommentActivity extends AppCompatActivity {


    public ActivityCommentBinding binding;
    private Animation mBottomInAnim,mBottomOutAnim;
    private onItemTouchListener listener;
    private List<CommentDetailBean> commentDetailBeanList;
    private Tencent mTencent;
    private UserInfo mInfo;
    private static final String APPID = "1105771437";
    private BookdetailBean bookdetailBean;
    private ExpandableListView expandableListView;
    private CommentExpandAdapter CommentAdapter;
    public void setListener(onItemTouchListener listener) {
        this.listener = listener;
    }
    private int curGroupId;
    private int curUserId;
    private moreCommentResponseAdapter ResponseAdapter;
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
    private String getUserId(){
        String m_szDevIDShort = "35" + //we make this look like a valid IMEI

                Build.BOARD.length()%10 +
                Build.BRAND.length()%10 +
                Build.CPU_ABI.length()%10 +
                Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 +
                Build.HOST.length()%10 +
                Build.ID.length()%10 +
                Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 +
                Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 +
                Build.TYPE.length()%10 +
                Build.USER.length()%10 ; //13 digit
        return m_szDevIDShort;
    }
    /**
     * 初始化评论数据
     */
    private void initData(){
        DataHandler.getCurId(getUserId());
        mBottomInAnim= AnimationUtils.loadAnimation(this,R.anim.read_bottom_in);
        mBottomOutAnim=AnimationUtils.loadAnimation(this,R.anim.read_bottom_out);
        expandableListView=binding.detailPageLvComment;
        commentDetailBeanList=new ArrayList<>();
        Intent intent=getIntent();
        bookdetailBean=(BookdetailBean)intent.getSerializableExtra("bookInfo");

        DataHandler.getCommentData(bookdetailBean.hashCode());
        DataHandler.setCallback(new DataHandler.CURDCallback() {
            @Override
            public void getDataCallback(List<CommentDetailBean> list) {
                commentDetailBeanList=list;
                App.runOnUiThread(()->{
                    initExpandableListView(commentDetailBeanList);
                });
            }

            @Override
            public void loadCurId(int curId) {
                curUserId=curId;
            }
        });

    }
    @SuppressLint("ClickableViewAccessibility")
    private void initClick(){
        initExpandableListView(commentDetailBeanList);
        SoftInputUtils softInputUtils=new SoftInputUtils();
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
        binding.moreCommentList.binding.moreCommentTitleArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=binding.moreCommentList.binding.louzhuInfo.moreCommentUserNickname.getText().toString();
                String content=binding.moreCommentList.binding.louzhuInfo.moreCommentResponseContent.getText().toString();
                binding.moreCommentList.binding.moreCommentInputArea.binding.moreCommentResponseInfo.setText("回复 "+name+": "+content);
            }
        });
        binding.commentMainInputArea.binding.moreCommentResponseInfo.setText("抢楼");
        /**
         * 这个是当楼主的评论函数
         */
        binding.commentMainInputArea.binding.moreCommentInputIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=(String)binding.commentMainInputArea.binding.moreCommentEditArea.getText().toString();
                if(!TextUtils.isEmpty(s)) {
                    Date date = new Date();
                    String strDateFormat = "yyyy-MM-dd HH:mm:ss";
                    SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
                    String dateStr = sdf.format(date);
                    String nickName = SpUtils.getInstance(CommentActivity.this).getString("username", null);//判断当前用户是否登录
                    if (TextUtils.isEmpty(nickName))
                        nickName = "匿名用户"+curUserId;  //当前用户的编号
                    CommentDetailBean commentDetailBean = new CommentDetailBean(nickName, s, dateStr);
                    commentDetailBean.setReplyList(new ArrayList<>());
                    RemoteDBbean remoteDBbean = new RemoteDBbean(bookdetailBean.hashCode(), nickName, s, dateStr, "root",commentDetailBeanList.size());//记录楼数好记录这是第几层楼
                    DataHandler.InsertReplyInfo(remoteDBbean);//后端插入一条数据
                    CommentAdapter.add(commentDetailBean);
                    binding.commentMainInputArea.binding.moreCommentEditArea.setText("");
                }
                else{
                    Toasty.warning(CommentActivity.this,"评论内容不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });

        /**
         * 这个是回复某一层楼的评论函数
         */
        binding.moreCommentList.binding.moreCommentInputArea.binding.moreCommentInputIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String content=binding.moreCommentList.binding.moreCommentInputArea.binding.moreCommentEditArea.getText().toString();
                if(!TextUtils.isEmpty(content)){
                    Date date = new Date();
                    String strDateFormat = "yyyy-MM-dd HH:mm:ss";
                    SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
                    String dateStr = sdf.format(date);
                    String nickName = SpUtils.getInstance(CommentActivity.this).getString("username", null);
                    if (TextUtils.isEmpty(nickName))
                        nickName = "匿名用户"+curUserId;
                    String reponseInfo=binding.moreCommentList.binding.moreCommentInputArea.binding.moreCommentResponseInfo.getText().toString();
                    int index=3;
                    while(reponseInfo.charAt(index)!=':'&&reponseInfo.charAt(index)!='：')  index++;
                    String repName=reponseInfo.substring(3,index);
                    RemoteDBbean remoteDBbean = new RemoteDBbean(bookdetailBean.hashCode(), nickName, content, dateStr, repName,curGroupId);  //当前组里面
                    DataHandler.InsertReplyInfo(remoteDBbean);
                    binding.moreCommentList.binding.moreCommentInputArea.binding.moreCommentEditArea.setText("");
                    ReplyDetailBean replyDetailBean=new ReplyDetailBean(nickName,content,dateStr,repName);
                    ResponseAdapter.add(replyDetailBean);
                    commentDetailBeanList.get(curGroupId).getReplyList().add(replyDetailBean);
                    CommentAdapter.notifyDataSetChanged();
                }
            }
        });
    }
    private void initExpandableListView(final List<CommentDetailBean> commentList){
        expandableListView.setGroupIndicator(null);
        //默认展开所有回复
        CommentAdapter = new CommentExpandAdapter(this, commentList);
        CommentAdapter.setCommentCallback(new CommentExpandAdapter.CommentCallback() {
            @Override
            public void OnItemClickListener(int groupId) {
                ShowBottomList(groupId);
            }

            @Override
            public void OnSendMainReplyInfo(String name, String content) {
                binding.moreCommentList.binding.moreCommentInputArea.binding.moreCommentResponseInfo.setText("回复 "+name
                        +"："+content);
            }
        });
        expandableListView.setAdapter(CommentAdapter);
        for(int i = 0; i<commentList.size(); i++){
            expandableListView.expandGroup(i);
        }
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                boolean isExpanded = expandableListView.isGroupExpanded(groupPosition);
                Log.e("测试", "onGroupClick: 当前的评论id>>>"+commentList.get(groupPosition).getId());
                ShowBottomList(groupPosition);
                return true;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                ShowBottomList(groupPosition);
                String name=commentDetailBeanList.get(groupPosition).getReplyList().get(childPosition).getNickName();
                String content=commentList.get(groupPosition).getReplyList().get(childPosition).getContent();
                binding.moreCommentList.binding.moreCommentInputArea.binding.moreCommentResponseInfo.setText("回复 "+name
                        +"："+content);
                return false;
            }
        });

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        if(binding.moreCommentList.getVisibility()==View.VISIBLE){
            binding.moreCommentList.setVisibility(View.GONE);
            binding.commentMainInputArea.setVisibility(View.VISIBLE);
        }
        else
            super.onBackPressed();
    }

    private void ShowBottomList(int groupId){
        if(binding.moreCommentList.getVisibility()==View.GONE) {
            curGroupId=groupId;
            binding.commentMainInputArea.setVisibility(View.GONE);//当前输入框隐藏
            binding.moreCommentList.setVisibility(View.VISIBLE);
            binding.moreCommentList.startAnimation(mBottomInAnim);
            String s=String.format("%d楼的回复",groupId+1);
            TextView textViewTitle=binding.moreCommentList.findViewById(R.id.more_comment_title);
            textViewTitle.setText(s);
            TextView textViewNickName=binding.moreCommentList.findViewById(R.id.louzhu_info).findViewById(R.id.more_comment_user_nickname);
            textViewNickName.setText(commentDetailBeanList.get(groupId).getNickName());
            TextView textViewContent=binding.moreCommentList.findViewById(R.id.louzhu_info).findViewById(R.id.more_comment_response_content);
            textViewContent.setText(commentDetailBeanList.get(groupId).getContent());
            TextView textViewTime=binding.moreCommentList.findViewById(R.id.louzhu_info).findViewById(R.id.more_comment_response_time);
            textViewTime.setText(commentDetailBeanList.get(groupId).getCreateDate());
            s=String.format("%d条回复",commentDetailBeanList.get(groupId).getReplyList().size());
            TextView textViewSum=binding.moreCommentList.findViewById(R.id.sum_comment_list);
            textViewSum.setText(s);
            ListView listView= binding.moreCommentList.binding.moreCommentListview;
             ResponseAdapter=new moreCommentResponseAdapter(this,R.layout.more_comment_reply_item);
            binding.moreCommentList.binding.moreCommentInputArea.binding.moreCommentResponseInfo.setText("回复 "+commentDetailBeanList.get(groupId).getNickName()
            +"："+commentDetailBeanList.get(groupId).getContent());
            List<ReplyDetailBean> list_reply=commentDetailBeanList.get(groupId).getReplyList();
            listView.setAdapter(ResponseAdapter);
            ResponseAdapter.addAll(list_reply);
        }
    }

    public BookdetailBean getBookdetailBean() {
        return bookdetailBean;
    }

    public interface onItemTouchListener{
        public void hideBottomLayout();
    }
}