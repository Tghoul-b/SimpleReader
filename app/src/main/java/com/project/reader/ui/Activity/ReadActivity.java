package com.project.reader.ui.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowId;
import android.view.WindowInsets;
import android.view.WindowManager;

import com.example.reader.R;
import com.project.reader.entity.BookChapterBean;
import com.project.reader.entity.BookChapterDB;
import com.project.reader.ui.util.tools.SystemBarUtils;
import com.project.reader.ui.widget.Page.PageLoader;
import com.project.reader.ui.widget.Page.PageView;
import com.project.reader.ui.widget.utils.StatusBarUtil;
import com.smarx.notchlib.NotchScreenManager;

public class ReadActivity extends AppCompatActivity  {
    public PageView mPageView;
    private PageLoader mPageLoader;
    private BookChapterBean bookChapterBean;
    private BookChapterDB bookChapterDB;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
           switch (msg.what){
               case 1:
                   init();
           }
        }
    };
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){  //电池电量变化
                int level=intent.getIntExtra("level",0);
                mPageLoader.updateBattery(level);
            }
            else if(Intent.ACTION_TIME_TICK.equals(intent.getAction())){
                mPageLoader.updateTime();
            }
        }
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        initData();
        initClick();
        processLogic();
    }

    @Override
    public void onResume() {
     super.onResume();
     initWidget();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if(true) {
                    return true;
                }
                    break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if(true) {
                    return mPageLoader.skipToNextPage();
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void initData(){
        bookChapterBean=(BookChapterBean)getIntent().getSerializableExtra("singleChapterInfo");
        bookChapterDB=(BookChapterDB)getIntent().getSerializableExtra("singleChapterDB");
        mPageView=findViewById(R.id.bookPageView);
        mPageLoader=mPageView.getPageLoader(bookChapterBean,bookChapterDB);
        mPageLoader.setmStatus(PageLoader.STATUS_LOADING_CHAPTER);//先是加载章节状态
    }
    private void initClick(){
        mPageView.setTouchListener(new PageView.TouchListener() {
            @Override
            public boolean onTouch() {
                return false;
            }

            @Override
            public void center() {

            }

            @Override
            public void prePage() {

            }

            @Override
            public void nextPage() {

            }

            @Override
            public void cancel() {

            }
        });
    }
    private void initWidget(){
        NotchScreenManager.getInstance().setDisplayInNotch(this);//刘海屏全屏适配方案
        SystemBarUtils.fullscreen(true,this);
        SystemBarUtils.hideStableNavBar(this);
        SystemBarUtils.hideStableStatusBar(this);
    }
    private void skipToChapterAndPage(){
        mPageLoader.skipToChapter(0);
    }
    private void init(){
        try {
            mPageLoader.refreshChapterList();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void processLogic(){
        mHandler.sendMessage(mHandler.obtainMessage(1));
        IntentFilter intentFilter = new IntentFilter();//注册广播
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mReceiver,intentFilter);//注册广播
    }
}