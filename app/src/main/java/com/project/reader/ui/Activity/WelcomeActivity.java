package com.project.reader.ui.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.reader.R;
import com.gyf.barlibrary.ImmersionBar;
import com.project.reader.entity.BookCaseDB;
import com.project.reader.entity.BookSrcBean;
import com.project.reader.entity.BookdetailBean;
import com.project.reader.ui.Handler.CrawlerHandler;
import com.project.reader.ui.Handler.baseCrawler;
import com.project.reader.ui.util.cache.ACache;
import com.project.reader.ui.util.permission.PermissionsChecker;
import com.project.reader.ui.util.network.Scrapy;
import com.project.reader.ui.util.tools.App;
import com.project.reader.ui.util.tools.BaseApi;
import com.project.reader.ui.util.tools.SystemBarUtils;
import com.project.reader.ui.util.tools.TrustAllTrustManager;

import org.jsoup.Connection;
import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import es.dmoral.toasty.Toasty;

public class WelcomeActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_STORAGE = 1;
    private static int WAIT_INTERVAL = 2000;
    static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Scrapy.loadMoreBook loadbooks;
    private List<String>  listRes;
    private long curTime=-1;//记录上一次进入activity的时间
    private String SuggestionUrl="https://www.qidian.com/rank/yuepiao?style=2&page=1";  //起点小说网的排行榜,
    private PermissionsChecker mPermissionsChecker;
    private Thread myThread = new Thread() {//创建子线程
        @Override
        public void run() {
            try {
                updateData();
                sleep(WAIT_INTERVAL);//使程序休眠
                Intent it = new Intent(WelcomeActivity.this, MainActivity.class);//启动MainActivity
                startActivity(it);
                finish();//关闭当前活动
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private void updateData(){  //更新书籍信息
        List<BookCaseDB> list= LitePal.findAll(BookCaseDB.class);
        for(BookCaseDB bookCaseDB:list){
            BookdetailBean bookdetailBean=new BookdetailBean(bookCaseDB);
            baseCrawler crawler= CrawlerHandler.getCrawler(bookdetailBean.getSourceClass());
            new Thread(()->{
                crawler.getChapterAndTime(bookdetailBean.getInfoUrl(),bookdetailBean);
                crawler.getChapterList(bookdetailBean,updated -> {
                   bookCaseDB.setUpdateOrNot(updated);
                });
                if(bookCaseDB.getLastChapter()!=bookdetailBean.getLastChapter())
                    bookCaseDB.setLastChapter(bookdetailBean.getLastChapter());
                if(bookCaseDB.getUpdateTime()!=bookdetailBean.getUpdate_time())
                    bookCaseDB.setUpdateTime(bookdetailBean.getUpdate_time());
                bookCaseDB.updateAll("bookId =?",Long.toString(bookCaseDB.getBookId()));
            }).start();
        }
    }
    private void initConfig(){
        Scrapy scrapy=new Scrapy();
        scrapy.initSuggestionBook(SuggestionUrl,this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //避免再次进入apps时会显示欢迎页面

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
               finish();
               return;
           }
        listRes=new ArrayList<>();
        disableChecks(this);//信任所有证书
        setContentView(R.layout.activity_welcome);
        initWidget();
        initConfig();
        InitClass();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            mPermissionsChecker = new PermissionsChecker(this);
            requestPermission();
        }else {
            start();
        }
    }
    public static void disableChecks(Context context){
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new TrustAllTrustManager()}, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void InitClass(){
        List<BookSrcBean> list= BaseApi.parseJson(this);
        ACache aCache=ACache.get(this);
        for(BookSrcBean bookSrcBean:list)
            aCache.put(bookSrcBean.getSourceName(),bookSrcBean.getSourceClass());
        String DirPath= Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"fontFiles";
    }
    private void initWidget(){
        ImmersionBar.with(this)
                .fullScreen(true)
                .transparentStatusBar()
                .init();
        SystemBarUtils.hideStableStatusBar(this);
    }
    private void start(){
        myThread.start();
    }
    private void requestPermission(){
        //获取读取和写入SD卡的权限
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST_STORAGE);
        } else {
            start();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_STORAGE: {
                // 如果取消权限，则返回的值为0
                if (grantResults.length > 0
                        &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //申请权限成功
                    start();
                } else {
                    //申请权限失败
                    finish();
                    Toasty.warning(getApplicationContext(),"请给予储存权限，否则程序无法正常运行！", Toast.LENGTH_SHORT);
                }
                return;
            }
        }
    }
}