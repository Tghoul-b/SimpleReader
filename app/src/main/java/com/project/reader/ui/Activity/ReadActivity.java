package com.project.reader.ui.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.reader.R;
import com.example.reader.databinding.ActivityReadBinding;
import com.project.reader.Config;
import com.project.reader.entity.BookCaseDB;
import com.project.reader.entity.BookChapterBean;
import com.project.reader.entity.BookChapterDB;
import com.project.reader.entity.BookdetailBean;
import com.project.reader.ui.Handler.CrawlerHandler;
import com.project.reader.ui.Handler.baseCrawler;
import com.project.reader.ui.util.Setting;
import com.project.reader.ui.util.cache.ACache;
import com.project.reader.ui.util.tools.App;
import com.project.reader.ui.util.tools.BaseApi;
import com.project.reader.ui.util.tools.BrightUtils;
import com.project.reader.ui.util.tools.SystemBarUtils;
import com.project.reader.ui.util.tools.Themetools;
import com.project.reader.ui.widget.Page.PageLoader;
import com.project.reader.ui.widget.Page.PageView;
import com.project.reader.ui.widget.View.MenuReadingSetting;
import com.project.reader.ui.widget.utils.StatusBarUtil;
import com.smarx.notchlib.NotchScreenManager;

import org.litepal.LitePal;

import java.util.List;

public class ReadActivity extends AppCompatActivity  {
    public PageView mPageView;
    private PageLoader mPageLoader;
    private BookChapterBean bookChapterBean;
    private BookChapterDB bookChapterDB;
    private ActivityReadBinding binding;
    private BookdetailBean bookdetailBean;
    private Animation mTopInAni,mTopOutAni,mBottomInAni,mBottomOutAni,slideLeftIn,slideLeftOut;
    private boolean showNavBar=false;  //显示底部的导航栏
    private boolean showSlideLayout=false;//显示侧滑栏
    private Setting setting;
    private  boolean HorizontalScreen;
    private int nightMode=0;//0代表日间模式,1代表夜间模式
    private boolean isCollected=false;
    private boolean ThreadCollected=false;
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
        bookdetailBean=(BookdetailBean)getIntent().getSerializableExtra("BOOK");
        bookChapterBean=new BookChapterBean();
        bookChapterBean.setChapterNum(bookdetailBean.getLastReadPosition());
        bookChapterBean.setBookName(bookdetailBean.getBookName());
        bookChapterBean.setSourceClass(bookdetailBean.getSourceClass());
        bookChapterDB=new BookChapterDB();
        bookChapterDB.setBookId(bookdetailBean.hashCode());
        mPageView=findViewById(R.id.bookPageView);
        mPageLoader=mPageView.getPageLoader(bookdetailBean,bookChapterBean,bookChapterDB);
        mPageLoader.setmStatus(PageLoader.STATUS_LOADING_CHAPTER);//先是加载章节状态
        mTopOutAni= AnimationUtils.loadAnimation(this,R.anim.read_top_out);
        mTopInAni=AnimationUtils.loadAnimation(this,R.anim.read_top_in);
        mBottomOutAni= AnimationUtils.loadAnimation(this,R.anim.read_bottom_out);
        mBottomInAni=AnimationUtils.loadAnimation(this,R.anim.read_bottom_in);
        slideLeftIn=AnimationUtils.loadAnimation(this,R.anim.read_slide_in);
        slideLeftOut=AnimationUtils.loadAnimation(this,R.anim.read_slide_out);
        HorizontalScreen=(setting.getHorizontalScreen()==1);
        nightMode=setting.getNightMode();
        Themetools.changeSeekBarTheme(this,binding.tvReadPageProcess);
        Themetools.changeSeekBarTheme(this,binding.readSettingMenu.binding.tvSettingMenuProgress);
    }
    private void changeReadMode(){
        if(nightMode==0){
            binding.nightModeBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_night_mode));
        }
        else{
            binding.nightModeBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_big_sun));
        }

    }
    public void hideLeftSlide(){
        LinearLayout linearLayout=binding.tvReadSlideLeft.drawerLayout;
        linearLayout.setVisibility(View.GONE);
        linearLayout.startAnimation(slideLeftOut);
        showSlideLayout=false;
        mPageView.drawCurPage(false);//调整成原来的颜色
    }
    private void initClick(){
        binding.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ReadActivity.this,CommentActivity.class);
                intent.putExtra("bookInfo",bookdetailBean);
                startActivity(intent);
            }
        });
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
            @Override
            public void changeReadStyle(int bacColorId, int textColorId) {
                mPageLoader.changeReadStyle(bacColorId,textColorId);
                nightMode=0;//退出夜间模式
                changeReadMode();
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
        binding.tvHvScreen.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                HorizontalScreen=!HorizontalScreen;
                int hv=HorizontalScreen?1:0;
                setting.setHorizontalScreen(hv);
                setting.saveAllConfig();
                changeHvText();
                setOrientation(HorizontalScreen);
            }
        });
        binding.turnPageMode.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                int pageMode=setting.getPageMode();
                createDialog(pageMode);
            }
        });
        binding.nightModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nightMode^=1;
                setting.initConfig();
                setting.setNightMode(nightMode);
                setting.saveAllConfig();
                changeReadMode();
                mPageLoader.changeReadNightMode(nightMode);
            }
        });
    }
    private void createDialog(int checkedItem){

        AlertDialog.Builder builder=new AlertDialog.Builder(ReadActivity.this);
        builder.setTitle("翻页模式");
        final String[]page_modes={"覆盖","滚动","仿真","滑动"};
        builder.setSingleChoiceItems(page_modes, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setting.setPageMode(which);
                setting.saveAllConfig();//保存设置
                mPageLoader.changePageMode(which);
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    private void changeHvText(){
        if(HorizontalScreen)
            binding.tvHvScreen.setText("竖屏阅读");
        else
            binding.tvHvScreen.setText("横屏阅读");
    }
    private void showMenu(){
        binding.readTopMenu.startAnimation(mTopInAni);
        binding.readTopMenu.setVisibility(View.VISIBLE);
        binding.readBottomBar.startAnimation(mBottomInAni);
        binding.readBottomBar.setVisibility(View.VISIBLE);
        binding.nightModeBtn.setVisibility(View.VISIBLE);
        binding.commentBtn.setVisibility(View.VISIBLE);
        showNavBar=true;
        showSystemBar();
    }
    private void hideMenu(){
        binding.readTopMenu.startAnimation(mTopOutAni);
        binding.readTopMenu.setVisibility(View.GONE);
        binding.readBottomBar.startAnimation(mBottomOutAni);
        binding.readBottomBar.setVisibility(View.GONE);
        binding.nightModeBtn.setVisibility(View.GONE);
        binding.commentBtn.setVisibility(View.GONE);
        if(binding.readSettingMenu.getVisibility()==View.VISIBLE) {
            binding.readSettingMenu.setVisibility(View.GONE);
            binding.readSettingMenu.startAnimation(mBottomOutAni);
        }
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
        changeHvText();
        if(!showNavBar)
            hideSystemBar();
        int height= BaseApi.getSoftButtonsBarSizePort(this);
        if(!HorizontalScreen) {  //竖屏的时候
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.readBottomBar.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, height);
            binding.readBottomBar.setLayoutParams(layoutParams);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.readSettingMenu.getLayoutParams();
            params.setMargins(0, 0, 0, height);//防止下面导航栏遮盖住了widget
            binding.readSettingMenu.setLayoutParams(params);
        }
        else{
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.readBottomBar.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
            binding.readBottomBar.setLayoutParams(layoutParams);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.readSettingMenu.getLayoutParams();
            params.setMargins(0, 0, 0, 0);//防止下面导航栏遮盖住了widget
            binding.readSettingMenu.setLayoutParams(params);
        }
        StatusBarUtil.setStatusBarColor(this,R.color.read_appbar_bg);//状态栏置成相同的颜色
        BrightUtils.setBrightness(this,setting.getBrightProgress());//设置亮度;
        if(setting.getFollow_sys_checked()==1)
            BrightUtils.followSystemBright(this);
        Integer idx=setting.getReadStyle();
        setOrientation(HorizontalScreen);
        changeReadMode();
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
    public void setOrientation(boolean isHorizontalScreen) {
        if (isHorizontalScreen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onBackPressed() {
        if(binding.readTopMenu.getVisibility()==View.VISIBLE){
            hideorShowMenu();
        }
      else{
          finish();
        }
    }
    private boolean isCollected(){
        long bookId=bookdetailBean.hashCode();
        List<BookCaseDB> listRes=LitePal.findAll(BookCaseDB.class);
        List<BookCaseDB> bookCaseDBlist= LitePal.where("bookId =?",Long.toString(bookId)).find(BookCaseDB.class);
        if(bookCaseDBlist==null||bookCaseDBlist.size()==0) {
            isCollected=false;
            ThreadCollected=false;
            return false;
        }
        isCollected=true;
        ThreadCollected=true;
        return true;
    }
    private void saveLastReadPosition(){
        int lastChapterNum=mPageLoader.getCurChapterNumber();
        BookCaseDB bookCaseDB=new BookCaseDB(bookdetailBean);
        bookCaseDB.setLastChapterNum(lastChapterNum);
        baseCrawler crawler= CrawlerHandler.getCrawler(bookdetailBean.getSourceClass());
        new Thread(()->{
            crawler.getChapterAndTime(bookdetailBean.getInfoUrl(),bookdetailBean);
            BookCaseDB bookCaseDB1=new BookCaseDB(bookdetailBean);//不能用isCollected来记录，因为在另一个线程里isCollected已经被修改了
            if(!ThreadCollected){
                bookCaseDB1.save();
            }else{
                bookCaseDB1.updateAll("bookId = ?",Long.toString(bookCaseDB1.getBookId()));//更新其中的数据
            }
        }).start();

    }
    public void finish(){
        if(!isCollected()){
            new AlertDialog.Builder(ReadActivity.this)
                    .setTitle("提示").setMessage("是否加入到书架?")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            saveLastReadPosition();
                            isCollected=true;
                            exit(true);
                        }
                    })
                    .setNegativeButton("取消",new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            exit(false);
                        }
                    } ).show();
        }
        else{
            isCollected=true;
            saveLastReadPosition();
            exit(true);
        }

    }
    private void exit(boolean flag){
        Intent intent=new Intent();
        intent.putExtra("isCollected",isCollected);
        if(flag)
            intent.putExtra("lastChapter",mPageLoader.getCurChapterNumber());
        else
            intent.putExtra("lastChapter",1);
        setResult(RESULT_OK,intent);
        super.finish();
    }

}