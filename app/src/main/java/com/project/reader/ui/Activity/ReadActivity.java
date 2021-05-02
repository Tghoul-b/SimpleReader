package com.project.reader.ui.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import android.view.ViewGroup;
import android.view.WindowId;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.reader.R;
import com.example.reader.databinding.ActivityReadBinding;
import com.project.reader.Config;
import com.project.reader.entity.BookChapterBean;
import com.project.reader.entity.BookChapterDB;
import com.project.reader.ui.util.Setting;
import com.project.reader.ui.util.tools.App;
import com.project.reader.ui.util.tools.BaseApi;
import com.project.reader.ui.util.tools.BrightUtils;
import com.project.reader.ui.util.tools.SystemBarUtils;
import com.project.reader.ui.widget.Page.PageLoader;
import com.project.reader.ui.widget.Page.PageView;
import com.project.reader.ui.widget.utils.StatusBarUtil;
import com.smarx.notchlib.NotchScreenManager;

import java.util.zip.Inflater;

public class ReadActivity extends AppCompatActivity  {
    public PageView mPageView;
    private PageLoader mPageLoader;
    private BookChapterBean bookChapterBean;
    private BookChapterDB bookChapterDB;
    private ActivityReadBinding binding;
    private Animation mTopInAni,mTopOutAni,mBottomInAni,mBottomOutAni,slideLeftIn,slideLeftOut;
    private boolean showNavBar=false;  //显示底部的导航栏
    private boolean showSlideLayout=false;//显示侧滑栏
    private Setting setting;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
           switch (msg.what){
               case 1:
                   init();
           }
        }
    };
    private final  BroadcastReceiver mReceiver = new BroadcastReceiver() {
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
        binding=ActivityReadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initData();
        initClick();
        processLogic();
    }
    @Override
    public void onResume() {
     super.onResume();
     initWidget();
    }
    private void initData(){
        setting=new Setting(this);
        bookChapterBean=(BookChapterBean)getIntent().getSerializableExtra("singleChapterInfo");
        bookChapterDB=(BookChapterDB)getIntent().getSerializableExtra("singleChapterDB");
        mPageView=findViewById(R.id.bookPageView);
        mPageLoader=mPageView.getPageLoader(bookChapterBean,bookChapterDB);
        mPageLoader.setmStatus(PageLoader.STATUS_LOADING_CHAPTER);//先是加载章节状态
        mTopOutAni= AnimationUtils.loadAnimation(this,R.anim.read_top_out);
        mTopInAni=AnimationUtils.loadAnimation(this,R.anim.read_top_in);
        mBottomOutAni= AnimationUtils.loadAnimation(this,R.anim.read_bottom_out);
        mBottomInAni=AnimationUtils.loadAnimation(this,R.anim.read_bottom_in);
        slideLeftIn=AnimationUtils.loadAnimation(this,R.anim.read_slide_in);
        slideLeftOut=AnimationUtils.loadAnimation(this,R.anim.read_slide_out);
    }
    public void hideLeftSlide(){
        LinearLayout linearLayout=binding.tvReadSlideLeft.drawerLayout;
        linearLayout.setVisibility(View.GONE);
        linearLayout.startAnimation(slideLeftOut);
        showSlideLayout=false;
        mPageView.drawCurPage(false);//调整成原来的颜色
    }
    private void initClick(){
        binding.readSettingMenu.setCallback(new MenuReadingSetting.Callback() {
            @Override
            public void changeSize(int dif) {
                mPageLoader.changeTextSize(dif);
            }

            @Override
            public void startFontActivity() {
                Intent intent=new Intent(ReadActivity.this,fontfamilyActivity.class);
                startActivityForResult(intent,Config.FONT_REQ);

            }
        });
        mPageView.setTouchListener(new PageView.TouchListener() {
            @Override
            public boolean onTouch() {
                if(showSlideLayout){
                    hideLeftSlide();
                    return false;
                }
                return true;
            }

            @Override
            public void center() {
                hideorShowMenu();
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
        binding.tvPreChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPageLoader.skipToPreChapter();
            }
        });
        binding.tvNextChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPageLoader.skipToNextChapter();
            }
        });
        binding.tvReadCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linearLayout=binding.tvReadSlideLeft.drawerLayout;
                mPageView.drawCurPage(true);  //黑暗色调
                linearLayout.setVisibility(View.VISIBLE);
                linearLayout.startAnimation(slideLeftIn);
                hideMenu();
                showSlideLayout=true;
            }
        });
        binding.tvReadSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.readBottomBar.setVisibility(View.GONE);
                binding.readSettingMenu.setVisibility(View.VISIBLE);
                binding.readSettingMenu.startAnimation(mBottomInAni);
            }
        });
        binding.tvReadSlideLeft.mainSlideLayout.tvSlideBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideLeftSlide();
            }
        });
        binding.tvReadSlideLeft.mainSlideLayout.tvSlideOrderChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPageLoader.changeListChapterOrder();
            }
        });
    }
    private void showMenu(){
        binding.readTopMenu.startAnimation(mTopInAni);
        binding.readTopMenu.setVisibility(View.VISIBLE);
        binding.readBottomBar.startAnimation(mBottomInAni);
        binding.readBottomBar.setVisibility(View.VISIBLE);
        showNavBar=true;
        showSystemBar();
    }
    private void hideMenu(){
        binding.readTopMenu.startAnimation(mTopOutAni);
        binding.readTopMenu.setVisibility(View.GONE);
        binding.readBottomBar.startAnimation(mBottomOutAni);
        binding.readBottomBar.setVisibility(View.GONE);
        binding.readSettingMenu.setVisibility(View.GONE);
        binding.readSettingMenu.startAnimation(mBottomOutAni);
        showNavBar=false;
        hideSystemBar();
    }
    private void hideorShowMenu(){
        if(binding.readTopMenu.getVisibility()==View.GONE){
            showMenu();
        }
        else{
           hideMenu();
        }
    }
    private void initWidget(){
        if(!showNavBar)
            hideSystemBar();
        int height= BaseApi.getSoftButtonsBarSizePort(this);
        RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)binding.readBottomBar.getLayoutParams();
        layoutParams.setMargins(0,0,0,height);
        binding.readBottomBar.setLayoutParams(layoutParams);
        RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams)binding.readSettingMenu.getLayoutParams();
        params.setMargins(0,0,0,height);//防止下面导航栏遮盖住了widget

        binding.readSettingMenu.setLayoutParams(params);
        StatusBarUtil.setStatusBarColor(this,R.color.read_appbar_bg);//状态栏置成相同的颜色
        BrightUtils.setBrightness(this,setting.getBrightProgress());//设置亮度;
    }
    private void hideSystemBar(){
        NotchScreenManager.getInstance().setDisplayInNotch(this);//刘海屏全屏适配方案
        SystemBarUtils.hideStableNavBar(this);
        SystemBarUtils.hideStableStatusBar(this);
    }
    private void showSystemBar(){
        SystemBarUtils.showUnStableNavBar(this);
        SystemBarUtils.showUnStableStatusBar(this);
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case Config.FONT_REQ:
                    String fontFamily=data.getStringExtra(Config.FONT_RES);
                    App.runOnUiThread(()->{
                        mPageLoader.changeFontFamily(fontFamily);
                    });
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}