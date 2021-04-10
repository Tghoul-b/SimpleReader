package com.project.reader.ui.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.example.reader.R;
import com.gyf.barlibrary.ImmersionBar;
import com.project.reader.ui.util.PermissionsChecker;
import com.project.reader.ui.util.Scrapy;
import com.project.reader.ui.util.SystemBarUtils;

import es.dmoral.toasty.Toasty;

public class WelcomeActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_STORAGE = 1;
    private static int WAIT_INTERVAL = 2000;
    static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private String SuggestionUrl="https://www.zhetian.org/top/1";  //起点小说网的排行榜,
    private PermissionsChecker mPermissionsChecker;
    private Thread myThread = new Thread() {//创建子线程
        @Override
        public void run() {
            try {
                sleep(WAIT_INTERVAL);//使程序休眠
                Intent it = new Intent(WelcomeActivity.this, MainActivity.class);//启动MainActivity
                startActivity(it);
                finish();//关闭当前活动
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private void initConfig(){
        new Scrapy().initSuggestionBook(SuggestionUrl,getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //避免再次进入apps时会显示欢迎页面
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
        }
        setContentView(R.layout.activity_welcome);
        initWidget();
        initConfig();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            mPermissionsChecker = new PermissionsChecker(this);
            requestPermission();
        }else {
            start();
        }
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
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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